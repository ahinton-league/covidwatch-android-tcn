package life.league.healthjourney.journey

import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import life.league.core.base.SingleLiveEvent
import life.league.core.observable.*
import life.league.core.repository.PointsRepository
import life.league.core.util.featureflags.FeatureFlagsUtils
import life.league.healthjourney.R
import life.league.healthjourney.featureflags.HealthJourneyFeatureFlags
import life.league.healthjourney.journey.models.CompletionMethod
import life.league.healthjourney.journey.models.HealthJourneyItemCompletionScreen
import life.league.healthjourney.journey.models.HelpfulTip
import life.league.healthjourney.journey.models.VerifiableActivityProgressDetail
import life.league.healthjourney.journey.repository.HealthJourneyRepository
import life.league.healthjourney.journey.usecase.HealthJourneyActivityCompletionUseCase
import life.league.healthjourney.programs.models.HealthJourneyItemDetail
import life.league.networking.callback.Failure
import life.league.networking.callback.Success
import life.league.rewards.model.UserAchievement


class HealthJourneyItemViewModel(
    private val healthJourneyItemId: String?,
    private val campaignId: String?,
    private val activityId: String?,
    private val healthJourneyRepository: HealthJourneyRepository,
    private val pointsRepository: PointsRepository,
    private val featureFlagsUtils: FeatureFlagsUtils,
    private val useCase: HealthJourneyActivityCompletionUseCase? = null,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    data class HealthJourneyItemViewState(
        val healthJourneyItem: State<HealthJourneyItemDetail>,
        val availableToComplete: Boolean,
        val itemCompleteCelebration: State<HealthJourneyItemCompletionScreen>,
        val itemCompleteAchievement: State<UserAchievement>,
        val verifiableActivityProgressDetail: State<VerifiableActivityProgressDetail>
    )

    val redirect: SingleLiveEvent<String> = SingleLiveEvent()

    private val mutableHealthJourneyItem: MutableStateFlow<State<HealthJourneyItemDetail>> =
        MutableStateFlow(Loading())
    val healthJourneyItem: StateFlow<State<HealthJourneyItemDetail>> =
        mutableHealthJourneyItem.asStateFlow()

    private val mutableVerifiableActivityProgress: MutableStateFlow<State<VerifiableActivityProgressDetail>> =
        MutableStateFlow(Uninitialized())
    val verifiableActivityProgress: StateFlow<State<VerifiableActivityProgressDetail>> =
        mutableVerifiableActivityProgress.asStateFlow()

    fun refreshHealthJourneyItem() {
        viewModelScope.launch(dispatcher) {
            healthJourneyRepository.getHealthJourneyItem(itemId = healthJourneyItemId, campaignId = campaignId, activityId = activityId)
                .collect { item ->
                    mutableHealthJourneyItem.emit(
                        when (item) {
                            is Success -> Loaded(item.response.healthJourneyItemDetail).also {
                                item.response.redirectLink?.also {
                                    redirect.postValue(it)
                                } ?: if (unsupportedActivity.value != true) {
                                    unsupportedActivity.postValue(
                                        item.response.healthJourneyItemDetail.cta.completionMethod is CompletionMethod.Unsupported)
                                }
                                if(featureFlagsUtils.getValue(HealthJourneyFeatureFlags.verifiableActivityProgress) && item.response.healthJourneyItemDetail.isVerifiableActivity() && item.response.healthJourneyItemDetail.isAutomaticMode()){
                                    refreshVerifiableActivityProgress(item.response.healthJourneyItemDetail.id)
                                }
                            }
                            is Failure -> Failed(R.string.health_journey_error)
                        }
                    )
                }
        }
    }

    private fun refreshVerifiableActivityProgress(id: String) {
       viewModelScope.launch(dispatcher) {
           mutableVerifiableActivityProgress.emit(Loading())
           healthJourneyRepository.getVerifiableActivityProgress(id)
               .collect { item ->
                   mutableVerifiableActivityProgress.emit(
                       when (item) {
                           is Success -> Loaded(item.response)
                           is Failure -> Failed(R.string.health_journey_error)
                       }
                   )
               }
        }
    }

    // availableToComplete conditions
    private val completionTimerGuard: Flow<Boolean> =
        healthJourneyItem
            .map { item -> item is Loaded && (!item().needsVerifying || delay(item().completionDisabledTimerMs ?: 0).let { true }) }
            .onStart { emit(false) }

    private val helpfulTipsComplete: Flow<Boolean> =
        healthJourneyItem
            .map { item -> item is Loaded && (!item().needsVerifying || item().helpfulTipsComplete) }
            .onStart { emit(false) }

    private val availableToComplete: Flow<Boolean> =
        if (featureFlagsUtils.getValue(HealthJourneyFeatureFlags.activityCompletionVerification)) combine(completionTimerGuard, helpfulTipsComplete) { conditions -> conditions.all { it } }
        else flow { emit(true) }

    private val mutableItemCompleteCelebration: MutableStateFlow<State<HealthJourneyItemCompletionScreen>> =
        MutableStateFlow(Uninitialized())
    val itemCompleteCelebration: StateFlow<State<HealthJourneyItemCompletionScreen>> =
        mutableItemCompleteCelebration.asStateFlow()

    //Observe / Update the activity / achievement status using this livedata
    private val mutableItemCompleteAchievement: MutableStateFlow<State<UserAchievement>> =
        MutableStateFlow(Uninitialized())
    val itemCompleteAchievement: StateFlow<State<UserAchievement>> =
        mutableItemCompleteAchievement

    val state: LiveData<HealthJourneyItemViewState> = combine(
        healthJourneyItem,
        availableToComplete,
        itemCompleteCelebration,
        itemCompleteAchievement,
        verifiableActivityProgress
    ) { healthJourneyItem, availableToComplete, itemCompleteCelebration, itemCompleteAchievement, verifiableActivityProgress->
        HealthJourneyItemViewState(
            healthJourneyItem = healthJourneyItem,
            availableToComplete = availableToComplete,
            itemCompleteCelebration = itemCompleteCelebration,
            itemCompleteAchievement = itemCompleteAchievement,
            verifiableActivityProgressDetail = verifiableActivityProgress
        )
    }.asLiveData()

    val unsupportedActivity: SingleLiveEvent<Boolean> = SingleLiveEvent()

    fun markHelpfulTipAsRead(helpfulTip: HelpfulTip) {
        viewModelScope.launch(dispatcher) {
            healthJourneyItemId?.run {
                healthJourneyRepository.setUserHealthActivityVerificationProgress(
                    userHealthActivityId = this,
                    activityVerificationProgress = HealthJourneyItemDetail.ActivityVerificationProgress(
                        helpfulTipsCompletedStatus = mapOf(helpfulTip.id to true)
                    )
                )
            }
        }
    }

    fun completeUserActivity(achievementEnabled: Boolean = featureFlagsUtils.getValue(HealthJourneyFeatureFlags.achievements)) {
        if (achievementEnabled) {
            completeActivityWithAchievement()
        } else {
            completeActivity()
        }
    }

    fun completionCelebrationShown() {
        mutableItemCompleteCelebration.value = Uninitialized()
    }

    fun completionAchievementShown() {
        mutableItemCompleteAchievement.value = Uninitialized()
    }

    private fun completeActivity() {
        mutableItemCompleteCelebration.apply {
            value = Loading()
            viewModelScope.launch(dispatcher) {
                healthJourneyItemId?.also { id ->
                    value = when (val outcome = healthJourneyRepository.completeActivity(id)) {
                        is Success -> Loaded(outcome().completionScreen)
                        is Failure -> Failed(R.string.loading_error)
                    }
                }
            }
        }
    }

    private fun completeActivityWithAchievement() {
        mutableItemCompleteAchievement.apply {
            value = Loading()
            viewModelScope.launch {
                healthJourneyItemId?.also { id ->
                    useCase?.completeActivity(id)?.collect { result ->
                        value = result
                    }
                }
            }
        }
    }

}
