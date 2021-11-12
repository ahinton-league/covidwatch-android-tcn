package life.league.healthjourney.models

import io.mockk.every
import io.mockk.mockkStatic
import life.league.healthjourney.R
import life.league.healthjourney.journey.models.HealthActivities
import life.league.healthjourney.journey.models.HealthActivitiesCategory
import life.league.healthjourney.journey.models.HealthActivitiesCategoryHeader
import life.league.healthjourney.journey.models.HealthJourneyItem
import org.joda.time.Duration
import org.joda.time.Instant
import org.joda.time.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class HealthActivitiesTest {

    val mockEarlierThisWeek = LocalDateTime(2019, 12, 31, 12, 0, 0).toDate()
    val mockEarlierToday = LocalDateTime(2020, 1, 1, 11, 0, 0).toDate()
    val mockNow = LocalDateTime(2020, 1, 1, 12, 0, 0).toDate()
    val mockNowPlusADay = LocalDateTime(2020, 1, 2, 12, 0, 0).toDate()
    val mockLaterToday = LocalDateTime(2020, 1, 1, 13, 0, 0).toDate()
    val mockLaterThisWeek = LocalDateTime(2020, 1, 3, 12, 0, 0).toDate()
    val mockLaterThisMonth = LocalDateTime(2020, 1, 15, 12, 0, 0).toDate()
    val mockLaterThisYear = LocalDateTime(2020, 6, 15, 12, 0, 0).toDate()

    @Before
    fun setUp() {
        mockkStatic(Instant::class)

        every { Instant.now().toDate() } returns mockNow
        every { Instant.now().plus(Duration.standardDays(1)).toDate() } returns mockNowPlusADay
    }

    @Test
    fun `toCategories(homeCategorySelector) - empty activities`() {
        val healthActivities = with(HealthActivities(listOf())) {
            toCategories(homeCategorySelector)
        }

        assertEquals(healthActivities.size, 0)
    }

    @Test
    fun `toCategories(homeCategorySelector) - 3 activities today and 3 upcoming activities`() {
        val todayHealthJourneyItems = listOf(
            HealthJourneyItem(status = "active", endDate = mockEarlierThisWeek),
            HealthJourneyItem(status = "active", endDate = mockEarlierToday),
            HealthJourneyItem(status = "active", endDate = mockNow),
            HealthJourneyItem(status = "active", endDate = mockLaterToday),
        )
        val upcomingHealthJourneyItems = listOf(
            HealthJourneyItem(status = "active", endDate = mockLaterThisWeek),
            HealthJourneyItem(status = "active", endDate = mockLaterThisMonth),
            HealthJourneyItem(status = "active", endDate = mockLaterThisYear),
        )
        val healthActivities =
            with(HealthActivities(todayHealthJourneyItems + upcomingHealthJourneyItems)) {
                toCategories(homeCategorySelector)
            }

        assertEquals(
            listOf(
                HealthActivitiesCategory(
                    HealthActivitiesCategoryHeader(titleResId = R.string.health_journey_today),
                    activities = todayHealthJourneyItems
                ),
                HealthActivitiesCategory(
                    HealthActivitiesCategoryHeader(titleResId = R.string.upcoming),
                    activities = upcomingHealthJourneyItems.take(2)
                )
            ),
            healthActivities
        )
    }

    @Test
    fun `toCategories(homeCategorySelector) - no activities today and 1 upcoming activity`() {
        val upcomingHealthJourneyItems = listOf(
            HealthJourneyItem(status = "active", endDate = mockLaterThisWeek),
        )
        val healthActivities = with(HealthActivities(upcomingHealthJourneyItems)) {
            toCategories(homeCategorySelector)
        }

        assertEquals(
            listOf(
                HealthActivitiesCategory(
                    HealthActivitiesCategoryHeader(titleResId = R.string.upcoming),
                    activities = upcomingHealthJourneyItems
                )
            ),
            healthActivities
        )
    }

    @Test
    fun `toCategories(homeCategorySelector) - 1 activity today and no upcoming activities`() {
        val todayHealthJourneyItems = listOf(
            HealthJourneyItem(status = "active", endDate = mockEarlierToday),
        )
        val healthActivities = with(HealthActivities(todayHealthJourneyItems)) {
            toCategories(homeCategorySelector)
        }

        assertEquals(
            listOf(
                HealthActivitiesCategory(
                    HealthActivitiesCategoryHeader(titleResId = R.string.health_journey_today),
                    activities = todayHealthJourneyItems
                )
            ),
            healthActivities
        )
    }

    @Test
    fun `toCategories(homeCategorySelector) - 1 inactive activity`() {
        val inactiveHealthJourneyItems = listOf(
            HealthJourneyItem(status = "inactive", endDate = mockEarlierToday),
        )
        val healthActivities = with(HealthActivities(inactiveHealthJourneyItems)) {
            toCategories(homeCategorySelector)
        }

        assertEquals(0, healthActivities.size)
    }
}