package org.covidwatch.android.data

import android.content.Context
import androidx.fragment.app.FragmentActivity
import life.league.core.util.Log
import life.league.core.util.SharedPreferencesUtils
import life.league.networking.socket.API
import okhttp3.Request

class CompositeLeagueAuthenticator(
    private val legacyAuthenticator: LegacyLeagueAuthenticator,
    context: Context
) : LeagueAuthenticator {

    companion object {
        const val TAG = "CompositeLeagueAuthenticator"
    }

    init {
        legacyAuthenticator.signOutListeners.add { invalidateSessionId ->
            signOutListeners.forEach { it(invalidateSessionId) }
        }
    }

    enum class AuthenticationMethod {
        Legacy,
        Auth0;

        companion object {
            fun parse(value: String): AuthenticationMethod? {
                return try {
                    valueOf(value)
                } catch (ex: Exception) {
                    Log.Companion.e(TAG, "Error parsing AuthenticationMethod enum: $value")
                    Legacy
                }
            }
        }
    }

    private val sharedPrefs =
        SharedPreferencesUtils(context = context, filename = "life.league.auth0_authentication")

    var authenticationMethod: AuthenticationMethod
        get() = sharedPrefs.getIntOrNull(
            CompositeLeagueAuthenticator::authenticationMethod.name,
        )?.let {
            AuthenticationMethod.values()[it]
        } ?: AuthenticationMethod.Legacy
        set(value) {
            sharedPrefs.putInt(
                CompositeLeagueAuthenticator::authenticationMethod.name,
                value.ordinal
            )
        }

    val authenticator: LeagueAuthenticator
        get() = when (authenticationMethod) {
            AuthenticationMethod.Legacy -> legacyAuthenticator
            AuthenticationMethod.Auth0 -> legacyAuthenticator
        }

    override val userId: String?
        get() = authenticator.userId
    override var pushNotificationToken: String?
        get() = authenticator.pushNotificationToken
        set(value) {
            legacyAuthenticator.pushNotificationToken = value
        }

    override val signOutListeners: MutableSet<(Boolean) -> Unit> = mutableSetOf()

    override val isLoggedIn: Boolean
        get() = authenticator.isLoggedIn
    override val canAuthenticateWithBiometrics: Boolean
        get() = authenticator.canAuthenticateWithBiometrics

//    override fun setupAuthCookiesForHostedWebview(url: String, includeDeviceToken: Boolean) =
//        authenticator.setupAuthCookiesForHostedWebview(url, includeDeviceToken)
//
//    override fun authenticateWebViewWithJavascript(webView: WebView) {
//        return authenticator.authenticateWebViewWithJavascript(webView)
//    }

    override val authenticationHeaderNames: List<String>
        get() = authenticator.authenticationHeaderNames

    override fun buildNewAuthenticatedRequest(request: Request) =
        authenticator.buildNewAuthenticatedRequest(request)

    override fun buildContentSaveUrl(endpoint: String) = authenticator.buildContentSaveUrl(endpoint)

    override suspend fun authenticateSocket(api: API) =
        authenticator.authenticateSocket(api)

    override suspend fun refreshAndAuthenticateSession(api: API) =
        authenticator.refreshAndAuthenticateSession(api)

    override suspend fun authenticateUsingBiometrics(
        activity: FragmentActivity,
        promptTitle: String
    ): Boolean = authenticator.authenticateUsingBiometrics(activity, promptTitle)

    override fun disconnect() {
        authenticator.disconnect()
    }

//    override fun authenticateAndLoadMarketplaceWebview(
//        webview: WebView,
//        url: String,
//        baseUrl: String
//    ) = authenticator.authenticateAndLoadMarketplaceWebview(webview, url, baseUrl)

//    suspend fun authenticateSocketAndPopulateUserFlags(
//        api: API,
//        UserRepository: UserRepository
//    ) = when (authenticationMethod) {
//        AuthenticationMethod.Legacy -> UserRepository.authenticateLegacyConnection(
//            api = api,
//            sessionId = legacyAuthenticator.sessionId,
//            deviceToken = legacyAuthenticator.deviceToken,
//            pushToken = legacyAuthenticator.pushNotificationToken
//        )
//        AuthenticationMethod.Auth0 -> UserRepository.authenticateAuth0Connection(
//            api = api,
//            jwt = auth0Authenticator.jwt,
//            pushNotificationToken = auth0Authenticator.pushNotificationToken
//        )
//    }

}