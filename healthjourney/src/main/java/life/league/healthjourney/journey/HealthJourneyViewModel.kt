package life.league.healthjourney.journey

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel


class HealthJourneyViewModel : ViewModel() {

    var jumpToToday: Boolean by mutableStateOf(false)
        private set

    fun onJumpToTodayClicked() {
        jumpToToday = true
    }

    fun jumpedToToday() {
        jumpToToday = false
    }

}
