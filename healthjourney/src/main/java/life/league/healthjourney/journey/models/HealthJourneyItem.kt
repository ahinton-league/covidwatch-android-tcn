package life.league.healthjourney.journey.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable
import java.util.*


/**
 * This is what product is referring to as a health journey activity. Renamed to HealthJourneyItem
 * so that it doesn't get confused with an actual Activity
 */
@JsonClass(generateAdapter = true)
data class HealthJourneyItem(
        val id: String = "",
        val name: String = "",
        val tagline: String = "",
        val description: String = "",
        @Json(name = "icon_url") val iconUrl: String = "",
        @Json(name = "activity_points") val activityPoints: Int = 0,
        @Json(name = "points_earned") val pointsEarned: Int = 0,
        val status: String = "",
        val suggested: Boolean = false,
        @Json(name = "start_date") val startDate: Date = Date(0),
        @Json(name = "end_date") val endDate: Date = Date(0),
        @Json(name = "completed_date") val completeDate: Date? = null,
        val type: String = "",
        @Json(name = "card_tagline") val cardTagline: String = "",
        @Json(name = "activity_expires") val activityExpires: Boolean = false,
        @Json(name = "campaign_info") val campaignInfo: CampaignInfo? = null,

) : Serializable {

    enum class Status(val text: String) {
            ACTIVE("active"),
            UPCOMING("upcoming"),
            EXPIRED("expired"),
            REMOVED("removed"),
            COMPLETED("completed")
    }
}

data class CampaignInfo(
        @Json(name = "campaign_id") val id: String = "",
        @Json(name = "name") val name: String = "",
        @Json(name = "start_date") val startDate: Date = Date(0),
)
