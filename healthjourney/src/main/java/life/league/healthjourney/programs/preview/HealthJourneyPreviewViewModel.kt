package life.league.healthjourney.programs.preview

import androidx.lifecycle.*
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
import life.league.healthjourney.journey.models.HealthJourneyItem
import life.league.networking.callback.Failure
import life.league.networking.callback.Success

class HealthJourneyPreviewViewModel(
    private val healthJourneyRepository: HealthJourneyRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
): ViewModel() {

    private val mutablePreviewItems: MutableLiveData<State<List<HealthJourneyItem>>> = MutableLiveData()
    val previewItems: LiveData<State<List<HealthJourneyItem>>> get() = mutablePreviewItems

    fun getPreview() {
        viewModelScope.launch(dispatcher) {
            mutablePreviewItems.postValue(Loading())
            healthJourneyRepository.getHealthJourneyPreviewItems().collect { result ->
                mutablePreviewItems.postValue(
                    when (result) {
                        is Success -> Loaded(result.response.activities)
                        is Failure -> Failed(R.string.loading_error)
                    }
                )
            }
        }
    }

}
