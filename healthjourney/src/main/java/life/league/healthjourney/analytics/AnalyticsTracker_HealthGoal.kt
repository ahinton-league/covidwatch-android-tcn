package life.league.healthjourney.analytics

import life.league.core.analytics.AnalyticsTracker
import life.league.healthjourney.settings.HealthJourneySettings

private class HealthGoalAnalytics {

    object Categories {
        const val HEALTH_GOALS = "Health Goals"
        const val HEALTH_PROGRAM_LIBRARY = "Health Program Library"
        const val ACTIVE_HEALTH_PROGRAM_DETAILS = "Active Health Program Details - %1\$s"
        const val HEALTH_PROGRAM_DETAILS = "Health Program Details - %1\$s"

    }

    object Actions {
        const val SELECT_PROGRAM = "Select a Program"
        const val ENROLL_IN_PROGRAM = "Enroll in Program"
        const val MARK_GOAL_COMPLETE = "Mark Goal as Complete"
        const val MARK_GOAL_SKIPPED = "Mark Goal as Skipped"
        const val CHAT_WITH_CARE_TEAM = "Chat with Care Team"
        const val VIEW_FILTERED_CATEGORY = "View Filtered Category"
        const val VIEW_HELPFUL_TIP_PROGRAM = "View Helpful Tip From Program"
        const val VIEW_HELPFUL_TIP_PROGRESS = "View Helpful Tip from Progress"
        const val VIEW_HELPFUL_TIP_GOAL = "View Helpful Tip From Goal"
        const val VIEW_CURRENT_PROGRAM_DETAILS = "View Current Program Details"
        const val LEAVE_PROGRAM = "Leave Program"
        const val VIEW_ALL = "View All"
        const val SELECT_RECOMMENDED_HEALTH_PROGRAM = "Select Recommended Health Program"
        const val VIEW_MODAL = "View Modal - %s"
        const val VIEW_THEME = "View Theme"
        const val START_PROGRAM_MODULE = "Open Start Program Modal"
        const val VIEW_CURRENT_PROGRAM_PROGRESS = "View Current Program Progress"
        const val VIEW_GOAL = "View Goal"
        const val CHAT_WITH_CARE_TEAM_HEALTH_GOALS = "Chat with Care Team in Health Goals"
        const val SELECT_PCO_PROMPT = "Select PCO Prompt"
        const val VIEW_INFO_MODAL = "View Info Modal"
        const val VIEW_BANNER_PROMPT = "View Banner Prompt"
        const val SUBMIT_FEEDBACK = "Submit Feedback"
        const val SKIP_FEEDBACK = "Skip Feedback"
    }

    object Labels {
        const val HEALTH_PROGRAMS = "Health Programs"

        const val PROGRAM_NAME = "%1\$s - %2\$s"
    }

    object Parameters {
        const val CAMPAIGN_ID = "campaign_id"
        const val REWARDS_ELIGIBILITY_STATUS = "rewards_eligibility_status"
    }


    object Pages {
        const val HEALTH_ARTICLES = "Articles"
        const val HEALTH_PROGRAMS = "Health Programs"
        const val HEALTH_PROGRAMS_DETAILS = "Health Programs Details"
        const val HEALTH_PROGRAMS_CATEGORY_FILTER = "Health Programs Category Filter"
        const val HEALTH_PROGRAMS_ENROLLMENT = "Health Programs Enrollment"
        const val HEALTH_PROGRAMS_GOAL_DETAILS = "Health Programs Goal Details"
        const val HEALTH_REWARDS = "Health Rewards"
        const val HEALTH_GOAL_COMPLETED = "Health Goal Completed"
        const val HEALTH_PROGRAM_COMPLETED = "Health Program Completed"
        const val HEALTH_PROGRAM_WEEKLY_GOALS = "Health Programs Weekly Goals"
        const val HEALTH_PROGRAM_PROGRESS = "Health Program Progress"
        const val HEALTH_PROGRAM_LIBRARY = "Health Program Library"
        const val HEALTH_PROGRAM_LIBRARY_CATEGORY = "Health Program Library Category - %1\$s"
        const val HEALTH_PROGRAM_DETAILS = "Health Program Details - %1\$s"
        const val HEALTH_PROGRAM_LIMIT_REACHED = "Health Program Limit Reached"
        const val ACTIVE_HEALTH_PROGRAM_DETAILS = "Active Health Program Details - %1\$s"
    }
}

