package life.league.rewards.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import life.league.core.util.DateUtils
import life.league.genesis.compose.accompanist.RemoteImage
import life.league.genesis.compose.component.button.ButtonStyle
import life.league.genesis.compose.component.button.GenesisButton
import life.league.genesis.compose.component.progress.HorizontalProgressBar
import life.league.genesis.compose.theme.GenesisTheme
import life.league.genesis.compose.theme.HalfVerticalSpacer
import life.league.genesis.compose.theme.OneAndHalfVerticalSpacer
import life.league.genesis.compose.theme.OneVerticalSpacer
import life.league.rewards.R
import life.league.rewards.RewardsModule
import life.league.rewards.model.AchievementDetail
import life.league.rewards.utils.getGreyScale
import life.league.rewards.utils.navigateToHealthJourney
import life.league.rewards.utils.pluralResource
import java.util.*

@Composable
fun BadgeDetail(
    modifier: Modifier = Modifier,
    achievementDetail: AchievementDetail,
    onCloseClick: () -> Unit = {},
    navController: NavController? = rememberNavController()
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //Badge Image
        RemoteImage(
            modifier = Modifier.size(120.dp),
            url = achievementDetail.achievementImage.medium,
            placeHolder = RewardsModule.configuration.drawables.placeHolderImageForBadges?.let { painterResource(id = it) },
            contentDescription = "${achievementDetail.name} ${stringResource(id = R.string.rewards_badge_image)}",
            colorFilter = if (achievementDetail.greyOut) ColorFilter.colorMatrix(colorMatrix = getGreyScale()) else null
        )

        //Achievement Name
        OneAndHalfVerticalSpacer()
        Text(
            text = achievementDetail.name,
            style = GenesisTheme.typography.h3,
            color = GenesisTheme.colors.textPrimary,
            textAlign = TextAlign.Center
        )

        //Earned Date
        if (achievementDetail.lastCompletedAt != null) {
            val date = DateUtils.formatDatePolicy(locale = Locale.getDefault(), date = achievementDetail.lastCompletedAt)
            HalfVerticalSpacer()
            Text(
                text = "${stringResource(R.string.rewards_earned)} $date",
                style = GenesisTheme.typography.subtitle1,
                color = GenesisTheme.colors.textSecondary,
                textAlign = TextAlign.Center
            )
        }


        OneAndHalfVerticalSpacer()
        if (achievementDetail.greyOut) {
            //Earned text
            Text(
                text = achievementDetail.description,
                style = GenesisTheme.typography.body1,
                color = GenesisTheme.colors.textSecondary,
                textAlign = TextAlign.Center
            )

            //Show Completion Times
            if (achievementDetail.completions > 0) {
                HalfVerticalSpacer()
                Card(
                    backgroundColor = GenesisTheme.colors.backgroundSecondary,
                    shape = RoundedCornerShape(GenesisTheme.shapes.smallSize)
                ) {
                    Text(
                        text = pluralResource(R.plurals.rewards_completed_times, achievementDetail.completions),
                        style = GenesisTheme.typography.subtitle2,
                        color = GenesisTheme.colors.textSecondary,
                        modifier = Modifier.padding(all = GenesisTheme.spacing.one)
                    )
                }
            }

            //Show Progress
            if (achievementDetail.showProgressbar) {
                OneVerticalSpacer()
                Text(
                    text = "${achievementDetail.progress?.progressTitle}",
                    style = GenesisTheme.typography.caption,
                    color = GenesisTheme.colors.textSecondary,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )

                HalfVerticalSpacer()
                HorizontalProgressBar(
                    modifier = Modifier.height(GenesisTheme.spacing.threeQuarters),
                    percentage = achievementDetail.progress?.overallProgressPercentage?.toFloat() ?: 0F,
                    animationDuration = 1000,
                    roundedCornerShape = GenesisTheme.shapes.largeRoundedCorner,
                    progressColor = GenesisTheme.colors.textPointsEarned,
                    backgroundColor = GenesisTheme.colors.fillNeutralLight,
                )
            }
        } else {
            //Earned text
            Text(
                text = achievementDetail.descriptionEarned,
                style = GenesisTheme.typography.body1,
                color = GenesisTheme.colors.textSecondary,
                textAlign = TextAlign.Center
            )

            OneVerticalSpacer()
            //Show Completion Times
            if (achievementDetail.completions > 1) {
                Card(
                    backgroundColor = GenesisTheme.colors.backgroundSecondary,
                    shape = RoundedCornerShape(GenesisTheme.shapes.smallSize)
                ) {
                    Text(
                        text = pluralResource(R.plurals.rewards_completed_times, achievementDetail.completions),
                        style = GenesisTheme.typography.subtitle2,
                        color = GenesisTheme.colors.textSecondary,
                        modifier = Modifier.padding(all = GenesisTheme.spacing.one)
                    )
                }
            }
        }

        //Back to Journey Button
        OneAndHalfVerticalSpacer()
        if (achievementDetail.greyOut) {
            GenesisButton(
                modifier = Modifier
                    .fillMaxWidth(),
                buttonStyle = ButtonStyle.Primary,
                text = stringResource(R.string.rewards_back_to_journey),
                onClick = {
                    onCloseClick.invoke()
                    navController?.popBackStack()
                    navController?.navigateToHealthJourney()
                }
            )
        }

        HalfVerticalSpacer()
        GenesisButton(
            modifier = Modifier.fillMaxWidth(),
            buttonStyle = ButtonStyle.Secondary,
            text = stringResource(R.string.rewards_close),
            onClick = onCloseClick
        )

    }
}
