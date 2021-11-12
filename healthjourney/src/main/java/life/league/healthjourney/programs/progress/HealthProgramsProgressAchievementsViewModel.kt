package life.league.healthjourney.programs.progress

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import life.league.core.observable.Loading
import life.league.core.observable.State
import life.league.core.observable.Uninitialized
import life.league.healthjourney.programs.models.HealthPrograms
import life.league.healthjourney.programs.models.HealthProgramsProgressInfo
import life.league.rewards.previewdata.MilestoneTrackerData


class HealthProgramsProgressAchievementsViewModel(
    private val progressInfoUseCase: HealthJourneyProgressInfoUseCase
): ViewModel() {

    var programProgressInfo by mutableStateOf<State<HealthProgramsProgressInfo>>(Uninitialized())
        private set

    fun getHealthProgramsProgressInfo() {
        viewModelScope.launch {
            programProgressInfo = Loading()
            progressInfoUseCase.fetchHealthProgramsProgressInfo().collect { result ->
                programProgressInfo = result
            }
        }
    }

}

fun getInfo() = HealthProgramsProgressInfo(
    recentAchievements = emptyList(),
    milestoneTrackerResult = MilestoneTrackerData.getMilestoneData(),
    healthPrograms = HealthPrograms()
)