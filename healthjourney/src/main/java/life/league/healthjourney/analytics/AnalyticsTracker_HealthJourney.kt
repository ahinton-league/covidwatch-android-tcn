package life.league.healthjourney.analytics

import life.league.core.analytics.AnalyticsTracker
import life.league.healthjourney.analytics.HealthJourneyAnalytics.*
import life.league.healthjourney.settings.HealthJourneySettings

private class HealthJourneyAnalytics {

    object Categories {
        const val HEALTH_JOURNEY_ACTIVITIES = "Health Journey Activities"
        const val HEALTH_JOURNEY_ACTIVITIES_UNSUPPORTED = "Health Journey Activities Unsupported"
        const val HEALTH_JOURNEY_NAVIGATION = "Health Journey Navigation"
        const val HEALTH_GOALS = "Health Goals"
        const val HEALTH_PROGRESS = "Health Progress"
    }

    object Actions {
        // TODO(HJ) This event will removed after we remove legacy Health Programs
        const val SELECT_PROGRAM_LIBRARY_TAB = "Select Program Library Tab"
        const val SELECT_PROGRESS_TAB = "Select Progress Tab"
        const val SELECT_ACTIVITIES_TAB = "Select Activities Tab"

        const val SELECT_NAVIGATION_TAB = "Select Navigation Tab"

        const val SELECT_ACTIVITY = "Select Activity"
        const val COMPLETE_ACTIVITY = "Complete Activity"
        const val CLOSE_ACTIVITY_COMPLETE = "Close Activity Complete"
        const val REMOVE_ACTIVITY = "Remove Activity"
        const val CONFIRM_REMOVAL = "Confirm Removal"
        const val CANCEL_REMOVAL = "Cancel Removal"
        const val CLOSE_REMOVAL = "Close Removal"
        const val CLOSE_ACTIVITY_REMOVED = "Close Activity Removed Confirmation"
        const val SELECT_CONTENT = "Select Content From Activity"
        const val CLOSE_ACTIVITY_SCREEN = "Close Activity Screen"
        const val PREVIEW_UPCOMING_ACTIVITIES = "Preview Upcoming Activities"
        const val GO_TO_PROGRAM_LIBRARY = "Go to Program Library"
        const val VIEW_BANNER_PROMPT = "View Banner Prompt"
        const val VIEW_ACTIVE_PROGRAM_DETAILS = "View Active Program Details"
        const val SELECT_PCO_PROMPT = "Select PCO Prompt"

        const val NEXT_STEP = "Next Step"
        const val PREVIOUS_STEP = "Previous Step"
        const val CLOSE_ACTIVITY = "Close Activity"
        const val LEAVE_ACTIVITY = "Leave Activity"
        const val CONTINUE_ACTIVITY = "Continue Activity"
        const val VIEW_ACTIVITY = "View Activity"
        const val NAVIGATE_TO_APP_STORE = "Navigate to app store"
        const val DISMISS = "Dismiss"
    }

    object Labels {
        const val HEALTH_JOURNEY_ACTIVITY = "%1\$s - %2\$s"
        const val PROGRAM_LIBRARY_CTA = "%1\$s"
        const val PROGRESS_BANNER_PROMPT_CTA = "%1\$s"
        const val PROGRAM_DETAILS_NAME = "%1\$s"
    }

    object Pages {
        const val HEALTH_JOURNEY_ACTIVITIES = "Health Journey Activities"

        const val HEALTH_JOURNEY_ACTIVITIES_PREVIEW = "Health Journey Activities - Preview"
        const val HEALTH_JOURNEY_PROGRESS_EMPTY_STATE = "Health Journey Progress - Empty State"
        const val HEALTH_JOURNEY_PROGRESS_ACTIVE_PROGRAMS = "Health Journey Progress - Active Programs"

        const val HEALTH_JOURNEY_ACTIVITY_COMPLETED = "Health Journey Activity Completed"
        const val HEALTH_JOURNEY_ACTIVITY_REMOVED = "Health Journey Activity Removed"
        const val HEALTH_JOURNEY_UNSUPPORTED_ACTIVITY = "Health Journey Activities Unsupported - %1\$s"
    }

}

