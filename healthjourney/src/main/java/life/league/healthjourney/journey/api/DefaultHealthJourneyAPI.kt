package life.league.healthjourney.journey.api

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import life.league.core.extension.startOfDay
import life.league.healthjourney.journey.models.*
import life.league.healthjourney.programs.models.HealthJourneyItemDetail
import life.league.healthjourney.settings.PointsSystem
import life.league.healthjourney.utils.addToDayOfYear
import life.league.networking.callback.Empty
import life.league.networking.callback.Failure
import life.league.networking.callback.Outcome
import life.league.networking.callback.Success
import life.league.networking.socket.API
import life.league.networking.socket.actions.sendAndReceiveCachedAndSocketData
import life.league.networking.socket.actions.sendAndReceiveData
import java.util.*


class DefaultHealthJourneyAPI(private val api: API): HealthJourneyAPI {

    /**
     * Documentation for Health Journey API:
     * https://everlong.atlassian.net/wiki/spaces/MH/pages/1768260127/API+Contracts+for+Health+Journey
     */

    companion object {
        private const val TAG = "LeagueAPI_HealthJourney"
    }
    
    override fun getHealthJourneyTimeline(): Flow<Outcome<HealthActivities>>  =
        api.sendAndReceiveCachedAndSocketData(messageRequest =
            HealthJourneyAPI.GetUserHealthActivitiesRequest(
                version = 1,
                activityStatuses = listOf("active", "completed"),
                requestedActivityCountStatuses = listOf("upcoming"),
                activityStatusesDateRanges =
                HealthJourneyAPI.ActivityTimeRanges(completed = HealthJourneyAPI.SecondsTimeRange(startDate = Date().time / 1000, endDate = null))
            )
        )

    private val hjItemsForDaySampleResponses = listOf(
        "sample_get_user_health_activities_today_with_active_activities",
        "sample_get_user_health_activities_today_with_no_active_activities",
        "sample_get_user_health_activities_today_no_activities_enrolled_programs_less_than_max_programs",
        "sample_get_user_health_activities_today_no_activities_enrolled_programs_equal_max_programs",
        "sample_get_user_health_activities_tomorrow_with_activities",
        "sample_get_user_health_activities_yesterday_with_activities",
        "sample_get_user_health_activities_no_activities",
    )

    // Todo remove sample responses
    override fun getHealthJourneyItemsForDay(date: Date): Flow<Outcome<HealthJourneyItemsResponse>> =
        api.sendAndReceiveCachedAndSocketData(
            messageRequest = HealthJourneyAPI.GetUserHealthActivitiesRequestV2(
                date = date.time,
                activityStatuses = listOf(
                    "active",
                    "completed",
                    "expired",
                    "removed"
                ),
                sampleResponse = hjItemsForDaySampleResponses.random()
            )
        )

    override fun getUpcomingHealthJourneyActivities(): Flow<Outcome<HealthActivities>> =
        api.sendAndReceiveCachedAndSocketData(
            HealthJourneyAPI.GetUserHealthActivitiesRequest(
                version = 1,
                activityStatuses = listOf("upcoming")
            )
        )

    override suspend fun setUserHealthActivityVerificationProgress(
        userHealthActivityId: String,
        activityVerificationProgress: HealthJourneyItemDetail.ActivityVerificationProgress
    ): Outcome<HealthJourneyItemDetail.ActivityVerificationProgressResponse> =
        api.sendAndReceiveData(
            HealthJourneyAPI.SetUserHealthActivityVerificationProgress(
                userHealthActivityId,
                activityVerificationProgress
            )
        )

    override fun getHealthJourneyActivityById(
        id: String?,
        campaignId: String?,
        activityId: String?
    ): Flow<Outcome<HealthJourneyItemDetailResponse>> =
        api.sendAndReceiveCachedAndSocketData(
            HealthJourneyAPI.GetUserHealthActivityRequest(
                id = id,
                campaignId = campaignId,
                activityId = activityId
            )
        )

    override fun getVerifiableActivityProgress(id: String): Flow<Outcome<VerifiableActivityProgressDetail>> =
        api.sendAndReceiveCachedAndSocketData<VerifiableActivityProgressDetail>(
            HealthJourneyAPI.GetVerifiableActivityProgressRequest(id = id)
        )
            .map { verifiableActivityProgressResponse ->
                when (verifiableActivityProgressResponse) {
                    is Success -> Success(verifiableActivityProgressResponse.response)
                    is Failure -> verifiableActivityProgressResponse
                }
            }

    override suspend fun removeHealthJourneyActivity(userHealthActivityId: String): Outcome<Empty> =
        api.sendAndReceiveData(
            HealthJourneyAPI.DismissUserHealthActivityRequest(
                userHealthActivityId = userHealthActivityId
            )
        )

    override suspend fun completeHealthJourneyActivity(userHealthActivityId: String, pointsSystem: PointsSystem): Outcome<HealthJourneyItemCompletionResponse> =
        api.sendAndReceiveData(
            HealthJourneyAPI.CompleteUserHealthActivityRequest(
                userHealthActivityId = userHealthActivityId
            )
        )

}