fun AnalyticsTracker.trackSelectPcoPrompt(programName: String, programId: String) {
    trackHealthGoalEvent(
            action = HealthGoalAnalytics.Actions.SELECT_PCO_PROMPT,
            label = programName,
            parameters = mapOf("program_id" to programId))
}

fun AnalyticsTracker.trackViewAllHealthPrograms() {
    trackHealthGoalEvent(HealthGoalAnalytics.Actions.VIEW_ALL, HealthGoalAnalytics.Labels.HEALTH_PROGRAMS)
}

fun AnalyticsTracker.trackRecommendedHealthProgram(programName: String?, programId: String, carouselName: String, carouselIndex: Int) {
    val params = mapOf(
                "program_id" to programId,
                "carousel_name" to carouselName,
                "carousel_recommendation_rank" to carouselIndex,
                "rewards_eligibility_status" to HealthJourneySettings.pointsSystem.eligibility.text)

    trackHealthGoalEvent(HealthGoalAnalytics.Actions.SELECT_RECOMMENDED_HEALTH_PROGRAM, programName, params)
}

fun AnalyticsTracker.trackRecommendedHealthProgramFromLibrary(programName: String,
                                                              programId: String,
                                                              carouselName: String,
                                                              carouselIndex: Int,
                                                              numberOfActivePrograms: Int?,
                                                              programLimit: Int?
) {
    trackEvent(
        category = HealthGoalAnalytics.Categories.HEALTH_PROGRAM_LIBRARY,
        action = HealthGoalAnalytics.Actions.SELECT_RECOMMENDED_HEALTH_PROGRAM,
        label = programName,
        parameters = mapOf(
            "campaign_id" to programId,
            "carousel_name" to carouselName,
            "carousel_recommendation_rank" to carouselIndex,
            "num_program_active" to numberOfActivePrograms,
            "num_program_limit" to programLimit,
            "rewards_eligibility_status" to HealthJourneySettings.pointsSystem.eligibility.text)
    )
}

fun AnalyticsTracker.trackViewBannerPrompt(bannerCta: String,
                                           numberOfActivePrograms: Int?,
                                           programLimit: Int?) {
    trackEvent(
        category = HealthGoalAnalytics.Categories.HEALTH_PROGRAM_LIBRARY,
        action = HealthGoalAnalytics.Actions.VIEW_BANNER_PROMPT,
        label = bannerCta,
        parameters = mapOf(
            "num_program_active" to numberOfActivePrograms,
            "num_program_limit" to programLimit,
            "rewards_eligibility_status" to HealthJourneySettings.pointsSystem.eligibility.text)
    )
}

fun AnalyticsTracker.trackViewInfoModal(programId: String, programName: String, heading: String) {
    trackEvent(
        category = HealthGoalAnalytics.Categories.HEALTH_PROGRAM_DETAILS.format(programName),
        action = HealthGoalAnalytics.Actions.VIEW_INFO_MODAL,
        label = heading,
        parameters = mapOf(
            "campaign_id" to programId,
            "rewards_eligibility_status" to HealthJourneySettings.pointsSystem.eligibility.text)
    )
}

fun AnalyticsTracker.trackHealthProgramClickFromLibrary(programName: String,
                                                              programId: String,
                                                              carouselName: String,
                                                              carouselIndex: Int,
                                                              numberOfActivePrograms: Int?,
                                                              programLimit: Int?
) {
    trackEvent(
        category = HealthGoalAnalytics.Categories.HEALTH_PROGRAM_LIBRARY,
        action = HealthGoalAnalytics.Actions.SELECT_PROGRAM,
        label = programName,
        parameters = mapOf(
            "campaign_id" to programId,
            "carousel_name" to carouselName,
            "carousel_recommendation_rank" to carouselIndex,
            "num_program_active" to numberOfActivePrograms,
            "num_program_limit" to programLimit,
            "rewards_eligibility_status" to HealthJourneySettings.pointsSystem.eligibility.text)
    )
}

private fun AnalyticsTracker.trackHealthGoalEvent(action: String, label: String? = null, parameters: Map<String, Any?>? = null) {
    trackEvent(HealthGoalAnalytics.Categories.HEALTH_GOALS, action, label, null, parameters)
}

