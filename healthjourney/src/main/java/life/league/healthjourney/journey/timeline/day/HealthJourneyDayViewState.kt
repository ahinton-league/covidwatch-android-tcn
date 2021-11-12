package life.league.healthjourney.journey.timeline.day

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import life.league.core.observable.State
import life.league.core.observable.Uninitialized

data class HealthJourneyDayViewState(
    val dayData: StateFlow<State<HealthJourneyDayViewData>> = MutableStateFlow(Uninitialized()),
    val config: HealthJourneyDayViewConfiguration = HealthJourneyDayViewConfiguration()
)