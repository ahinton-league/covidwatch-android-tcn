package life.league.rewards.model

data class UserAchievement(
    val achievementCategories: List<Category>,
    val sideDetails: AchievementSideDetails,
    val grandTotal: Int? = null
) {
    fun getAllCategoryAchievements() = achievementCategories.flatMap { category -> category.allAchievements }

    fun getAllCompletedAchievements() = achievementCategories.flatMap { category -> category.completed.orEmpty() }

    fun getAllInProgressAchievements() = achievementCategories.flatMap { category -> category.inProgress.orEmpty() }
}

data class AchievementSideDetails(
    val title: String,
    val descriptionOne: String,
    val descriptionTwo: String,
    val rewardsMessage: String,
    val headline: String
)

data class AchievementCategory(
    val name: String,
    val completed: List<AchievementDetail>,
    val inProgress: List<AchievementDetail>
)