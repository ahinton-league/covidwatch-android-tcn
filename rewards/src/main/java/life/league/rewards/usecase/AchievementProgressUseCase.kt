package life.league.rewards.usecase

import kotlinx.coroutines.flow.map
import life.league.core.observable.Failed
import life.league.core.observable.Loaded
import life.league.networking.callback.Failure
import life.league.networking.callback.Success
import life.league.rewards.repository.AchievementsRepository

class AchievementProgressUseCase(private val repository: AchievementsRepository) {

    fun fetchAchievementsProgress() = repository.fetchAchievementsProgress().map { achievementProgress ->
        when (achievementProgress) {
            is Success -> {
                Success(response = achievementProgress.response)
            }
            is Failure -> Failure(achievementProgress.errorResponse)
        }
    }
}