private fun paginatedActivityEventParameters(
    activityId: String,
    totalSteps: Int,
    currentStep: Int
) = mapOf(
    "activity_id" to activityId,
    "total_steps" to totalSteps,
    "current_step" to currentStep,
    "rewards_eligibility_status" to HealthJourneySettings.pointsSystem.eligibility.text
)

fun AnalyticsTracker.trackPaginatedHealthJourneyActivityNextStep(
    activityType: String? = null,
    activityName: String? = null,
    activityId: String,
    totalSteps: Int,
    currentStep: Int
) {
    trackEvent(
        category = Categories.HEALTH_JOURNEY_ACTIVITIES,
        action = Actions.NEXT_STEP,
        label = Labels.HEALTH_JOURNEY_ACTIVITY.format(activityType, activityName),
        value = null,
        parameters = paginatedActivityEventParameters(activityId, totalSteps, currentStep)

    )
}

fun AnalyticsTracker.trackPaginatedHealthJourneyActivityPreviousStep(
    activityType: String? = null,
    activityName: String? = null,
    activityId: String,
    totalSteps: Int,
    currentStep: Int
) {
    trackEvent(
        category = Categories.HEALTH_JOURNEY_ACTIVITIES,
        action = Actions.PREVIOUS_STEP,
        label = Labels.HEALTH_JOURNEY_ACTIVITY.format(activityType, activityName),
        value = null,
        parameters = paginatedActivityEventParameters(activityId, totalSteps, currentStep)
    )
}

fun AnalyticsTracker.trackPaginatedHealthJourneyActivityComplete(
    activityType: String? = null,
    activityName: String? = null,
    activityId: String,
    totalSteps: Int,
    currentStep: Int
) {
    trackEvent(
        category = Categories.HEALTH_JOURNEY_ACTIVITIES,
        action = Actions.COMPLETE_ACTIVITY,
        label = Labels.HEALTH_JOURNEY_ACTIVITY.format(activityType, activityName),
        value = null,
        parameters = paginatedActivityEventParameters(activityId, totalSteps, currentStep)
    )
}

fun AnalyticsTracker.trackPaginatedHealthJourneyActivityClose(
    activityType: String? = null,
    activityName: String? = null,
    activityId: String,
    totalSteps: Int,
    currentStep: Int
) {
    trackEvent(
        category = Categories.HEALTH_JOURNEY_ACTIVITIES,
        action = Actions.CLOSE_ACTIVITY,
        label = Labels.HEALTH_JOURNEY_ACTIVITY.format(activityType, activityName),
        value = null,
        parameters = paginatedActivityEventParameters(activityId, totalSteps, currentStep)
    )
}

fun AnalyticsTracker.trackPaginatedHealthJourneyActivityLeave(
    activityType: String? = null,
    activityName: String? = null,
    activityId: String,
    totalSteps: Int,
    currentStep: Int
) {
    trackEvent(
        category = Categories.HEALTH_JOURNEY_ACTIVITIES,
        action = Actions.LEAVE_ACTIVITY,
        label = Labels.HEALTH_JOURNEY_ACTIVITY.format(activityType, activityName),
        value = null,
        parameters = paginatedActivityEventParameters(activityId, totalSteps, currentStep)
    )
}

fun AnalyticsTracker.trackPaginatedHealthJourneyActivityContinue(
    activityType: String? = null,
    activityName: String? = null,
    activityId: String,
    totalSteps: Int,
    currentStep: Int
) {
    trackEvent(
        category = Categories.HEALTH_JOURNEY_ACTIVITIES,
        action = Actions.CONTINUE_ACTIVITY,
        label = Labels.HEALTH_JOURNEY_ACTIVITY.format(activityType, activityName),
        value = null,
        parameters = paginatedActivityEventParameters(activityId, totalSteps, currentStep)
    )
}

