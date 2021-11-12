package life.league.healthjourney.utils

import android.content.Context
import android.content.res.Resources
import junit.framework.TestCase
import life.league.healthjourney.R
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`

class DataPointsUtilTest {

    @Mock
    private lateinit var contextMock: Context

    @Mock
    private lateinit var mockContextResources: Resources

    @Before
    fun setUp() {
        contextMock = Mockito.mock(Context::class.java)
        mockContextResources = Mockito.mock(Resources::class.java)
        `when`(contextMock.resources).thenReturn(mockContextResources)
        `when`(mockContextResources.getString(R.string.steps)).thenReturn("steps")
        `when`(mockContextResources.getString(R.string.move_minutes)).thenReturn("move minutes")
        `when`(mockContextResources.getString(R.string.calories_expended)).thenReturn("energy burned")
        `when`(mockContextResources.getString(R.string.and)).thenReturn(" and ")
    }

    @Test
    fun getDataPointStringResTest() {
        val dataPoints1 = listOf("steps", "active_duration")
        val dataPoints2 = listOf("steps", "active_duration", "energy_burned")
        val dataPoints3 = listOf("steps", "active_duration", "energy_burned", "some_data_point")
        val dataPoints4 = listOf("steps")

        TestCase.assertEquals(
            DataPointsUtil.getDataPointString(contextMock, dataPoints1),
            "steps and move minutes"
        )

        TestCase.assertEquals(
            DataPointsUtil.getDataPointString(contextMock, dataPoints2),
            "steps, move minutes and energy burned"
        )

        TestCase.assertEquals(
            DataPointsUtil.getDataPointString(contextMock, dataPoints3),
            "steps, move minutes, energy burned and some_data_point"
        )

        TestCase.assertEquals(
            DataPointsUtil.getDataPointString(contextMock, dataPoints4),
            "steps"
        )
    }
}