package life.league.healthjourney.journey.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.coroutines.flow.Flow
import life.league.healthjourney.journey.models.*
import life.league.healthjourney.programs.models.HealthJourneyItemDetail
import life.league.healthjourney.settings.OptimumTokens
import life.league.healthjourney.settings.PointsSystem
import life.league.networking.callback.Empty
import life.league.networking.callback.Outcome
import life.league.networking.socket.MessageRequest
import java.util.*


interface HealthJourneyAPI {

    @JsonClass(generateAdapter = true)
    data class SecondsTimeRange(
        @Json(name = "start_date") val startDate: Long?,
        @Json(name = "end_date") val endDate: Long?
    )

    @JsonClass(generateAdapter = true)
    data class ActivityTimeRanges(
        val completed: SecondsTimeRange? = null,
    )

    @JsonClass(generateAdapter = true)
    data class GetUserHealthActivitiesRequest(
        val version: Int? = null,
        @Json(name = "activity_statuses") val activityStatuses: List<String>? = null,
        @Json(name = "requested_activity_count_statuses") val requestedActivityCountStatuses: List<String>? = null,
        @Json(name = "activity_statuses_date_ranges") val activityStatusesDateRanges: ActivityTimeRanges? = null,
    ): MessageRequest("get_user_health_activities")

    @JsonClass(generateAdapter = true)
    data class GetUserHealthActivitiesRequestV2(
        val version: Int = 2,
        @Json(name = "activity_statuses") val activityStatuses: List<String>? = null,
        val date: Long,
        @Json(name = "sample_response") val sampleResponse: String? = null,
    ): MessageRequest("get_user_health_activities")

    @JsonClass(generateAdapter = true)
    data class GetUserHealthActivityRequest(
        val id: String? = null,
        @Json(name = "campaign_id") val campaignId: String? = null,
        @Json(name = "activity_id") val activityId: String? = null
    ): MessageRequest("get_user_health_activity")

    @JsonClass(generateAdapter = true)
    data class GetVerifiableActivityProgressRequest(@Json(name = "activity_id") val id: String): MessageRequest("get_verifiable_activity_progress")

    @JsonClass(generateAdapter = true)
    data class SetUserHealthActivityVerificationProgress(
        @Json(name = "user_health_activity_id") val userHealthActivityId: String,
        @Json(name = "activity_verification_progress") val activityVerificationProgress: HealthJourneyItemDetail.ActivityVerificationProgress
    ): MessageRequest("set_user_health_activity_verification_progress")

    @JsonClass(generateAdapter = true)
    data class DismissUserHealthActivityRequest(
        @Json(name = "user_health_activity_id") val userHealthActivityId: String
    ): MessageRequest("dismiss_user_health_activity")

    @JsonClass(generateAdapter = true)
    data class CompleteUserHealthActivityRequest(
        @Json(name = "user_health_activity_id") val userHealthActivityId: String
    ): MessageRequest("complete_user_health_activity")

    data class Token(@Json(name = "token") val optimumTokens: OptimumTokens)

    fun getHealthJourneyTimeline(): Flow<Outcome<HealthActivities>>
    fun getHealthJourneyItemsForDay(date: Date): Flow<Outcome<HealthJourneyItemsResponse>>

    fun getHealthJourneyActivityById(id: String?, campaignId: String?, activityId: String?): Flow<Outcome<HealthJourneyItemDetailResponse>>

    fun getVerifiableActivityProgress(id: String): Flow<Outcome<VerifiableActivityProgressDetail>>

    fun getUpcomingHealthJourneyActivities(): Flow<Outcome<HealthActivities>>

    suspend fun setUserHealthActivityVerificationProgress(
        userHealthActivityId: String,
        activityVerificationProgress: HealthJourneyItemDetail.ActivityVerificationProgress
    ): Outcome<HealthJourneyItemDetail.ActivityVerificationProgressResponse>

    suspend fun removeHealthJourneyActivity(userHealthActivityId: String): Outcome<Empty>

    suspend fun completeHealthJourneyActivity(userHealthActivityId: String, pointsSystem: PointsSystem) : Outcome<HealthJourneyItemCompletionResponse>

}
