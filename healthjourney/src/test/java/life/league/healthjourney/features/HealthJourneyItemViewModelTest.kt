package life.league.healthjourney.features


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runBlockingTest
import life.league.core.observable.Failed
import life.league.core.observable.Loaded
import life.league.core.observable.Loading
import life.league.core.observable.Uninitialized
import life.league.core.repository.PointsRepository
import life.league.core.test.rule.TestCoroutineRule
import life.league.core.util.featureflags.FeatureFlagsUtils
import life.league.healthjourney.R
import life.league.healthjourney.extensions.stateFlowTest
import life.league.healthjourney.featureflags.HealthJourneyFeatureFlags
import life.league.healthjourney.journey.HealthJourneyItemViewModel
import life.league.healthjourney.journey.models.*
import life.league.healthjourney.journey.repository.HealthJourneyRepository
import life.league.healthjourney.journey.usecase.HealthJourneyActivityCompletionUseCase
import life.league.healthjourney.programs.models.CustomFields
import life.league.healthjourney.programs.models.HealthJourneyItemDetail
import life.league.networking.callback.Failure
import life.league.networking.callback.Success
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class HealthJourneyItemViewModelTest {

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineRule = TestCoroutineRule()

    @MockK
    private lateinit var healthJourneyRepository: HealthJourneyRepository

    @MockK
    private lateinit var pointsRepository: PointsRepository

    @MockK
    private lateinit var featureFlagUtils: FeatureFlagsUtils

    @MockK
    private lateinit var useCase: HealthJourneyActivityCompletionUseCase

    private lateinit var healthJourneyItemViewModel: HealthJourneyItemViewModel

    @MockK
    private lateinit var stateObserver: Observer<HealthJourneyItemViewModel.HealthJourneyItemViewState>

    private val itemId = "hj_item_id"
    private val campaignId = "hj_campaign_id"
    private val activityId = "hj_activity_id"
    private val healthJourneyItem = HealthJourneyItemDetailResponse(healthJourneyItemDetail = HealthJourneyItemDetail(id = itemId))
    private val verifiableActivityProgressDetail = VerifiableActivityProgressDetail()

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)

        every {
            healthJourneyRepository.getHealthJourneyItem(itemId, campaignId, activityId)
        } returns flow { emit(Success(healthJourneyItem)) }

        every {
            healthJourneyRepository.getVerifiableActivityProgress(itemId)
        } returns flow { emit(Success(verifiableActivityProgressDetail)) }

        every {
            featureFlagUtils.getValue(HealthJourneyFeatureFlags.activityCompletionVerification)
        } returns false

        healthJourneyItemViewModel =
            HealthJourneyItemViewModel(
                itemId,
                campaignId,
                activityId,
                healthJourneyRepository,
                pointsRepository,
                featureFlagUtils,
                dispatcher = coroutineRule.testDispatcher,
                useCase = useCase
            )
        healthJourneyItemViewModel.state.observeForever(stateObserver)
    }

    @Test
    fun `State changes`() {
        val completionScreen = HealthJourneyItemCompletionScreen()
        coEvery {
            healthJourneyRepository.completeActivity(any())
        } answers {
            Success(HealthJourneyItemCompletionResponse(completionScreen))
        }
        every {
            featureFlagUtils.getValue(HealthJourneyFeatureFlags.verifiableActivityProgress)
        } returns false
        healthJourneyItemViewModel.refreshHealthJourneyItem()
        healthJourneyItemViewModel.completeUserActivity(achievementEnabled = false)
        verify(ordering = Ordering.ORDERED) {
            stateObserver.onChanged(
                HealthJourneyItemViewModel.HealthJourneyItemViewState(
                    healthJourneyItem = Loading(),
                    availableToComplete = true,
                    itemCompleteCelebration = Uninitialized(),
                    itemCompleteAchievement = Uninitialized(),
                    verifiableActivityProgressDetail = Uninitialized()
                )
            )
            stateObserver.onChanged(
                HealthJourneyItemViewModel.HealthJourneyItemViewState(
                    healthJourneyItem = Loaded(healthJourneyItem.healthJourneyItemDetail),
                    availableToComplete = true,
                    itemCompleteCelebration = Uninitialized(),
                    itemCompleteAchievement = Uninitialized(),
                    verifiableActivityProgressDetail = Uninitialized()
                )
            )
            stateObserver.onChanged(
                HealthJourneyItemViewModel.HealthJourneyItemViewState(
                    healthJourneyItem = Loaded(healthJourneyItem.healthJourneyItemDetail),
                    availableToComplete = true,
                    itemCompleteCelebration = Loaded(completionScreen),
                    itemCompleteAchievement = Uninitialized(),
                    verifiableActivityProgressDetail = Uninitialized()
                )
            )
        }
    }

    @Test
    fun `State changes - when showing goal progress`() {
        val healthJourneyItemWithProgress = HealthJourneyItemDetailResponse(healthJourneyItemDetail = HealthJourneyItemDetail(id = itemId, type = HealthJourneyItemDetail.VERIFIABLE_ACTIVITY, customFields = CustomFields(campaignMode = CampaignMode.AUTOMATIC.string)))

        every {
            healthJourneyRepository.getHealthJourneyItem(itemId, campaignId, activityId)
        } returns flow { emit(Success(healthJourneyItemWithProgress)) }

        every {
            featureFlagUtils.getValue(HealthJourneyFeatureFlags.verifiableActivityProgress)
        } returns true
        healthJourneyItemViewModel.refreshHealthJourneyItem()

        verify(ordering = Ordering.ORDERED) {
            stateObserver.onChanged(
                HealthJourneyItemViewModel.HealthJourneyItemViewState(
                    healthJourneyItem = Loading(),
                    availableToComplete = true,
                    itemCompleteCelebration = Uninitialized(),
                    itemCompleteAchievement = Uninitialized(),
                    verifiableActivityProgressDetail = Uninitialized()
                )
            )
            stateObserver.onChanged(
                HealthJourneyItemViewModel.HealthJourneyItemViewState(
                    healthJourneyItem = Loading(),
                    availableToComplete = true,
                    itemCompleteCelebration = Uninitialized(),
                    itemCompleteAchievement = Uninitialized(),
                    verifiableActivityProgressDetail = Loading()
                )
            )
            stateObserver.onChanged(
                HealthJourneyItemViewModel.HealthJourneyItemViewState(
                    healthJourneyItem = Loading(),
                    availableToComplete = true,
                    itemCompleteCelebration = Uninitialized(),
                    itemCompleteAchievement = Uninitialized(),
                    verifiableActivityProgressDetail = Loaded(verifiableActivityProgressDetail)
                )
            )
            stateObserver.onChanged(
                HealthJourneyItemViewModel.HealthJourneyItemViewState(
                    healthJourneyItem = Loaded(healthJourneyItemWithProgress.healthJourneyItemDetail),
                    availableToComplete = true,
                    itemCompleteCelebration = Uninitialized(),
                    itemCompleteAchievement = Uninitialized(),
                    verifiableActivityProgressDetail = Loaded(verifiableActivityProgressDetail)
                )
            )
        }
    }

    @Test
    fun `completeActivity() - error`()  = coroutineRule.runBlockingTest{
        // setup mocks
        coEvery {
            healthJourneyRepository.completeActivity(any())
        } answers {
            Failure("Failed to get complete activity")
        }

        stateFlowTest(
            stateFlow = healthJourneyItemViewModel.itemCompleteCelebration,
            action = { healthJourneyItemViewModel.completeUserActivity(achievementEnabled = false) }
        ) { results ->
            assert(results[0] is Uninitialized)
            assert(results[1] is Loading)
            assert(results[2] is Failed)
            assert(results[2] == Failed<HealthJourneyItemCompletionScreen>(R.string.loading_error))
        }
    }

    @Test
    fun `getHealthJourneyActivities() - success`() = coroutineRule.runBlockingTest{
        // setup mocks
        coEvery {
            healthJourneyRepository.completeActivity(any())
        } answers {
            Success(HealthJourneyItemCompletionResponse(HealthJourneyItemCompletionScreen()))
        }

        stateFlowTest(
            stateFlow = healthJourneyItemViewModel.itemCompleteCelebration,
            action = { healthJourneyItemViewModel.completeUserActivity(achievementEnabled = false) }
        ) { results ->
            assert(results[0] is Uninitialized)
            assert(results[1] is Loading)
            assert(results[2] is Loaded)
            assert(results[2] == Loaded(HealthJourneyItemCompletionScreen()))
        }

    }

}
