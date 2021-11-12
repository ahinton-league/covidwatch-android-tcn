package life.league.healthjourney.programs.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import life.league.core.base.SingleLiveEvent
import life.league.core.observable.Failed
import life.league.core.observable.Loaded
import life.league.core.observable.Loading
import life.league.core.observable.State
import life.league.healthjourney.R
import life.league.healthjourney.journey.models.CampaignMode
import life.league.healthjourney.programs.models.CustomFields
import life.league.healthjourney.programs.models.HealthProgramStart
import life.league.healthjourney.programs.models.HealthProgramDetails
import life.league.healthjourney.programs.repository.HealthProgramsRepository
import life.league.networking.callback.Failure
import life.league.networking.callback.Success

class HealthProgramDetailsViewModel(
    private val programId: String,
    private val healthProgramsRepository: HealthProgramsRepository
) : ViewModel() {

    private val mutableHealthProgram: MutableLiveData<State<HealthProgramDetails>> =
        MutableLiveData()
    val healthProgram: LiveData<State<HealthProgramDetails>> get() = mutableHealthProgram

    val programAdded: SingleLiveEvent<State<HealthProgramStart>> = SingleLiveEvent()

    val isWearablesConsentGiven: SingleLiveEvent<State<Boolean>> = SingleLiveEvent()

    init {
        getHealthProgram()
    }

    fun getHealthProgram() {
        viewModelScope.launch(Dispatchers.IO) {
            mutableHealthProgram.postValue(Loading())
            healthProgramsRepository.getHealthProgramDetails(programId).collect { outcome ->
                mutableHealthProgram.postValue(
                    when (outcome) {
                        is Success -> Loaded(outcome.response)
                        is Failure -> Failed(R.string.loading_error)
                    }
                )
            }
        }
    }

    fun addProgramToJourney(campaignMode: CampaignMode? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            programAdded.postValue(Loading())
            programAdded.postValue(
                when (val outcome = healthProgramsRepository.addHealthProgramToJourney(programId, CustomFields(campaignMode?.string))) {
                    is Failure -> Failed(R.string.error_start_program)
                    is Success -> Loaded(outcome.response)
                }
            )
        }
    }

    fun getWearableConsentForDataPoints(dataPoints: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            isWearablesConsentGiven.postValue(Loading())
            var isConsentRequested = false
            when (val outcome = healthProgramsRepository.getWearableConsentForDataPoints(dataPoints)) {
                is Failure -> isWearablesConsentGiven.postValue(Failed(R.string.error_start_program))
                is Success -> {
                    outcome.response.response.filter { dataPoints.contains(it.key) }.forEach {
                        if(it.value.any { it.consentRequested }) {
                            isConsentRequested = true
                        }
                    }
                    isWearablesConsentGiven.postValue(Loaded(isConsentRequested))
                }
            }
        }
    }

}
