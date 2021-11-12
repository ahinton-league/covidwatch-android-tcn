package life.league.rewards.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.pager.*
import life.league.genesis.compose.accompanist.RemoteImage
import life.league.genesis.compose.component.button.ButtonStyle
import life.league.genesis.compose.component.button.GenesisButton
import life.league.genesis.compose.theme.FourVerticalSpacer
import life.league.genesis.compose.theme.GenesisTheme
import life.league.genesis.compose.theme.QuarterVerticalSpacer
import life.league.genesis.compose.theme.TwoVerticalSpacer
import life.league.rewards.R
import life.league.rewards.RewardsModule
import life.league.rewards.model.AchievementDetail
import life.league.rewards.utils.navigateToViewAllAchievements

@ExperimentalPagerApi
@Composable
fun AchievementCelebrationView(
    modifier: Modifier = Modifier,
    achievements: List<AchievementDetail>,
    onCloseClick: (() -> Unit) = {},
    onViewAchievementClick: (() -> Unit) = {},
    navController: NavController? = rememberNavController()
) {
    val scrollState = rememberScrollState()

    //Create Pager State for Pager Dots
    val pagerState = rememberPagerState()

    ConstraintLayout(
        modifier = modifier
            .scrollable(state = scrollState, orientation = Orientation.Vertical, enabled = false)
            .fillMaxSize()
            .background(color = GenesisTheme.colors.fillLight)
    ) {
        val (pager, viewAchievements, pagerDots, close) = createRefs()

        IconButton(
            modifier = Modifier.constrainAs(close) {
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                },
            onClick = onCloseClick
        ) {
            Icon(
                painter = painterResource(id = RewardsModule.configuration.drawables.celebrationModalCloseImage),
                contentDescription = stringResource(R.string.rewards_close_achievement_celebration),
                modifier = Modifier.size(15.dp)
            )
        }

        if (achievements.size > 1) {
            //Create Pager Dots
            val pagerDotsBottomMargin = GenesisTheme.spacing.one
            HorizontalPagerIndicator(
                modifier = Modifier
                    .constrainAs(pagerDots) {
                        bottom.linkTo(viewAchievements.top, margin = pagerDotsBottomMargin)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .padding(GenesisTheme.spacing.one),
                pagerState = pagerState
            )
        }

        //Achievement Detail PagerView
        val topSpacing = GenesisTheme.spacing.two
        AchievementDetailPager(
            pagerState = pagerState,
            achievements = achievements,
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(pager) {
                    top.linkTo(close.bottom, margin = topSpacing)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        //View Achievements
        val buttonBottomMargin = GenesisTheme.spacing.two
        GenesisButton(
            modifier = Modifier
                .fillMaxWidth(fraction = 0.9f)
                .constrainAs(viewAchievements) {
                    bottom.linkTo(parent.bottom, margin = buttonBottomMargin)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            buttonStyle = ButtonStyle.Primary,
            text = stringResource(R.string.rewards_view_achievements),
            onClick = {
                navController?.navigateToViewAllAchievements()
                onViewAchievementClick.invoke()
            }
        )
    }
}

@ExperimentalPagerApi
@Composable
internal fun AchievementDetailPager(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    achievements: List<AchievementDetail>
) {
    HorizontalPager(
        modifier = modifier.fillMaxWidth(),
        count = achievements.size,
        state = pagerState,
        verticalAlignment = Alignment.Top
    ) { position ->

        val achievementDetail = achievements[position]
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //Title TextView
            Text(
                text = achievementDetail.celebrationTitle,
                style = GenesisTheme.typography.h1,
                color = GenesisTheme.colors.textPrimary
            )

            //SubTitle TextView
            QuarterVerticalSpacer()
            Text(
                text = achievementDetail.celebrationSubTitle,
                style = GenesisTheme.typography.body2,
                color = GenesisTheme.colors.textSecondary
            )

            //Load the badge image
            FourVerticalSpacer()
            RemoteImage(
                url = achievementDetail.achievementImage.large,
                contentDescription = "${achievementDetail.name} " + stringResource(R.string.rewards_badge_image),
                contentScale = ContentScale.FillBounds,
                placeHolder = RewardsModule.configuration.drawables.placeHolderImageForBadges?.let { painterResource(id = it) },
                modifier = Modifier.size(180.dp)
            )

            //Info TextView
            TwoVerticalSpacer()
            Text(
                text = stringResource(R.string.rewards_you_completed),
                style = GenesisTheme.typography.subtitle1,
                color = GenesisTheme.colors.textSecondary,
            )

            //ProgramName TextView
            QuarterVerticalSpacer()
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = achievementDetail.name,
                style = GenesisTheme.typography.h2,
                color = GenesisTheme.colors.textPrimary,
                textAlign = TextAlign.Center
            )

            // TODO Uncomment Points field when response sends points data for PCH/League
            //Earned Grand Total Text TextView
            /*OneVerticalSpacer()
            Text(
                text = stringResource(R.string.rewards_earned_grand_total_of),
                style = GenesisTheme.typography.subtitle2,
                color = GenesisTheme.colors.textSecondary
            )

            //Points Text TextView
            HalfVerticalSpacer()
            Card(
                backgroundColor = GenesisTheme.colors.textPointsEarned,
                shape = RoundedCornerShape(GenesisTheme.shapes.hugeSize)
            ) {
                Text(
                    text = "2,500 pts",
                    style = GenesisTheme.typography.subtitle2,
                    color = GenesisTheme.colors.textPrimary,
                    modifier = Modifier.padding(
                        start = GenesisTheme.spacing.one,
                        end = GenesisTheme.spacing.one,
                        top = GenesisTheme.spacing.threeQuarters,
                        bottom = GenesisTheme.spacing.threeQuarters
                    )
                )
            }*/
        }
    }
}