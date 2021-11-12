package life.league.healthjourney.programs.progress

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import life.league.core.analytics.AnalyticsTracker
import life.league.core.observable.*
import life.league.genesis.compose.component.banner.AssetBanner
import life.league.genesis.compose.component.progress.GenesisIntermittentProgressBar
import life.league.genesis.compose.component.row.ProgressRow
import life.league.genesis.compose.component.widget.GenesisEmptyStateContentWidget
import life.league.genesis.compose.theme.GenesisTheme
import life.league.genesis.compose.theme.OneVerticalSpacer
import life.league.genesis.compose.theme.ThreeVerticalSpacer
import life.league.genesis.extension.getResourceIdFromAttr
import life.league.genesis.extension.setGenesisContent
import life.league.healthjourney.R
import life.league.healthjourney.analytics.trackActiveProgramDetails
import life.league.healthjourney.analytics.trackViewProgressBannerPrompt
import life.league.healthjourney.journey.HealthJourneyFragmentDirections
import life.league.healthjourney.programs.models.HealthPrograms
import life.league.healthjourney.utils.generateProgressBars
import life.league.healthjourney.utils.getProgressCaption
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


@Composable
private fun HealthProgramsProgressScreen(
    programs: State<HealthPrograms>,
    navController: NavController,
    analyticsTracker: AnalyticsTracker
) {
    when (programs) {
        is Loading, is Uninitialized -> LoadingScreen()
        is Failed -> Unit
        is Loaded ->
            if (programs().programs.isNotEmpty())
                HealthProgramsProgressList(
                    modifier = Modifier.fillMaxSize(),
                    programs = programs(),
                    navController = navController,
                    analyticsTracker = analyticsTracker
                )
            else
                GenesisEmptyStateContentWidget(
                    modifier = Modifier.fillMaxSize(),
                    title = stringResource(id = R.string.havent_started_programs),
                    description = stringResource(
                        id = R.string.tap_add_to_browse_programs
                    )
                ) {
                    Image(
                        painter = painterResource(
                            id = LocalContext.current.getResourceIdFromAttr(
                                R.attr.drawable_health_programs_not_enrolled
                            )
                        ),
                        contentDescription = null
                    )
                }
    }
}

@Composable
private fun HealthProgramsProgressList(
    modifier: Modifier,
    programs: HealthPrograms,
    navController: NavController,
    analyticsTracker: AnalyticsTracker
) {
    LazyColumn(modifier = modifier) {
        item {
            Text(
                modifier = Modifier
                    .padding(horizontal = GenesisTheme.spacing.oneAndHalf)
                    .padding(top = GenesisTheme.spacing.twoAndHalf),
                text = stringResource(R.string.active_programs),
                style = GenesisTheme.typography.h3
            )
        }

        item {
            OneVerticalSpacer()
        }

        items(programs.programs) { program ->
            ProgressRow(
                modifier = Modifier.padding(horizontal = GenesisTheme.spacing.half),
                title = program.name,
                body = program.getProgressCaption(),
                imageUrl = program.imageUrl,
                placeholder = ColorPainter(color = GenesisTheme.colors.backgroundSecondary),
                progressBars = program.generateProgressBars(),
                onClick = {
                    analyticsTracker.trackActiveProgramDetails(
                        programName = program.name,
                        campaignId = program.id)
                    navController.navigate(HealthJourneyFragmentDirections.actionHealthJourneyFragmentToHealthProgramDetailsFragmentV2(program.id))
                }
            )
        }

        item {
            ThreeVerticalSpacer()
        }

        item {
            programs.run {
                if (numberOfAvailablePrograms == 0) {
                    ProgramLimitBanner { ctaString ->
                        analyticsTracker.trackViewProgressBannerPrompt(
                            buttonCtaText = ctaString,
                            numberOfActivePrograms = programs.programs.size,
                            programLimit = programs.programEnrollmentLimit)
                        programLimitModal?.also {
                            navController.navigate(HealthJourneyFragmentDirections.actionHealthJourneyFragmentToHealthProgramsLimitMessageDialog(it))
                        }
                    }
                } else {
                    EnrollInProgramsBanner(programs = programs) { ctaString ->
                        analyticsTracker.trackViewProgressBannerPrompt(
                            buttonCtaText = ctaString,
                            numberOfActivePrograms = programs.programs.size,
                            programLimit = programs.programEnrollmentLimit)
                        navController.navigate(HealthJourneyFragmentDirections.actionHealthJourneyFragmentToHealthProgramLibraryFragment())
                    }
                }
            }
        }

        item {
            ThreeVerticalSpacer()
        }

    }

}

@Composable
fun EnrollInProgramsBanner(programs: HealthPrograms, onClick: (String) -> Unit) {
    val resources = LocalContext.current.resources
    val ctaString = if (programs.numberOfAvailablePrograms != null)
        resources.getQuantityString(R.plurals.start_more_programs, programs.numberOfAvailablePrograms, programs.numberOfAvailablePrograms)
    else stringResource(R.string.start_health_program_unlimited)
    AssetBanner(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = GenesisTheme.spacing.oneAndHalf),
        title = ctaString,
        body = stringResource(R.string.keep_the_momentum),
        backgroundColor = GenesisTheme.colors.backgroundSecondary,
        onClick = { onClick(ctaString) },
        imageAttr = R.attr.drawable_health_programs_enrollment_available
    )
}

@Composable
fun ProgramLimitBanner(onClick: (String) -> Unit) {
    val ctaString = stringResource(id = R.string.reached_program_limit)
    AssetBanner(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = GenesisTheme.spacing.oneAndHalf),
        title = stringResource(id = R.string.reached_program_limit),
        body = stringResource(id = R.string.learn_more_lowercase),
        backgroundColor = GenesisTheme.colors.backgroundSecondary,
        onClick = { onClick(ctaString) },
        imageAttr = R.attr.drawable_health_programs_enrollment_available
    )
}

@Composable
private fun LoadingScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        GenesisIntermittentProgressBar()
    }
}


class HealthProgramsProgressFragment : Fragment() {

    private val viewModel: HealthProgramsProgressViewModel by viewModel()
    private val analyticsTracker: AnalyticsTracker by inject()
    private val navController: NavController by lazy { findNavController() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setGenesisContent {
                HealthProgramsProgressScreen(
                    programs = viewModel.programs,
                    navController = navController,
                    analyticsTracker = analyticsTracker
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getHealthPrograms()
    }

}