fun AnalyticsTracker.trackProgramSelection(programName: String?, programId: String, carouselName: String, carouselIndex: Int) {
    val params = mapOf(
                "program_id" to programId,
                "carousel_name" to carouselName,
                "carousel_index" to carouselIndex,
                "rewards_eligibility_status" to HealthJourneySettings.pointsSystem.eligibility.text
        )

    trackHealthGoalEvent(HealthGoalAnalytics.Actions.SELECT_PROGRAM, programName, params)
}

fun AnalyticsTracker.trackEnrollInProgram(programId: String, programName: String?) {
    val params = mapOf(
                "program_id" to programId,
                "rewards_eligibility_status" to HealthJourneySettings.pointsSystem.eligibility.text)

    trackHealthGoalEvent(HealthGoalAnalytics.Actions.ENROLL_IN_PROGRAM, programName, params)
}

fun AnalyticsTracker.trackMarkGoalAsComplete(goalName: String, programName: String, programId: String) {
    val params = mapOf(
                "program_id" to programId,
                "rewards_eligibility_status" to HealthJourneySettings.pointsSystem.eligibility.text)

    trackHealthGoalEvent(HealthGoalAnalytics.Actions.MARK_GOAL_COMPLETE, "$programName - $goalName", params)
}

fun AnalyticsTracker.trackMarkGoalAsSkipped(goalName: String, programName: String, programId: String) {
    val params = mapOf(
                "program_id" to programId,
                "rewards_eligibility_status" to HealthJourneySettings.pointsSystem.eligibility.text)

    trackHealthGoalEvent(HealthGoalAnalytics.Actions.MARK_GOAL_SKIPPED, "$programName - $goalName", params)
}

fun AnalyticsTracker.trackCareTeamBanner(cardTitle: String = "") {
    trackHealthGoalEvent(HealthGoalAnalytics.Actions.CHAT_WITH_CARE_TEAM, cardTitle,
                mapOf("rewards_eligibility_status" to HealthJourneySettings.pointsSystem.eligibility.text))

}

fun AnalyticsTracker.trackViewFilteredCategory(category: String?,
                                               numberOfActivePrograms: Int?,
                                               programLimit: Int?) {
    trackEvent(
        category = HealthGoalAnalytics.Categories.HEALTH_PROGRAM_LIBRARY,
        action = HealthGoalAnalytics.Actions.VIEW_FILTERED_CATEGORY,
        label = category,
        parameters =  mapOf(
            "num_program_active" to numberOfActivePrograms,
            "num_program_limit" to programLimit,
            "rewards_eligibility_status" to HealthJourneySettings.pointsSystem.eligibility.text)
    )
}

fun AnalyticsTracker.trackViewFilteredHealthProgramCategory(category: String?) {
    trackHealthGoalEvent(HealthGoalAnalytics.Actions.VIEW_FILTERED_CATEGORY, category,
                mapOf("rewards_eligibility_status" to HealthJourneySettings.pointsSystem.eligibility.text))
}

fun AnalyticsTracker.trackViewHelpfulTipFromProgram(programId: String, programName: String, contentName: String, url: String?) {
    val params = mapOf(
                "program_id" to programId,
                "content_url" to url,
                "rewards_eligibility_status" to HealthJourneySettings.pointsSystem.eligibility.text
        )
    trackHealthGoalEvent(HealthGoalAnalytics.Actions.VIEW_HELPFUL_TIP_PROGRAM, "$programName - $contentName", params)
}

fun AnalyticsTracker.trackViewHelpfulTipFromProgress(programId: String, programName: String, contentName: String, url: String?) {
    val params = mapOf(
                "program_id" to programId,
                "content_url" to url,
                "rewards_eligibility_status" to HealthJourneySettings.pointsSystem.eligibility.text)

    trackHealthGoalEvent(HealthGoalAnalytics.Actions.VIEW_HELPFUL_TIP_PROGRESS, "$programName - $contentName", params)
}

fun AnalyticsTracker.trackViewHelpfulTipFromGoal(programId: String, goalName: String, contentName: String, url: String?) {
    val params = mapOf(
                    "program_id" to programId,
                    "content_url" to url,
                    "rewards_eligibility_status" to HealthJourneySettings.pointsSystem.eligibility.text)

    trackHealthGoalEvent(HealthGoalAnalytics.Actions.VIEW_HELPFUL_TIP_GOAL, "$goalName - $contentName", params)
}

