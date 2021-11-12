package life.league.rewards.usecase

import app.cash.turbine.test
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import life.league.networking.callback.Failure
import life.league.networking.callback.Success
import life.league.rewards.previewdata.MilestoneTrackerData
import life.league.rewards.repository.AchievementsRepository
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime

@ExperimentalTime
class AchievementProgressUseCaseTest {

    @MockK
    private lateinit var repository : AchievementsRepository

    private lateinit var achievementProgressUseCase: AchievementProgressUseCase

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        achievementProgressUseCase = AchievementProgressUseCase(repository = repository)
    }

    @After
    fun clear() {
        clearAllMocks()
    }

    @Test
    fun `get user milestone result successfully`() = runBlockingTest {
        val milestoneTrackerResult = MilestoneTrackerData.getMilestoneData()
        val response = flowOf(Success(MilestoneTrackerData.getMilestoneData()))

        coEvery {
            repository.fetchAchievementsProgress()
        } answers {
            response
        }

        achievementProgressUseCase.fetchAchievementsProgress().test {
            awaitItem().run {
                val milestoneResult = (this as Success).response
                assertEquals(milestoneTrackerResult, milestoneResult)
            }
            awaitComplete()
        }
    }

    @Test
    fun `get user milestone result with error`() = runBlockingTest {
        val response = flowOf(Failure(errorResponse = "Error getting info"))

        coEvery {
            repository.fetchAchievementsProgress()
        } answers {
            response
        }

        achievementProgressUseCase.fetchAchievementsProgress().test {
            awaitItem().run {
                Assert.assertTrue("Error getting info", this is Failure)
            }
            awaitComplete()
        }
    }

}