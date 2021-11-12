package life.league.healthjourney.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import life.league.genesis.compose.theme.GenesisTheme
import life.league.genesis.compose.theme.OneVerticalSpacer
import life.league.healthjourney.R

/**
 * Todo(HJ Revamp): Create model
 */
data class SuggestedActivityItem(val message: String, val resourceId: Int)

@Composable
fun SuggestedActivitiesCarousel(
        modifier: Modifier = Modifier,
        title: String,
        description: String,
        activityItems: List<SuggestedActivityItem>
) {
    Column(modifier = modifier
            .background(color = GenesisTheme.colors.backgroundSecondary)
            .padding(start = GenesisTheme.spacing.oneAndHalf, top = GenesisTheme.spacing.oneAndHalf, bottom = GenesisTheme.spacing.oneAndHalf)
    ) {
        OneVerticalSpacer()
        Text(text = title, style = GenesisTheme.typography.h4)
        Text(text = description, style = GenesisTheme.typography.body2)
        OneVerticalSpacer()
        LazyRow(modifier = Modifier.fillMaxWidth()) {

            items(activityItems) {
                SuggestedActivitiesCard(
                        modifier = Modifier.padding(end = GenesisTheme.spacing.threeQuarters),
                        painter = painterResource(id = it.resourceId),
                        text = it.message,
                        drawableContentDescription = "activity icon"
                )
            }
        }
    }
}