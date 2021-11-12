package life.league.healthjourney.settings

// Settings that can be customized by parent apps
// These should be set in onCreate in the Application Class
object HealthJourneySettings {

    var pointsSystem: PointsSystem = PointsSystem.League
    var internalBaseUrl: String = "https://app.internal.league.com"

}
