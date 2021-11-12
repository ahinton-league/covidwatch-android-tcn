package life.league.healthjourney.programs.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import life.league.healthjourney.journey.models.StatusCounts
import java.io.Serializable

/**
 * Represents a single program without all details ([HealthProgramDetail] for class including all
 * details). This class object is used for both enrolled and unenrolled programs
 */
@JsonClass(generateAdapter = true)
data class HealthProgram(
    @Json(name = "program_id") val id: String = "",
    val name: String = "",
    val description: String = "",
    @Json(name = "image_url") val imageUrl: String = "",
    @Json(name = "total_days") val totalDays: Int = 0,
    @Json(name = "remaining_days") val remainingDays: Int = 0,
    @Json(name = "available_points") val availablePoints: Int = 0,
    @Json(name = "progress_percentage") val progressPercentage: Int = 0,
    @Json(name = "total_activities_count") val totalActivitiesCount: Int = 0,
    @Json(name = "activity_status_counts") val activityStatusCounts: StatusCounts? = null,
    @Json(name = "completed_activity_progress_percentage") val completedActivityProgressPercentage: Int = 0,
    @Json(name = "missed_activity_progress_percentage") val missedActivityProgressPercentage: Int = 0,
): Serializable
