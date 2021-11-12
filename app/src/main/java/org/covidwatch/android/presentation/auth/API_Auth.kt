package org.covidwatch.android.presentation.auth

import android.os.Build
import life.league.core.util.Log
import life.league.networking.callback.*
import life.league.networking.models.AuthInfo
import life.league.networking.socket.API
import life.league.networking.socket.actions.onAuthenticated
import life.league.networking.socket.actions.sendAndReceiveData
import org.json.JSONException
import org.json.JSONObject

private const val TAG = "LeagueAPI_Auth"

/**
 * Auth0 specific version of authenticate JWT
 */
suspend fun API.authenticateJWT(jwt: String): Outcome<Empty> {
    val json = JSONObject()

    try {
        json.put("message_type", "authenticate_jwt")

        val info = JSONObject()
        info.put("jwt", jwt)
        info.put("api_version", 1)

        json.put("info", info)
    } catch (e: JSONException) {
        val message = "authenticateJWT failed: $e"
        log.e(TAG, message)
        return Failure(message)
    }

    val result: Outcome<Empty> = sendAndReceiveData(json, requiresAuthentication = false)
    if (result is Success) {
        onAuthenticated()
    }
    return result
}

/**
 * Call this when we want to verify the auth code with the backend
 * Used with PC Health
 */
suspend fun API.authorizeDevice(
    deviceAuthorizationCode: String
): Outcome<AuthInfo> {

    val json = JSONObject()
    try {
        json.put("message_type", "authorize_device")

        val info = JSONObject()
        info.put("device_authorization_code", deviceAuthorizationCode)
        info.put("os_version", "" + Build.VERSION.SDK_INT)
        info.put("device_type", Build.MODEL)
        info.put("api_version", 1)

        json.put("info", info)
    } catch (e: JSONException) {
        val message = "Error building JSON in AuthorizeDevice: $e"
        Log.e(TAG, message)
        return Failure(message)
    }

    return sendAndReceiveData(json, requiresAuthentication = false)
}

/**
 * Call this to send a verification code to the user
 */
internal fun API.requestDeviceAuth(
    phoneCountryCode: Int? = null,
    phoneNumber: String? = null,
    voiceCall: Boolean = false,
    callback: RequestCallback<Empty>
) {

    val json = JSONObject()
    try {
        json.put("message_type", "request_device_authorization")

        val info = JSONObject()
        info.put("phone_country_code", phoneCountryCode)
        info.put("phone_number", phoneNumber)
        info.put("voice_call", voiceCall)

        json.put("info", info)
    } catch (e: JSONException) {
        val message = "Error building JSON in RequestDeviceAuth: $e"
        Log.e(TAG, message)
        callback.onFailure(message)
        return
    }

    sendAndReceiveData(json, false, callback)
}

/**
 * Call this after the user enters their email and password
 *
 * Specific to PC Health
 */
internal fun API.setUserCredentials(
    email: String,
    password: String,
    signInMethod: SignInMethod,
    tenantId: String,
    callback: RequestCallback<UserCredential>
) {

    val json = JSONObject()
    try {
        json.put("message_type", "set_user_credentials")

        val info = JSONObject()
        info.put("email", email)
        info.put("auth_token", password)
        info.put("auth_method", signInMethod.value)
        info.put("tenant_id", tenantId)

        json.put("info", info)
    } catch (e: JSONException) {
        val message = "Error building JSON in setUserCredentials: $e"
        Log.e(TAG, message)
        callback.onFailure(message)
        return
    }

    sendAndReceiveData(json, false, callback)
}

/**
 * Call this when we want to verify the auth code with the backend
 */
suspend fun API.confirmPhoneNumberChange(
    smsCode: String,
): Outcome<AuthInfo> {

    val json = JSONObject()
    try {
        json.put("message_type", "confirm_phone_number_change")
        val info = JSONObject()
        info.put("sms_code", smsCode)
        json.put("info", info)
    } catch (e: JSONException) {
        val message = "Error building JSON in AuthorizeDevice: $e"
        android.util.Log.e(TAG, message)
        return Failure(message)
    }

    return sendAndReceiveData(json)
}

/**
 * Call this to send a verification code after the user updates the phone number
 */
fun API.requestPhoneNumberChange(
    phoneCountryCode: Int,
    phoneNumber: String,
    voiceCall: Boolean = false,
    callback: RequestCallback<Empty>
) {

    val json = JSONObject()
    try {
        json.put("message_type", "request_phone_number_change")

        val info = JSONObject()
        info.put("phone_country_code", phoneCountryCode.toString())
        info.put("phone_number", phoneNumber)
        info.put("voice_call", voiceCall)

        json.put("info", info)
    } catch (e: JSONException) {
        val message = "Error building JSON in RequestDeviceAuth: $e"
        android.util.Log.e(TAG, message)
        callback.onFailure(message)
        return
    }

    sendAndReceiveData(json, callback)
}