package life.league.rewards.usecase

import kotlinx.coroutines.flow.map
import life.league.networking.callback.Failure
import life.league.networking.callback.Success
import life.league.rewards.repository.AchievementsRepository

class RecentAchievementUseCase(private val repository: AchievementsRepository) {

    fun fetchRecentlyEarnedAchievements() = repository.fetchRecentlyEarnedAchievements().map { achievementList ->
        when (achievementList) {
            is Success -> Success(response = achievementList.response)
            is Failure -> Failure(achievementList.errorResponse)
        }
    }
}