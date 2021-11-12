package life.league.healthjourney.journey.activitycompletion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.accompanist.pager.ExperimentalPagerApi
import life.league.core.analytics.AnalyticsTracker
import life.league.core.extension.findNavControllerSafely
import life.league.genesis.compose.theme.GenesisTheme
import life.league.genesis.extension.setGenesisContent
import life.league.healthjourney.analytics.trackPaginatedHealthJourneyActivityClose
import life.league.healthjourney.analytics.trackPaginatedHealthJourneyActivityComplete
import life.league.healthjourney.analytics.trackPaginatedHealthJourneyActivityNextStep
import life.league.healthjourney.analytics.trackPaginatedHealthJourneyActivityPreviousStep
import org.koin.android.ext.android.inject


class MultiStepHealthJourneyItemCompletionFragment : Fragment() {

    companion object {
        const val COMPLETED_RESULT_KEY = "completed"
    }

    private val args: MultiStepHealthJourneyItemCompletionFragmentArgs by navArgs()
    private val analyticsTracker: AnalyticsTracker by inject()
    private var currentStep: Int = 1


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!args.complete) {
            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
                navigateToExitConfirmationScreen()
            }
        }
        return ComposeView(requireContext()).apply {
            setGenesisContent {
                GenesisTheme
                MultiStepHealthJourneyItemScreen(
                    modifier = Modifier.fillMaxSize(),
                    onCloseClick = {
                        analyticsTracker.trackPaginatedHealthJourneyActivityClose(
                            totalSteps = args.steps.steps.size,
                            currentStep = args.steps.steps.size,
                            activityId = args.activityId,
                            activityName = args.activityName,
                            activityType = args.activityType
                        )
                        if (!args.complete) {
                            navigateToExitConfirmationScreen()
                        } else {
                            findNavControllerSafely()?.popBackStack()
                        }
                    },
                    onDoneClick = {
                        analyticsTracker.trackPaginatedHealthJourneyActivityComplete(
                            totalSteps = args.steps.steps.size,
                            currentStep = args.steps.steps.size,
                            activityId = args.activityId,
                            activityName = args.activityName,
                            activityType = args.activityType
                        )
                        findNavControllerSafely()?.apply {
                            previousBackStackEntry?.savedStateHandle?.set(COMPLETED_RESULT_KEY, true)
                            popBackStack()
                        }
                    },
                    doneText = args.doneText,
                    steps = args.steps.steps,
                    lifecycleCoroutineScope = viewLifecycleOwner.lifecycleScope,
                    onPreviousPage = { pageIndex ->
                        analyticsTracker.trackPaginatedHealthJourneyActivityPreviousStep(
                            totalSteps = args.steps.steps.size,
                            currentStep = pageIndex + 1,
                            activityId = args.activityId,
                            activityName = args.activityName,
                            activityType = args.activityType
                        )
                    },
                    onNextPage = { pageIndex ->
                        analyticsTracker.trackPaginatedHealthJourneyActivityNextStep(
                            totalSteps = args.steps.steps.size,
                            currentStep = pageIndex + 1,
                            activityId = args.activityId,
                            activityName = args.activityName,
                            activityType = args.activityType
                        )
                    },
                    onPageChange = { page -> currentStep = page + 1 },
                    showDoneButton = true
                )
            }
        }
    }

    private fun navigateToExitConfirmationScreen() {
        findNavControllerSafely()?.navigate(MultiStepHealthJourneyItemCompletionFragmentDirections.actionMultiStepHealthJourneyItemCompletionFragmentToLeaveHealthJourneyActivityConfirmationFragment(
            totalSteps = args.steps.steps.size,
            currentStep = currentStep,
            activityId = args.activityId,
            activityName = args.activityName,
            activityType = args.activityType
        ))
    }

}
