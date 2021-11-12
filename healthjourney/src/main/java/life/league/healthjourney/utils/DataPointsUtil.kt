package life.league.healthjourney.utils

import android.content.Context
import life.league.healthjourney.R
import java.lang.StringBuilder

object DataPointsUtil {
    fun getDataPointString(context: Context, dataPoints: List<String>): String {

        val description = StringBuilder()
        dataPoints.forEachIndexed { index, dataPoint ->
            description.append(
                when (dataPoint) {
                    "steps" -> context.resources.getString(R.string.steps)
                    "active_duration" -> context.resources.getString(R.string.move_minutes)
                    "energy_burned" -> context.resources.getString(R.string.calories_expended)
                    else -> dataPoint
                }
            )
            if (index < dataPoints.size - 2) {
                description.append(", ")
            } else if (index == dataPoints.size - 2) {
                description.append(context.resources.getString(R.string.and))
            }
        }
        return description.toString()
    }
}