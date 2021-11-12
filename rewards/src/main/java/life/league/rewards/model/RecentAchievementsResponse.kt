package life.league.rewards.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RecentAchievementsResponse(
    @Json(name = "recent_achievements") val recentAchievements: List<AchievementDetail>?
)