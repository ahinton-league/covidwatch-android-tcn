package life.league.healthjourney.journey

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navOptions
import life.league.core.analytics.AnalyticsTracker
import life.league.core.base.RootFragment
import life.league.core.extension.navigateSafely
import life.league.core.extension.popBackStackOrFinish
import life.league.core.navigation.NavigationTarget
import life.league.core.observable.Failed
import life.league.core.observable.Loaded
import life.league.core.observable.Loading
import life.league.core.util.DateUtils
import life.league.core.util.RatingUtils
import life.league.core.util.featureflags.FeatureFlagsUtils
import life.league.genesis.extension.isNotNullOrEmpty
import life.league.genesis.widget.button.Button
import life.league.healthjourney.R
import life.league.healthjourney.analytics.*
import life.league.healthjourney.databinding.HealthJourneyItemFragmentBinding
import life.league.healthjourney.journey.activitycompletion.MultiStepHealthJourneyItemCompletionFragment.Companion.COMPLETED_RESULT_KEY
import life.league.healthjourney.journey.models.CompletionMethod
import life.league.healthjourney.journey.models.HealthJourneyItemCompletionScreen
import life.league.healthjourney.journey.models.HelpfulTip
import life.league.healthjourney.programs.models.HealthJourneyItemDetail
import life.league.healthjourney.journey.models.VerifiableActivityProgressDetail
import life.league.healthjourney.utils.DataPointsUtil
import life.league.healthjourney.utils.GoalProgressUtil
import life.league.healthjourney.utils.getPointsString
import life.league.rewards.dialogs.ComposableSheetContent
import life.league.rewards.dialogs.RewardsComposableBottomSheetFragment
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.util.*


class HealthJourneyItemFragment : RootFragment() {

    private lateinit var binding: HealthJourneyItemFragmentBinding
    private val args: HealthJourneyItemFragmentArgs by navArgs()
    private val viewModel: HealthJourneyItemViewModel by viewModel {
        parametersOf(args.healthJourneyItemId, args.campaignId, args.activityId)
    }
    private var onCtaClicked: (HealthJourneyItemCompletionScreen?) -> Unit = {}
    private val analyticsTracker: AnalyticsTracker by inject()
    private val featureFlagsUtils: FeatureFlagsUtils by inject()

    private lateinit var controller: HealthJourneyItemController

    private var onUnsupportedActivity: () -> Unit = {}


    private val navigateToPointsSystemSignUp: (HealthJourneyItemDetail) -> Unit = {
        analyticsTracker.trackSignUpForPcoPromptSelected(
            cta = getString(R.string.missing_out_on_points),
            activityId = it.id,
            activityName = it.name,
            activityType = it.type
        )
        rootActivity?.handleNavigation(
            NavigationTarget(
                NavigationTarget.POINTS_SYSTEM_SIGN_UP,
                bundleOf()
            ), false
        )
    }

    private val navigateToUnsupportedActivityNotice: (HealthJourneyItemDetail) -> Unit = {
        findNavControllerSafely()
            ?.navigateSafely(HealthJourneyItemFragmentDirections.actionHealthJourneyActivityFragmentToHealthJourneyItemCompletionTypeUnsupportedBottomSheetDialog(
                activityType = it.type,
                activityName = it.name,
                activityId = it.id,
            ))
    }

