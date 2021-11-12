package life.league.healthjourney.journey.timeline

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import life.league.core.extension.startOfDay
import life.league.core.extension.withEach
import life.league.core.observable.Failed
import life.league.core.observable.Loaded
import life.league.core.observable.Loading
import life.league.core.observable.State
import life.league.healthjourney.journey.models.HealthJourneyItemSections
import life.league.healthjourney.journey.models.HealthJourneyItemsResponse
import life.league.healthjourney.journey.timeline.day.HealthJourneyDayViewConfiguration
import life.league.healthjourney.journey.timeline.day.HealthJourneyDayViewData
import life.league.healthjourney.journey.timeline.day.HealthJourneyDayViewState
import life.league.healthjourney.journey.usecase.GetHealthJourneyItemsForDayUseCase
import life.league.healthjourney.utils.addToDayOfYear
import life.league.networking.callback.Failure
import life.league.networking.callback.Outcome
import life.league.networking.callback.Success
import java.util.*


class HealthJourneyDayPagerViewModel(
    private val getHealthJourneyItemsForDay: GetHealthJourneyItemsForDayUseCase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) : ViewModel() {

    private val yesterday: Date = Date().startOfDay().addToDayOfYear(-1)
    private val today: Date = Date().startOfDay()
    private val tomorrow: Date = Date().startOfDay().addToDayOfYear(1)

    var todayIndex: Int = 1

    var dates: List<Date> by mutableStateOf(listOf(yesterday, today, tomorrow))
        private set

    var overline: Overline? by mutableStateOf(Overline.TODAY)
        private set

    var currentPage: Int by mutableStateOf(1)
        private set

    private val _dayState:
            MutableMap<Date, StateFlow<HealthJourneyDayViewState>> = mutableMapOf()

    private val _dayData:
            MutableMap<Date, StateFlow<State<HealthJourneyDayViewData>>> = mutableMapOf()

    private val _scrollStates:
            MutableMap<Date, MutableStateFlow<HealthJourneyDayViewConfiguration>> = mutableMapOf()

    init {
        dates.alsoFetchData()
    }

    fun onPageChange(page: Int) {
        currentPage = page
        overline = dates[page].toOverline()
        when (page) {
            0 -> generatePreviousDate()
            dates.lastIndex -> generateNextDate()
        }
    }

    fun getDayState(date: Date): StateFlow<HealthJourneyDayViewState> =
            _dayState.getOrPut(date) {
                combine(
                        _scrollStates.getOrPut(date) { MutableStateFlow(
                            HealthJourneyDayViewConfiguration()
                        ) },
                        // Todo: replace with suggest activities. Just did this to setup the combine
                        _scrollStates.getOrPut(date) { MutableStateFlow(
                            HealthJourneyDayViewConfiguration()
                        ) }
                ) { _, config ->
                    HealthJourneyDayViewState(
                        dayData = _dayData.getOrPut(date) { createDayStateFlow(date) },
                        config = config,
                    )
                }.stateIn(scope = viewModelScope, started = SharingStarted.Eagerly, initialValue = HealthJourneyDayViewState())
            }

    fun setScrollPositionForDay(date: Date, firstVisibleItemIndex: Int, firstVisibleItemOffset: Int) {
        viewModelScope.launch(dispatcher) {
            _scrollStates[date]?.emit(
                HealthJourneyDayViewConfiguration(
                    firstVisibleItemIndex = firstVisibleItemIndex,
                    firstVisibleItemOffset = firstVisibleItemOffset)
            )
        }
    }

    private fun Outcome<HealthJourneyItemsResponse>.toHealthJourneyDayData(date: Date) =
        when (this) {
            is Success -> Loaded(this().createHealthJourneyItemSections(date))
            is Failure -> Failed("")
        }

    private fun HealthJourneyItemsResponse.createHealthJourneyItemSections(date: Date) =
        HealthJourneyDayViewData(
            sections =
            HealthJourneyItemSections(
                programSections = programSections(),
                completedItems = createCompleteSection(),
                missedItems = createMissedSection()
            ),
            isPast = date < dates[todayIndex],
            isFuture = date > dates[todayIndex],
            programsAvailable = programsAvailable()
        )

    private fun createDayStateFlow(date: Date): StateFlow<State<HealthJourneyDayViewData>> =
        getHealthJourneyItemsForDay(date)
            .map { it.toHealthJourneyDayData(date) }
            .stateIn(scope = viewModelScope, started = StartedEagerlyWhileSubscribed(), initialValue = Loading())

    private fun Date.toOverline(): Overline? =
        when (this) {
            yesterday -> Overline.YESTERDAY
            today -> Overline.TODAY
            tomorrow -> Overline.TOMORROW
            else -> null
        }

    private fun Date.alsoFetchData() = also { getDayState(it) }
    private fun List<Date>.alsoFetchData() = apply { withEach { alsoFetchData() } }

    /**
     * Fetches the next two days as a basic infinite scroll pagination
     */
    private fun generateNextDate() {
        dates = dates + listOf(
            dates.last().addToDayOfYear(1),
            dates.last().addToDayOfYear(2),
        ).alsoFetchData()
    }

    /**
     * Fetches the previous two days as a basic infinite scroll pagination
     */
    private fun generatePreviousDate() {
        todayIndex += 2
        currentPage += 2
        dates = listOf(
            dates.first().addToDayOfYear(-2),
            dates.first().addToDayOfYear(-1),
        ).alsoFetchData() + dates
    }

}
