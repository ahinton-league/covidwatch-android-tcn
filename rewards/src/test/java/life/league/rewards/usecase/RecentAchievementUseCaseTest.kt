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
import life.league.rewards.model.RecentAchievementsResponse
import life.league.rewards.previewdata.AchievementCompletionTestData
import life.league.rewards.repository.AchievementsRepository
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime

@ExperimentalTime
class RecentAchievementUseCaseTest {

    @MockK
    private lateinit var repository : AchievementsRepository

    private lateinit var recentAchievementUseCase: RecentAchievementUseCase

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        recentAchievementUseCase = RecentAchievementUseCase(repository = repository)
    }

    @After
    fun clear() {
        clearAllMocks()
    }

    @Test
    fun `get user recently completed achievements successfully`() = runBlockingTest {
        val achievements = AchievementCompletionTestData.getCompletedStreaks()
        val response = flowOf(Success(RecentAchievementsResponse(recentAchievements = AchievementCompletionTestData.getCompletedStreaks())))

        coEvery {
            repository.fetchRecentlyEarnedAchievements()
        } answers {
            response
        }

        recentAchievementUseCase.fetchRecentlyEarnedAchievements().test {
            awaitItem().run {
                val achievement = (this as Success).response.recentAchievements
                assertEquals(achievements, achievement)
            }
            awaitComplete()
        }
    }

    @Test
    fun `get all user achievements with error`() = runBlockingTest {
        val response = flowOf(Failure(errorResponse = "Error getting Info"))

        coEvery {
            repository.fetchRecentlyEarnedAchievements()
        } answers {
            response
        }

        recentAchievementUseCase.fetchRecentlyEarnedAchievements(
            
        ).test {
            awaitItem().run {
                Assert.assertTrue("Error getting Info", this is Failure)
            }
            awaitComplete()
        }
    }

}