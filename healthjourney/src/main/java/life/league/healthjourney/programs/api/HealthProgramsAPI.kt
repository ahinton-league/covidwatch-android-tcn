package life.league.healthjourney.programs.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.coroutines.flow.Flow
import life.league.healthjourney.journey.models.WearableConsentResponse
import life.league.healthjourney.programs.models.*
import life.league.networking.callback.Empty
import life.league.networking.callback.Outcome
import life.league.networking.socket.MessageRequest

interface HealthProgramsAPI {

    @JsonClass(generateAdapter = true)
    data class GetHealthGoalProgramRequest(
        @Json(name = "program_id") val id: String,
        val version: Int = 1
    ) :
        MessageRequest("get_health_goal_program")

    @JsonClass(generateAdapter = true)
    data class GetHealthGoalProgramsRequest(
        @Json(name = "category_id") val categoryId: String? = null,
        val version: Int = 1
    ) :
        MessageRequest("get_health_goal_programs")

    @JsonClass(generateAdapter = true)
    data class GetUserHealthGoalProgramsRequest(val version: Int = 1) :
        MessageRequest("get_user_health_goal_programs")

    // Moshi doesn't support adapters for classes with no properties
    @JsonClass(generateAdapter = false)
    class GetHealthProgramsCategoriesRequest :
        MessageRequest("get_health_program_categories")

    @JsonClass(generateAdapter = true)
    class GetHealthProgramCuratedCarouselsRequest(val type: String) :
        MessageRequest("get_health_program_curated_carousels")

    // Moshi doesn't support adapters for classes with no properties
    @JsonClass(generateAdapter = false)
    class GetHealthProgramSuggestedCarouselsRequest :
        MessageRequest("get_health_program_suggested_carousels")

    @JsonClass(generateAdapter = true)
    data class StartHealthGoalProgramRequest(
        @Json(name = "program_id") val programId: String,
        @Json(name = "campaign_custom_fields") val customFields: CustomFields?,
        val version: Int = 2
    ) : MessageRequest("start_health_goal_program")

    @JsonClass(generateAdapter = true)
    data class QuitHealthGoalProgramRequest(
        @Json(name = "user_program_id") val userProgramId: String,
        val version: Int = 1
    ) : MessageRequest("quit_health_goal_program")

    @JsonClass(generateAdapter = true)
    data class GetWearablesConsentForDataPoints(
        @Json(name = "data_points") val dataPoints: List<String>,
    ) : MessageRequest("get_wearables_consent_for_data_points")


    fun getAllHealthPrograms(): Flow<Outcome<HealthPrograms>>
    fun getHealthProgramDetails(id: String): Flow<Outcome<HealthProgramDetailsResponse>>

    suspend fun startHealthGoalProgram(id: String, customFields: CustomFields?): Outcome<HealthProgramStart>
    suspend fun leaveHealthGoalProgram(id: String): Outcome<Empty>

    suspend fun getWearableConsentForDataPoints(dataPoints: List<String>): Outcome<WearableConsentResponse>

    fun getHealthProgramsByCategoryId(id: String): Flow<Outcome<HealthPrograms>>
    fun getHealthProgramsCategories(): Flow<Outcome<HealthProgramsCategories>>
    fun getCuratedCarousels(type: CuratedCarouselType): Flow<Outcome<HealthProgramsCarousels>>
    fun getHealthProgramSuggestedCarousels(): Flow<Outcome<HealthProgramsCarousels>>
    fun getHealthProgramsInProgress(): Flow<Outcome<HealthPrograms>>
    enum class CuratedCarouselType(val string: String) {
        ProgramLibrary("programLibrary"),
        HomeFeed("homeFeed"),
        Featured("featured"),
    }

}
