package life.league.rewards.previewdata

import life.league.rewards.model.*

object AchievementCompletionTestData {

    private const val MOCK_IMAGE_URL = "https://emojipedia-us.s3.dualstack.us-west-1.amazonaws.com/thumbs/160/apple/285/party-popper_1f389.png"

    fun createUserAchievementDataForActivityCompletion(
        includeCompleted: Boolean = true,
        includeInProgress: Boolean = true
    ): UserAchievement {

        val streaks = Category(
            name = "Weekly Streaks",
            group = "Streak",
            completed = if (includeCompleted) getCompletedStreaks() else emptyList(),
            inProgress = if (includeInProgress) getInProgressStreaks() else emptyList(),
            emptyState = getEmptyState()
        )

        val activities = Category(
            name = "Activities",
            group = "Activity",
            completed = if (includeCompleted) getCompletedActivities() else emptyList(),
            inProgress = if (includeInProgress) getInProgressActivities() else emptyList(),
            emptyState = getEmptyState()
        )

        val categories = listOf(streaks, activities)

        return UserAchievement(
            achievementCategories = categories,
            sideDetails = AchievementSideDetails(title = "", descriptionOne = "", descriptionTwo = "", rewardsMessage = "", headline = ""),
            grandTotal = null
        )
    }

    fun createResponse(includeCompleted: Boolean = true,
                       includeInProgress: Boolean = true): AchievementCompletionResponse {
        val streaksCompleted = getCompletedStreaks()
        val streaksInProgress = getInProgressStreaks()

        val activityCompleted = getCompletedActivities()
        val activityInProgress = getInProgressActivities()

        val categoryStreaks = Category(
            name = "Weekly Streaks",
            group = "Streak",
            inProgress = if (includeInProgress) streaksInProgress else emptyList(),
            completed = if (includeCompleted) streaksCompleted else emptyList(),
            emptyState = getEmptyState()
        )

        val categoryActivities = Category(
            name = "Activities",
            group = "Activity",
            inProgress = if (includeInProgress) activityInProgress else emptyList(),
            completed = if (includeCompleted) activityCompleted else emptyList(),
            emptyState = getEmptyState()
        )

        return AchievementCompletionResponse(categories = listOf(categoryStreaks, categoryActivities))
    }

    fun getCompletedStreaks(): List<AchievementDetail> {
        val completeAchievement1 = AchievementDetail(
            id = "popcvjkngjshfjsncpojvsdiojns",
            name = "3-week streak",
            description = "Complete at least 1 activity a week, 3 weeks in a row.",
            completions = 3,
            descriptionEarned = "You earned this badge by completing at least 1 activity a week, 3 weeks in a row.",
            achievementImage = AchievementImage(
                small = MOCK_IMAGE_URL,
                medium = MOCK_IMAGE_URL,
                large = MOCK_IMAGE_URL
            ),
            lastCompletedAt = null,
            progress = AchievementProgress(
                overallProgressPercentage = 100,
                progressTitle = "",
                criteria = AchievementCriteria(
                    totalActivitiesToBeCompleted = 3,
                    activitiesCompleted = 3
                )
            ),
            celebrationSubTitle = "",
            celebrationTitle = ""
        )

        return listOf(completeAchievement1)
    }

