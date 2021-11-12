package life.league.rewards.components

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import life.league.genesis.compose.accompanist.RemoteImage
import life.league.genesis.compose.component.widget.GenesisNoScrollGridView
import life.league.genesis.compose.theme.GenesisTheme
import life.league.genesis.compose.theme.HalfVerticalSpacer
import life.league.rewards.R
import life.league.rewards.RewardsModule
import life.league.rewards.dialogs.ComposableSheetContent
import life.league.rewards.dialogs.RewardsComposableBottomSheetFragment
import life.league.rewards.model.AchievementDetail
import life.league.rewards.utils.getGreyScale
import life.league.rewards.utils.navigateToViewAllAchievements

@Composable
fun AchievementSection(
    modifier: Modifier = Modifier,
    titleText: String,
    achievements: List<AchievementDetail>,
    showViewAll: Boolean = false,
    navController: NavController? = rememberNavController()
) {
    val context = LocalContext.current

    ConstraintLayout(modifier = modifier) {
        val (title, viewAll, grid) = createRefs()

        //Title TextView
        Text(
            text = titleText,
            style = GenesisTheme.typography.h3,
            color = GenesisTheme.colors.textPrimary,
            modifier = Modifier.constrainAs(title) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
            }
        )

        if (showViewAll) {
            //View All Text Button
            Text(
                modifier = Modifier
                    .constrainAs(viewAll) {
                        end.linkTo(parent.end)
                        top.linkTo(title.top)
                        bottom.linkTo(title.bottom)
                    }
                    .clickable(onClick = {
                        navController?.navigateToViewAllAchievements()
                    }),
                text = stringResource(R.string.rewards_view_all),
                style = GenesisTheme.typography.subtitle1,
                color = GenesisTheme.colors.textLinkDefault
            )
        }

        val gridTopMargin = GenesisTheme.spacing.oneAndHalf
        GenesisNoScrollGridView(
            modifier = Modifier
                .constrainAs(grid) {
                    top.linkTo(title.bottom, margin = gridTopMargin)
                    start.linkTo(title.start)
                },
            items = achievements,
            verticalSpacing = GenesisTheme.spacing.oneAndHalf
        ) { badge ->
            Column(
                modifier = Modifier.fillMaxWidth().clickable(onClick = {
                    showBadgeDetails(context = context, badge = badge, navController = navController)
                }),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                //Badge Image
                RemoteImage(
                    modifier = Modifier.size(64.dp),
                    url = badge.achievementImage.medium,
                    placeHolder = RewardsModule.configuration.drawables.placeHolderImageForBadges?.let { painterResource(id = it) },
                    contentDescription = "${badge.name} ${stringResource(id = R.string.rewards_badge_image)}",
                    colorFilter = if (badge.greyOut) ColorFilter.colorMatrix(colorMatrix = getGreyScale()) else null,
                    contentScale = ContentScale.Fit,
                    alpha = if (badge.greyOut) 0.3F else 1F,
                )

                //Badge Name
                HalfVerticalSpacer()
                Text(
                    text = badge.name,
                    style = GenesisTheme.typography.caption,
                    color = if (badge.greyOut) GenesisTheme.colors.textSecondary else GenesisTheme.colors.textPrimary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

private fun showBadgeDetails(
    context: Context,
    badge: AchievementDetail,
    navController: NavController?
) {
    RewardsComposableBottomSheetFragment.Builder()
        .setView(contentView = ComposableSheetContent.BadgeDetailsView(data = badge))
        .setNavController(navController)
        .show((context as FragmentActivity).supportFragmentManager)
}