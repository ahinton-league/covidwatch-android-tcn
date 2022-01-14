package life.league.healthjourney.journey.timeline

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import life.league.core.analytics.AnalyticsTracker
import life.league.core.base.RootFragment
import life.league.core.util.DateUtils
import life.league.core.util.LocaleUtils
import life.league.genesis.extension.setGenesisContent
import life.league.healthjourney.analytics.trackActivitySelection
import life.league.healthjourney.components.HealthJourneyPageControls
import life.league.healthjourney.journey.HealthJourney
import life.league.healthjourney.journey.HealthJourneyFragmentDirections
import life.league.healthjourney.journey.HealthJourneyViewModel
import life.league.healthjourney.journey.models.HealthJourneyItem
import life.league.healthjourney.journey.timeline.day.HealthJourneyDayScreen
import life.league.healthjourney.settings.ApplicationDeeplinkHandler
import life.league.healthjourney.settings.ComposeContentProvider
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


@ExperimentalPagerApi
@Composable
private fun HealthJourneyDayPagerScreen(
    modifier: Modifier = Modifier,
    todayIndex: Int,
    title: String,
    overline: String?,
    onPageChange: (Int) -> Unit,
    dates: List<Date>,
    jumpToTodayEvent: Boolean,
    updatedCurrentPage: Int,
    onJumpedToToday: () -> Unit,
    onItemClick: (HealthJourneyItem) -> Unit,
    viewModel: HealthJourneyDayPagerViewModel,
    headerContent: (@Composable () -> Unit)? = null
) {
    Column(modifier = modifier) {
        val pagerState = rememberPagerState(initialPage = updatedCurrentPage)
        val coroutineScope = rememberCoroutineScope()

        pagerState.apply {
            SynchronizeCurrentPage(
                updateCurrentPage = updatedCurrentPage,
                coroutineScope = coroutineScope
            )
            CurrentPageObserver(onPageChange)
            if (jumpToTodayEvent) {
                onJumpedToToday()
                LaunchedEffect(pagerState) {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(todayIndex)
                    }
                }
            }
        }

        headerContent?.invoke()


        HealthJourneyPageControls(
            modifier = Modifier.fillMaxWidth(),
            onPreviousClick = { pagerState.safePagerPrevious(coroutineScope) },
            onNextClick = { pagerState.safePagerNext(coroutineScope) },
            title = title,
            overline = overline
        )

        HorizontalPager(
            modifier = Modifier.weight(1f),
            count = dates.size,
            state = pagerState,
            key = { page -> dates[page].time }
        ) { page ->

            val state by viewModel.getDayState(dates[page]).collectAsState()
            val lazyListState = rememberLazyListState()

            LaunchedEffect(lazyListState) {
                snapshotFlow { lazyListState.firstVisibleItemScrollOffset }.collect {
                    viewModel.setScrollPositionForDay(
                        dates[page],
                        lazyListState.firstVisibleItemIndex,
                        lazyListState.firstVisibleItemScrollOffset
                    )
                }
            }

            HealthJourneyDayScreen(
                modifier = Modifier.fillMaxSize(),
                state = state,
                onItemClick = onItemClick,
                lazyListState = lazyListState,
            )
        }
    }
}


@Composable
@ExperimentalPagerApi
private fun PagerState.CurrentPageObserver(block: (Int) -> Unit) {
    LaunchedEffect(this) {
        snapshotFlow { currentPage }.collect {
            block(it)
        }
    }
}

@Composable
@ExperimentalPagerApi
private fun PagerState.SynchronizeCurrentPage(
    updateCurrentPage: Int,
    coroutineScope: CoroutineScope
) {
    if (updateCurrentPage != currentPage) {
        LaunchedEffect(this) {
            coroutineScope.launch {
                scrollToPage(updateCurrentPage)
            }
        }
    }
}

@ExperimentalPagerApi
private val safePagerNext: PagerState.(CoroutineScope) -> Unit = { coroutineScope ->
    if (currentPage + 1 < pageCount) {
        coroutineScope.launch {
            animateScrollToPage(page = currentPage + 1)
        }
    }
}

@ExperimentalPagerApi
private val safePagerPrevious: PagerState.(CoroutineScope) -> Unit = { coroutineScope ->
    coroutineScope.launch {
        if (currentPage - 1 >= 0) {
            animateScrollToPage(page = currentPage - 1)
        }
    }
}

class HealthJourneyDayPagerFragment : RootFragment() {

    private val viewModel: HealthJourneyDayPagerViewModel by viewModel()
    private val parentViewModel: HealthJourneyViewModel by sharedViewModel()
    private val analyticsTracker: AnalyticsTracker by inject()
    private val dayPagerHeaderProvider: ComposeContentProvider? =
        HealthJourney.configuration.dayPagerHeaderProvider?.invoke()
    private val applicationDeeplinkHandler: ApplicationDeeplinkHandler by HealthJourney.configuration.koinApplication.koin.inject()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        dayPagerHeaderProvider?.refreshData()
        setGenesisContent {
            with(viewModel) {
                val locale = LocaleUtils.getCurrentLocale(LocalContext.current)
                val selectedDate = dates[currentPage]
                HealthJourneyDayPagerScreen(
                    modifier = Modifier.fillMaxSize(),
                    todayIndex = todayIndex,
                    title = DateUtils.formatDateMonthDay(locale, selectedDate),
                    overline = overline?.run { stringResource(id = stringRes) }
                        ?: DateUtils.weekDay(locale, selectedDate),
                    onPageChange = ::onPageChange,
                    dates = dates,
                    jumpToTodayEvent = parentViewModel.jumpToToday,
                    onJumpedToToday = parentViewModel::jumpedToToday,
                    viewModel = viewModel,
                    onItemClick = { item ->
                        analyticsTracker.trackActivitySelection(item.type, item.name, item.id)
                        findNavControllerSafely()?.navigate(
                            HealthJourneyFragmentDirections.actionHealthJourneyFragmentToHealthJourneyActivityFragment(
                                healthJourneyItemId = item.id
                            )
                        )
                    },
                    updatedCurrentPage = viewModel.currentPage,
                    headerContent = {
                        dayPagerHeaderProvider?.Content(deeplinkHandler = this@HealthJourneyDayPagerFragment::handleDeeplink)
                    }
                )
            }
        }
    }

    private fun handleDeeplink(url: String) {
        // The module first checks to see if it can handle the deeplink, otherwise it passes
        // the deeplink along to the parent's application's deeplink hook
        Uri.parse(url).let { uri ->
            if (findNavControllerSafely()?.graph?.hasDeepLink(uri) == true) {
                findNavControllerSafely()?.navigate(uri)
            } else {
                applicationDeeplinkHandler.handleDeeplink(url)
            }
        }
    }

}
