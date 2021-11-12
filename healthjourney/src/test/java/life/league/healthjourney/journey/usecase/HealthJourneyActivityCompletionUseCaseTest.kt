package life.league.healthjourney.journey.usecase

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
import life.league.healthjourney.journey.models.HealthJourneyItemCompletionResponse
import life.league.healthjourney.journey.models.HealthJourneyItemCompletionScreen
import life.league.healthjourney.journey.repository.HealthJourneyRepository
import life.league.networking.callback.Failure
import life.league.networking.callback.Success
import life.league.rewards.previewdata.AchievementCompletionTestData
import life.league.rewards.repository.AchievementsRepository
import life.league.rewards.usecase.AchievementUseCase
import org.junit.*
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime

@ExperimentalTime
class HealthJourneyActivityCompletionUseCaseTest {

    @get:Rule
    val coroutineRule = TestCoroutineRule()

    @MockK
    private lateinit var healthJourneyRepository : HealthJourneyRepository

    @MockK
    private lateinit var repository : AchievementsRepository

    private lateinit var achievementUseCase: AchievementUseCase

    private lateinit var useCase: HealthJourneyActivityCompletionUseCase

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        achievementUseCase = AchievementUseCase(repository = repository)
        useCase = HealthJourneyActivityCompletionUseCase(
            healthJourneyRepository = healthJourneyRepository,
            achievementUseCase = achievementUseCase,
            dispatcher = coroutineRule.testDispatcher
        )
    }

    @After
    fun clear() {
        clearAllMocks()
    }

    @Test
    fun `complete activity with achievements successfully`() = runBlockingTest {
        val userAchievement = AchievementCompletionTestData.createUserAchievementDataForActivityCompletion(
            includeInProgress = false
        )

        val healthJourneyItemCompletion = HealthJourneyItemCompletionResponse(completionScreen = HealthJourneyItemCompletionScreen(
            title = "",
            rewardsMessage = "",
            descriptionTwo = "",
            descriptionOne = "",
            image = "",
            eyebrowHeadline = ""
        ))
        val healthJourneyResponse = Success(healthJourneyItemCompletion)
        coEvery {
            healthJourneyRepository.completeActivity("xyz")
        } answers {
            healthJourneyResponse
        }

        val response = flowOf(Success(AchievementCompletionTestData.createResponse(includeInProgress = false)))
        coEvery {
            repository.fetchAchievementInfoOnCompletion()
        } answers {
            response
        }

        useCase.completeActivity("xyz").test {
            awaitItem().run {
                val achievement = (this as Loaded).data
                assertEquals(userAchievement, achievement)
            }
            awaitComplete()
        }
    }

    @Test
    fun `complete activity with achievements failure`() = runBlockingTest {
        val response = flowOf(Failure(errorResponse = "Unable To Fetch Info"))

        val healthJourneyItemCompletion = HealthJourneyItemCompletionResponse(completionScreen = HealthJourneyItemCompletionScreen(
            title = "",
            rewardsMessage = "",
            descriptionTwo = "",
            descriptionOne = "",
            image = "",
            eyebrowHeadline = ""
        ))
        val healthJourneyResponse = Success(healthJourneyItemCompletion)
        coEvery {
            healthJourneyRepository.completeActivity("xyz")
        } answers {
            healthJourneyResponse
        }

        coEvery {
            repository.fetchAchievementInfoOnCompletion()
        } answers {
            response
        }

        useCase.completeActivity("xyz").test {
            awaitItem().run {
                Assert.assertTrue("Unable To Fetch Info", this is Failed<*>)
            }
            awaitComplete()
        }
    }

}