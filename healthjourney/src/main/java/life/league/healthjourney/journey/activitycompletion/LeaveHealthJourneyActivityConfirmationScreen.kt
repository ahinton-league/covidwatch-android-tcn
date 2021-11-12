package life.league.healthjourney.journey.activitycompletion

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import life.league.genesis.compose.component.button.GenesisButtonsFooter
import life.league.genesis.compose.theme.*
import life.league.healthjourney.R

@Composable
fun LeaveHealthJourneyActivityConfirmationScreen(
    modifier: Modifier = Modifier,
    imagePainter: Painter,
    title: String,
    subtitle: String,
    primaryButtonText: String,
    secondaryButtonText: String,
    onPrimaryButtonClick: () -> Unit,
    onSecondaryButtonClick: () -> Unit
) {
    Column(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                modifier = Modifier.size(254.dp),
                contentScale = ContentScale.FillBounds,
                painter = imagePainter,
                contentDescription = null
            )

            OneAndHalfVerticalSpacer()

            Text(
                modifier = Modifier.padding(horizontal = GenesisTheme.spacing.oneAndHalf),
                text = title, style = GenesisTheme.typography.h2)

            ThreeQuartersVerticalSpacer()

            Text(modifier = Modifier.padding(horizontal = GenesisTheme.spacing.oneAndHalf),
                text = subtitle, style = GenesisTheme.typography.body2)

        }
        GenesisButtonsFooter(
            modifier = Modifier.padding(horizontal = GenesisTheme.spacing.oneAndHalf),
            primaryButtonText = primaryButtonText,
            secondaryButtonText = secondaryButtonText,
            onPrimaryButtonClick = onPrimaryButtonClick,
            onSecondaryButtonClick = onSecondaryButtonClick)
    }
}

@Preview
@Composable
fun LeaveHealthJourneyActivityConfirmationScreenPreview(@PreviewParameter(ThemeProvider::class) theme: Theme) {
    GenesisTheme(theme = theme) {
        Surface {
            LeaveHealthJourneyActivityConfirmationScreen(
                modifier = Modifier.fillMaxSize(),
                imagePainter = painterResource(id = R.drawable.ic_warning),
                title = stringResource(R.string.health_journey_leave_activity_confirmation_title),
                subtitle = stringResource(R.string.health_journey_leave_activity_confirmation_subtitle),
                primaryButtonText = stringResource(R.string.health_journey_leave_activity),
                secondaryButtonText = stringResource(R.string.health_journey_keep_going),
                onPrimaryButtonClick = { },
                onSecondaryButtonClick = { }
            )
        }
    }
}