package life.league.rewards.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import life.league.networking.callback.Outcome
import life.league.networking.socket.API
import life.league.networking.socket.actions.sendAndReceiveFlowData
import life.league.rewards.api.AchievementsApi
import life.league.rewards.model.AchievementCompletionResponse
import life.league.rewards.model.MilestoneTrackerResult
import life.league.rewards.model.RecentAchievementsResponse

/**
 * This Repository will hold all rewards related API calls until rewards module gets bigger and requires more segregation
 * The Repository will make the network calls in IO thread by default since dispatcher is defaulted to IO
 */
class AchievementsRepository(private val api: API, private val dispatcher: CoroutineDispatcher = Dispatchers.IO) {

    companion object {
        private const val TAG = "AchievementsRepository"
    }

    fun fetchAchievementInfoOnCompletion(): Flow<Outcome<AchievementCompletionResponse>> {
        return api.sendAndReceiveFlowData<AchievementCompletionResponse>(
            messageRequest = AchievementsApi.FetchAchievementInfoOnCompletion(),
            tag = TAG
        ).flowOn(dispatcher)
    }

    fun fetchAllAchievements(): Flow<Outcome<AchievementCompletionResponse>> {
        return api.sendAndReceiveFlowData<AchievementCompletionResponse>(
            messageRequest = AchievementsApi.FetchAllAchievements(),
            tag = TAG
        ).flowOn(dispatcher)
    }

    fun fetchRecentlyEarnedAchievements(): Flow<Outcome<RecentAchievementsResponse>> {
        return api.sendAndReceiveFlowData<RecentAchievementsResponse>(
            messageRequest = AchievementsApi.FetchRecentlyEarnedAchievements(),
            tag = TAG
        ).flowOn(dispatcher)
    }

    fun fetchAchievementsProgress(): Flow<Outcome<MilestoneTrackerResult>> {
        return api.sendAndReceiveFlowData<MilestoneTrackerResult>(
            messageRequest = AchievementsApi.FetchAchievementsProgress(),
            tag = TAG
        ).flowOn(dispatcher)
    }

}