package life.league.healthjourney.programs.progress

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import life.league.core.observable.*
import life.league.healthjourney.R
import life.league.healthjourney.programs.models.HealthPrograms
import life.league.healthjourney.programs.repository.HealthProgramsRepository
import life.league.networking.callback.Failure
import life.league.networking.callback.Success


class HealthProgramsProgressViewModel(
    private val healthProgramsRepository: HealthProgramsRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
): ViewModel() {

    var programs by mutableStateOf<State<HealthPrograms>>(Uninitialized())
        private set

    fun getHealthPrograms() {
        viewModelScope.launch(dispatcher) {
            programs = Loading()
            healthProgramsRepository.getHealthProgramsInProgress().collect { result ->
                programs = when (result) {
                    is Success -> Loaded(result.response)
                    is Failure -> Failed(R.string.loading_error)
                }
            }
        }
    }
}