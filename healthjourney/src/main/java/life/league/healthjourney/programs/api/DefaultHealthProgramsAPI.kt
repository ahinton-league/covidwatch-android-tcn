package life.league.healthjourney.programs.api

import kotlinx.coroutines.flow.Flow
import life.league.healthjourney.journey.models.WearableConsentResponse
import life.league.healthjourney.programs.models.HealthProgramStart
import life.league.healthjourney.programs.api.HealthProgramsAPI.*
import life.league.healthjourney.programs.models.*
import life.league.networking.callback.Empty
import life.league.networking.callback.Outcome
import life.league.networking.socket.API
import life.league.networking.socket.actions.sendAndReceiveCachedAndSocketData
import life.league.networking.socket.actions.sendAndReceiveData


class DefaultHealthProgramsAPI(private val api: API) : HealthProgramsAPI {
    companion object {
        const val TAG = "HealthPrograms_API"
    }

    override fun getAllHealthPrograms(): Flow<Outcome<HealthPrograms>> =
        api.sendAndReceiveCachedAndSocketData(
            GetHealthGoalProgramsRequest(),
            tag = TAG
        )

    override fun getHealthProgramDetails(id: String): Flow<Outcome<HealthProgramDetailsResponse>> =
        api.sendAndReceiveCachedAndSocketData(
            GetHealthGoalProgramRequest(id = id),
            tag = TAG
        )

    override suspend fun startHealthGoalProgram(id: String, customFields: CustomFields?): Outcome<HealthProgramStart> =
        api.sendAndReceiveData(
            StartHealthGoalProgramRequest(programId = id, customFields = customFields),
            tag = TAG
        )

    override suspend fun leaveHealthGoalProgram(id: String): Outcome<Empty> =
        api.sendAndReceiveData(
            QuitHealthGoalProgramRequest(userProgramId = id),
            tag = TAG
        )

    override fun getHealthProgramsByCategoryId(id: String): Flow<Outcome<HealthPrograms>> =
        api.sendAndReceiveCachedAndSocketData(
            GetHealthGoalProgramsRequest(categoryId = id),
            tag = TAG
        )

    override fun getCuratedCarousels(type: CuratedCarouselType): Flow<Outcome<HealthProgramsCarousels>> =
        api.sendAndReceiveCachedAndSocketData(
            GetHealthProgramCuratedCarouselsRequest(type = type.string),
            tag = TAG
        )

    override fun getHealthProgramSuggestedCarousels(): Flow<Outcome<HealthProgramsCarousels>> =
        api.sendAndReceiveCachedAndSocketData(
            GetHealthProgramSuggestedCarouselsRequest(),
            tag = TAG
        )

    override fun getHealthProgramsInProgress(): Flow<Outcome<HealthPrograms>> =
        api.sendAndReceiveCachedAndSocketData(
            GetUserHealthGoalProgramsRequest(),
            tag = TAG
        )

    override fun getHealthProgramsCategories(): Flow<Outcome<HealthProgramsCategories>> =
        api.sendAndReceiveCachedAndSocketData(
            GetHealthProgramsCategoriesRequest(),
            tag = TAG
        )

    override suspend fun getWearableConsentForDataPoints(dataPoints: List<String>): Outcome<WearableConsentResponse> =
        api.sendAndReceiveData(
            GetWearablesConsentForDataPoints(dataPoints),
            tag = TAG
        )
}
