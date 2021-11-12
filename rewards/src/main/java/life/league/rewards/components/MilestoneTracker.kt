package life.league.rewards.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import life.league.genesis.compose.accompanist.RemoteImage
import life.league.genesis.compose.theme.GenesisTheme
import life.league.rewards.R
import life.league.rewards.RewardsModule
import life.league.rewards.model.MilestoneTrackerResult
import life.league.rewards.previewdata.MilestoneTrackerData

@Preview
@Composable
fun MilestoneTracker(
    modifier: Modifier = Modifier,
    milestoneData: MilestoneTrackerResult = MilestoneTrackerData.getMilestoneData(),
    componentHeight: Dp = 160.dp
) {
    Box(modifier = modifier
        .fillMaxWidth()
        .height(componentHeight)) {
        Image(
            modifier = modifier
                .fillMaxWidth()
                .height(componentHeight),
            painter = painterResource(id = RewardsModule.configuration.drawables.milestoneTrackerSwoopImage),
            contentDescription = stringResource(R.string.rewards_content_description_swoop),
            contentScale = ContentScale.FillBounds
        )

        val rowPadding = GenesisTheme.spacing.one
        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(componentHeight)
                .padding(start = rowPadding, end = rowPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            for (milestone in milestoneData.milestones) {

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    //Milestone image
                    RemoteImage(
                        placeHolder = RewardsModule.configuration.drawables.placeHolderImageForBadges?.let { painterResource(id = it) },
                        contentDescription = stringResource(R.string.rewards_content_description_badge_image),
                        modifier = Modifier.size(40.dp),
                        url = milestone.image.medium
                    )

                    //Milestone Info TextView
                    Text(
                        text = "${milestone.count} ${milestone.unit}",
                        style = GenesisTheme.typography.h3,
                        color = GenesisTheme.colors.textPrimary
                    )

                    //Milestone Additional Info TextView
                    Text(
                        text = milestone.subtitle,
                        style = GenesisTheme.typography.subtitle1,
                        color = GenesisTheme.colors.textSecondary
                    )
                }
            }
        }
    }
}