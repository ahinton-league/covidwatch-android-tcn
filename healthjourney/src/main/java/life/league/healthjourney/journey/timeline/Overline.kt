package life.league.healthjourney.journey.timeline

import androidx.annotation.StringRes
import life.league.healthjourney.R

enum class Overline(@StringRes val stringRes: Int) {
    YESTERDAY(R.string.core_yesterday),
    TODAY(R.string.core_today),
    TOMORROW(R.string.core_tomorrow)
}
