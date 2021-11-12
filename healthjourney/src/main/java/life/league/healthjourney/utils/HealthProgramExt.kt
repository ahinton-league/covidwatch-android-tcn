package life.league.healthjourney.utils

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import life.league.core.util.DateUtils
import life.league.genesis.compose.component.progress.ProgressBarConfiguration
import life.league.genesis.compose.theme.GenesisTheme
import life.league.genesis.compose.theme.squareEnd
import life.league.genesis.compose.theme.squareStart
import life.league.healthjourney.R
import life.league.healthjourney.programs.models.HealthProgram
import life.league.healthjourney.programs.models.HealthProgramDetails

fun HealthProgram.generateProgressBars(): List<ProgressBarConfiguration> =
    listOf(
        ProgressBarConfiguration(
            progress = completedActivityProgressPercentage / 100f,
            shape = { GenesisTheme.shapes.largeRoundedCorner.squareEnd() },
            color = { GenesisTheme.colors.backgroundSuccessHighlight } // Todo: colour to be confirmed
        ),
        ProgressBarConfiguration(
            progress = missedActivityProgressPercentage / 100f,
            shape = { GenesisTheme.shapes.largeRoundedCorner.squareStart() },
            color = { GenesisTheme.colors.fillPrimary } // Todo: colour to be confirmed
        )
    )

fun HealthProgramDetails.generateProgressBars(): List<ProgressBarConfiguration> =
    listOf(
        ProgressBarConfiguration(
            progress = completedActivityProgressPercentage / 100f,
            shape = { GenesisTheme.shapes.largeRoundedCorner.squareEnd() },
            color = { GenesisTheme.colors.backgroundSuccessHighlight } // Todo: colour to be confirmed
        ),
        ProgressBarConfiguration(
            progress = missedActivityProgressPercentage / 100f,
            shape = { GenesisTheme.shapes.largeRoundedCorner.squareStart() },
            color = { GenesisTheme.colors.fillPrimary } // Todo: colour to be confirmed
        )
    )


fun HealthProgram.getOverline(context: Context, verbose: Boolean = false): String =
    listOfNotNull(
        getTotalActivitiesCount(context),
        getPointsString(context, verbose)
    ).joinToString(" • ")

private fun HealthProgram.getTotalActivitiesCount(context: Context) =
    if(totalActivitiesCount > 0)
        context.resources.getQuantityString(
            R.plurals.activities,
            totalActivitiesCount,
            totalActivitiesCount
        )
    else null

private fun HealthProgram.getPointsString(context: Context, verbose: Boolean = false): String? =
    if (availablePoints > 0)
        context.resources.getQuantityString(
            if (verbose) R.plurals.points_verbose else R.plurals.points,
            availablePoints,
            availablePoints
        )
    else null

@Composable
fun HealthProgram.getCompletedActivitiesString(): String? =
    activityStatusCounts?.run {
        stringResource(id = R.string.activities_completed, "$completed/$totalActivitiesCount")
    }

@Composable
fun HealthProgram.getMissedActivitiesString(): String? =
    activityStatusCounts?.run {
        stringResource(R.string.activities_missed, expired)
    }


@Composable
fun HealthProgram.getProgressCaption(): String =
    listOfNotNull(
        getCompletedActivitiesString(),
        getMissedActivitiesString()
    ).joinToString(" • ")

fun HealthProgramDetails.getProgressCaption(context: Context): String =
    listOfNotNull(
        getCompletedActivitiesString(context),
        getMissedActivitiesString(context)
    ).joinToString(" • ")


fun HealthProgramDetails.getCompletedActivitiesString(context: Context): String? =
    activityStatusCounts?.run {
        context.getString(R.string.activities_completed, "$completed/$totalActivitiesCount")
    }

fun HealthProgramDetails.getMissedActivitiesString(context: Context): String? =
    activityStatusCounts?.run {
        context.getString(R.string.activities_missed, expired)
    }

fun HealthProgramDetails.getOverline(context: Context, verbose: Boolean = false): String =
    if (status == HealthProgramDetails.ACTIVE) {
        getProgressCaption(context)
    } else {
        listOfNotNull(
            getTotalActivitiesCount(context),
            getPointsString(context, verbose)
        ).joinToString(" • ")
    }

private fun HealthProgramDetails.getTotalActivitiesCount(context: Context) =
    if(totalActivitiesCount > 0)
        context.resources.getQuantityString(
            R.plurals.activities,
            totalActivitiesCount,
            totalActivitiesCount
        )
    else null

private fun HealthProgramDetails.getActivitiesCompletedInfo(context: Context) =
    activityStatusCounts?.run{context.resources.getString(R.string.activities_completed, "$completed/$totalActivitiesCount")}

private fun HealthProgramDetails.getDaysRemainingString(context: Context) =
    when {
        remainingDays == null -> null
        remainingDays < 7 ->
            context.resources.getQuantityString(
                R.plurals.days_remaining,
                remainingDays,
                remainingDays
            )

        remainingDays < 30 ->
            context.resources.getQuantityString(
                R.plurals.weeks_remaining,
                DateUtils.daysToWeeks(remainingDays),
                DateUtils.daysToWeeks(remainingDays)
            )

        else ->
            context.resources.getQuantityString(
                R.plurals.months_remaining,
                DateUtils.daysToMonths(remainingDays),
                DateUtils.daysToMonths(remainingDays)
            )
    }

private fun HealthProgramDetails.getPointsString(context: Context, verbose: Boolean = false): String? =
    if (availablePoints > 0)
        context.resources.getQuantityString(
            if (verbose) R.plurals.points_verbose else R.plurals.points,
            availablePoints,
            availablePoints
        )
    else null