fun AnalyticsTracker.trackCurrentProgramDetails(programId: String, programName: String?) {
    val params = mapOf(
                "program_id" to programId,
                "rewards_eligibility_status" to HealthJourneySettings.pointsSystem.eligibility.text)

    trackHealthGoalEvent(HealthGoalAnalytics.Actions.VIEW_CURRENT_PROGRAM_DETAILS, programName, params)
}

fun AnalyticsTracker.trackLeaveProgram(programId: String, programName: String?) {
    val params = mapOf(
                "program_id" to programId,
                "rewards_eligibility_status" to HealthJourneySettings.pointsSystem.eligibility.text)

    trackHealthGoalEvent(HealthGoalAnalytics.Actions.LEAVE_PROGRAM, programName, params)
}


fun AnalyticsTracker.trackLeaveProgram(buttonCta: String,
                                       programId: String,
                                       programName: String) {
    trackEvent(
        category = HealthGoalAnalytics.Categories.ACTIVE_HEALTH_PROGRAM_DETAILS.format(programName),
        action = HealthGoalAnalytics.Actions.LEAVE_PROGRAM,
        label = buttonCta,
        parameters =  mapOf(
            "campaign_id" to programId,
            "rewards_eligibility_status" to HealthJourneySettings.pointsSystem.eligibility.text)
    )
}

fun AnalyticsTracker.trackEnrollInProgram(buttonCta: String,
                                       programId: String,
                                       programName: String) {
    trackEvent(
        category = HealthGoalAnalytics.Categories.HEALTH_PROGRAM_DETAILS.format(programName),
        action = HealthGoalAnalytics.Actions.ENROLL_IN_PROGRAM,
        label = buttonCta,
        parameters =  mapOf(
            "campaign_id" to programId,
            "rewards_eligibility_status" to HealthJourneySettings.pointsSystem.eligibility.text)
    )
}

fun AnalyticsTracker.trackFinePrint(header: String) {
    val params = mapOf("rewards_eligibility_status" to HealthJourneySettings.pointsSystem.eligibility.text)
    trackHealthGoalEvent(String.format(HealthGoalAnalytics.Actions.VIEW_MODAL, header),
            parameters = params)
}

fun AnalyticsTracker.trackViewTheme(programName: String, weekNum: Int, programId: String) {
    val params = mapOf(
                "program_id" to programId,
                "rewards_eligibility_status" to HealthJourneySettings.pointsSystem.eligibility.text)

    trackHealthGoalEvent(HealthGoalAnalytics.Actions.VIEW_THEME, "$programName - week $weekNum", params)
}

fun AnalyticsTracker.trackStartProgramModule(programId: String, programName: String?) {
    val params = mapOf(
                "program_id" to programId,
                "rewards_eligibility_status" to HealthJourneySettings.pointsSystem.eligibility.text)

    trackHealthGoalEvent(HealthGoalAnalytics.Actions.START_PROGRAM_MODULE, programName, params)
}

fun AnalyticsTracker.trackCurrentProgramProgress(programId: String, programName: String) {
    val params = mapOf(
                "program_id" to programId,
                "rewards_eligibility_status" to HealthJourneySettings.pointsSystem.eligibility.text)

    trackHealthGoalEvent(HealthGoalAnalytics.Actions.VIEW_CURRENT_PROGRAM_PROGRESS, programName, params)
}

fun AnalyticsTracker.trackLeagueInfoModal(programId: String, programName: String, header: String) {
    val params = mapOf("program_id" to programId,
                "rewards_eligibility_status" to HealthJourneySettings.pointsSystem.eligibility.text)
    trackHealthGoalEvent(String.format(HealthGoalAnalytics.Actions.VIEW_MODAL, header),
            programName, params)
}

fun AnalyticsTracker.trackViewGoal(programId: String?, programName: String?, goalName: String?, isTodaysGoal: Boolean) {
    val params = mapOf(
                "program_id" to programId,
                "is_goal_available" to isTodaysGoal,
                "rewards_eligibility_status" to HealthJourneySettings.pointsSystem.eligibility.text)

    trackHealthGoalEvent(HealthGoalAnalytics.Actions.VIEW_GOAL, "$programName - $goalName", params)
}

fun AnalyticsTracker.trackCareTeamHealthGoal(programId: String, programName: String, goalName: String) {
    val params = mapOf(
                "program_id" to programId,
                "rewards_eligibility_status" to HealthJourneySettings.pointsSystem.eligibility.text)

    trackHealthGoalEvent(HealthGoalAnalytics.Actions.CHAT_WITH_CARE_TEAM_HEALTH_GOALS, "$programName - $goalName", params)
}

