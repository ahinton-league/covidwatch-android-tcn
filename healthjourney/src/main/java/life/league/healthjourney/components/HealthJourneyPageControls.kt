package life.league.healthjourney.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import life.league.genesis.R
import life.league.genesis.compose.theme.*


@Preview
@Composable
fun HealthJourneyPageControlsPreview(@PreviewParameter(ThemeProvider::class) theme: Theme) {
    GenesisTheme(theme = theme) {
        Column(modifier = Modifier.padding(GenesisTheme.spacing.oneAndHalf)) {
            HealthJourneyPageControls(
                modifier = Modifier.fillMaxWidth(),
                onPreviousClick = { },
                onNextClick = { },
                title = "Sept 30",
                overline = "Today"
            )

            OneVerticalSpacer()

            HealthJourneyPageControls(
                modifier = Modifier.fillMaxWidth(),
                onPreviousClick = { },
                onNextClick = { },
                title = "Sept 30",
            )

        }
    }
}

@Composable
internal fun HealthJourneyPageControls(
    modifier: Modifier = Modifier,
    overline: String? = null,
    onPreviousClick: () -> Unit,
    previousButtonContentDescription: String = stringResource(id = R.string.genesis_previous),
    nextButtonContentDescription: String = stringResource(id = R.string.genesis_next),
    onNextClick: () -> Unit,
    title: String,
) {

    Row(
        modifier = modifier
            .background(color = GenesisTheme.colors.backgroundPageControls)
            .padding(vertical = GenesisTheme.spacing.oneAndHalf)
            .padding(horizontal = GenesisTheme.spacing.one),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(onClick = onPreviousClick) {
            Icon(
                modifier = Modifier.rotate(180f),
                painter = painterResource(id = R.drawable.ic_right_chevron_gray),
                contentDescription = previousButtonContentDescription,
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            overline?.also {
                Text(text = overline, style = GenesisTheme.typography.overline, color = GenesisTheme.colors.textSecondary)
                QuarterVerticalSpacer()
            }
            Text(text = title, style = GenesisTheme.typography.h3)
        }

        IconButton(onClick = onNextClick) {
            Icon(
                painter = painterResource(id = R.drawable.ic_right_chevron_gray),
                contentDescription = nextButtonContentDescription
            )
        }

    }

}