private fun unsupportedActivityEventParameters(activityId: String) =
    mapOf(
        "activity_id" to activityId,
        "rewards_eligibility_status" to HealthJourneySettings.pointsSystem.eligibility.text
    )

fun AnalyticsTracker.trackUnsupportedActivityNavigateToPlayStore(
    activityType: String? = null,
    activityName: String? = null,
    activityId: String,
) {
    trackEvent(
        category = Categories.HEALTH_JOURNEY_ACTIVITIES_UNSUPPORTED,
        action = Actions.NAVIGATE_TO_APP_STORE,
        label = Labels.HEALTH_JOURNEY_ACTIVITY.format(activityType, activityName),
        value = null,
        parameters = unsupportedActivityEventParameters(activityId)
    )
}

fun AnalyticsTracker.trackUnsupportedActivityDismiss(
    activityType: String? = null,
    activityName: String? = null,
    activityId: String,
) {
    trackEvent(
        category = Categories.HEALTH_JOURNEY_ACTIVITIES_UNSUPPORTED,
        action = Actions.DISMISS,
        label = Labels.HEALTH_JOURNEY_ACTIVITY.format(activityType, activityName),
        value = null,
        parameters = unsupportedActivityEventParameters(activityId)
    )
}

fun AnalyticsTracker.trackUnsupportedActivityScreenView(
    activityName: String? = null,
) {
    viewScreen(Pages.HEALTH_JOURNEY_UNSUPPORTED_ACTIVITY.format(activityName))
}

private fun AnalyticsTracker.trackHealthJourneyActivitiesEvent(category: String = Categories.HEALTH_JOURNEY_ACTIVITIES, action: String, activityType: String? = null, activityName: String? = null, parameters: Map<String, Any?>? = null) {
    trackEvent(
        category = category,
        action = action,
        label = HealthJourneyAnalytics.Labels.HEALTH_JOURNEY_ACTIVITY.format(activityType, activityName),
        value = null,
        parameters = parameters)
}

private fun AnalyticsTracker.trackHealthJourneyProgressEvent(category: String = Categories.HEALTH_JOURNEY_ACTIVITIES, action: String, label: String? = null, parameters: Map<String, Any?>? = null) {
    trackEvent(category, action, label, null, parameters)
}

fun AnalyticsTracker.viewHealthJourneyActivities() {
    viewScreen(HealthJourneyAnalytics.Pages.HEALTH_JOURNEY_ACTIVITIES)
}

fun AnalyticsTracker.viewHealthJourneyActivitiesPreview() {
    viewScreen(HealthJourneyAnalytics.Pages.HEALTH_JOURNEY_ACTIVITIES_PREVIEW)
}

fun AnalyticsTracker.viewHealthJourneyProgressEmptyState() {
    viewScreen(HealthJourneyAnalytics.Pages.HEALTH_JOURNEY_PROGRESS_EMPTY_STATE)
}

fun AnalyticsTracker.viewHealthJourneyProgressActivePrograms() {
    viewScreen(HealthJourneyAnalytics.Pages.HEALTH_JOURNEY_PROGRESS_ACTIVE_PROGRAMS)
}

fun AnalyticsTracker.viewHealthJourneyActivityCompleted() {
    viewScreen(HealthJourneyAnalytics.Pages.HEALTH_JOURNEY_ACTIVITY_COMPLETED)
}

fun AnalyticsTracker.viewHealthJourneyActivityRemoved() {
    viewScreen(HealthJourneyAnalytics.Pages.HEALTH_JOURNEY_ACTIVITY_REMOVED)
}

fun AnalyticsTracker.trackSignUpForPcoPromptSelected(cta: String, activityId: String, activityType: String, activityName: String) {
    trackEvent(
        category = Categories.HEALTH_JOURNEY_ACTIVITIES,
        action = HealthJourneyAnalytics.Actions.SELECT_PCO_PROMPT,
        label = Labels.HEALTH_JOURNEY_ACTIVITY.format(activityType, activityName),
        parameters = mapOf(
            "cta" to cta,
            "activity_id" to activityId,
            "rewards_eligibility_status" to HealthJourneySettings.pointsSystem.eligibility.text
        )
    )
}

