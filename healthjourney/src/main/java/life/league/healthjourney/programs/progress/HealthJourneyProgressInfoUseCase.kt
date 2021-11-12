package life.league.healthjourney.programs.progress

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import life.league.core.observable.Failed
import life.league.core.observable.Loaded
import life.league.core.observable.State
import life.league.healthjourney.programs.models.HealthPrograms
import life.league.healthjourney.programs.models.HealthProgramsProgressInfo
import life.league.healthjourney.programs.repository.HealthProgramsRepository
import life.league.networking.callback.Success
import life.league.rewards.model.AchievementDetail
import life.league.rewards.model.MilestoneTrackerResult
import life.league.rewards.usecase.AchievementProgressUseCase
import life.league.rewards.usecase.RecentAchievementUseCase

class HealthJourneyProgressInfoUseCase(
    private val healthProgramsRepository: HealthProgramsRepository,
    private val achievementProgressUseCase: AchievementProgressUseCase,
    private val recentAchievementUseCase: RecentAchievementUseCase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    companion object {
        private const val ERROR_MESSAGE = "Oops, there seems to have been a problem."
    }

    fun fetchHealthProgramsProgressInfo() = combine(
        healthProgramsRepository.getHealthProgramsInProgress(),
        recentAchievementUseCase.fetchRecentlyEarnedAchievements(),
        achievementProgressUseCase.fetchAchievementsProgress()
    ) { healthProgramsResponse, recentAchievementsResponse, milestoneResponse ->

        var healthPrograms: HealthPrograms? = null
        var recentAchievements: List<AchievementDetail>? = null
        var milestoneTrackerResult: MilestoneTrackerResult? = null

        when(healthProgramsResponse) {
            is Success -> healthPrograms = healthProgramsResponse.response
            else -> Failed<Flow<State<HealthProgramsProgressInfo>>>(ERROR_MESSAGE)
        }

        when(recentAchievementsResponse) {
            is Success -> recentAchievements = recentAchievementsResponse.response.recentAchievements
            else -> Failed<Flow<State<HealthProgramsProgressInfo>>>(ERROR_MESSAGE)
        }

        when(milestoneResponse) {
            is Success -> milestoneTrackerResult = milestoneResponse.response
            else -> Failed<Flow<State<HealthProgramsProgressInfo>>>(ERROR_MESSAGE)
        }

        if (healthPrograms != null && milestoneTrackerResult != null) {
            Loaded(
                HealthProgramsProgressInfo(
                healthPrograms = healthPrograms,
                milestoneTrackerResult = milestoneTrackerResult,
                recentAchievements = recentAchievements.orEmpty()
            )
            )
        } else {
            Failed(ERROR_MESSAGE)
        }

    }.flowOn(dispatcher)
}