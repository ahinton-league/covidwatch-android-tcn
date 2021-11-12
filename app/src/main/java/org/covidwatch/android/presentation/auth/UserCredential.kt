package org.covidwatch.android.presentation.auth

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserCredential(
    @Json(name = "user_id") val userId: String,
    @Json(name = "phone_set") val isPhoneSet: Boolean? = null
)