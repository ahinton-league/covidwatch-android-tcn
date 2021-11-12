package life.league.healthjourney.analytics

import life.league.core.analytics.AnalyticsTracker
import life.league.healthjourney.settings.HealthJourneySettings

private class HealthConnectAppsAndDevicesAnalytics {
    object Pages {
        const val PROGRAM_CONNECT_APPS_AND_DEVICES = "Health Program - Connect Apps and Devices"
    }

    object Categories {
        const val PROGRAM_CONNECT_APPS_AND_DEVICES = "Health Program - Connect Apps and Devices"
    }

    object Actions {
        const val CONNECT_APPS_AND_DEVICES = "Connect Apps and Devices"
        const val SKIP_CONNECT_APPS_AND_DEVICES = "Skip Connect Apps and Devices"
    }

    object Parameters {
        const val CAMPAIGN_ID = "campaign_id"
        const val REWARDS_ELIGIBILITY_STATUS = "rewards_eligibility_status"
    }
}

fun AnalyticsTracker.viewAppsAndDevicesSettings(campaignId: String) {
    viewScreen(
        HealthConnectAppsAndDevicesAnalytics.Pages.PROGRAM_CONNECT_APPS_AND_DEVICES,
        parameters = mapOf(
            HealthConnectAppsAndDevicesAnalytics.Parameters.CAMPAIGN_ID to campaignId,
            HealthConnectAppsAndDevicesAnalytics.Parameters.REWARDS_ELIGIBILITY_STATUS to HealthJourneySettings.pointsSystem.eligibility.text
        )
    )
}

fun AnalyticsTracker.trackConnectAppsAndDevices(campaignId: String) {
    trackEvent(
        HealthConnectAppsAndDevicesAnalytics.Categories.PROGRAM_CONNECT_APPS_AND_DEVICES,
        HealthConnectAppsAndDevicesAnalytics.Actions.CONNECT_APPS_AND_DEVICES,
        parameters = mapOf(
            HealthConnectAppsAndDevicesAnalytics.Parameters.CAMPAIGN_ID to campaignId,
            HealthConnectAppsAndDevicesAnalytics.Parameters.REWARDS_ELIGIBILITY_STATUS to HealthJourneySettings.pointsSystem.eligibility.text
        )
    )
}

fun AnalyticsTracker.trackSkipConnectAppsAndDevices(campaignId: String) {
    trackEvent(
        HealthConnectAppsAndDevicesAnalytics.Categories.PROGRAM_CONNECT_APPS_AND_DEVICES,
        HealthConnectAppsAndDevicesAnalytics.Actions.SKIP_CONNECT_APPS_AND_DEVICES,
        parameters = mapOf(
            HealthConnectAppsAndDevicesAnalytics.Parameters.CAMPAIGN_ID to campaignId,
            HealthConnectAppsAndDevicesAnalytics.Parameters.REWARDS_ELIGIBILITY_STATUS to HealthJourneySettings.pointsSystem.eligibility.text
        )
    )
}