package life.league.healthjourney.features


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import io.mockk.*
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import life.league.core.observable.Failed
import life.league.core.observable.Loaded
import life.league.core.observable.Loading
import life.league.core.observable.State
import life.league.core.test.rule.TestCoroutineRule
import life.league.healthjourney.R
import life.league.healthjourney.journey.HealthJourneyRemovalConfirmationViewModel
import life.league.healthjourney.journey.repository.HealthJourneyRepository
import life.league.healthjourney.programs.repository.HealthProgramsRepository
import life.league.networking.callback.Empty
import life.league.networking.callback.Failure
import life.league.networking.callback.Success
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RemoveHealthJourneyItemConfirmationViewModelTest {

    @get:Rule
    val coroutineRule = TestCoroutineRule()

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var healthJourneyRepository: HealthJourneyRepository
    @MockK
    private lateinit var healthProgramsRepository: HealthProgramsRepository

    private lateinit var removeHealthJourneyItemConfirmationViewModel: HealthJourneyRemovalConfirmationViewModel

    @MockK
    private lateinit var itemRemovedObserver: Observer<State<Empty>>

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)

        removeHealthJourneyItemConfirmationViewModel = HealthJourneyRemovalConfirmationViewModel(
            "item_id",
            null,
            healthJourneyRepository,
            healthProgramsRepository,
            coroutineRule.testDispatcher)
        removeHealthJourneyItemConfirmationViewModel.healthJourneyRemovalComplete.observeForever(itemRemovedObserver)
    }

    @Test
    fun `completeActivity() - error`() {
        // setup mocks
        coEvery {
            healthJourneyRepository.removeActivity(any())
        } answers {
            Failure("Failed to remove activity")
        }

        removeHealthJourneyItemConfirmationViewModel.removeActivity()

        verify(ordering = Ordering.ORDERED) {
            itemRemovedObserver.onChanged(Loading())
            itemRemovedObserver.onChanged(Failed(R.string.health_journey_error))
        }

    }

    @Test
    fun `getHealthJourneyActivities() - success`() {
        // setup mocks
        coEvery {
            healthJourneyRepository.removeActivity(any())
        } answers {
            Success(Empty)
        }

        removeHealthJourneyItemConfirmationViewModel.removeActivity()

        verify(ordering = Ordering.ORDERED) {
            itemRemovedObserver.onChanged(Loading())
            itemRemovedObserver.onChanged(Loaded(Empty))
        }

    }

}
