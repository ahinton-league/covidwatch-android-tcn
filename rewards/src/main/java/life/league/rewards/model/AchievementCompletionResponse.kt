package life.league.rewards.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import life.league.genesis.extension.isNotNullOrEmpty
import java.util.*

@JsonClass(generateAdapter = true)
data class AchievementCompletionResponse(
    @Json(name = "achievements_info") val categories: List<Category>
)

@JsonClass(generateAdapter = true)
data class Category(
    @Json(name = "category_title") val name: String,
    @Json(name = "category_group") val group: String,
    @Json(name = "completed") val completed: List<AchievementDetail>?,
    @Json(name = "in_progress") val inProgress: List<AchievementDetail>?,
    @Json(name = "empty") val emptyState: EmptyState?,
) {
    val allAchievements = completed.orEmpty() + inProgress.orEmpty()
    val shouldShowEmptyState = allAchievements.isEmpty() && emptyState?.title.isNotNullOrEmpty()
}

@JsonClass(generateAdapter = true)
data class AchievementDetail(
    @Json(name = "id") val id: String,
    @Json(name = "description") val description: String,
    @Json(name = "name") val name: String,
    @Json(name = "celebration_title") val celebrationTitle: String,
    @Json(name = "celebration_subtitle") val celebrationSubTitle: String,
    @Json(name = "description_earned") val descriptionEarned: String,
    @Json(name = "last_completed_at") val lastCompletedAt: Date? = null,
    @Json(name = "completions") val completions: Int,
    @Json(name = "image") val achievementImage: AchievementImage,
    @Json(name = "progress") val progress: AchievementProgress?
    ) {
    var greyOut : Boolean = completions == 0 && progress?.overallProgressPercentage != null && progress.overallProgressPercentage < 100
    var showProgressbar: Boolean = progress?.overallProgressPercentage != 0 || progress.overallProgressPercentage != 100
}

@JsonClass(generateAdapter = true)
data class AchievementImage(
    @Json(name = "small") val small: String,
    @Json(name = "medium") val medium: String,
    @Json(name = "large") val large: String
)

@JsonClass(generateAdapter = true)
data class AchievementProgress(
    @Json(name = "overall") val overallProgressPercentage: Int,
    @Json(name = "title") val progressTitle: String,
    @Json(name = "criteria") val criteria: AchievementCriteria,
)

@JsonClass(generateAdapter = true)
data class AchievementCriteria(
    @Json(name = "threshold") val totalActivitiesToBeCompleted: Int,
    @Json(name = "current") val activitiesCompleted: Int,
)

@JsonClass(generateAdapter = true)
data class EmptyState(
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String,
    @Json(name = "cta") val cta: Cta,
)

@JsonClass(generateAdapter = true)
data class Cta(
    @Json(name = "url") val url: String,
    @Json(name = "title") val title: String
)