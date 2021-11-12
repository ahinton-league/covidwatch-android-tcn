package life.league.rewards.utils

import androidx.core.net.toUri
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import life.league.rewards.RewardsModule
import life.league.rewards.dialogs.AchievementCelebrationDialogFragment
import life.league.rewards.model.AchievementDetail

object RewardNavigation {

    val HEALTH_PROGRAMS = "${RewardsModule.configuration.pathPrefixForDeeplink}/app/member/health-programs"

    val HEALTH_JOURNEY = "${RewardsModule.configuration.pathPrefixForDeeplink}/app/member/health-journey?tab=0"

    val VIEW_ALL_ACHIEVEMENTS = "${RewardsModule.configuration.pathPrefixForDeeplink}/app/member/achievements"

}

fun NavController.navigateToViewAllAchievements() {
    navigate(RewardNavigation.VIEW_ALL_ACHIEVEMENTS.toUri())
}

fun NavController.navigateToHealthJourneyPrograms() {
    navigate(RewardNavigation.HEALTH_PROGRAMS.toUri())
}

fun NavController.navigateToHealthJourney() {
    navigateUp()
    navigate(RewardNavigation.HEALTH_JOURNEY.toUri())
}

fun NavController.navigateToDynamicDeeplink(deeplink: String) {
    navigate("${RewardsModule.configuration.pathPrefixForDeeplink}/app$deeplink")
}

fun showAchievementCelebrationDialog(
    completedBadges: List<AchievementDetail>,
    navController: NavController?,
    fragmentManager: FragmentManager
) {
    AchievementCelebrationDialogFragment(
        data = completedBadges,
        navController = navController
    ).show(fragmentManager, "")
}