package life.league.healthjourney.programs.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import life.league.healthjourney.journey.models.WearableConsentResponse
import life.league.healthjourney.programs.api.HealthProgramsAPI
import life.league.healthjourney.programs.models.*
import life.league.networking.callback.Empty
import life.league.networking.callback.Failure
import life.league.networking.callback.Outcome
import life.league.networking.callback.Success

class DefaultHealthProgramsRepository(private val healthProgramsAPI: HealthProgramsAPI):
    HealthProgramsRepository {
    override fun getHealthProgramDetails(id: String): Flow<Outcome<HealthProgramDetails>> =
        healthProgramsAPI.getHealthProgramDetails(id).map { healthJourneyItemDetailResponse ->
            when (healthJourneyItemDetailResponse) {
                is Success -> Success(healthJourneyItemDetailResponse.response.healthProgram)
                is Failure -> healthJourneyItemDetailResponse
            }
        }

    override suspend fun addHealthProgramToJourney(id: String, customFields: CustomFields?): Outcome<HealthProgramStart> =
        healthProgramsAPI.startHealthGoalProgram(id, customFields)

    override suspend fun removeHealthProgramFromJourney(id: String): Outcome<Empty> =
        healthProgramsAPI.leaveHealthGoalProgram(id)

    override suspend fun getWearableConsentForDataPoints(dataPoints: List<String>): Outcome<WearableConsentResponse> =
        healthProgramsAPI.getWearableConsentForDataPoints(dataPoints)

    override fun getAllHealthPrograms(): Flow<Outcome<HealthPrograms>> =
        healthProgramsAPI.getAllHealthPrograms()

    override fun getHealthProgramsInProgress(): Flow<Outcome<HealthPrograms>> =
        healthProgramsAPI.getHealthProgramsInProgress()

    override fun getCuratedHealthProgramsCarouselsForLibrary(): Flow<Outcome<HealthProgramsCarousels>> =
        healthProgramsAPI.getCuratedCarousels(HealthProgramsAPI.CuratedCarouselType.ProgramLibrary)

    override fun getCuratedHealthProgramsCarouselsForHomeFeed(): Flow<Outcome<HealthProgramsCarousels>> =
        healthProgramsAPI.getCuratedCarousels(HealthProgramsAPI.CuratedCarouselType.HomeFeed)

    override fun getCuratedHealthProgramsCarouselsForFeatured(): Flow<Outcome<HealthProgramsCarousels>> =
        healthProgramsAPI.getCuratedCarousels(HealthProgramsAPI.CuratedCarouselType.Featured)

    override fun getSuggestedCarousels(): Flow<Outcome<HealthProgramsCarousels>> =
        healthProgramsAPI.getHealthProgramSuggestedCarousels()

    override fun getHealthProgramsCategories(): Flow<Outcome<HealthProgramsCategories>> =
        healthProgramsAPI.getHealthProgramsCategories()

    override fun getHealthProgramCategoryById(id: String): Flow<Outcome<HealthPrograms>> =
        healthProgramsAPI.getHealthProgramsByCategoryId(id)

}
