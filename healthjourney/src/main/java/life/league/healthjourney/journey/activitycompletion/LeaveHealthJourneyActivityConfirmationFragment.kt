package life.league.healthjourney.journey.activitycompletion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import life.league.core.analytics.AnalyticsTracker
import life.league.core.extension.findNavControllerSafely
import life.league.genesis.compose.component.button.GenesisButtonsFooter
import life.league.genesis.compose.theme.*
import life.league.genesis.extension.setGenesisContent
import life.league.healthjourney.R
import life.league.healthjourney.analytics.trackPaginatedHealthJourneyActivityContinue
import life.league.healthjourney.analytics.trackPaginatedHealthJourneyActivityLeave
import life.league.healthjourney.journey.HealthJourney
import org.koin.android.ext.android.inject


class LeaveHealthJourneyActivityConfirmationFragment : Fragment() {

    private val args: LeaveHealthJourneyActivityConfirmationFragmentArgs by navArgs()
    private val analyticsTracker: AnalyticsTracker by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setGenesisContent {
            LeaveHealthJourneyActivityConfirmationScreen(
                imagePainter = painterResource(id = HealthJourney.configuration.drawables.exitActivityConfirmation ),
                title = stringResource(R.string.health_journey_leave_activity_confirmation_title),
                subtitle = stringResource(R.string.health_journey_leave_activity_confirmation_subtitle),
                primaryButtonText = stringResource(R.string.health_journey_leave_activity),
                secondaryButtonText = stringResource(R.string.health_journey_keep_going),
                onPrimaryButtonClick = {
                    analyticsTracker.trackPaginatedHealthJourneyActivityLeave(
                        totalSteps = args.totalSteps,
                        currentStep = args.currentStep,
                        activityId = args.activityId,
                        activityName = args.activityName,
                        activityType = args.activityType
                    )
                    findNavControllerSafely()?.navigate(LeaveHealthJourneyActivityConfirmationFragmentDirections.actionLeaveHealthJourneyActivityConfirmationFragmentPop()) },
                onSecondaryButtonClick = {
                    analyticsTracker.trackPaginatedHealthJourneyActivityContinue(
                        totalSteps = args.totalSteps,
                        currentStep = args.currentStep,
                        activityId = args.activityId,
                        activityName = args.activityName,
                        activityType = args.activityType
                    )
                    findNavControllerSafely()?.popBackStack()
                }
            )
        }
    }

}
