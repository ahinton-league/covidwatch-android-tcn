package org.covidwatch.android.data

import android.os.Build
import life.league.core.model.user.UserFlags
import life.league.networking.callback.Failure
import life.league.networking.callback.Outcome
import life.league.networking.callback.Success
import life.league.networking.socket.API
import life.league.networking.socket.actions.onAuthenticated
import life.league.networking.socket.actions.sendAndReceiveData
import org.json.JSONException
import org.json.JSONObject
import java.util.*

private const val TAG = "API_Auth"

/**
 * Authenticate device with backend after successful connection. This is for legacy
 * League connections.
 */
suspend fun API.authenticate(
    sessionId: String,
    deviceToken: String,
    pushToken: String? = null
): Outcome<UserFlags> {
    log.d(TAG, "Connected to web socket, authenticating...")

    val json = JSONObject()
    try {
        json.put("message_type", "authenticate")

        val info = JSONObject()
        info.put("session_id", sessionId)
        info.put("device_token", deviceToken)
        info.put("version", appVersion)
        info.put("platform", "android")
        info.put("os_version", "" + Build.VERSION.SDK_INT)
        info.put("device_type", Build.MODEL)
        info.put("tz_name", TimeZone.getDefault().id)
        info.put("push_notification_endpoint", "fcm")
        if (pushToken?.isNotEmpty() == true) {
            info.put("push_notification_token", pushToken)
        }
        json.put("info", info)
        json.put("api_version", API.API_VERSION)
    } catch (e: JSONException) {
        "Error building JSON for authentication message: $e".let {
            log.e(TAG, it)
            return Failure(it)
        }
    }

    val result: Outcome<UserFlags> = sendAndReceiveData(json, requiresAuthentication = false)
    if (result is Success) {
        onAuthenticated()
    }
    return result
}