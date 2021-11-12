package life.league.healthjourney.utils

import java.text.NumberFormat
import kotlin.math.roundToInt

object GoalProgressUtil {

    fun convertFloatToReadableNumber(currentProgress: Float): String {
        return NumberFormat.getIntegerInstance().format(currentProgress.roundToInt())
    }

    fun getPrettyPercentage(progressPercentage: Float): String {
        val nf = NumberFormat.getPercentInstance()
        nf.minimumFractionDigits = 0
        return nf.format(progressPercentage)
    }

    fun getProgressPercentage(currentProgress: Float, grandTotal: Float): Float {
        return if(grandTotal == 0f) 0f else (currentProgress / grandTotal)
    }
}