fun AnalyticsTracker.viewHealthProgramLimitReached() {
    viewScreen(HealthGoalAnalytics.Pages.HEALTH_PROGRAM_LIMIT_REACHED)
}

fun AnalyticsTracker.viewHealthGetInspired() {
    viewScreen(HealthGoalAnalytics.Pages.HEALTH_ARTICLES)
}

fun AnalyticsTracker.viewHealthPrograms() {
    viewScreen(HealthGoalAnalytics.Pages.HEALTH_PROGRAMS)
}

fun AnalyticsTracker.viewHealthProgramLibrary() {
    viewScreen(HealthGoalAnalytics.Pages.HEALTH_PROGRAM_LIBRARY)
}

fun AnalyticsTracker.viewHealthProgramLibraryCategory(categoryName: String?) {
    viewScreen(HealthGoalAnalytics.Pages.HEALTH_PROGRAM_LIBRARY_CATEGORY.format(categoryName))
}

fun AnalyticsTracker.viewHealthProgramDetails(programName: String) {
    viewScreen(HealthGoalAnalytics.Pages.HEALTH_PROGRAM_DETAILS.format(programName))
}

fun AnalyticsTracker.viewActiveHealthProgramDetails(programName: String) {
    viewScreen(HealthGoalAnalytics.Pages.ACTIVE_HEALTH_PROGRAM_DETAILS.format(programName))
}

fun AnalyticsTracker.viewHealthProgramsDetails() {
    viewScreen(HealthGoalAnalytics.Pages.HEALTH_PROGRAMS_DETAILS)
}

fun AnalyticsTracker.viewHealthProgramsCategoryFilter() {
    viewScreen(HealthGoalAnalytics.Pages.HEALTH_PROGRAMS_CATEGORY_FILTER)
}

fun AnalyticsTracker.viewHealthProgramsEnrollment() {
    viewScreen(HealthGoalAnalytics.Pages.HEALTH_PROGRAMS_ENROLLMENT)
}

fun AnalyticsTracker.viewHealthProgramsGoalDetails() {
    viewScreen(HealthGoalAnalytics.Pages.HEALTH_PROGRAMS_GOAL_DETAILS)
}

fun AnalyticsTracker.viewHealthRewards() {
    viewScreen(HealthGoalAnalytics.Pages.HEALTH_REWARDS)
}

fun AnalyticsTracker.viewHealthGoalCompleted() {
    viewScreen(HealthGoalAnalytics.Pages.HEALTH_GOAL_COMPLETED)
}

fun AnalyticsTracker.viewHealthProgramCompleted() {
    viewScreen(HealthGoalAnalytics.Pages.HEALTH_PROGRAM_COMPLETED)
}

fun AnalyticsTracker.viewHealthProgramWeeklyGoals() {
    viewScreen(HealthGoalAnalytics.Pages.HEALTH_PROGRAM_WEEKLY_GOALS)
}

fun AnalyticsTracker.viewHealthProgramProgress() {
    viewScreen(HealthGoalAnalytics.Pages.HEALTH_PROGRAM_PROGRESS)
}

fun AnalyticsTracker.trackLeaveProgramFeedback(programName: String, campaignId: String) {
    trackEvent(
        category = HealthGoalAnalytics.Categories.ACTIVE_HEALTH_PROGRAM_DETAILS.format(programName),
        action = HealthGoalAnalytics.Actions.SUBMIT_FEEDBACK,
        parameters = mapOf(
            HealthGoalAnalytics.Parameters.CAMPAIGN_ID to campaignId,
            HealthGoalAnalytics.Parameters.REWARDS_ELIGIBILITY_STATUS to HealthJourneySettings.pointsSystem.eligibility.text
        )
    )
}

fun AnalyticsTracker.trackSkipLeaveProgramFeedback(programName: String, campaignId: String) {
    trackEvent(
        category = HealthGoalAnalytics.Categories.ACTIVE_HEALTH_PROGRAM_DETAILS.format(programName),
        action = HealthGoalAnalytics.Actions.SKIP_FEEDBACK,
        parameters = mapOf(
            HealthGoalAnalytics.Parameters.CAMPAIGN_ID to campaignId,
            HealthGoalAnalytics.Parameters.REWARDS_ELIGIBILITY_STATUS to HealthJourneySettings.pointsSystem.eligibility.text
        )
    )
}

