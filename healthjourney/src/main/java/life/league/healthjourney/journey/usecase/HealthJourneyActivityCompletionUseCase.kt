package life.league.healthjourney.journey.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import life.league.core.observable.Failed
import life.league.healthjourney.journey.models.HealthJourneyItemCompletionScreen
import life.league.healthjourney.journey.repository.HealthJourneyRepository
import life.league.networking.callback.Failure
import life.league.networking.callback.Success
import life.league.rewards.usecase.AchievementUseCase

class HealthJourneyActivityCompletionUseCase(
    private val healthJourneyRepository: HealthJourneyRepository,
    private val achievementUseCase: AchievementUseCase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun completeActivity(activityId: String) = when(val result = healthJourneyRepository.completeActivity(activityId)) {
        is Success -> completeAchievement(result.response.completionScreen)
        is Failure -> flowOf(Failed(result.errorResponse))
    }.flowOn(dispatcher)

    private fun completeAchievement(completionData: HealthJourneyItemCompletionScreen) = achievementUseCase.fetchAchievementInfoOnCompletion(
        title = completionData.title,
        descriptionOne = completionData.descriptionOne,
        descriptionTwo = completionData.descriptionTwo,
        rewardsMessage = completionData.rewardsMessage,
        headline = completionData.eyebrowHeadline
    )

}