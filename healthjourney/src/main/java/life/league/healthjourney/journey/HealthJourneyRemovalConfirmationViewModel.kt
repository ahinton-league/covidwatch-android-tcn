package life.league.healthjourney.journey

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import life.league.core.base.SingleLiveEvent
import life.league.core.observable.Failed
import life.league.core.observable.Loaded
import life.league.core.observable.Loading
import life.league.core.observable.State
import life.league.healthjourney.R
import life.league.healthjourney.journey.repository.HealthJourneyRepository
import life.league.healthjourney.programs.repository.HealthProgramsRepository
import life.league.networking.callback.*


class HealthJourneyRemovalConfirmationViewModel(
        private val healthJourneyItemId: String?,
        private val healthProgramId: String?,
        private val healthJourneyRepository: HealthJourneyRepository,
        private val healthProgramsRepository: HealthProgramsRepository,
        private val dispatcher: CoroutineDispatcher = Dispatchers.IO
        ) : ViewModel() {

    val healthJourneyRemovalComplete: SingleLiveEvent<State<Empty>> = SingleLiveEvent()

    fun removeActivity() {
        healthJourneyRemovalComplete.apply {
            postValue(Loading())
            viewModelScope.launch(dispatcher) {
                postValue(
                    when(val outcome = executeRemove()) {
                        is Failure -> Failed(R.string.health_journey_error)
                        is Success -> Loaded(outcome())
                    })
            }
        }
    }

    private suspend fun executeRemove(): Outcome<Empty> =
        (healthJourneyItemId?.let {
            healthJourneyRepository.removeActivity(healthJourneyItemId)
        } ?: healthProgramId?.let {
            healthProgramsRepository.removeHealthProgramFromJourney(healthProgramId)
        } ?: Failure("Failed to perform removal on Health Journey timeline: Ids are null"))

}