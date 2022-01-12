package life.league.healthjourney.settings

class NullApplicationDeeplinkHandler : ApplicationDeeplinkHandler {
    override fun handleDeeplink(url: String) {
        // noop
    }
}