fun AnalyticsTracker.trackSelectProgressTab() {
    trackEvent(
        category = HealthJourneyAnalytics.Categories.HEALTH_JOURNEY_NAVIGATION,
        action = HealthJourneyAnalytics.Actions.SELECT_NAVIGATION_TAB,
        label = "progress",
        value = null,
        parameters = mapOf(
            "rewards_eligibility_status" to HealthJourneySettings.pointsSystem.eligibility.text
        )
    )
}

fun AnalyticsTracker.trackSelectActivitiesTab() {
    trackEvent(
        category = HealthJourneyAnalytics.Categories.HEALTH_JOURNEY_NAVIGATION,
        action = HealthJourneyAnalytics.Actions.SELECT_NAVIGATION_TAB,
        label = "activities",
        value = null,
        parameters = mapOf(
            "rewards_eligibility_status" to HealthJourneySettings.pointsSystem.eligibility.text
        )
    )
}


fun AnalyticsTracker.trackGoToProgramLibrary(buttonCtaText: String) {
    val params = mapOf(
        "rewards_eligibility_status" to HealthJourneySettings.pointsSystem.eligibility.text
    )
    trackHealthJourneyProgressEvent(
        action = HealthJourneyAnalytics.Actions.GO_TO_PROGRAM_LIBRARY,
        label = Labels.PROGRAM_LIBRARY_CTA.format(buttonCtaText),
        parameters = params
    )
}

fun AnalyticsTracker.trackViewProgressBannerPrompt(buttonCtaText: String, numberOfActivePrograms: Int, programLimit: Int) {
    val params = mapOf(
        "num_program_active" to numberOfActivePrograms,
        "num_program_limit" to programLimit,
        "rewards_eligibility_status" to HealthJourneySettings.pointsSystem.eligibility.text
    )
    trackHealthJourneyProgressEvent(
        action = HealthJourneyAnalytics.Actions.VIEW_BANNER_PROMPT,
        label = Labels.PROGRESS_BANNER_PROMPT_CTA.format(buttonCtaText),
        parameters = params
    )
}

fun AnalyticsTracker.trackActiveProgramDetails(programName: String, campaignId: String) {
    val params = mapOf(
        "campaign_id" to campaignId,
        "rewards_eligibility_status" to HealthJourneySettings.pointsSystem.eligibility.text
    )
    trackHealthJourneyProgressEvent(
        action = HealthJourneyAnalytics.Actions.VIEW_ACTIVE_PROGRAM_DETAILS,
        label = Labels.PROGRAM_DETAILS_NAME.format(programName),
        parameters = params
    )
}

fun AnalyticsTracker.trackPreviewUpcomingActivities() {
    val params = mapOf(
        "rewards_eligibility_status" to HealthJourneySettings.pointsSystem.eligibility.text
    )
    trackHealthJourneyActivitiesEvent(
        action = HealthJourneyAnalytics.Actions.PREVIEW_UPCOMING_ACTIVITIES,
        parameters = params)
}

fun AnalyticsTracker.trackActivitySelection(activityType: String, activityName: String, activityId: String) {
    val params = mapOf(
            "activity_id" to activityId,
            "rewards_eligibility_status" to HealthJourneySettings.pointsSystem.eligibility.text
    )
    trackHealthJourneyActivitiesEvent(
            action = HealthJourneyAnalytics.Actions.SELECT_ACTIVITY,
            activityType = activityType,
            activityName = activityName,
            parameters = params)
}

fun AnalyticsTracker.trackActivityComplete(activityType: String, activityName: String, cta: String, activityId: String) {
    val params = mapOf(
            "cta" to cta,
            "activity_id" to activityId,
            "rewards_eligibility_status" to HealthJourneySettings.pointsSystem.eligibility.text
    )
    trackHealthJourneyActivitiesEvent(
            action = HealthJourneyAnalytics.Actions.COMPLETE_ACTIVITY,
            activityType = activityType,
            activityName = activityName,
            parameters = params)
}

