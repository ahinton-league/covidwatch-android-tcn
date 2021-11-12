package life.league.healthjourney.journey.models

import androidx.annotation.StringRes


data class HealthJourneyItemSections(
        val programSections: Map<String, List<HealthJourneyItem>>,
        val completedItems: HealthJourneyItemsSection,
        val missedItems: HealthJourneyItemsSection,
) {
        fun isEmpty(): Boolean =
                (listOf(completedItems.items, missedItems.items) + programSections.values)
                        .all { it.isEmpty() }

        fun allActioned(): Boolean =
                programSections.values.all { it.isEmpty() }
                        && (!completedItems.isEmpty() || !missedItems.isEmpty())
}

data class HealthJourneyItemsSection(
        @StringRes val header: Int? = null,
        val items: List<HealthJourneyItem>,
) {
        fun isEmpty(): Boolean = items.isEmpty()
}
