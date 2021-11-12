package life.league.healthjourney.programs.repository

import kotlinx.coroutines.flow.Flow
import life.league.healthjourney.journey.models.WearableConsentResponse
import life.league.healthjourney.programs.models.HealthProgramStart
import life.league.healthjourney.programs.models.*
import life.league.networking.callback.Empty
import life.league.healthjourney.programs.models.HealthPrograms
import life.league.healthjourney.programs.models.HealthProgramsCarousels
import life.league.healthjourney.programs.models.HealthProgramsCategories
import life.league.networking.callback.Outcome

interface HealthProgramsRepository {

    fun getHealthProgramDetails(id: String): Flow<Outcome<HealthProgramDetails>>

    suspend fun addHealthProgramToJourney(id: String, customFields: CustomFields?): Outcome<HealthProgramStart>
    suspend fun removeHealthProgramFromJourney(id: String): Outcome<Empty>

    suspend fun getWearableConsentForDataPoints(dataPoints: List<String>): Outcome<WearableConsentResponse>

    fun getAllHealthPrograms(): Flow<Outcome<HealthPrograms>>
    fun getHealthProgramCategoryById(id: String): Flow<Outcome<HealthPrograms>>
    fun getHealthProgramsInProgress(): Flow<Outcome<HealthPrograms>>
    fun getCuratedHealthProgramsCarouselsForLibrary(): Flow<Outcome<HealthProgramsCarousels>>
    fun getCuratedHealthProgramsCarouselsForHomeFeed(): Flow<Outcome<HealthProgramsCarousels>>
    fun getCuratedHealthProgramsCarouselsForFeatured(): Flow<Outcome<HealthProgramsCarousels>>
    fun getSuggestedCarousels(): Flow<Outcome<HealthProgramsCarousels>>
    fun getHealthProgramsCategories(): Flow<Outcome<HealthProgramsCategories>>

}