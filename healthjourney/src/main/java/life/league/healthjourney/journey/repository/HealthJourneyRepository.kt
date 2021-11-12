package life.league.healthjourney.journey.repository

import kotlinx.coroutines.flow.Flow
import life.league.healthjourney.journey.models.*
import life.league.healthjourney.programs.models.HealthJourneyItemDetail
import life.league.networking.callback.Empty
import life.league.networking.callback.Outcome
import java.util.*

interface HealthJourneyRepository {

    fun getHealthJourneyTimeline(): Flow<Outcome<HealthActivities>>
    fun getHealthJourneyPreviewItems(): Flow<Outcome<HealthActivities>>
    fun getHealthJourneyItem(itemId: String?, campaignId: String?, activityId: String?): Flow<Outcome<HealthJourneyItemDetailResponse>>
    fun getVerifiableActivityProgress(activityId: String): Flow<Outcome<VerifiableActivityProgressDetail>>
    fun getHealthJourneyItemsForDay(date: Date): Flow<Outcome<HealthJourneyItemsResponse>>

    suspend fun setUserHealthActivityVerificationProgress(
        userHealthActivityId: String,
        activityVerificationProgress: HealthJourneyItemDetail.ActivityVerificationProgress): Outcome<HealthJourneyItemDetail.ActivityVerificationProgressResponse>

    suspend fun completeActivity(userHealthActivityId: String): Outcome<HealthJourneyItemCompletionResponse>
    suspend fun removeActivity(userHealthActivityId: String): Outcome<Empty>

}
