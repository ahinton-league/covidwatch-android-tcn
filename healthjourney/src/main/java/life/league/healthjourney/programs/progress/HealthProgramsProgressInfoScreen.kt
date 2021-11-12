package life.league.healthjourney.programs.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import life.league.core.analytics.AnalyticsTracker
import life.league.core.observable.*
import life.league.genesis.compose.component.progress.GenesisCenteredIntermittentProgressBar
import life.league.genesis.compose.component.row.ProgressRow
import life.league.genesis.compose.component.widget.GenesisEmptySectionView
import life.league.genesis.compose.theme.*
import life.league.healthjourney.R
import life.league.healthjourney.analytics.trackActiveProgramDetails
import life.league.healthjourney.analytics.trackViewProgressBannerPrompt
import life.league.healthjourney.journey.HealthJourneyFragmentDirections
import life.league.healthjourney.programs.models.HealthPrograms
import life.league.healthjourney.programs.models.HealthProgramsProgressInfo
import life.league.healthjourney.utils.generateProgressBars
import life.league.healthjourney.utils.getProgressCaption
import life.league.rewards.components.MilestoneTracker
import life.league.rewards.components.RecentlyEarnedAchievements
import life.league.rewards.utils.navigateToHealthJourneyPrograms

@Composable
fun HealthProgramsProgressInfoScreen(
    healthProgramsInfo: State<HealthProgramsProgressInfo>,
    navController: NavController,
    analyticsTracker: AnalyticsTracker
) {
    when (healthProgramsInfo) {
        is Loading, is Uninitialized -> GenesisCenteredIntermittentProgressBar()
        is Failed -> Unit
        is Loaded -> RenderScreen(healthProgramsInfo.data, navController, analyticsTracker)
    }
}

@Composable
fun RenderScreen(
    healthProgramsProgressInfo: HealthProgramsProgressInfo,
    navController: NavController,
    analyticsTracker: AnalyticsTracker
) {

    LazyColumn {

        item {
            //Display the Milestone Component
            MilestoneTracker(
                modifier = Modifier.fillMaxWidth(),
                milestoneData = healthProgramsProgressInfo.milestoneTrackerResult
            )
        }

        item {
            RenderHealthPrograms(healthProgramsProgressInfo.healthPrograms, navController, analyticsTracker)
        }

        item {
            //Display Recently Earned Achievements
            HalfVerticalSpacer()
            Spacer(modifier = Modifier.fillMaxWidth().height(8.dp).background(GenesisTheme.colors.dividerPrimary))
            RecentlyEarnedAchievements(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = GenesisTheme.spacing.two,
                        bottom = GenesisTheme.spacing.two,
                        start = GenesisTheme.spacing.oneAndHalf,
                        end = GenesisTheme.spacing.oneAndHalf
                    ),
                achievements = healthProgramsProgressInfo.recentAchievements,
                navController = navController
            )
        }
    }
}

@Composable
fun RenderHealthPrograms(
    healthPrograms: HealthPrograms,
    navController: NavController,
    analyticsTracker: AnalyticsTracker
) {
    when {
        healthPrograms.programs.isNotEmpty() -> {
            HealthProgramsProgressList(
                modifier = Modifier.fillMaxSize(),
                programs = healthPrograms,
                navController = navController,
                analyticsTracker = analyticsTracker
            )
        }
        else -> {
            GenesisEmptySectionView(modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = GenesisTheme.spacing.two,
                    bottom = GenesisTheme.spacing.two,
                    start = GenesisTheme.spacing.oneAndHalf,
                    end = GenesisTheme.spacing.oneAndHalf
                ),
                sectionHeader = stringResource(id = R.string.active_programs),
                title = stringResource(R.string.health_journey_no_active_health_programs),
                description = stringResource(R.string.health_journey_add_program_to_track),
                ctaText = stringResource(R.string.health_journey_view_programs),
                ctaAction = { navController.navigateToHealthJourneyPrograms() })
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
    Column(modifier = modifier) {
        Text(
            modifier = Modifier
                .padding(horizontal = GenesisTheme.spacing.oneAndHalf)
                .padding(top = GenesisTheme.spacing.twoAndHalf),
            text = stringResource(R.string.active_programs),
            style = GenesisTheme.typography.h3
        )

        OneVerticalSpacer()

        for(program in programs.programs) {
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

        ThreeVerticalSpacer()

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

        TwoVerticalSpacer()
    }

}
