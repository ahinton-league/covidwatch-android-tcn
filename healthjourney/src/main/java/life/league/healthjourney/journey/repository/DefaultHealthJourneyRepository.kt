package life.league.healthjourney.journey.repository

import kotlinx.coroutines.flow.Flow
import life.league.healthjourney.journey.api.HealthJourneyAPI
import life.league.healthjourney.journey.models.*
import life.league.healthjourney.programs.models.HealthJourneyItemDetail
import life.league.healthjourney.settings.HealthJourneySettings
import life.league.networking.callback.Outcome
import java.util.*

class DefaultHealthJourneyRepository(private val healthJourneyAPI: HealthJourneyAPI) : HealthJourneyRepository {

    override fun getHealthJourneyTimeline(): Flow<Outcome<HealthActivities>> =
        healthJourneyAPI.getHealthJourneyTimeline()

    override fun getHealthJourneyPreviewItems(): Flow<Outcome<HealthActivities>> =
        healthJourneyAPI.getUpcomingHealthJourneyActivities()

    override fun getHealthJourneyItem(itemId: String?, campaignId: String?, activityId: String?): Flow<Outcome<HealthJourneyItemDetailResponse>> =
        healthJourneyAPI.getHealthJourneyActivityById(itemId, campaignId, activityId)

    override fun getVerifiableActivityProgress(activityId: String): Flow<Outcome<VerifiableActivityProgressDetail>> =
        healthJourneyAPI.getVerifiableActivityProgress(activityId)

    override fun getHealthJourneyItemsForDay(date: Date): Flow<Outcome<HealthJourneyItemsResponse>> =
        healthJourneyAPI.getHealthJourneyItemsForDay(date)

    override suspend fun setUserHealthActivityVerificationProgress(
        userHealthActivityId: String,
        activityVerificationProgress: HealthJourneyItemDetail.ActivityVerificationProgress): Outcome<HealthJourneyItemDetail.ActivityVerificationProgressResponse> =
        healthJourneyAPI.setUserHealthActivityVerificationProgress(
            userHealthActivityId,
            activityVerificationProgress
        )

    override suspend fun completeActivity(userHealthActivityId: String): Outcome<HealthJourneyItemCompletionResponse> =
        healthJourneyAPI.completeHealthJourneyActivity(
            userHealthActivityId,
            HealthJourneySettings.pointsSystem
        )

    override suspend fun removeActivity(userHealthActivityId: String) =
        healthJourneyAPI.removeHealthJourneyActivity(userHealthActivityId)

}