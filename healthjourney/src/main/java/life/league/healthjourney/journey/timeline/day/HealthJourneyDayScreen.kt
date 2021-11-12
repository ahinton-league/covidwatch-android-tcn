package life.league.healthjourney.journey.timeline.day

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import life.league.core.observable.*
import life.league.genesis.compose.accompanist.RemoteImage
import life.league.genesis.compose.component.banner.Style
import life.league.genesis.compose.component.banner.TagBanner
import life.league.genesis.compose.component.divider.HorizontalDivider
import life.league.genesis.compose.component.progress.GenesisCenteredIntermittentProgressBar
import life.league.genesis.compose.component.widget.GenesisEmptyStateWidget
import life.league.genesis.compose.theme.GenesisTheme
import life.league.genesis.compose.theme.OneAndHalfVerticalSpacer
import life.league.healthjourney.R
import life.league.healthjourney.journey.HealthJourney
import life.league.healthjourney.journey.models.HealthJourneyItem
import life.league.healthjourney.journey.models.HealthJourneyItemsSection
import life.league.healthjourney.utils.getCaption


@Composable
internal fun HealthJourneyDayScreen(
    modifier: Modifier = Modifier,
    state: HealthJourneyDayViewState,
    onItemClick: (HealthJourneyItem) -> Unit,
    lazyListState: LazyListState,
) {
    val coroutineScope = rememberCoroutineScope()

    val dataState by state.dayData.collectAsState()

    when (val data = dataState) {
        is Loaded -> {
            with(data()) {
                if (sections.isEmpty()) {
                    when {
                        isPast -> PastEmptyState()
                        isFuture -> FutureEmptyState()
                        programsAvailable -> EmptyStateProgramsAvailable()
                        else -> EmptyStateProgramsNoAvailable()
                    }
                } else {

                    lazyListState.RestoreScrollState(
                        coroutineScope = coroutineScope,
                        firstVisibleItemIndex = state.config.firstVisibleItemIndex,
                        firstVisibleItemOffset = state.config.firstVisibleItemOffset
                    )
                    LazyColumn(
                        modifier = modifier,
                        state = lazyListState,
                        verticalArrangement = Arrangement.spacedBy(GenesisTheme.spacing.one),
                    ) {
                        if (sections.allActioned() && !isPast) {
                            allItemsActionedMessage()
                            item {
                                HorizontalDivider(modifier = Modifier.padding(top = GenesisTheme.spacing.oneAndHalf))
                            }
                        } else if (data().sections.programSections.isNotEmpty()) {
                            data().sections.programSections.forEach { section ->
                                journeyItemsSection(section.key, section.value, onItemClick)
                            }
                            item {
                                HorizontalDivider(modifier = Modifier.padding(top = GenesisTheme.spacing.oneAndHalf))
                            }
                        }
                        journeyItemsSection(sections.completedItems, onItemClick)
                        journeyItemsSection(sections.missedItems, onItemClick)

                        // Render any suggested activities categories
                    }
                }
            }

        }
        is Loading, is Uninitialized -> GenesisCenteredIntermittentProgressBar()
        is Failed -> Unit
    }

}

@Composable
private fun LazyListState.RestoreScrollState(coroutineScope: CoroutineScope, firstVisibleItemIndex: Int, firstVisibleItemOffset: Int) {
    LaunchedEffect(this) {
        coroutineScope.launch {
            scrollToItem(
                index = firstVisibleItemIndex,
                scrollOffset = firstVisibleItemOffset
            )
        }
    }
}

private fun LazyListScope.journeyItemsSection(header: String?, items: List<HealthJourneyItem>, onItemClick: (HealthJourneyItem) -> Unit) {
    if (items.isNotEmpty()) {
        item {
            OneAndHalfVerticalSpacer()
        }
        header?.also {
            item {
                Text(
                    modifier = Modifier
                        .padding(horizontal = GenesisTheme.spacing.oneAndHalf),
                    text = header,
                    style = GenesisTheme.typography.subtitle1
                )
            }
        }
        journeyItems(items = items, onItemClick = onItemClick)
    }
}

