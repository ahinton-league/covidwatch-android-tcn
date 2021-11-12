package life.league.healthjourney.journey.models

import com.squareup.moshi.Json
import java.io.Serializable

data class WearableConsentResponse(
    @Json(name = "data_point_device_map") val response: Map<String, List<ConsentData>>
) : Serializable

data class ConsentData(
    @Json(name = "consent_requested") val consentRequested: Boolean,
    val device: String,
    @Json(name = "receiving_data") val receivingData: Boolean,
) : Serializable