fun AnalyticsTracker.trackCloseActivityComplete(activityType: String, activityName: String, activityId: String) {
    val params = mapOf(
            "activity_id" to activityId,
            "rewards_eligibility_status" to HealthJourneySettings.pointsSystem.eligibility.text
    )
    trackHealthJourneyActivitiesEvent(
            action = HealthJourneyAnalytics.Actions.CLOSE_ACTIVITY_COMPLETE,
            activityType = activityType,
            activityName = activityName,
            parameters = params)
}

fun AnalyticsTracker.trackActivityRemove(activityType: String, activityName: String, activityId: String) {
    val params = mapOf(
            "activity_id" to activityId,
            "rewards_eligibility_status" to HealthJourneySettings.pointsSystem.eligibility.text
    )
    trackHealthJourneyActivitiesEvent(
            action = HealthJourneyAnalytics.Actions.REMOVE_ACTIVITY,
            activityType = activityType,
            activityName = activityName,
            parameters = params)
}

fun AnalyticsTracker.trackConfirmRemoval(activityType: String, activityName: String, activityId: String) {
    val params = mapOf(
            "activity_id" to activityId,
            "rewards_eligibility_status" to HealthJourneySettings.pointsSystem.eligibility.text
    )
    trackHealthJourneyActivitiesEvent(
            action = HealthJourneyAnalytics.Actions.CONFIRM_REMOVAL,
            activityType = activityType,
            activityName = activityName,
            parameters = params)
}

fun AnalyticsTracker.trackCancelRemoval(activityType: String, activityName: String, activityId: String) {
    val params = mapOf(
            "activity_id" to activityId,
            "rewards_eligibility_status" to HealthJourneySettings.pointsSystem.eligibility.text
    )
    trackHealthJourneyActivitiesEvent(
            action = HealthJourneyAnalytics.Actions.CANCEL_REMOVAL,
            activityType = activityType,
            activityName = activityName,
            parameters = params)
}

fun AnalyticsTracker.trackCloseRemoval(activityType: String, activityName: String, activityId: String) {
    val params = mapOf(
            "activity_id" to activityId,
            "rewards_eligibility_status" to HealthJourneySettings.pointsSystem.eligibility.text
    )
    trackHealthJourneyActivitiesEvent(
            action = HealthJourneyAnalytics.Actions.CLOSE_REMOVAL,
            activityType = activityType,
            activityName = activityName,
            parameters = params)
}

fun AnalyticsTracker.trackCloseActivityRemoved(activityType: String, activityName: String, activityId: String) {
    val params = mapOf(
            "activity_id" to activityId,
            "rewards_eligibility_status" to HealthJourneySettings.pointsSystem.eligibility.text
    )
    trackHealthJourneyActivitiesEvent(
            action = HealthJourneyAnalytics.Actions.CLOSE_ACTIVITY_REMOVED,
            activityType = activityType,
            activityName = activityName,
            parameters = params)
}

fun AnalyticsTracker.trackSelectContentFromActivity(activityType: String, activityName: String, activityId: String, contentType: String, contentUrl: String, linkName: String) {
    val params = mapOf(
            "activity_id" to activityId,
            "content_type" to contentType,
            "content_url" to contentUrl,
            "link_name" to linkName,
            "rewards_eligibility_status" to HealthJourneySettings.pointsSystem.eligibility.text
    )
    trackHealthJourneyActivitiesEvent(
            action = HealthJourneyAnalytics.Actions.SELECT_CONTENT,
            activityType = activityType,
            activityName = activityName,
            parameters = params)
}

fun AnalyticsTracker.trackCloseActivityScreen(activityType: String, activityName: String, activityId: String) {
    val params = mapOf(
            "activity_id" to activityId,
            "rewards_eligibility_status" to HealthJourneySettings.pointsSystem.eligibility.text
    )
    trackHealthJourneyActivitiesEvent(
            action = HealthJourneyAnalytics.Actions.CLOSE_ACTIVITY_SCREEN,
            activityType = activityType,
            activityName = activityName,
            parameters = params)
}
