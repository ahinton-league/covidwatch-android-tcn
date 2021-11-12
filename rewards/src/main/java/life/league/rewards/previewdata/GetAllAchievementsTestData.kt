package life.league.rewards.previewdata

import life.league.rewards.model.*

object GetAllAchievementsTestData {

    private const val MOCK_IMAGE_URL = "https://emojipedia-us.s3.dualstack.us-west-1.amazonaws.com/thumbs/160/apple/285/party-popper_1f389.png"

    fun createUserAchievementData(
        includeCompleted: Boolean = true,
        includeInProgress: Boolean = true,
        includePrograms: Boolean = true,
        returnEmpty: Boolean = false
    ): UserAchievement {
        val programs = Category(
            name = "Programs",
            group = "Program",
            completed = if (includeCompleted && !returnEmpty) getCompletedPrograms() else emptyList(),
            inProgress = if (includeInProgress && !returnEmpty) getInProgressPrograms() else emptyList(),
            emptyState = getEmptyState()
        )

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

        val categories = if (includePrograms) listOf(programs, streaks, activities) else listOf(streaks, activities)

        return UserAchievement(
            achievementCategories = categories,
            sideDetails = AchievementSideDetails(title = "", descriptionOne = "", descriptionTwo = "", rewardsMessage = "", headline = ""),
            grandTotal = null
        )
    }

    fun createResponse(includeCompleted: Boolean = true,
                       includeInProgress: Boolean = true,
                       includePrograms: Boolean = true,
                       returnEmpty: Boolean = false): AchievementCompletionResponse {
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

        val programs = Category(
            name = "Programs",
            group = "Program",
            inProgress = if (includePrograms && includeInProgress && !returnEmpty) getInProgressPrograms() else emptyList(),
            completed = if (includePrograms && includeCompleted && !returnEmpty) getCompletedPrograms() else emptyList(),
            emptyState = getEmptyState()
        )

        return AchievementCompletionResponse(categories = listOf(programs, categoryStreaks, categoryActivities))
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

        val completeAchievement2 = AchievementDetail(
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
                overallProgressPercentage = 100,
                progressTitle = "",
                criteria = AchievementCriteria(
                    totalActivitiesToBeCompleted = 5,
                    activitiesCompleted = 5
                )
            ),
            celebrationSubTitle = "",
            celebrationTitle = ""
        )

        val completeAchievement3 = AchievementDetail(
            id = "hxchjvbbjfsfnadnsda",
            name = "8-week streak",
            description = "Complete at least 1 activity a week, 8 weeks in a row.",
            completions = 1,
            descriptionEarned = "You earned this badge by completing at least 1 activity a week, 8 weeks in a row.",
            achievementImage = AchievementImage(
                small = MOCK_IMAGE_URL,
                medium = MOCK_IMAGE_URL,
                large = MOCK_IMAGE_URL
            ),
            lastCompletedAt = null,
            progress = AchievementProgress(
                overallProgressPercentage = 85,
                progressTitle = "",
                criteria = AchievementCriteria(
                    totalActivitiesToBeCompleted = 5,
                    activitiesCompleted = 4
                )
            ),
            celebrationSubTitle = "",
            celebrationTitle = ""
        )

