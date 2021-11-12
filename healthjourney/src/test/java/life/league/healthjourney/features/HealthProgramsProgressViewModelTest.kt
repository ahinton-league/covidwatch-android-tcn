package life.league.healthjourney.features


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flow
import life.league.core.observable.Failed
import life.league.core.observable.Loaded
import life.league.core.observable.Uninitialized
import life.league.core.test.rule.TestCoroutineRule
import life.league.healthjourney.programs.models.HealthProgram
import life.league.healthjourney.programs.models.HealthPrograms
import life.league.healthjourney.programs.progress.HealthJourneyProgressInfoUseCase
import life.league.healthjourney.programs.progress.HealthProgramsProgressViewModel
import life.league.healthjourney.programs.repository.HealthProgramsRepository
import life.league.networking.callback.Failure
import life.league.networking.callback.Outcome
import life.league.networking.callback.Success
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class HealthProgramsProgressViewModelTest {

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    
    @get:Rule
    val coroutineRule = TestCoroutineRule()

    @MockK
    private lateinit var healthProgramsRepository: HealthProgramsRepository

    private lateinit var healthProgressViewModel: HealthProgramsProgressViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        healthProgressViewModel = HealthProgramsProgressViewModel(
            healthProgramsRepository,
            dispatcher = coroutineRule.testDispatcher
        )
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `getHealthPrograms() - error`() {
        every {
            healthProgramsRepository.getHealthProgramsInProgress()
        } answers {
            flow<Outcome<HealthPrograms>> { emit(Failure("Failed to get health programs in progress")) }
        }

        assert(healthProgressViewModel.programs is Uninitialized)

        healthProgressViewModel.getHealthPrograms()

        assert(healthProgressViewModel.programs is Failed)

    }

    @Test
    fun `getHealthPrograms() - success - non empty programs`() {
        val programs = HealthPrograms(programs = listOf(HealthProgram(id = "1"), HealthProgram(id = "3"), HealthProgram(id = "2") ))
        every {
            healthProgramsRepository.getHealthProgramsInProgress()
        } answers {
            flow { emit(Success(programs)) }
        }
        assert(healthProgressViewModel.programs is Uninitialized)

        healthProgressViewModel.getHealthPrograms()

        assert(healthProgressViewModel.programs == Loaded(programs))

    }

    @Test
    fun `getHealthPrograms() - success - empty programs`() {
        every {
            healthProgramsRepository.getHealthProgramsInProgress()
        } answers {
            flow { emit(Success(HealthPrograms())) }
        }

        assert(healthProgressViewModel.programs is Uninitialized)

        healthProgressViewModel.getHealthPrograms()

        assert(healthProgressViewModel.programs == Loaded(HealthPrograms()))

    }

}
