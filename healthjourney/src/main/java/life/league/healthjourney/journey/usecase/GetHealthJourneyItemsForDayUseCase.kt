package life.league.healthjourney.journey.usecase

import kotlinx.coroutines.flow.Flow
import life.league.healthjourney.journey.models.HealthJourneyItemsResponse
import life.league.healthjourney.journey.repository.HealthJourneyRepository
import life.league.networking.callback.Outcome
import java.util.*

class GetHealthJourneyItemsForDayUseCase(
    private val healthJourneyRepository: HealthJourneyRepository,
) {

    operator fun invoke(date: Date): Flow<Outcome<HealthJourneyItemsResponse>> =
        healthJourneyRepository.getHealthJourneyItemsForDay(date)

}