private fun LazyListScope.journeyItemsSection(section: HealthJourneyItemsSection, onItemClick: (HealthJourneyItem) -> Unit) {
    if (section.items.isNotEmpty()) {
        item {
            OneAndHalfVerticalSpacer()
        }
        section.header?.also { journeySectionHeader(it) }
        journeyItems(items = section.items, onItemClick = onItemClick)
    }
}


private fun LazyListScope.journeySectionHeader(@StringRes headerRes: Int) {
    item {
        Text(
            modifier = Modifier
                .padding(horizontal = GenesisTheme.spacing.oneAndHalf),
            text = stringResource(id = headerRes),
            style = GenesisTheme.typography.subtitle1
        )
    }
}

private fun LazyListScope.journeyItems(items: List<HealthJourneyItem>, onItemClick: (HealthJourneyItem) -> Unit) {
    items(items) { item ->
        JourneyItemTagBanner(
            item = item,
            onClick = { onItemClick(item) }
        )
    }
}

@Composable
private fun JourneyItemTagBanner(item: HealthJourneyItem, onClick: () -> Unit) {
    TagBanner(
        modifier = Modifier.padding(horizontal = GenesisTheme.spacing.oneAndHalf),
        title = item.name,
        overline = item.campaignInfo?.name?.takeIf { item.status != HealthJourneyItem.Status.ACTIVE.text },
        underline = item.getCaption(LocalContext.current).takeIf { it.isNotBlank() },
        onClick = onClick,
        style = when (item.status) {
            HealthJourneyItem.Status.ACTIVE.text -> Style.AVAILABLE
            else -> Style.COMPLETE
        }
    ) {
        RemoteImage(url = item.iconUrl, contentDescription = null)
    }
}

private fun LazyListScope.allItemsActionedMessage() {
    // Todo: add text alignment and modifier to empty state component
    item {
        Box(modifier = Modifier
            .padding(horizontal = GenesisTheme.spacing.three)
            .padding(top = GenesisTheme.spacing.three)) {

            GenesisEmptyStateWidget(
                resDrawable = HealthJourney.configuration.drawables.healthJourneyDayCompleteCelebration,
                title = stringResource(id = R.string.on_a_roll),
                description = stringResource(id = R.string.come_back_tomorrow_or_browse_suggested_activities)
            )
        }
    }
}

@Composable
private fun EmptyStateProgramsAvailable() {
    // Todo: add text alignment and modifier to [GenesisEmptyStateWidget]
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(GenesisTheme.spacing.three)) {

        GenesisEmptyStateWidget(
            resDrawable = HealthJourney.configuration.drawables.healthJourneyCurrentDayEmptyProgramsAvailable,
            title = stringResource(id = R.string.nothing_here_yet),
            description = stringResource(R.string.add_programs_or_suggested_activities)

        )
    }
}

@Composable
private fun EmptyStateProgramsNoAvailable() {
    // Todo: add text alignment and modifier to [GenesisEmptyStateWidget]
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(GenesisTheme.spacing.three)) {

        GenesisEmptyStateWidget(
            resDrawable = HealthJourney.configuration.drawables.healthJourneyCurrentDayEmptyNoProgramsAvailable,
            title = stringResource(id = R.string.nothing_here_yet),
            description = stringResource(R.string.browse_suggest_activities)
        )
    }
}

@Composable
private fun PastEmptyState() {
    // Todo: add text alignment and modifier to [GenesisEmptyStateWidget]
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(GenesisTheme.spacing.three)) {
        GenesisEmptyStateWidget(
            resDrawable = HealthJourney.configuration.drawables.healthJourneyPastDayEmpty,
            title = stringResource(R.string.rest_day),
            description = stringResource(R.string.rest_day_description)
        )
    }

}

@Composable
private fun FutureEmptyState() {
    // Todo: add text alignment and modifier to [GenesisEmptyStateWidget]
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(GenesisTheme.spacing.three)) {
        GenesisEmptyStateWidget(
            resDrawable = HealthJourney.configuration.drawables.healthJourneyFutureDayEmpty,
            title = stringResource(R.string.nothing_planned_yet),
            description = stringResource(R.string.no_activities_scheduled)
        )
    }

}
