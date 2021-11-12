package life.league.rewards.usecase

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import life.league.core.observable.Failed
import life.league.core.observable.Loaded
import life.league.core.test.rule.TestCoroutineRule
import life.league.networking.callback.Failure
import life.league.networking.callback.Success
import life.league.rewards.previewdata.AchievementCompletionTestData
import life.league.rewards.previewdata.GetAllAchievementsTestData
import life.league.rewards.repository.AchievementsRepository
import org.junit.*
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime

@ExperimentalTime
class AchievementCompletionUseCaseTest {

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineRule = TestCoroutineRule()

    @MockK
    private lateinit var repository : AchievementsRepository

    private lateinit var achievementUseCase: AchievementUseCase

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        achievementUseCase = AchievementUseCase(repository = repository)
    }

    @After
    fun clear() {
        clearAllMocks()
    }

    @Test
    fun `get user completed achievements on activity completion`() = coroutineRule.runBlockingTest {
        val userAchievement = AchievementCompletionTestData.createUserAchievementDataForActivityCompletion(
            includeInProgress = false
        )
        val response = flowOf(Success(AchievementCompletionTestData.createResponse(includeInProgress = false)))

        coEvery {
            repository.fetchAchievementInfoOnCompletion()
        } answers {
            response
        }

        achievementUseCase.fetchAchievementInfoOnCompletion(
            headline = "",
            rewardsMessage = "",
            descriptionTwo = "",
            descriptionOne = "",
            title = ""
        ).test {
            awaitItem().run {
                val achievement = (this as Loaded).data
                assertEquals(userAchievement, achievement)
            }
            awaitComplete()
        }
    }

    @Test
    fun `get user inprogress achievements on activity completion`() = coroutineRule.runBlockingTest {
        val userAchievement = AchievementCompletionTestData.createUserAchievementDataForActivityCompletion(
            includeCompleted = false
        )
        val response = flowOf(Success(AchievementCompletionTestData.createResponse(includeCompleted = false)))

        coEvery {
            repository.fetchAchievementInfoOnCompletion()
        } answers {
            response
        }

        achievementUseCase.fetchAchievementInfoOnCompletion(
            headline = "",
            rewardsMessage = "",
            descriptionTwo = "",
            descriptionOne = "",
            title = ""
        ).test {
            awaitItem().run {
                val achievement = (this as Loaded).data
                assertEquals(userAchievement, achievement)
            }
            awaitComplete()
        }
    }

    @Test
    fun `get user inprogress and completed achievements on activity completion`() = coroutineRule.runBlockingTest {
        val userAchievement = AchievementCompletionTestData.createUserAchievementDataForActivityCompletion()
        val response = flowOf(Success(AchievementCompletionTestData.createResponse()))

        coEvery {
            repository.fetchAchievementInfoOnCompletion()
        } answers {
            response
        }

        achievementUseCase.fetchAchievementInfoOnCompletion(
            headline = "",
            rewardsMessage = "",
            descriptionTwo = "",
            descriptionOne = "",
            title = ""
        ).test {
            awaitItem().run {
                val achievement = (this as Loaded).data
                assertEquals(userAchievement, achievement)
            }
            awaitComplete()
        }
    }

    @Test
    fun `get error on fetchAchievementInfoOnCompletion`() = coroutineRule.runBlockingTest {
        val response = flowOf(Failure(errorResponse = "Unable To Fetch Achievement Info"))

        coEvery {
            repository.fetchAchievementInfoOnCompletion()
        } answers {
            response
        }

        achievementUseCase.fetchAchievementInfoOnCompletion(
            headline = "",
            rewardsMessage = "",
            descriptionTwo = "",
            descriptionOne = "",
            title = ""
        ).test {
            awaitItem().run {
                Assert.assertTrue("Unable To Fetch Achievement Info", this is Failed<*>)
            }
            awaitComplete()
        }
    }

    @Test
    fun `get all user achievements successfully`() = coroutineRule.runBlockingTest {
        val userAchievement = GetAllAchievementsTestData.createUserAchievementData(returnEmpty = false)
        val response = flowOf(Success(GetAllAchievementsTestData.createResponse()))

        coEvery {
            repository.fetchAllAchievements()
        } answers {
            response
        }

        achievementUseCase.fetchAllAchievements(
            
        ).test {
            awaitItem().run {
                val achievement = (this as Loaded).data
                assertEquals(userAchievement, achievement)
            }
            awaitComplete()
        }
    }

    @Test
    fun `get all user achievements with error`() = coroutineRule.runBlockingTest {
        val response = flowOf(Failure(errorResponse = "Unable To Fetch Achievement Info"))

        coEvery {
            repository.fetchAllAchievements()
        } answers {
            response
        }

        achievementUseCase.fetchAllAchievements(
            
        ).test {
            awaitItem().run {
                Assert.assertTrue("Unable To Fetch Achievement Info", this is Failed<*>)
            }
            awaitComplete()
        }
    }
}