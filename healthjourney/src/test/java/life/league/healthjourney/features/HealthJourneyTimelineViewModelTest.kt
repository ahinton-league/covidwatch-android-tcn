package life.league.healthjourney.features


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import life.league.core.observable.Failed
import life.league.core.observable.Loaded
import life.league.core.observable.Loading
import life.league.core.observable.State
import life.league.core.test.rule.TestCoroutineRule
import life.league.healthjourney.R
import life.league.healthjourney.journey.HealthJourneyTimelineViewModel
import life.league.healthjourney.journey.repository.HealthJourneyRepository
import life.league.healthjourney.journey.models.HealthActivities
import life.league.healthjourney.journey.models.HealthActivitiesCategory
import life.league.healthjourney.journey.models.HealthActivitiesCategoryHeader
import life.league.healthjourney.journey.models.HealthJourneyItem
import life.league.networking.callback.Failure
import life.league.networking.callback.Success
import life.league.networking.socket.API
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*

class HealthJourneyTimelineViewModelTest {

    @get:Rule
    val coroutineRule = TestCoroutineRule()

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var api: API

    @MockK
    private lateinit var healthJourneyRepository: HealthJourneyRepository

    private lateinit var healthJourneyViewModel: HealthJourneyTimelineViewModel

    @MockK
    private lateinit var state: Observer<State<HealthJourneyTimelineViewModel.HealthJourneyTimelineViewState>>

    private fun mockHealthActivity(id: String) =
        HealthJourneyItem(id = id, endDate = Date(), status = "active")

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)

        healthJourneyViewModel = HealthJourneyTimelineViewModel(
            healthJourneyRepository,
            dispatcher = coroutineRule.testDispatcher
        )
        healthJourneyViewModel.state.observeForever(state)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `getHealthJourneyActivities() - error`()  {
        // setup mocks
        every {
            healthJourneyRepository.getHealthJourneyTimeline()
        } returns flowOf(Failure("Failed to get journey activities"))

        healthJourneyViewModel.getActivities()

        verify(ordering = Ordering.ORDERED) {
            state.onChanged(Loading())
            state.onChanged(Failed(R.string.health_journey_error))
        }

    }

    @Test
    fun `getHealthJourneyActivities() - success`() = runBlockingTest {
        val activityList = (0..10).map { mockHealthActivity(it.toString()) }
        val mockedActivities = HealthActivities(activityList)
        // setup mocks
        every {
            healthJourneyRepository.getHealthJourneyTimeline()
        } returns flowOf(Success(mockedActivities))

        healthJourneyViewModel.getActivities()

        verify(ordering = Ordering.ORDERED) {
            state.onChanged(Loading())
            state.onChanged(
                Loaded(
                    HealthJourneyTimelineViewModel.HealthJourneyTimelineViewState(
                        activityCategories = listOf(
                            HealthActivitiesCategory(
                                HealthActivitiesCategoryHeader(R.string.health_journey_today), activityList
                            )
                        ),
                        previewAvailable = false
                    )
                )
            )
        }

    }

}
