package life.league.healthjourney.programs.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import life.league.healthjourney.journey.models.StatusCounts
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class HealthProgramDetails(
    @Json(name = "program_id") val id: String = "",
    @Json(name = "user_program_id") val userProgramId: String? = null,
    val name: String = "",
    val description: String = "",
    @Json(name = "long_description") val longDescription: String = "",
    @Json(name = "total_days") val totalDays: Int = 0,
    @Json(name = "available_points") val availablePoints: Int = 0,
    @Json(name = "image_url") val imageUrl: String = "",
    val status: String = "",
    @Json(name = "remaining_days") val remainingDays: Int? = null,
    @Json(name = "progress_percentage") val progressPercentage: Int? = null,
    @Json(name = "content_provider_modal") val contentProviderModal: Modal? = null,
    @Json(name = "campaign_content_config") val campaignContentConfig: CampaignContentConfig? = null,
    val goals: List<Goal> = emptyList(),
    @Json(name = "total_activities_count") val totalActivitiesCount: Int = 0,
    @Json(name = "activity_status_counts") val activityStatusCounts: StatusCounts? = null,
    @Json(name = "completed_activity_progress_percentage") val completedActivityProgressPercentage: Int = 0,
    @Json(name = "missed_activity_progress_percentage") val missedActivityProgressPercentage: Int = 0,
    @Json(name = "achievement_image_url") val achievementImage: String = "",
    ) : Serializable {

    companion object {
        const val ACTIVE = "active"
        const val UNAVAILABLE = "unavailable"
        const val AVAILABLE = "available"
    }

}

