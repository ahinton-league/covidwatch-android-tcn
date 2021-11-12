package life.league.healthjourney.journey

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import life.league.core.analytics.AnalyticsTracker
import life.league.genesis.compose.component.button.GenesisButtonsFooter
import life.league.genesis.compose.theme.*
import life.league.healthjourney.R
import life.league.healthjourney.analytics.trackUnsupportedActivityDismiss
import life.league.healthjourney.analytics.trackUnsupportedActivityNavigateToPlayStore
import life.league.healthjourney.analytics.trackUnsupportedActivityScreenView
import life.league.healthjourney.journey.activitycompletion.MultiStepHealthJourneyItemCompletionFragmentArgs
import org.koin.android.ext.android.inject

class HealthJourneyItemCompletionTypeUnsupportedBottomSheetDialog : BottomSheetDialogFragment() {

    private val args: HealthJourneyItemCompletionTypeUnsupportedBottomSheetDialogArgs by navArgs()
    private val analyticsTracker: AnalyticsTracker by inject()

    override fun onResume() {
        super.onResume()
        analyticsTracker.trackUnsupportedActivityScreenView(
            activityName = args.activityName,
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        ComposeView(requireContext()).apply {
            setContent {
                GenesisTheme {
                    Surface(modifier = Modifier
                        .clip(
                            RoundedCornerShape(
                                topStart = GenesisTheme.shapes.extraLargeSize,
                                topEnd = GenesisTheme.shapes.extraLargeSize
                            )
                        )
                        .fillMaxWidth(),
                        shape = RoundedCornerShape(topStart = GenesisTheme.shapes.extraLargeSize, topEnd = GenesisTheme.shapes.extraLargeSize)) {
                        Column(
                            modifier = Modifier
                                .clip(
                                    RoundedCornerShape(
                                        topStart = GenesisTheme.shapes.extraLargeSize,
                                        topEnd = GenesisTheme.shapes.extraLargeSize
                                    )
                                )
                                .fillMaxWidth()
                        ) {

                            OneVerticalSpacer()
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                IconButton(onClick = { dismiss() }, modifier = Modifier.padding(end = GenesisTheme.spacing.one)) {
                                    Icon(painter = painterResource(id = R.drawable.ic_navigation_close), contentDescription = getString(R.string.close))
                                }
                            }

                            OneAndHalfVerticalSpacer()

                            Text(modifier = Modifier
                                .padding(horizontal = GenesisTheme.spacing.oneAndHalf),
                                text = context.getString(R.string.health_journey_update_app_to_continue), style = GenesisTheme.typography.h3)

                            HalfVerticalSpacer()

                            Text(modifier = Modifier
                                .padding(horizontal = GenesisTheme.spacing.oneAndHalf),
                                text = context.getString(R.string.health_journey_incompatible_app_version), style = GenesisTheme.typography.body1)

                            FourVerticalSpacer()

                            GenesisButtonsFooter(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = GenesisTheme.spacing.one),
                                primaryButtonText = stringResource(R.string.health_journey_go_to_play_store),
                                secondaryButtonText = stringResource(R.string.health_journey_maybe_later),
                                onPrimaryButtonClick = {
                                    analyticsTracker.trackUnsupportedActivityNavigateToPlayStore(
                                        activityId = args.activityId,
                                        activityName = args.activityName,
                                        activityType = args.activityType
                                    )
                                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${context.applicationContext.packageName}")))
                                },
                                onSecondaryButtonClick = {
                                    analyticsTracker.trackUnsupportedActivityDismiss(
                                        activityId = args.activityId,
                                        activityName = args.activityName,
                                        activityType = args.activityType
                                    )
                                    dismiss()
                                }
                            )

                        }
                    }

                }

            }
        }
}
