package life.league.healthjourney.utils

import life.league.core.extension.toCalendar
import java.util.*

fun Date.addToDayOfYear(numberOfDays: Int): Date =
    toCalendar().apply { add(Calendar.DAY_OF_YEAR, numberOfDays) }.time