package life.league.healthjourney.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import life.league.genesis.compose.theme.GenesisTheme
import life.league.genesis.compose.theme.HalfVerticalSpacer
import life.league.genesis.compose.theme.Theme
import life.league.genesis.compose.theme.ThemeProvider
import life.league.healthjourney.R


@Preview
@Composable
fun SuggestedActivitiesCardPreview(@PreviewParameter(ThemeProvider::class) theme: Theme) {
    GenesisTheme(theme) {
        Column {
            SuggestedActivitiesCard(
                painter = painterResource(id = R.drawable.ic_bulb),
                text = "Similar to your past activities"
            )
            SuggestedActivitiesCard(
                painter = painterResource(id = R.drawable.ic_bulb),
                text = "Similar to your past activities"
            )
        }

    }
}

@Composable
fun SuggestedActivitiesCard(modifier: Modifier = Modifier, painter: Painter, text: String, drawableContentDescription: String? = null) {
    val backgroundShape = GenesisTheme.shapes.largeRoundedCorner

    Row(
            modifier = modifier
                    .background(color = GenesisTheme.colors.backgroundSuggested, shape = backgroundShape)
                    .border(color = GenesisTheme.colors.borderNeutralLighter, width = 1.dp, shape = backgroundShape)
                    .height(142.dp)
                    .width(156.dp)
                    .clip(backgroundShape)
    ) {
        Divider(
                color = GenesisTheme.colors.backgroundSuggestedDark,
                modifier = Modifier
                        .width(6.dp)
                        .fillMaxHeight()
        )
        Column(
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier
                        .fillMaxSize()
                        .padding(
                                start = GenesisTheme.spacing.one,
                                top = GenesisTheme.spacing.quarter,
                                end = GenesisTheme.spacing.quarter,
                                bottom = GenesisTheme.spacing.one
                        )
        ) {
            Icon(
                    painter = painter,
                    contentDescription = drawableContentDescription,
            )
            HalfVerticalSpacer()
            Text(text = text, style = GenesisTheme.typography.subtitle1)
        }
    }
}