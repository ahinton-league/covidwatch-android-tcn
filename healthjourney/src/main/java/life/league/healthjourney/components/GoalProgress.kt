package life.league.healthjourney.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import life.league.genesis.compose.theme.GenesisTheme
import life.league.genesis.compose.theme.GenesisTheme.colors
import life.league.genesis.compose.theme.Theme
import life.league.genesis.compose.theme.ThemeProvider
import life.league.genesis.compose.component.progress.CircularProgressBar
import life.league.healthjourney.R
import life.league.healthjourney.utils.GoalProgressUtil
import java.util.*

private enum class ProgressState {
    Start, Finish
}

@Preview
@Composable
fun GoalProgressPreview(@PreviewParameter(ThemeProvider::class) theme: Theme) {
    GenesisTheme(theme = theme) {
        Column {
            GoalProgress(
                modifier = Modifier
                    .padding(GenesisTheme.spacing.half)
                    .fillMaxWidth(),
                dataPoint = "Some long datapoint value",
                percentage = 0.0f,
                currentProgress = 7000f,
                maxProgress = 7000f,
                animationDuration = 1000
            )
            GoalProgress(
                modifier = Modifier
                    .padding(GenesisTheme.spacing.half),
                dataPoint = "STEPS",
                percentage = 0.2f,
                currentProgress = 1421f,
                maxProgress = 7000f,
                animationDuration = 1000
            )
            Spacer(modifier = Modifier.height(16.dp))
            GoalProgress(
                modifier = Modifier
                    .padding(GenesisTheme.spacing.half),
                dataPoint = "STEPS",
                percentage = 1f,
                currentProgress = 7000f,
                maxProgress = 7000f,
                animationDuration = 1000
            )
            Spacer(modifier = Modifier.height(16.dp))
            GoalProgress(
                modifier = Modifier
                    .padding(GenesisTheme.spacing.half),
                dataPoint = "STEPS",
                percentage = 1.21f,
                currentProgress = 8490f,
                maxProgress = 7000f,
                animationDuration = 1000
            )
        }
    }
}

@Composable
fun GoalProgress(
    modifier: Modifier = Modifier,
    dataPoint: String,
    currentProgress: Float,
    maxProgress: Float,
    percentage: Float,
    unit: String = "",
    animationDuration: Int = 2000,
    isLoading: Boolean = false
) {
    var state by remember { mutableStateOf(ProgressState.Start) }

    val currentGoalProgress = animateFloatAsState(
        targetValue = if (state == ProgressState.Start) 0f else currentProgress,
        animationSpec = tween(animationDuration)
    )

    val currentPercentage = animateFloatAsState(
        targetValue = if (state == ProgressState.Start) 0f else percentage,
        animationSpec = tween(animationDuration)
    )

    Column(modifier = modifier) {
        Text(
            modifier = Modifier.padding(GenesisTheme.spacing.two),
            text = stringResource(R.string.health_journey_your_progress),
            style = GenesisTheme.typography.label
        )
        Spacer(modifier = Modifier.height(GenesisTheme.spacing.one))
        BoxWithConstraints(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(GenesisTheme.spacing.half)
                .width(200.dp)
                .height(200.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            CircularProgressBar(
                percentage = percentage,
                animationDuration = animationDuration,
                radius = 100.dp,
                strokeWidth = 12.dp,
                progressArcUnFilledColor = GenesisTheme.colors.backgroundInputDisabled
            )
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(GenesisTheme.spacing.two)
            ) {
                if (!isLoading) {
                    Text(
                        text = dataPoint.uppercase(Locale.getDefault()),
                        style = GenesisTheme.typography.overline,
                        color = colors.textSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = GoalProgressUtil.convertFloatToReadableNumber(currentGoalProgress.value),
                        style = GenesisTheme.typography.h2
                    )
                    Text(
                        text = stringResource(id = R.string.health_journey_max_progress).format(
                            GoalProgressUtil.convertFloatToReadableNumber(maxProgress),
                            unit
                        ),
                        style = GenesisTheme.typography.body2,
                        color = colors.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(GenesisTheme.spacing.half))
                    if(currentPercentage.value >= 1) {
                        Row(verticalAlignment = Alignment.CenterVertically){
                            Icon(
                                painter = painterResource(R.drawable.ic_complete_icon),
                                contentDescription = null
                            )
                            Text(
                                text = stringResource(id = R.string.health_journey_percentage_complete).format(""),
                                style = GenesisTheme.typography.body2,
                                color = colors.textSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    } else {
                        Text(
                            text = stringResource(id = R.string.health_journey_percentage_complete).format(
                                GoalProgressUtil.getPrettyPercentage(currentPercentage.value)
                            ),
                            style = GenesisTheme.typography.body2,
                            color = colors.textSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                } else {
                    Spacer(
                        modifier = Modifier
                            .width(30.dp)
                            .height(10.dp)
                            .background(colors.fillNeutralLight)
                    )
                    Spacer(modifier = Modifier.height(GenesisTheme.spacing.half))
                }
            }
        }
        LaunchedEffect(key1 = Unit, block = {
            state = ProgressState.Finish
        })

        Text(
            modifier = Modifier.padding(
                horizontal = GenesisTheme.spacing.two
            ),
            text = stringResource(id = R.string.health_journey_progress_disclaimer),
            style = GenesisTheme.typography.body2,
            color = colors.textSecondary,
            textAlign = TextAlign.Center
        )
    }

}