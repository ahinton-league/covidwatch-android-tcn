package life.league.healthjourney.featureflags

import life.league.core.util.featureflags.FeatureFlag
import life.league.core.util.featureflags.FeatureFlags

object HealthJourneyFeatureFlags : FeatureFlags {
    val activityCompletionVerification = FeatureFlag.BooleanFeatureFlag(
        id = "rel_health_journey_activity_completion_verification",
        name = "Health Journey Activity Completion Verification",
        description = "Adds in the checks for verifying activity completion",
        defaultValue = false
    )

    val healthJourneyRevamp = FeatureFlag.BooleanFeatureFlag(
        id = "rel_health_journey_revamp",
        name = "Health Journey Revamp",
        description = "Shows the new revamped health journey activities view",
        defaultValue = false
    )

    val wearableProgramManualFlow = FeatureFlag.BooleanFeatureFlag(
        id = "rel_mht_wearableProgramManualFlow",
        name = "Health Journey Wearable program manual flow",
        description = "Enables manual flow for Wearable programs",
        defaultValue = true
    )

    val verifiableActivityProgress = FeatureFlag.BooleanFeatureFlag(id = "rel_mht_verifiableActivityProgress",
        name = "Verifiable Activity Progress",
        description = "Show verifiable activity progress. To be deleted once this feature is ready to be released to all users.",
        defaultValue = false)

    val achievements = FeatureFlag.BooleanFeatureFlag(
        id = "np_rel_mm_achievements",
        name = "Achievements",
        description = "Enable Achievements",
        defaultValue = false
    )
}
