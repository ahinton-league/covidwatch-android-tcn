package life.league.healthjourney.journey.models

import com.squareup.moshi.Json
import life.league.healthjourney.R
import life.league.healthjourney.journey.timeline.day.HealthJourneyDayViewData
import life.league.healthjourney.programs.models.HealthJourneyItemDetail
import java.util.*

data class HealthJourneyItemsResponse(
    @Json(name = "user_health_activities") val items: List<HealthJourneyItem> = emptyList(),
    @Json(name = "number_of_enrolled_programs") val numberOfEnrolledPrograms: Int = 0,
    @Json(name = "max_programs_limit") val maximumNumberOfPrograms: Int = 0,
) {

    fun programsAvailable(): Boolean = numberOfEnrolledPrograms < maximumNumberOfPrograms

    fun programSections() =
        items
            .filter { it.status == HealthJourneyItem.Status.ACTIVE.text }
            .groupBy { item -> item.campaignInfo?.name.orEmpty() }

    fun createCompleteSection() =
        HealthJourneyItemsSection(
            header = R.string.completed,
            items = items.filter { it.status == HealthJourneyItem.Status.COMPLETED.text }
        )

    fun createMissedSection() =
        HealthJourneyItemsSection(
            header = R.string.missed,
            items = items.filter {
                it.status == HealthJourneyItem.Status.REMOVED.text
                        || it.status == HealthJourneyItem.Status.EXPIRED.text
            }
        )
}
