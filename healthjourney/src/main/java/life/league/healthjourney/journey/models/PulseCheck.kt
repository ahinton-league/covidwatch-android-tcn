package life.league.healthjourney.journey.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class PulseCheck(
    @Json(name = "name") val dataFields: String,
    @Json(name = "ctaText") val ctaText: String,
    @Json(name = "ctaUrl") val ctaUrl: String,
    @Json(name = "scenario") val scenario: String,
) : Serializable

enum class PulseCheckScenario(val value: String) {
    CAMPAIGN_REMOVED("campaign_removed"),
    CAMPAIGN_COMPLETED("campaign_completed"),
}