package life.league.healthjourney.utils

import android.content.Context
import life.league.core.util.DateUtils
import life.league.core.util.LocaleUtils
import life.league.healthjourney.R
import life.league.healthjourney.journey.models.HealthJourneyItem
import life.league.healthjourney.programs.models.HealthJourneyItemDetail

fun HealthJourneyItem.getCaption(context: Context): String =
    listOfNotNull(
        getDateString(context),
        getPointsString(context)
    ).joinToString(" • ")


private fun HealthJourneyItem.getPointsString(context: Context): String? =
    when {
        status == HealthJourneyItemDetail.Status.COMPLETED.text && pointsEarned > 0 ->
            context.resources.getQuantityString(
                R.plurals.you_earned_points_count,
                pointsEarned,
                pointsEarned
            )

        status != HealthJourneyItem.Status.UPCOMING.text && activityPoints > 0 ->
            context.resources.getQuantityString(
                R.plurals.earn_points_count,
                activityPoints,
                activityPoints
            )
        else -> null
    }

private fun HealthJourneyItem.getDateString(context: Context): String? =
    when (status) {
        HealthJourneyItem.Status.ACTIVE.text -> {
            if (activityExpires)
                context.getString(
                    R.string.expires_on,
                    DateUtils.formatDateMonthDay(LocaleUtils.getCurrentLocale(context), endDate)
                )
            else null
        }
        HealthJourneyItem.Status.UPCOMING.text -> context.getString(
            R.string.available_on,
            DateUtils.formatDateMonthDay(LocaleUtils.getCurrentLocale(context), startDate)
        )
        HealthJourneyItem.Status.COMPLETED.text -> context.getString(R.string.done)
        else -> null
    }

fun HealthJourneyItemDetail.getPointsString(context: Context): String? =
    when {
        status == HealthJourneyItemDetail.Status.COMPLETED.text && pointsEarned > 0 ->
            context.resources.getQuantityString(
                R.plurals.you_earned_points_count,
                pointsEarned,
                pointsEarned
            )

        activityPoints > 0 ->
            context.resources.getQuantityString(
                R.plurals.earn_points_count,
                activityPoints,
                activityPoints
            )
        else -> null
    }

