package life.league.rewards.api

import com.squareup.moshi.JsonClass
import life.league.networking.socket.MessageRequest

interface AchievementsApi {

    @JsonClass(generateAdapter = true)
    data class FetchAchievementInfoOnCompletion(val version: Int = 1) :
        MessageRequest("fetch_and_sync_achievement_updates")

    @JsonClass(generateAdapter = true)
    data class FetchAllAchievements(val version: Int = 1) :
        MessageRequest("get_user_achievements")

    @JsonClass(generateAdapter = true)
    data class FetchRecentlyEarnedAchievements(val version: Int = 1) :
        MessageRequest("get_recent_achievements")

    @JsonClass(generateAdapter = true)
    data class FetchAchievementsProgress(val version: Int = 1) :
        MessageRequest("get_achievements_stats")

}