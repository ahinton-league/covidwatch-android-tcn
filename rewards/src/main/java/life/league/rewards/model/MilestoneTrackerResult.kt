package life.league.rewards.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MilestoneTrackerResult(
    @Json(name = "achievements_stats") val milestones: List<Milestone>
)

@JsonClass(generateAdapter = true)
data class Milestone(
    @Json(name = "cumulative_count_stat") val count: Int,
    @Json(name = "unit") val unit: String,
    @Json(name = "subtitle") val subtitle: String,
    @Json(name = "image") val image: AchievementImage
)