        return listOf(completeAchievement1, completeAchievement2, completeAchievement3)
    }

    private fun getInProgressStreaks(): List<AchievementDetail> {
        val inProgressAchievement1 = AchievementDetail(
            id = "mnnmxchjsbfjadnf",
            name = "10-week streak",
            description = "Complete at least 1 activity a week, 10 weeks in a row.",
            completions = 0,
            descriptionEarned = "You earned this badge by completing at least 1 activity a week, 10 weeks in a row.",
            achievementImage = AchievementImage(
                small = MOCK_IMAGE_URL,
                medium = MOCK_IMAGE_URL,
                large = MOCK_IMAGE_URL
            ),
            lastCompletedAt = null,
            progress = AchievementProgress(
                overallProgressPercentage = 75,
                progressTitle = "",
                criteria = AchievementCriteria(
                    totalActivitiesToBeCompleted = 10,
                    activitiesCompleted = 7
                )
            ),
            celebrationSubTitle = "",
            celebrationTitle = ""
        )

        val inProgressAchievement2 = AchievementDetail(
            id = "trefsdnmxchjsbfjadnf",
            name = "20-week streak",
            description = "Complete at least 1 activity a week, 20 weeks in a row.",
            completions = 0,
            descriptionEarned = "You earned this badge by completing at least 1 activity a week, 20 weeks in a row.",
            achievementImage = AchievementImage(
                small = MOCK_IMAGE_URL,
                medium = MOCK_IMAGE_URL,
                large = MOCK_IMAGE_URL
            ),
            lastCompletedAt = null,
            progress = AchievementProgress(
                overallProgressPercentage = 35,
                progressTitle = "",
                criteria = AchievementCriteria(
                    totalActivitiesToBeCompleted = 20,
                    activitiesCompleted = 7
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
                overallProgressPercentage = 33,
                progressTitle = "",
                criteria = AchievementCriteria(
                    totalActivitiesToBeCompleted = 3,
                    activitiesCompleted = 1
                )
            ),
            celebrationSubTitle = "",
            celebrationTitle = ""
        )

        val completeAchievement2 = AchievementDetail(
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
                overallProgressPercentage = 100,
                progressTitle = "",
                criteria = AchievementCriteria(
                    totalActivitiesToBeCompleted = 5,
                    activitiesCompleted = 5
                )
            ),
            celebrationSubTitle = "",
            celebrationTitle = ""
        )

        val completeAchievement3 = AchievementDetail(
            id = "8329e9123e091",
            name = "8 activities",
            description = "Complete 8 activities",
            completions = 1,
            descriptionEarned = "You earned this badge by completing 8 activities",
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
                    totalActivitiesToBeCompleted = 8,
                    activitiesCompleted = 8
                )
            ),
            celebrationSubTitle = "",
            celebrationTitle = ""
        )

        return listOf(completeAchievement1, completeAchievement2, completeAchievement3)
    }

    private fun getInProgressActivities(): List<AchievementDetail> {
        val inProgressAchievement1 = AchievementDetail(
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
                overallProgressPercentage = 10,
                progressTitle = "",
                criteria = AchievementCriteria(
                    totalActivitiesToBeCompleted = 10,
                    activitiesCompleted = 1
                )
            ),
            celebrationSubTitle = "",
            celebrationTitle = ""
        )

        val inProgressAchievement2 = AchievementDetail(
            id = "8329e9123e091",
            name = "15 activities",
            description = "Complete 15 activities",
            completions = 0,
            descriptionEarned = "You earned this badge by completing 15 activities",
            achievementImage = AchievementImage(
                small = MOCK_IMAGE_URL,
                medium = MOCK_IMAGE_URL,
                large = MOCK_IMAGE_URL
            ),
            lastCompletedAt = null,
            progress = AchievementProgress(
                overallProgressPercentage = 7,
                progressTitle = "",
                criteria = AchievementCriteria(
                    totalActivitiesToBeCompleted = 15,
                    activitiesCompleted = 1
                )
            ),
            celebrationSubTitle = "",
            celebrationTitle = ""
        )

        return listOf(inProgressAchievement1, inProgressAchievement2)
    }

    private fun getCompletedPrograms(): List<AchievementDetail> {
        val program1 = AchievementDetail(
            id = "yppasdmasdmaskd",
            name = "Happier, Healthier Family",
            description = "Stay Healthy",
            completions = 1,
            descriptionEarned = "You earned this badge by completing Happier, Healthier Family",
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
                    totalActivitiesToBeCompleted = 1,
                    activitiesCompleted = 1
                )
            ),
            celebrationSubTitle = "",
            celebrationTitle = ""
        )

        val program2 = AchievementDetail(
            id = "yppasdmasppasoasod",
            name = "Fundamentals of Healthy Movement",
            description = "Healthy Movement",
            completions = 1,
            descriptionEarned = "You earned this badge by completing Fundamentals of Healthy Movement",
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
                    totalActivitiesToBeCompleted = 1,
                    activitiesCompleted = 1
                )
            ),
            celebrationSubTitle = "",
            celebrationTitle = ""
        )

        val program3 = AchievementDetail(
            id = "yppasdmasppasoasod",
            name = "Staying Hydrated",
            description = "Drink Water",
            completions = 1,
            descriptionEarned = "You earned this badge by completing Staying Hydrated",
            achievementImage = AchievementImage(
                small = MOCK_IMAGE_URL,
                medium = MOCK_IMAGE_URL,
                large = MOCK_IMAGE_URL
            ),
            lastCompletedAt = null,
            progress = AchievementProgress(
                overallProgressPercentage = 0,
                progressTitle = "",
                criteria = AchievementCriteria(
                    totalActivitiesToBeCompleted = 1,
                    activitiesCompleted = 0
                )
            ),
            celebrationSubTitle = "",
            celebrationTitle = ""
        )

        return listOf(program1, program2, program3)
    }

    private fun getInProgressPrograms(): List<AchievementDetail> {
        val program1 = AchievementDetail(
            id = "pmdkcvmkdfkasdf",
            name = "Stretches for the Workday",
            description = "WorkDay Stretches",
            completions = 0,
            descriptionEarned = "You earned this badge by completing Stretches for the Workday",
            achievementImage = AchievementImage(
                small = MOCK_IMAGE_URL,
                medium = MOCK_IMAGE_URL,
                large = MOCK_IMAGE_URL
            ),
            lastCompletedAt = null,
            progress = AchievementProgress(
                overallProgressPercentage = 0,
                progressTitle = "",
                criteria = AchievementCriteria(
                    totalActivitiesToBeCompleted = 1,
                    activitiesCompleted = 0
                )
            ),
            celebrationSubTitle = "",
            celebrationTitle = ""
        )

        return listOf(program1)
    }

    private fun getEmptyState() = EmptyState(
        title = "",
        description = "",
        cta = Cta(url = "", title = "")
    )

}