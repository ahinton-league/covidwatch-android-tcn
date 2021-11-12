package life.league.rewards.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import life.league.genesis.compose.component.widget.GenesisEmptySectionView
import life.league.rewards.R
import life.league.rewards.model.AchievementDetail
import life.league.rewards.utils.navigateToViewAllAchievements

@Composable
fun RecentlyEarnedAchievements(
    modifier: Modifier = Modifier,
    achievements: List<AchievementDetail>,
    navController: NavController? = rememberNavController()
) {
    if (achievements.isNullOrEmpty()) {
        GenesisEmptySectionView(
            modifier = modifier,
            sectionHeader = stringResource(id = R.string.rewards_achievements),
            title = stringResource(R.string.rewards_nothing_to_see_here),
            description = stringResource(R.string.rewards_complete_activities_to_unlock),
            navigationText = stringResource(id = R.string.rewards_view_all),
            navigationAction = { navController?.navigateToViewAllAchievements() }
        )
    } else {
        AchievementSection(
            modifier = modifier,
            titleText = stringResource(R.string.rewards_achievements),
            achievements = achievements,
            showViewAll = true,
            navController = navController
        )
    }
}