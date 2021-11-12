package life.league.healthjourney.programs.progress

import app.cash.turbine.test
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import life.league.core.observable.Failed
import life.league.core.observable.Loaded
import life.league.core.test.rule.TestCoroutineRule
import life.league.healthjourney.programs.models.HealthProgram
import life.league.healthjourney.programs.models.HealthPrograms
import life.league.healthjourney.programs.models.HealthProgramsProgressInfo
import life.league.healthjourney.programs.repository.HealthProgramsRepository
import life.league.networking.callback.Failure
import life.league.networking.callback.Success
import life.league.rewards.model.RecentAchievementsResponse
import life.league.rewards.previewdata.AchievementCompletionTestData
import life.league.rewards.previewdata.MilestoneTrackerData
import life.league.rewards.repository.AchievementsRepository
import life.league.rewards.usecase.AchievementProgressUseCase
import life.league.rewards.usecase.RecentAchievementUseCase
import org.junit.*
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime

@ExperimentalTime
class HealthJourneyProgressInfoUseCaseTest {

    @get:Rule
    val coroutineRule = TestCoroutineRule()

    @MockK
    private lateinit var healthProgramsRepository: HealthProgramsRepository

    @MockK
    private lateinit var achievementsRepository: AchievementsRepository

    private lateinit var recentAchievementUseCase: RecentAchievementUseCase

    private lateinit var achievementProgressUseCase: AchievementProgressUseCase

    private lateinit var useCase: HealthJourneyProgressInfoUseCase

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        recentAchievementUseCase = RecentAchievementUseCase(repository = achievementsRepository)
        achievementProgressUseCase = AchievementProgressUseCase(repository = achievementsRepository)

        useCase = HealthJourneyProgressInfoUseCase(
            healthProgramsRepository = healthProgramsRepository,
            recentAchievementUseCase = recentAchievementUseCase,
            achievementProgressUseCase = achievementProgressUseCase,
            dispatcher = coroutineRule.testDispatcher
        )
    }

    @After
    fun clear() {
        clearAllMocks()
    }

    @Test
    fun `fetch progressInfo successfully`() = runBlockingTest(context = coroutineRule.testDispatcher) {
        val programs = HealthPrograms(programs = listOf(HealthProgram(id = "1"), HealthProgram(id = "3"), HealthProgram(id = "2") ))
        every {
            healthProgramsRepository.getHealthProgramsInProgress()
        } answers {
            flow { emit(Success(programs)) }
        }

        val recentsResponse = RecentAchievementsResponse(recentAchievements = AchievementCompletionTestData.getCompletedStreaks())
        every {
            recentAchievementUseCase.fetchRecentlyEarnedAchievements()
        } answers {
            flow { emit(Success(recentsResponse)) }
        }

        val mileStone = MilestoneTrackerData.getMilestoneData()
        every {
            achievementProgressUseCase.fetchAchievementsProgress()
        } answers {
            flow { emit(Success(mileStone)) }
        }

        val result = HealthProgramsProgressInfo(
            healthPrograms = programs,
            recentAchievements = recentsResponse.recentAchievements.orEmpty(),
            milestoneTrackerResult = mileStone
        )

        useCase.fetchHealthProgramsProgressInfo().test {
            awaitItem().run {
                val achievement = (this as Loaded).data
                assertEquals(result, achievement)
            }
            awaitComplete()
        }
    }

    @Test
    fun `fetch progressInfo programs failure`() = runBlockingTest(context = coroutineRule.testDispatcher) {
        val response = flowOf(Failure(errorResponse = "Unable To Fetch Info"))
        coEvery {
            healthProgramsRepository.getHealthProgramsInProgress()
        } answers {
            response
        }

        val recentsResponse = RecentAchievementsResponse(recentAchievements = AchievementCompletionTestData.getCompletedStreaks())
        every {
            recentAchievementUseCase.fetchRecentlyEarnedAchievements()
        } answers {
            flow { emit(Success(recentsResponse)) }
        }

        val mileStone = MilestoneTrackerData.getMilestoneData()
        every {
            achievementProgressUseCase.fetchAchievementsProgress()
        } answers {
            flow { emit(Success(mileStone)) }
        }

        useCase.fetchHealthProgramsProgressInfo().test {
            awaitItem().run {
                Assert.assertTrue("Unable To Fetch Info", this is Failed<*>)
            }
            awaitComplete()
        }
    }

    @Test
    fun `fetch progressInfo recents failure`() = runBlockingTest(context = coroutineRule.testDispatcher) {
        val programs = HealthPrograms(programs = listOf(HealthProgram(id = "1"), HealthProgram(id = "3"), HealthProgram(id = "2") ))
        every {
            healthProgramsRepository.getHealthProgramsInProgress()
        } answers {
            flow { emit(Success(programs)) }
        }

        val recentsResponse = flowOf(Failure(errorResponse = "Unable To Fetch Info"))
        every {
            recentAchievementUseCase.fetchRecentlyEarnedAchievements()
        } answers {
            recentsResponse
        }

        val mileStone = MilestoneTrackerData.getMilestoneData()
        every {
            achievementProgressUseCase.fetchAchievementsProgress()
        } answers {
            flow { emit(Success(mileStone)) }
        }

        useCase.fetchHealthProgramsProgressInfo().test {
            awaitItem().run {
                Assert.assertFalse(this is Failed<*>)
            }
            awaitComplete()
        }
    }

    @Test
    fun `fetch progressInfo milestone failure`() = runBlockingTest(context = coroutineRule.testDispatcher) {
        val programs = HealthPrograms(programs = listOf(HealthProgram(id = "1"), HealthProgram(id = "3"), HealthProgram(id = "2") ))
        every {
            healthProgramsRepository.getHealthProgramsInProgress()
        } answers {
            flow { emit(Success(programs)) }
        }

        val recentsResponse = RecentAchievementsResponse(recentAchievements = AchievementCompletionTestData.getCompletedStreaks())
        every {
            recentAchievementUseCase.fetchRecentlyEarnedAchievements()
        } answers {
            flow { emit(Success(recentsResponse)) }
        }

        val mileStone = flowOf(Failure(errorResponse = "Unable To Fetch Info"))
        every {
            achievementProgressUseCase.fetchAchievementsProgress()
        } answers {
            mileStone
        }

        useCase.fetchHealthProgramsProgressInfo().test {
            awaitItem().run {
                Assert.assertTrue("Unable To Fetch Info", this is Failed<*>)
            }
            awaitComplete()
        }
    }
}