    private fun getInProgressStreaks(): List<AchievementDetail> {
        val inProgressAchievement1 = AchievementDetail(
            id = "hxchjvbbjfsfnadnsda",
            name = "5-week streak",
            description = "Complete at least 1 activity a week, 5 weeks in a row.",
            completions = 1,
            descriptionEarned = "You earned this badge by completing at least 1 activity a week, 5 weeks in a row.",
            achievementImage = AchievementImage(
                small = MOCK_IMAGE_URL,
                medium = MOCK_IMAGE_URL,
                large = MOCK_IMAGE_URL
            ),
            lastCompletedAt = null,
            progress = AchievementProgress(
                overallProgressPercentage = 80,
                progressTitle = "",
                criteria = AchievementCriteria(
                    totalActivitiesToBeCompleted = 5,
                    activitiesCompleted = 3
                )
            ),
            celebrationSubTitle = "",
            celebrationTitle = ""
        )

        val inProgressAchievement2 = AchievementDetail(
            id = "hxchjvbbjfsfnadnsda",
            name = "8-week streak",
            description = "Complete at least 1 activity a week, 8 weeks in a row.",
            completions = 0,
            descriptionEarned = "You earned this badge by completing at least 1 activity a week, 8 weeks in a row.",
            achievementImage = AchievementImage(
                small = MOCK_IMAGE_URL,
                medium = MOCK_IMAGE_URL,
                large = MOCK_IMAGE_URL
            ),
            lastCompletedAt = null,
            progress = AchievementProgress(
                overallProgressPercentage = 33,
                progressTitle = "",
                criteria = AchievementCriteria(
                    totalActivitiesToBeCompleted = 8,
                    activitiesCompleted = 3
                )
            ),
            celebrationSubTitle = "",
            celebrationTitle = ""
        )

        return listOf(inProgressAchievement1, inProgressAchievement2)
    }


    private fun getCompletedActivities(): List<AchievementDetail> {
        val completeAchievement1 = AchievementDetail(
            id = "8329e9123e091",
            name = "3 activities",
            description = "Complete 3 activities",
            completions = 3,
            descriptionEarned = "You earned this badge by completing 3 activities",
            achievementImage = AchievementImage(
                small = MOCK_IMAGE_URL,
                medium = MOCK_IMAGE_URL,
                large = MOCK_IMAGE_URL
            ),
            lastCompletedAt = null,
            progress = AchievementProgress(
                overallProgressPercentage = 100,
                progressTitle = "",
                criteria = AchievementCriteria(
                    totalActivitiesToBeCompleted = 3,
                    activitiesCompleted = 3
                )
            ),
            celebrationSubTitle = "",
            celebrationTitle = ""
        )

        return listOf(completeAchievement1)
    }

    private fun getInProgressActivities(): List<AchievementDetail> {
        val inProgressAchievement1 = AchievementDetail(
            id = "8329e9123e091",
            name = "5 activities",
            description = "Complete 5 activities",
            completions = 1,
            descriptionEarned = "You earned this badge by completing 5 activities",
            achievementImage = AchievementImage(
                small = MOCK_IMAGE_URL,
                medium = MOCK_IMAGE_URL,
                large = MOCK_IMAGE_URL
            ),
            lastCompletedAt = null,
            progress = AchievementProgress(
                overallProgressPercentage = 65,
                progressTitle = "",
                criteria = AchievementCriteria(
                    totalActivitiesToBeCompleted = 5,
                    activitiesCompleted = 3
                )
            ),
            celebrationSubTitle = "",
            celebrationTitle = ""
        )

        val inProgressAchievement2 = AchievementDetail(
            id = "8329e9123e091",
            name = "10 activities",
            description = "Complete 10 activities",
            completions = 0,
            descriptionEarned = "You earned this badge by completing 10 activities",
            achievementImage = AchievementImage(
                small = MOCK_IMAGE_URL,
                medium = MOCK_IMAGE_URL,
                large = MOCK_IMAGE_URL
            ),
            lastCompletedAt = null,
            progress = AchievementProgress(
                overallProgressPercentage = 30,
                progressTitle = "",
                criteria = AchievementCriteria(
                    totalActivitiesToBeCompleted = 10,
                    activitiesCompleted = 3
                )
            ),
            celebrationSubTitle = "",
            celebrationTitle = ""
        )

        return listOf(inProgressAchievement1, inProgressAchievement2)
    }
    
    private fun getEmptyState() = EmptyState(
        title = "",
        description = "",
        cta = Cta(url = "", title = "")
    )
}