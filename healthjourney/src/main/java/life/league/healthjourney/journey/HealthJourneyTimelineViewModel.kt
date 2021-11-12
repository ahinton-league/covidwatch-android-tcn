package life.league.healthjourney.journey

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import life.league.core.observable.Failed
import life.league.core.observable.Loaded
import life.league.core.observable.Loading
import life.league.core.observable.State
import life.league.healthjourney.R
import life.league.healthjourney.journey.repository.HealthJourneyRepository
import life.league.healthjourney.journey.models.HealthActivitiesCategory
import life.league.networking.callback.Failure
import life.league.networking.callback.Success


class HealthJourneyTimelineViewModel(
    private val healthJourneyRepository: HealthJourneyRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    data class HealthJourneyTimelineViewState(
        val previewAvailable: Boolean,
        val activityCategories: List<HealthActivitiesCategory>
    )

    private val mutableState: MutableLiveData<State<HealthJourneyTimelineViewState>> = MutableLiveData()
    val state: LiveData<State<HealthJourneyTimelineViewState>> get() = mutableState

    fun getActivities() {

        // If this is a refresh don't change state to loading to keep displaying old data
        if (mutableState.value !is Loaded) {
            mutableState.value = Loading()
        }

        viewModelScope.launch(dispatcher) {
            healthJourneyRepository.getHealthJourneyTimeline().collect { outcome ->
                mutableState.postValue(
                    when (outcome) {
                        is Success -> Loaded(
                            HealthJourneyTimelineViewState(
                                previewAvailable = (outcome().activityStatusCounts?.upcoming ?: 0) > 0,
                                activityCategories = outcome().toCategories()
                            )
                        )
                        is Failure -> Failed(R.string.health_journey_error)
                    }
                )
            }
        }

    }
}