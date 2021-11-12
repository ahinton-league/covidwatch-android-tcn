package org.covidwatch.android.presentation.auth

import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.authentication.storage.CredentialsManagerException
import com.auth0.android.authentication.storage.SecureCredentialsManager
import com.auth0.android.authentication.storage.SharedPreferencesStorage
import com.auth0.android.callback.Callback
import com.auth0.android.jwt.JWT
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.squareup.moshi.JsonClass
import kotlinx.coroutines.suspendCancellableCoroutine
import life.league.core.model.SignOutReason
import life.league.core.util.Log
import life.league.core.util.SessionUtils
import life.league.core.util.SharedPreferencesUtils
import life.league.networking.callback.Empty
import life.league.networking.callback.Failure
import life.league.networking.callback.Outcome
import life.league.networking.callback.Success
import life.league.networking.content.ContentAPIAuthenticator
import life.league.networking.rest.LeagueRestAPIAuthenticator
import life.league.networking.socket.API
import life.league.networking.socket.LeagueSocketAPIAuthenticator
import okhttp3.Request
import kotlin.coroutines.resume

/**
 * Wrapper for auth0 authentication
 */
open class Auth0Authenticator internal constructor(
    // this internal constructor is used for unit testing, so we can mock deps
    context: Context,
    clientId: String,
    domain: String,
    private val scheme: String,
    sessionUtils: SessionUtils,
    private val account: Auth0 = Auth0(clientId, domain),
    private val apiClient: AuthenticationAPIClient = AuthenticationAPIClient(account),
    sharedPreferencesStorage: SharedPreferencesStorage = SharedPreferencesStorage(context),
    private val credentialStorage: SecureCredentialsManager = SecureCredentialsManager(
        context,
        apiClient,
        sharedPreferencesStorage
    ),
    private val biometricCredentialStorage: SecureCredentialsManager = SecureCredentialsManager(
        context,
        apiClient,
        sharedPreferencesStorage
    )
) : LeagueSocketAPIAuthenticator, ContentAPIAuthenticator, LeagueRestAPIAuthenticator {

    constructor(
        clientId: String,
        domain: String,
        scheme: String,
        sessionUtils: SessionUtils,
        context: Context
    ) : this(
        context,
        clientId,
        domain,
        scheme,
        sessionUtils
    )

    companion object {
        const val TAG = "Auth0Authenticator"
        const val RC_UNLOCK_AUTHENTICATION = 123
        const val AUTHORIZATION_HEADER_NAME = "Authorization"
    }

    init {
        sessionUtils.signOutListeners.add { localContext, reason ->
            if (isLoggedIn) {
                val localUserId = userId
                clearState(localContext, reason)
                signOutListeners.forEach {
                    it(
                        (reason != null && reason != SignOutReason.ExpiredTimeout) || (localUserId != null)
                    )
                }
            }
        }
    }

    private val sharedPrefs =
        SharedPreferencesUtils(context = context, filename = "life.league.auth0_authentication")

    override var userId: String?
        get() = sharedPrefs.getString(Auth0Authenticator::userId.name, null)
        set(value) {
            sharedPrefs.putString(Auth0Authenticator::userId.name, value)
            try {
                FirebaseCrashlytics.getInstance().setUserId(value ?: "")
            } catch (th: Throwable) {
                Log.w(TAG, "Unable to set user id in firebase")
            }
        }

    var jwt: String?
        get() = sharedPrefs.getString(
            Auth0Authenticator::jwt.name,
            defaultValue = null,
            useCache = true,
            shouldDecrypt = true
        )
        set(value) {
            sharedPrefs.putString(
                Auth0Authenticator::jwt.name,
                value,
                useCache = true,
                shouldEncrypt = true
            )
        }

    override val canAuthenticateWithBiometrics: Boolean
        get() = biometricCredentialStorage.hasValidCredentials()

    // This needs to get called from onActivityResult in order for the auth0 biometrics
    // check to work
    fun checkAuthenticationResult(requestCode: Int, resultCode: Int): Boolean =
        biometricCredentialStorage.checkAuthenticationResult(requestCode, resultCode)

    /**
     * @param useLeagueAppCallback overrides the callback url to use league stage's url.
     * Used for feature module presenter apps. Don't use this in a production application.
     * // TODO: have this support production too
     */
    suspend fun login(context: Context) =
        suspendCancellableCoroutine<Outcome<UserCredentials>> { cont ->
            // TODO: add privacy lock challenge
            val builder = WebAuthProvider.login(account)
            builder.withScheme(scheme)
                .withScope("openid profile email offline_access")
                .start(context, object : Callback<Credentials, AuthenticationException> {
                    override fun onFailure(error: AuthenticationException) {
                        Log.e(TAG, "Failed authentication", error, true)
                        cont.resume(Failure("${error.getCode()} - ${error.getDescription()}"))
                    }

                    override fun onSuccess(result: Credentials) {
                        Log.v(TAG, "User ${result.idToken.jwt.userId} authenticated", true)
                        credentialStorage.saveCredentials(result)
                        result.userCredentials.also {
                            jwt = it.jwt
                            userId = it.userId
                            cont.resume(Success(it))
                        }

                    }
                })
        }

    // TODO: make this private so it's only called from clearState
    suspend fun logout(context: Context) =
        suspendCancellableCoroutine<Outcome<Empty>> { cont ->
            val builder = WebAuthProvider.logout(account)
            builder.withScheme(scheme)
                .start(context, object : Callback<Void?, AuthenticationException> {
                    override fun onFailure(error: AuthenticationException) {
                        Log.e(TAG, "Failed to logout", error, true)
                        cont.resume(Failure(error.toString()))
                        // TODO: should we try to manually clear state anyways??
                    }

                    override fun onSuccess(result: Void?) {
                        Log.v(TAG, "Logged out", true)
                        jwt = null
                        userId = null
                        cont.resume(Success(Empty))
                    }
                })
        }

    // APIAuthenticator and ContentAPIAuthenticator methods

    override var pushNotificationToken: String? = null

    override val signOutListeners: MutableSet<(Boolean) -> Unit> = mutableSetOf()

    override val isLoggedIn: Boolean
        get() = userId != null && jwt != null


    suspend fun refreshJwt() = suspendCancellableCoroutine<Outcome<UserCredentials>> { cont ->
        // getCredentials automatically refreshes the token
        credentialStorage.getCredentials(object :
            Callback<Credentials, CredentialsManagerException> {
            override fun onSuccess(result: Credentials) {
                Log.v(TAG, "User ${result.idToken.jwt.userId} refreshed", true)
                userId = result.idToken.jwt.userId
                jwt = result.accessToken
                cont.resume(Success(result.userCredentials))
            }

            override fun onFailure(error: CredentialsManagerException) {
                Log.e(TAG, "Failed to renew token, renewAuth api call failed", error, true)
                cont.resume(Failure(error.toString()))
            }
        })
    }

    override fun buildNewAuthenticatedRequest(request: Request): Request {
        val builder = request.newBuilder()
        jwt?.let { token ->
            if (token.isNotBlank()) {
                builder.addHeader(AUTHORIZATION_HEADER_NAME, token)
            }
        }
        return builder.build()
    }

    override val authenticationHeaderNames = listOf(AUTHORIZATION_HEADER_NAME)

    override fun buildContentSaveUrl(endpoint: String): String = "${endpoint}content"

    override suspend fun authenticateSocket(api: API): Boolean {
        jwt?.let { token ->
            return api.authenticateJWT(token) is Success
        }
        return false
    }

    @Synchronized
    override suspend fun refreshAndAuthenticateSession(api: API): Boolean {
        if (refreshJwt() is Success) {
            return authenticateSocket(api)
        }
        return false
    }

    /**
     * Note: `activity` must implement onActivityResult, and call checkAuthenticationResult with
     * the results of any requests that matched code RC_UNLOCK_AUTHENTICATION
     */
    override suspend fun authenticateUsingBiometrics(
        activity: FragmentActivity,
        promptTitle: String
    ): Boolean = suspendCancellableCoroutine { cont ->
        if (!biometricCredentialStorage.requireAuthentication(
                activity,
                RC_UNLOCK_AUTHENTICATION,
                promptTitle,
                null
            )
        ) {
            if (cont.isActive) {
                cont.resume(value = false)
            }
        }

        biometricCredentialStorage.getCredentials(object :
            Callback<Credentials, CredentialsManagerException> {
            override fun onFailure(error: CredentialsManagerException) {
                // We do a check to see if it's active because there's a bug in the
                // android sdk that causes the results of a biometrics check to return
                // more than once
                if (cont.isActive) {
                    cont.resume(false)
                }
            }

            override fun onSuccess(result: Credentials) {
                userId = result.idToken.jwt.userId
                jwt = result.accessToken
                if (cont.isActive) {
                    cont.resume(true)
                }
            }
        })
    }

    override fun disconnect() {
    }

    private suspend fun clearState(context: Context?, reason: SignOutReason?) {
        userId?.let {
            if (
                (reason != null && reason != SignOutReason.ExpiredTimeout)
            ) {
                credentialStorage.clearCredentials()
            }
        } ?: run {
            credentialStorage.clearCredentials()
        }

        jwt = null
        userId = null

        context?.let { logout(it) }
    }
}

/**
 * Public credentials meant to be exposed to the rest of the app
 */
@JsonClass(generateAdapter = true)
data class UserCredentials(val userId: String?, val jwt: String)

private val Credentials.userCredentials get() = UserCredentials(idToken.jwt.userId, accessToken)

private val String.jwt get() = JWT(this)

private val JWT.userId: String? get() = getClaim("https://el/user_id").asString()