package life.league.rewards.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import life.league.genesis.compose.accompanist.RemoteImage
import life.league.genesis.compose.component.button.ButtonStyle
import life.league.genesis.compose.component.button.GenesisButton
import life.league.genesis.compose.component.progress.HorizontalProgressBar
import life.league.genesis.compose.theme.GenesisTheme
import life.league.genesis.compose.theme.HalfVerticalSpacer
import life.league.genesis.compose.theme.QuarterVerticalSpacer
import life.league.rewards.R
import life.league.rewards.RewardsModule
import life.league.rewards.model.AchievementDetail
import life.league.rewards.model.UserAchievement
import life.league.rewards.utils.pluralResource

@ExperimentalPagerApi
@Composable
fun ActivityCompletionView(
    modifier: Modifier = Modifier,
    userAchievement: UserAchievement,
    onCloseClick: () -> Unit = {}
) {
    ConstraintLayout(
        modifier = modifier
            .background(color = GenesisTheme.colors.fillLight)
            .fillMaxWidth()
    ) {
        //Create References for our views
        val (title, subtitle, pager, close) = createRefs()

        //Title TextView
        Text(
            modifier = Modifier.constrainAs(title) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
            text = stringResource(R.string.rewards_activity_complete),
            style = GenesisTheme.typography.h3,
            color = GenesisTheme.colors.textPrimary
        )

        //SubTitle TextView
        val subtitleTopMargin = GenesisTheme.spacing.half
        val text = if (userAchievement.grandTotal != null) {
            "${stringResource(R.string.rewards_well_done)} ${
                pluralResource(
                    R.plurals.rewards_points_earned_greeting,
                    userAchievement.grandTotal
                )
            }"
        } else {
            stringResource(R.string.rewards_well_done)
        }
        Text(
            modifier = Modifier.constrainAs(subtitle) {
                top.linkTo(title.bottom, margin = subtitleTopMargin)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
            text = text,
            style = GenesisTheme.typography.subtitle2,
            color = GenesisTheme.colors.textPrimary
        )

        //Badge Progress Details
        val badgeTopMargin = GenesisTheme.spacing.two
        AchievementDetailViewPager(
            modifier = Modifier
                .height(150.dp) //Need to hardcode height since the new pager version is forcing the pager to fill max size
                .constrainAs(pager) {
                    top.linkTo(subtitle.bottom, margin = badgeTopMargin)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                },
            achievements = userAchievement.getAllCategoryAchievements()
        )

        //Close Button
        val closeTopMargin = GenesisTheme.spacing.two
        GenesisButton(
            modifier = Modifier
                .fillMaxWidth(fraction = 0.9f)
                .constrainAs(close) {
                    top.linkTo(pager.bottom, margin = closeTopMargin)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            buttonStyle = ButtonStyle.Primary,
            text = stringResource(id = R.string.rewards_close),
            onClick = onCloseClick
        )
    }
}

@ExperimentalPagerApi
@Composable
internal fun AchievementDetailViewPager(
    modifier: Modifier,
    achievements: List<AchievementDetail>
) {
    val pagerState = rememberPagerState()

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            count = achievements.size,
            state = pagerState,
            contentPadding = PaddingValues(horizontal = GenesisTheme.spacing.two)
        ) { position ->
            AchievementDetailView(achievements[position])
        }

        if (achievements.size > 1) {
            HalfVerticalSpacer()
            HorizontalPagerIndicator(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(GenesisTheme.spacing.one),
                pagerState = pagerState,
                activeColor = GenesisTheme.colors.fillIndicator,
                inactiveColor = GenesisTheme.colors.fillNeutralLight
            )
        }

    }
}

@Composable
internal fun AchievementDetailView(achievementDetail: AchievementDetail) {
    ConstraintLayout(modifier = Modifier.fillMaxWidth(fraction = 0.8f)) {
        //Create References
        val (image, badgeInfo) = createRefs()

        //Load the badge image
        RemoteImage(
            modifier = Modifier
                .constrainAs(image) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
                .size(64.dp),
            url = achievementDetail.achievementImage.small,
            contentDescription = "${achievementDetail.name} ${stringResource(id = R.string.rewards_badge_image)}",
            contentScale = ContentScale.Fit,
            placeHolder = RewardsModule.configuration.drawables.placeHolderImageForBadges?.let { painterResource(id = it) }
        )

        val badgeNameLeftMargin = GenesisTheme.spacing.one
        Column(modifier = Modifier.constrainAs(badgeInfo) {
            top.linkTo(image.top)
            bottom.linkTo(image.bottom)
            start.linkTo(image.end, margin = badgeNameLeftMargin)
        }) {
            Text(
                text = achievementDetail.name,
                style = GenesisTheme.typography.subtitle1,
                color = GenesisTheme.colors.textPrimary
            )

            QuarterVerticalSpacer()
            Text(
                text = "${achievementDetail.progress?.progressTitle}",
                style = GenesisTheme.typography.body2,
                color = GenesisTheme.colors.textSecondary
            )

            HalfVerticalSpacer()
            HorizontalProgressBar(
                modifier = Modifier.height(GenesisTheme.spacing.threeQuarters).fillMaxWidth(fraction = 0.8f),
                percentage = achievementDetail.progress?.overallProgressPercentage?.toFloat() ?: 0F,
                animationDuration = 1000,
                roundedCornerShape = GenesisTheme.shapes.largeRoundedCorner,
                progressColor = GenesisTheme.colors.textPointsEarned,
                backgroundColor = GenesisTheme.colors.fillNeutralLight,
            )
        }
    }
}

