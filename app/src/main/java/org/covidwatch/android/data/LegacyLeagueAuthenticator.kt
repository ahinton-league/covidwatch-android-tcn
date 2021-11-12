package org.covidwatch.android.data

import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.suspendCancellableCoroutine
import life.league.core.model.SignOutReason
import life.league.core.util.EnvironmentUtils
import life.league.core.util.SessionUtils
import life.league.core.util.SharedPreferencesUtils
import life.league.networking.callback.Success
import life.league.networking.json.JsonAdapter
import life.league.networking.socket.API
import okhttp3.Request
import org.koin.core.component.KoinComponent


class LegacyLeagueAuthenticator(
    private val context: Context,
    environmentUtils: EnvironmentUtils,
    sessionUtils: SessionUtils,
    val jsonAdapter: JsonAdapter
) : LeagueAuthenticator, KoinComponent {

    companion object {
        const val SESSION_ID_HEADER_NAME = "x-league-sessionid"
        const val DEVICE_TOKEN_HEADER_NAME = "x-league-devicetoken"
    }

    init {
        sessionUtils.signOutListeners.add { _, reason ->
            val localUserId = userId
            if ((reason != null && reason != SignOutReason.ExpiredTimeout)
//                || (localUserId != null && !privacyLockUtils.getHasUserEnabledPrivacyLock(
//                    localUserId
//                ))
            ) {
                clearAuthDataForBiometrics()
            }
            clearState()
            signOutListeners.forEach {
                it(
                    (reason != null && reason != SignOutReason.ExpiredTimeout) || (localUserId != null)
                )
            }
        }
        environmentUtils.environmentChangedListeners.add {
            clearState()
            clearAuthDataForBiometrics()
            deviceToken = null
        }
//        privacyLockUtils.hasUserEnabledPrivacyLockListeners.add { userId, isEnabled ->
//            if (userId == this.userId) {
//                if (isEnabled) {
//                    saveAuthDataForBiometrics()
//                } else {
//                    clearAuthDataForBiometrics()
//                }
//            }
//        }
    }

    private val sharedPrefs =
        SharedPreferencesUtils(context = context, filename = "life.league.league_authentication")

//    private val appVersion =
//        context.packageManager.getPackageInfo(context.packageName, 0).versionName

    override val isLoggedIn: Boolean
        get() = sessionId != null && deviceToken != null

    override val signOutListeners: MutableSet<(Boolean) -> Unit> = mutableSetOf()

    fun signIn(sessionId: String?, deviceToken: String?, userId: String?) {
        this.sessionId = sessionId
        this.deviceToken = deviceToken
        this.userId = userId
//        if (userId != null && privacyLockUtils.getHasUserEnabledPrivacyLock(userId)) {
//            saveAuthDataForBiometrics()
//        } else {
//            clearAuthDataForBiometrics()
//        }
    }

    override var userId: String?
        get() = sharedPrefs.getString(LegacyLeagueAuthenticator::userId.name, null)
        private set(value) {
            sharedPrefs.putString(LegacyLeagueAuthenticator::userId.name, value)
            FirebaseCrashlytics.getInstance().setUserId(value ?: "")
        }

    var sessionId: String?
        get() = sharedPrefs.getString(
            LegacyLeagueAuthenticator::sessionId.name,
            defaultValue = null,
            useCache = true,
            shouldDecrypt = true
        )
        private set(value) {
            sharedPrefs.putString(
                LegacyLeagueAuthenticator::sessionId.name,
                value,
                useCache = true,
                shouldEncrypt = true
            )
        }

    var deviceToken: String?
        get() = sharedPrefs.getString(
            LegacyLeagueAuthenticator::deviceToken.name,
            defaultValue = null,
            useCache = true,
            shouldDecrypt = true
        )
        private set(value) {
            sharedPrefs.putString(
                LegacyLeagueAuthenticator::deviceToken.name,
                value,
                useCache = true,
                shouldEncrypt = true
            )
        }

    override var pushNotificationToken: String?
        get() = sharedPrefs.getString(
            LegacyLeagueAuthenticator::pushNotificationToken.name,
            defaultValue = null,
            useCache = true,
            shouldDecrypt = true
        )
        set(value) {
            sharedPrefs.putString(
                LegacyLeagueAuthenticator::pushNotificationToken.name,
                value,
                useCache = true,
                shouldEncrypt = true
            )
        }

    /**
     *     This is used to keep around auth info so that a successful biometrics challenge can
     *     authenticate the session. To keep sessions secure, it should never be used directly.
     */
//    private var biometricAuthInfo: LegacyBiometricAuthInfo?
//        get() = sharedPrefs.getJsonable(
//            LegacyBiometricAuthInfo::class.java,
//            jsonAdapter,
//            LegacyLeagueAuthenticator::biometricAuthInfo.name,
//            defaultValue = null,
//            useCache = false,
//            shouldDecrypt = true
//        )
//        private set(value) {
//            sharedPrefs.putJsonable(
//                LegacyBiometricAuthInfo::class.java,
//                jsonAdapter,
//                LegacyLeagueAuthenticator::biometricAuthInfo.name,
//                value,
//                useCache = false,
//                shouldEncrypt = true
//            )
//        }

    override val canAuthenticateWithBiometrics: Boolean
        get() = false
//        get() = sharedPrefs.contains(LegacyLeagueAuthenticator::biometricAuthInfo.name)

    private fun saveAuthDataForBiometrics() {
//        biometricAuthInfo = LegacyBiometricAuthInfo(
//            userId = userId,
//            sessionId = sessionId,
//            deviceToken = deviceToken
//        )
    }

    private fun clearAuthDataForBiometrics() {
//        biometricAuthInfo = null
    }

    override fun buildNewAuthenticatedRequest(request: Request): Request {
        val builder = request.newBuilder()
        sessionId?.let { sid ->
            if (sid.isNotBlank()) {
                builder.addHeader(SESSION_ID_HEADER_NAME, sid)
            }
        }
        deviceToken?.let { deviceToken ->
            if (deviceToken.isNotBlank()) {
                builder.addHeader(DEVICE_TOKEN_HEADER_NAME, deviceToken)
            }
        }
        return builder.build()
    }

    override val authenticationHeaderNames =
        listOf(SESSION_ID_HEADER_NAME, DEVICE_TOKEN_HEADER_NAME)

    override fun buildContentSaveUrl(endpoint: String) = "${endpoint}contentsave/$sessionId"

    override suspend fun authenticateSocket(api: API): Boolean =
        sessionId?.let { sid ->
            deviceToken?.let { dt ->
                api.authenticate(sid, dt, pushNotificationToken) is Success
            }
        } ?: false


    override suspend fun refreshAndAuthenticateSession(api: API): Boolean {
        // League has no way to refresh a session token
        return false
    }

    override suspend fun authenticateUsingBiometrics(
        activity: FragmentActivity,
        promptTitle: String
    ): Boolean = suspendCancellableCoroutine { cont ->
//        activity.let {
//            PrivacyLockLoginPrompt(
//                activity = activity,
//                promptTitle = promptTitle,
//                privacyLockUtils = privacyLockUtils,
//                analyticsTracker = get(),
//                onSuccess = {
//                    biometricAuthInfo?.let {
//                        signIn(
//                            sessionId = it.sessionId,
//                            deviceToken = it.deviceToken,
//                            userId = it.userId
//                        )
//                    }
//                    cont.resume(true)
//                },
//                onFailure = {
//                    cont.resume(false)
//                }
//            ).promptForBiometrics()
//        }
    }

    override fun disconnect() {
    }

    private fun clearState() {
        userId = null
        sessionId = null
    }

//    /**
//     * Loads the appropriate auth cookies for use in a league webview
//     */
//    override fun setupAuthCookiesForHostedWebview(url: String, includeDeviceToken: Boolean) {
//        val cookieManager = CookieManager.getInstance()
//        cookieManager.setAcceptCookie(true)
//        cookieManager.setCookie(url, "waitForMobileAuth=false;")
//        cookieManager.setCookie(url, "no_auth=true;")
//        cookieManager.setCookie(url, "Device=Android;")
//        cookieManager.setCookie(url, "V=$appVersion;")
//        if (isLoggedIn) {
//            cookieManager.setCookie(url, "U=$userId;")
//            cookieManager.setCookie(url, "R=consumer;")
//            cookieManager.setCookie(url, "SID=$sessionId;")
//            cookieManager.setCookie(url, "SID2=$sessionId;")
//            if (includeDeviceToken) {
//                cookieManager.setCookie(url, "DT=$deviceToken;")
//                cookieManager.setCookie(url, "DT2_$userId=$deviceToken;")
//            }
//        }
//        cookieManager.flush()
//    }

//    override fun authenticateWebViewWithJavascript(webView: WebView) {
//        // do nothing, the cookies authenticate the webview
//    }
//
//    override fun authenticateAndLoadMarketplaceWebview(
//        webview: WebView,
//        url: String,
//        baseUrl: String
//    ) {
//        val sidCookieString = "SID=${sessionId}"
//        val dtCookieString = "DT=${deviceToken}"
//        CookieManager.getInstance().setCookie(baseUrl, sidCookieString) {
//            CookieManager.getInstance().setCookie(baseUrl, dtCookieString) {
//                webview.loadUrl(url)
//            }
//        }
//    }
//
//    @JsonClass(generateAdapter = true)
//    data class LegacyBiometricAuthInfo(
//        val userId: String?,
//        val sessionId: String?,
//        val deviceToken: String?
//    )

}