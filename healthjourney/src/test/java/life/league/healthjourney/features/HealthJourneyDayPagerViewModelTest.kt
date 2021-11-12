package life.league.healthjourney.features


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runBlockingTest
import life.league.core.extension.startOfDay
import life.league.core.observable.Failed
import life.league.core.observable.Loaded
import life.league.core.test.rule.TestCoroutineRule
import life.league.healthjourney.extensions.stateFlowTest
import life.league.healthjourney.journey.models.CampaignInfo
import life.league.healthjourney.journey.models.HealthJourneyItem
import life.league.healthjourney.journey.models.HealthJourneyItemsResponse
import life.league.healthjourney.journey.timeline.HealthJourneyDayPagerViewModel
import life.league.healthjourney.journey.timeline.day.HealthJourneyDayViewConfiguration
import life.league.healthjourney.journey.usecase.GetHealthJourneyItemsForDayUseCase
import life.league.networking.callback.Failure
import life.league.networking.callback.Success
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*

class HealthJourneyDayPagerViewModelTest {

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineRule = TestCoroutineRule()

    @MockK
    private lateinit var useCase: GetHealthJourneyItemsForDayUseCase

    private lateinit var viewModel: HealthJourneyDayPagerViewModel

    private val testCampaignName: String = "Test Campaign"

    private val healthJourneyItemsResponse = HealthJourneyItemsResponse(
        items = List(5) { HealthJourneyItem(status = HealthJourneyItem.Status.ACTIVE.text, campaignInfo = CampaignInfo(name = testCampaignName)) } +
                List(5) { HealthJourneyItem(status = HealthJourneyItem.Status.COMPLETED.text) }
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)

        every {
            useCase.invoke(any())
        } returns flow { emit(Success(healthJourneyItemsResponse)) }

        viewModel =
            HealthJourneyDayPagerViewModel(
                 getHealthJourneyItemsForDay = useCase,
                dispatcher = coroutineRule.testDispatcher,
            )
    }

    @Test
    fun `onPageChange - Start of list`() {

        val initialDatesSize = viewModel.dates.size
        val initialTodayIndex = viewModel.todayIndex

        viewModel.onPageChange(0)

        assert(viewModel.dates.size == initialDatesSize + 2)
        assert(viewModel.todayIndex == initialTodayIndex + 2)
        assert(viewModel.currentPage == 2)


    }

    @Test
    fun `onPageChange - End of list`() {

        val initialDatesSize = viewModel.dates.size
        val initialTodayIndex = viewModel.todayIndex
        val newPageIndex = viewModel.dates.lastIndex

        viewModel.onPageChange(newPageIndex)


        assert(viewModel.dates.size == initialDatesSize + 2)
        assert(viewModel.todayIndex == initialTodayIndex)
        assert(viewModel.currentPage == newPageIndex)

    }

    @Test
    fun `onPageChange - Middle of list`() {

        val initialDatesSize = viewModel.dates.size
        val initialTodayIndex = viewModel.todayIndex
        val newPageIndex = viewModel.dates.lastIndex / 2

        viewModel.onPageChange(newPageIndex)

        assert(viewModel.dates.size == initialDatesSize)
        assert(viewModel.todayIndex == initialTodayIndex)
        assert(viewModel.currentPage == newPageIndex)

    }

    @Test
    fun `Get Day State - Failure`() = coroutineRule.runBlockingTest {

        every {
            useCase.invoke(any())
        } returns flow { emit(Failure("")) }

        val dayState = viewModel.getDayState(Date(0).startOfDay())

        stateFlowTest(
            stateFlow = dayState.value.dayData,
            action = { }
        ) { results ->

            assert(results[0] is Failed)

        }
    }

    @Test
    fun `Get Day State - Success`() = coroutineRule.runBlockingTest {

        val dayState = viewModel.getDayState(Date().startOfDay())

        stateFlowTest(
            stateFlow = dayState.value.dayData,
            action = { }
        ) { results ->
            val data = results[0]
            assert(data is Loaded
                    && data().sections.programSections.size == 1
                    && data().sections.programSections[testCampaignName]?.size == 5
                    && data().sections.completedItems.items.size == 5)
        }
    }

    @Test
    fun `Update Scroll State`() = coroutineRule.runBlockingTest {

        val day = Date().startOfDay()
        val dayState = viewModel.getDayState(day)
        val scrollState = HealthJourneyDayViewConfiguration()

        stateFlowTest(
            stateFlow = dayState,
            action = { viewModel.setScrollPositionForDay(day, 10, 10) }
        ) { results ->

            val dataBefore = results[0].dayData.value
            assert(results[0].config == scrollState)
            assert(dataBefore is Loaded
                    && dataBefore().sections.programSections.size == 1
                    && dataBefore().sections.programSections[testCampaignName]?.size == 5
                    && dataBefore().sections.completedItems.items.size == 5)

            val dataAfter = results[1].dayData.value
            assert(results[1].config == HealthJourneyDayViewConfiguration(10, 10))
            assert(dataAfter is Loaded
                    && dataAfter().sections.programSections.size == 1
                    && dataAfter().sections.programSections[testCampaignName]?.size == 5
                    && dataAfter().sections.completedItems.items.size == 5)

        }
    }


}
