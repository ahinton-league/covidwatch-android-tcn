package life.league.healthjourney.journey.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import life.league.core.extension.greaterThanDayAway
import life.league.core.extension.lessThanDayAway
import life.league.core.extension.lessThanMonthAway
import life.league.core.extension.lessThanWeekAway
import life.league.healthjourney.R
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class HealthActivities(
    @Json(name = "user_health_activities") val activities: List<HealthJourneyItem> = mutableListOf(),
    @Json(name = "activity_status_counts") val activityStatusCounts: StatusCounts? = null
) : Serializable {

    @Transient
    private val activityCategoryType: Comparator<HealthActivitiesCategoryHeader?> =
        compareBy { header ->
            activitiesCategorySortedOrder.indexOf(header?.titleResId)
        }

    @Transient
    private val activitiesCategorySortedOrder = arrayOf(
        R.string.health_journey_today,
        R.string.this_week,
        R.string.this_month,
        R.string.later,
        R.string.preview,
        R.string.upcoming
    )

    @Transient
    private val defaultCategorySelector: (HealthJourneyItem) -> HealthActivitiesCategoryHeader? =
        { activity: HealthJourneyItem ->
            activity.run {
                when (status) {
                    "active" -> HealthActivitiesCategoryHeader(
                        titleResId = when {
                            endDate.lessThanDayAway() -> R.string.health_journey_today
                            endDate.lessThanWeekAway() -> R.string.this_week
                            endDate.lessThanMonthAway() -> R.string.this_month
                            else -> R.string.later
                        },
                    )
                    "completed" -> HealthActivitiesCategoryHeader(
                        titleResId = when {
                            completeDate?.lessThanDayAway() == true -> R.string.health_journey_today
                            completeDate?.lessThanWeekAway() == true -> R.string.this_week
                            completeDate?.lessThanMonthAway() == true -> R.string.this_month
                            else -> R.string.later
                        },
                    )
                    "upcoming" -> HealthActivitiesCategoryHeader(
                        R.string.preview,
                        R.string.preview_description,
                    )
                    else -> null
                }
            }
        }

    val homeCategorySelector: (HealthJourneyItem) -> HealthActivitiesCategoryHeader? =
        { activity: HealthJourneyItem ->
            activity.run {
                when (status) {
                    "active" ->
                        when {
                            endDate.lessThanDayAway() -> HealthActivitiesCategoryHeader(R.string.health_journey_today)
                            endDate.greaterThanDayAway() -> HealthActivitiesCategoryHeader(R.string.upcoming)
                            else -> null
                        }
                    else -> null
                }
            }
        }

    fun List<HealthJourneyItem>.sortedActiveActivities(): List<HealthJourneyItem> =
        filter { activity -> activity.status == HealthJourneyItem.Status.ACTIVE.text }.sortedBy { it.endDate }

    fun List<HealthJourneyItem>.sortedCompletedActivities(): List<HealthJourneyItem> =
        filter { activity -> activity.status == HealthJourneyItem.Status.COMPLETED.text }.sortedBy { it.completeDate }

    fun List<HealthJourneyItem>.sortedActiveAndCompleteActivities(): List<HealthJourneyItem> =
        sortedActiveActivities() + sortedCompletedActivities()

    fun toCategories(categorySelector: (HealthJourneyItem) -> HealthActivitiesCategoryHeader? = defaultCategorySelector): List<HealthActivitiesCategory> =
        activities
            .groupBy { activity -> categorySelector(activity) }
            .toSortedMap(activityCategoryType)
            .mapNotNull { category ->
                category.key?.let { key ->
                    val categoryActivities = category.value.sortedActiveAndCompleteActivities()
                    HealthActivitiesCategory(
                        header = key,
                        activities = (when (categorySelector) {
                            homeCategorySelector -> if (key.titleResId == R.string.upcoming) categoryActivities.take(
                                2
                            ) else categoryActivities
                            else -> categoryActivities
                        })
                    )
                }
            }
}
