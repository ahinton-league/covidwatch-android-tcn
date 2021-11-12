package life.league.rewards.usecase

import kotlinx.coroutines.flow.map
import life.league.core.observable.Failed
import life.league.core.observable.Loaded
import life.league.networking.callback.Failure
import life.league.networking.callback.Success
import life.league.rewards.model.AchievementCompletionResponse
import life.league.rewards.model.AchievementSideDetails
import life.league.rewards.model.UserAchievement
import life.league.rewards.repository.AchievementsRepository

class AchievementUseCase(private val repository: AchievementsRepository) {

    fun fetchAchievementInfoOnCompletion(
        headline: String,
        title: String,
        descriptionOne: String,
        descriptionTwo: String,
        rewardsMessage: String
    ) = repository.fetchAchievementInfoOnCompletion().map { achievementInfo ->
        when (achievementInfo) {
            is Success -> {
                Loaded(data = getUserAchievements(
                    response = achievementInfo.response,
                    headline = headline,
                    title = title,
                    descriptionOne = descriptionOne,
                    descriptionTwo = descriptionTwo,
                    rewardsMessage = rewardsMessage
                ))
            }
            is Failure -> Failed(achievementInfo.errorResponse)
        }
    }

    fun fetchAllAchievements() = repository.fetchAllAchievements().map { achievementInfo ->
        when (achievementInfo) {
            is Success -> {
                Loaded(data = getUserAchievements(
                    response = achievementInfo.response
                ))
            }
            is Failure -> Failed(achievementInfo.errorResponse)
        }
    }

    private fun getUserAchievements(
        response: AchievementCompletionResponse,
        headline: String = "",
        title: String = "",
        descriptionOne: String = "",
        descriptionTwo: String = "",
        rewardsMessage: String = ""
    ): UserAchievement {

        return UserAchievement(
            achievementCategories = response.categories,
            sideDetails = AchievementSideDetails(
                title = title,
                descriptionOne = descriptionOne,
                descriptionTwo = descriptionTwo,
                rewardsMessage = rewardsMessage,
                headline = headline
            )
        )
    }
}