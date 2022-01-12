package org.covidwatch.android.presentation

import life.league.healthjourney.settings.ApplicationDeeplinkHandler

object CovidWatchDeeplinkHandler : ApplicationDeeplinkHandler {
    var deeplinkListener : ((String) -> Unit)? = {}

    override fun handleDeeplink(url: String) {
        deeplinkListener?.invoke(url)
    }
}