package life.league.healthjourney.journey.models

import com.squareup.moshi.Json
import life.league.healthjourney.programs.models.HealthJourneyItemDetail

data class HealthJourneyItemDetailResponse(
    @Json(name = "health_activity") val healthJourneyItemDetail: HealthJourneyItemDetail = HealthJourneyItemDetail(),
    @Json(name = "redirect_link") val redirectLink: String? = null
)
