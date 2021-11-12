package life.league.healthjourney.journey.timeline.day

import life.league.healthjourney.journey.models.HealthJourneyItemSections

data class HealthJourneyDayViewData(
    val sections: HealthJourneyItemSections,
    val programsAvailable: Boolean,
    val isPast: Boolean,
    val isFuture: Boolean,
)