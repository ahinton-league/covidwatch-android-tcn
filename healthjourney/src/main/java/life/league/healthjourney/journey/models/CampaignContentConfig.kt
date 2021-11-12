package life.league.healthjourney.programs.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import life.league.healthjourney.journey.models.PulseCheck
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class CampaignContentConfig(
    @Json(name = "dataFields") val dataFields: List<String>,
    @Json(name = "ctaUrl") val ctaUrl: String,
    @Json(name = "pulse_checks") val pulseChecks: List<PulseCheck>?,
) : Serializable
