package life.league.healthjourney.programs.library

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import life.league.core.extension.collectAsync
import life.league.core.observable.Failed
import life.league.core.observable.Loaded
import life.league.core.observable.Loading
import life.league.core.observable.State
import life.league.healthjourney.R
import life.league.healthjourney.programs.models.HealthPrograms
import life.league.healthjourney.programs.models.HealthProgramsCarousel
import life.league.healthjourney.programs.repository.HealthProgramsRepository
import life.league.networking.callback.Failure
import life.league.networking.callback.Outcome
import life.league.networking.callback.Success

class HealthProgramCategoryViewModel(
    private val healthProgramsRepository: HealthProgramsRepository
) : ViewModel() {

    private val mutableHealthPrograms: MutableLiveData<State<HealthPrograms>> = MutableLiveData(Loading())
    val healthPrograms: LiveData<State<HealthPrograms>> get() = mutableHealthPrograms

    fun initializeFromCarousel(healthProgramsCarousel: HealthProgramsCarousel) {
        mutableHealthPrograms.postValue(Loaded(healthProgramsCarousel.toHealthPrograms()))
    }

    fun getCategory(id: String) {
        val currentState = mutableHealthPrograms.value
        // If already loaded from carousel then don't show loading state but fetch the category
        if (currentState !is Loaded) mutableHealthPrograms.postValue(Loading())

        healthProgramsRepository.getHealthProgramCategoryById(id).collectAsync(viewModelScope) { outcome ->
            mutableHealthPrograms.postValue(
                when (outcome) {
                    is Success -> Loaded(outcome.response)
                    is Failure -> if (currentState !is Loaded) Failed(R.string.loading_error) else currentState
                }
            )
        }
    }

}