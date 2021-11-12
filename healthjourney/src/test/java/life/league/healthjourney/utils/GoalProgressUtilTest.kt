package life.league.healthjourney.utils

import life.league.healthjourney.utils.GoalProgressUtil.convertFloatToReadableNumber
import life.league.healthjourney.utils.GoalProgressUtil.getPrettyPercentage
import life.league.healthjourney.utils.GoalProgressUtil.getProgressPercentage
import org.junit.Assert
import org.junit.Test

class GoalProgressUtilTest {

    @Test
    fun testConvertFloatToReadableNumber() {
        Assert.assertEquals(convertFloatToReadableNumber(800f), "800")
        Assert.assertEquals(convertFloatToReadableNumber(8000f), "8,000")
        Assert.assertEquals(convertFloatToReadableNumber(0f), "0")
    }

    @Test
    fun testGetProgressPercentage() {
        Assert.assertEquals(getProgressPercentage(200f, 500f), 0.4f)
        Assert.assertEquals(getProgressPercentage(8000f, 10000f), 0.8f)
        Assert.assertEquals(getProgressPercentage(0f, 0f), 0f)
        Assert.assertEquals(getProgressPercentage(10f, 0f), 0f)
    }

    @Test
    fun testGetPrettyPercentage() {
        Assert.assertEquals(getPrettyPercentage(0.4f), "40%")
        Assert.assertEquals(getPrettyPercentage(0.888f), "89%")
        Assert.assertEquals(getPrettyPercentage(0.834f), "83%")
        Assert.assertEquals(getPrettyPercentage(0f), "0%")
        Assert.assertEquals(getPrettyPercentage(1f), "100%")
    }
}