    private val navigateToHelpfulTip: (HelpfulTip) -> Unit = { tip ->
        viewModel.markHelpfulTipAsRead(tip)

        rootActivity?.navigateToDeeplink(
            tip.url,
            openBrowserIfUnsupported = true,
            setRootNavigation = false,
            navController = findNavController()
        )
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshHealthJourneyItem()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        HealthJourneyItemFragmentBinding.inflate(inflater, container, false).apply {
            binding = this
            disabledFooter.setOnClickListener { /* to prevent clicks passing through this view */ }
            controller = HealthJourneyItemController(
                context = requireContext(),
                analyticsTracker = analyticsTracker,
                lifecycleCoroutineScope = lifecycleScope,
                navigateToPointsSystemSignUp = navigateToPointsSystemSignUp,
                navigateToUnsupportedActivity = navigateToUnsupportedActivityNotice,
                navigateToHelpfulTip = navigateToHelpfulTip,
                featureFlagsUtils = featureFlagsUtils)
            recyclerView.setController(controller)
        }.root

    private fun confirmRemoveHealthJourneyItem(healthJourneyItem: HealthJourneyItemDetail) {
        analyticsTracker.trackActivityRemove(
            healthJourneyItem.type,
            healthJourneyItem.name,
            healthJourneyItem.id
        )
        findNavControllerSafely()
            ?.navigateSafely(
                HealthJourneyItemFragmentDirections
                    .actionHealthJourneyActivityFragmentToRemoveHealthJourneyItemConfirmationBottomSheetDialog(
                        healthJourneyItem.toHealthJourneyItem()
                    )
            )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.redirect.observe(viewLifecycleOwner, { redirect ->
            if(redirect.isNotBlank()){
                rootActivity?.navigateToDeeplink(
                    redirect,
                    openBrowserIfUnsupported = true,
                    setRootNavigation = false,
                    navController = findNavController(),
                    navOptions = navOptions {
                        popUpTo(R.id.healthJourneyActivityFragment){ inclusive = true}
                    }
                )
            }
        })

        viewModel.state.observe(viewLifecycleOwner, { state ->

            controller.state = state

            when (val healthJourneyItem = state.healthJourneyItem) {
                is Loaded -> {

                    binding.configureToolbar(healthJourneyItem())
                    binding.configurePageHeader(healthJourneyItem())
                    onUnsupportedActivity = {
                        findNavControllerSafely()
                            ?.navigateSafely(HealthJourneyItemFragmentDirections.actionHealthJourneyActivityFragmentToHealthJourneyItemCompletionTypeUnsupportedBottomSheetDialog(
                                activityType = healthJourneyItem().type,
                                activityName = healthJourneyItem().name,
                                activityId = healthJourneyItem().id,
                            ))
                    }

                    binding.removeActivity.setOnClickListener { confirmRemoveHealthJourneyItem(healthJourneyItem()) }

                    when (healthJourneyItem().status) {
                        HealthJourneyItemDetail.Status.COMPLETED.text -> binding.configureCompletedHealthJourneyItem(healthJourneyItem())
                        HealthJourneyItemDetail.Status.UPCOMING.text -> binding.configureUpcomingHealthJourneyItem(healthJourneyItem())
                        HealthJourneyItemDetail.Status.ACTIVE.text -> binding.configureActiveHealthJourneyItem(healthJourneyItem(), state.availableToComplete)
                    }
                }
                else -> Unit
            }

            when (val itemCompleteCelebration = state.itemCompleteCelebration) {
                is Loaded -> {
                    onCtaClicked(itemCompleteCelebration())
                    binding.completeActivity.setLoadingStateOff()
                    binding.removeActivity.setButtonStyle(Button.BUTTON_STYLE_MINIMAL)
                    viewModel.completionCelebrationShown()
                }
                is Loading -> {
                    binding.completeActivity.setLoadingStateOn()
                    binding.removeActivity.setButtonStyle(Button.BUTTON_STYLE_DISABLED)
                }
                is Failed -> {
                    binding.completeActivity.setLoadingStateOff()
                    binding.removeActivity.setButtonStyle(Button.BUTTON_STYLE_MINIMAL)
                    Toast.makeText(
                        context,
                        itemCompleteCelebration.getErrorMessage(requireContext()),
                        Toast.LENGTH_LONG
                    ).show()
                    viewModel.completionCelebrationShown()
                }
            }


            when (val itemCompletionAchievement = state.itemCompleteAchievement) {
                is Loaded -> {
                    binding.completeActivity.setLoadingStateOff()
                    binding.removeActivity.setButtonStyle(Button.BUTTON_STYLE_MINIMAL)

                    //Close Detail View before showing bottom sheet
                    findNavControllerSafely()?.popBackStack()

                    //Show Activity Celebration Toast View
                    RewardsComposableBottomSheetFragment
                        .Builder()
                        .setView(ComposableSheetContent.ActivityCompletionView(itemCompletionAchievement()))
                        .setNavController(findNavControllerSafely())
                        .show(parentFragmentManager)
                    viewModel.completionAchievementShown()
                }
                is Loading -> {
                    binding.completeActivity.setLoadingStateOn()
                    binding.removeActivity.setButtonStyle(Button.BUTTON_STYLE_DISABLED)
                }
                is Failed -> {
                    binding.completeActivity.setLoadingStateOff()
                    binding.removeActivity.setButtonStyle(Button.BUTTON_STYLE_MINIMAL)
                    Toast.makeText(
                        context,
                        itemCompletionAchievement.getErrorMessage(requireContext()),
                        Toast.LENGTH_LONG
                    ).show()
                    viewModel.completionAchievementShown()
                }
            }


            when (val verifiableActivityProgress = state.verifiableActivityProgressDetail) {
                is Loaded ->
                    binding.setVerifiableActivityProgressDataState(verifiableActivityProgress.data)
                is Loading ->
                    binding.setVerifiableActivityProgressLoadingState()
                else -> Unit
            }
        })

        viewModel.unsupportedActivity.observe(viewLifecycleOwner, { unsupported ->
            if (unsupported) { onUnsupportedActivity() }
        })

    }

    private fun navigateToMultiStepHealthJourneyItem(completionMethod: CompletionMethod.MultiStep, healthJourneyItem: HealthJourneyItemDetail) {
        findNavControllerSafely()
            ?.navigateSafely(HealthJourneyItemFragmentDirections.actionHealthJourneyActivityFragmentToMultiStepHealthJourneyItemCompletionFragment(
                steps = completionMethod,
                doneText = if (healthJourneyItem.isComplete) getString(R.string.close) else healthJourneyItem.cta.text,
                activityType = healthJourneyItem.type,
                activityName = healthJourneyItem.name,
                activityId = healthJourneyItem.id,
                complete = healthJourneyItem.isComplete
            ))
    }

    private fun HealthJourneyItemFragmentBinding.configureCompletedHealthJourneyItem(healthJourneyItem: HealthJourneyItemDetail) {
        completeActivity.apply {
            isVisible = true
            setButtonStyle(Button.BUTTON_STYLE_PRIMARY)

            val cta = healthJourneyItem.cta
            when (cta.completionMethod) {
                is CompletionMethod.MultiStep -> {
                    setText(getString(R.string.view_activity))
                    setOnClick { navigateToMultiStepHealthJourneyItem(cta.completionMethod, healthJourneyItem) }
                }
                else -> {
                    setText(getString(R.string.close))
                    setOnClick { findNavControllerSafely()?.popBackStack() }
                }
            }
        }

        removeActivity.isVisible = false
        disabledFooter.isVisible = false
    }

    private fun HealthJourneyItemFragmentBinding.configureUpcomingHealthJourneyItem(healthJourneyItem: HealthJourneyItemDetail) {
        completeActivity.isVisible = false
        removeActivity.isVisible = false
        disabledFooter.apply {
            isVisible = true
            setDescriptionText(
                resources.getQuantityString(
                    R.plurals.come_back_in,
                    DateUtils.dayDiff(Date(), healthJourneyItem.startDate) + 1,
                    DateUtils.dayDiff(Date(), healthJourneyItem.startDate) + 1
                )
            )
        }
    }

    private fun HealthJourneyItemFragmentBinding.configureActiveHealthJourneyItem(healthJourneyItem: HealthJourneyItemDetail, availableToComplete: Boolean) = with(healthJourneyItem) {
        disabledFooter.isVisible = false
        removeActivity.isVisible = true

        completeActivity.apply {
            setButtonStyle(if (availableToComplete) Button.BUTTON_STYLE_PRIMARY else Button.BUTTON_STYLE_DISABLED)
            when (cta.completionMethod) {
                is CompletionMethod.Base -> setText(cta.completionMethod.text)
                is CompletionMethod.MultiStep -> setText(cta.completionMethod.text)
                CompletionMethod.Unsupported -> Unit
            }
        }
        onCtaClicked = { healthJourneyItemCompletionScreen ->
            if (cta.url.isNotNullOrEmpty()) {
                rootActivity?.navigateToDeeplink(
                    cta.url,
                    true,
                    false,
                    navController = findNavController(),
                    navOptions = getNavOptions(this)
                )
            } else {
                healthJourneyItemCompletionScreen?.also {
                    findNavControllerSafely()
                        ?.navigateSafely(
                            HealthJourneyItemFragmentDirections
                                .actionHealthJourneyActivityFragmentToHealthJourneyItemCompleteBottomSheetDialog(
                                    healthJourneyItemCompletionScreen,
                                    this.toHealthJourneyItem()
                                )
                        )
                } ?: findNavController().popBackStack()
            }
        }
        when (cta.completionMethod) {
            is CompletionMethod.Base -> {
                completeActivity.apply {
                    setOnClick { view ->
                        //TODO: Put RatingUtils.incrementNumHealthJourneyActivitiesCompleted() in the view model
                        //  and test that the call is made after RatingUtils is refactored to use SharedPreferencesUtils
                        //  and context is no longer required as a parameter
                        RatingUtils.incrementNumHealthJourneyActivitiesCompleted(view.context)
                        analyticsTracker.trackActivityComplete(
                            healthJourneyItem.type,
                            healthJourneyItem.name,
                            cta.text,
                            healthJourneyItem.id
                        )
                        if (cta.markAsComplete) viewModel.completeUserActivity() else onCtaClicked(null)
                    }
                    isVisible = !(isVerifiableActivity() && isAutomaticMode())
                }
            }
            is CompletionMethod.MultiStep -> {
                findNavControllerSafely()?.apply {
                    currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>(
                        COMPLETED_RESULT_KEY)?.observe(viewLifecycleOwner) { result ->
                        if (result) {
                            if (cta.markAsComplete) viewModel.completeUserActivity() else onCtaClicked(null)
                        }
                        currentBackStackEntry?.savedStateHandle?.remove<Boolean>(COMPLETED_RESULT_KEY)
                    }
                }
                completeActivity.apply {
                    isVisible = true
                    setOnClick { navigateToMultiStepHealthJourneyItem(cta.completionMethod, healthJourneyItem) }
                    isVisible = !(isVerifiableActivity() && isAutomaticMode())
                }
            }
            CompletionMethod.Unsupported -> {
                completeActivity.isVisible = false
            }
        }
        if (!availableToComplete) {
            completeActivity.setOnClick(null)
        }
    }

    private fun HealthJourneyItemFragmentBinding.configureToolbar(healthJourneyItem: HealthJourneyItemDetail) = with(healthJourneyItem) {
        toolbar.setNavigationOnClickListener {
            analyticsTracker.trackCloseActivityScreen(type, name, id)
            findNavControllerSafely()?.popBackStackOrFinish(requireActivity())
        }
    }

    private fun HealthJourneyItemFragmentBinding.configurePageHeader(healthJourneyItem: HealthJourneyItemDetail) = with(healthJourneyItem) {
        pageHeader.apply {
            setHeaderText(name)
            setOverline(tagline)
            setDescriptionText(getPointsString(context) ?: "")
            setBadgeImageUrl(iconUrl)
            setSwoopColorAttrRes(R.attr.color_background_page_swoop_primary)
        }
    }

    private fun getNavOptions(hjItem: HealthJourneyItemDetail): NavOptions? =
        if (hjItem.isModuleActivity()) NavOptions.Builder()
            .setPopUpTo(R.id.healthJourneyActivityFragment, true).build()
        else null

    private fun HealthJourneyItemFragmentBinding.setVerifiableActivityProgressDataState(verifiableActivityProgressDetail: VerifiableActivityProgressDetail) {
        goalProgress.apply {
            visibility = View.VISIBLE
            modifier = Modifier.fillMaxWidth()
            dataPoint = DataPointsUtil.getDataPointString(requireContext(), listOf(verifiableActivityProgressDetail.dataType))
            unit = verifiableActivityProgressDetail.getGoalUnit()
            currentProgress = verifiableActivityProgressDetail.currentProgress
            maxProgress = verifiableActivityProgressDetail.grandTotal
            percentage = GoalProgressUtil.getProgressPercentage(currentProgress, maxProgress)
            animationDuration = 2000
            isLoading = false
        }
    }

    private fun HealthJourneyItemFragmentBinding.setVerifiableActivityProgressLoadingState() {
        goalProgress.apply {
            visibility = View.VISIBLE
            modifier = Modifier.fillMaxWidth()
            percentage = 0f
            currentProgress = 0f
            maxProgress = 0f
            isLoading = true
        }
    }

}
