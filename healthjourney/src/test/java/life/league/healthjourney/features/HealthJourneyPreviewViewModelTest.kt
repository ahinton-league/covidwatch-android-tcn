package life.league.healthjourney.features


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flow
import life.league.core.observable.Failed
import life.league.core.observable.Loaded
import life.league.core.observable.Loading
import life.league.core.observable.State
import life.league.core.test.rule.TestCoroutineRule
import life.league.healthjourney.R
import life.league.healthjourney.journey.repository.HealthJourneyRepository
import life.league.healthjourney.journey.models.HealthActivities
import life.league.healthjourney.journey.models.HealthJourneyItem
import life.league.healthjourney.programs.preview.HealthJourneyPreviewViewModel
import life.league.networking.callback.Failure
import life.league.networking.callback.Outcome
import life.league.networking.callback.Success
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class HealthJourneyPreviewViewModelTest {

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineRule = TestCoroutineRule()

    @MockK
    private lateinit var healthJourneyRepository: HealthJourneyRepository

    private lateinit var healthJourneyPreviewViewModel: HealthJourneyPreviewViewModel

    @MockK
    private lateinit var previewItems: Observer<State<List<HealthJourneyItem>>>

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)

        healthJourneyPreviewViewModel = HealthJourneyPreviewViewModel(healthJourneyRepository, coroutineRule.testDispatcher)
        healthJourneyPreviewViewModel.previewItems.observeForever(previewItems)
    }

    @Test
    fun `getPreview() - error`() {
        // setup mocks
        every {
            healthJourneyRepository.getHealthJourneyPreviewItems()
        } answers {
            flow<Outcome<HealthActivities>> { emit(Failure("Failed to get health journey preview")) }
        }

        healthJourneyPreviewViewModel.getPreview()

        verify(ordering = Ordering.ORDERED) {
            previewItems.onChanged(Loading())
            previewItems.onChanged(Failed(R.string.loading_error))
        }

    }

    private fun mockHealthActivity(id: String) = HealthJourneyItem(id = id, status = "upcoming")

    @Test
    fun `getHealthJourneyActivities() - success`() {
        val activityList = (0..10).map { mockHealthActivity(it.toString()) }
        val mockedActivities = HealthActivities(activityList)
        // setup mocks
        every {
            healthJourneyRepository.getHealthJourneyPreviewItems()
        } answers {
            flow { emit(Success(mockedActivities)) }
        }

        healthJourneyPreviewViewModel.getPreview()

        verify(ordering = Ordering.ORDERED) {
            previewItems.onChanged(Loading())
            previewItems.onChanged(Loaded(mockedActivities.activities))
        }

    }

}
