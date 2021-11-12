package life.league.healthjourney.journey.activitycompletion

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleCoroutineScope
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.flow.collect
import life.league.genesis.compose.component.pager.GenesisHorizontalPager
import life.league.genesis.compose.theme.GenesisTheme
import life.league.genesis.compose.theme.OneAndHalfHorizontalSpacer
import life.league.genesis.compose.theme.Theme
import life.league.genesis.compose.theme.ThemeProvider
import life.league.genesis.widget.text.RichText
import life.league.healthjourney.R


@ExperimentalPagerApi
@Composable
fun MultiStepHealthJourneyItemScreen(
    modifier: Modifier = Modifier,
    onCloseClick: () -> Unit,
    onDoneClick: () -> Unit,
    doneText: String,
    steps: List<String>,
    lifecycleCoroutineScope: LifecycleCoroutineScope?,
    onPageChange: (Int) -> Unit,
    onNextPage: (Int) -> Unit,
    onPreviousPage: (Int) -> Unit,
    showDoneButton: Boolean,
) {
    val pagerState = rememberPagerState()

    val currentPage = remember { mutableStateOf(0) }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { newPage ->
            onPageChange(newPage)
            when {
                newPage < currentPage.value -> onPreviousPage(currentPage.value)
                newPage > currentPage.value -> onNextPage(currentPage.value)
            }
            currentPage.value = newPage
        }
    }

    Column(modifier = modifier) {
        Row(
                modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = GenesisTheme.spacing.half),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onCloseClick) {
                Icon(
                        painter = painterResource(id = R.drawable.ic_navigation_close),
                        contentDescription = stringResource(id = R.string.close)
                )
            }

            Text(text = stringResource(id = R.string.health_journey_x_of_y,
                    pagerState.currentPage + 1, pagerState.pageCount),
                    style = GenesisTheme.typography.subtitle1)

            OneAndHalfHorizontalSpacer()
        }

        GenesisHorizontalPager(
            doneButtonText = doneText,
            onDoneClick = onDoneClick,
            pagerState = pagerState,
            showDoneButton = showDoneButton,
            pageCount = steps.size
        ) { page ->
            LazyColumn(
                    modifier = Modifier
                            .fillMaxSize()
                            .padding(top = GenesisTheme.spacing.oneAndHalf),
                    contentPadding = PaddingValues(all = GenesisTheme.spacing.oneAndHalf),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
            ) {
                item {
                    AndroidView(
                            modifier = Modifier.fillMaxSize(),
                            factory = { context ->
                                RichText(context).apply {
                                    lifecycleScope = lifecycleCoroutineScope
                                }
                            },
                            update = { textView ->
                                textView.setTextFromHtml(steps[page])
                            }
                    )
                }
            }
        }
    }
}

@ExperimentalPagerApi
@Preview
@Composable
fun MultiStepHealthJourneyItemScreenPreview(@PreviewParameter(ThemeProvider::class) theme: Theme) {
    GenesisTheme(theme = theme) {
        Surface {
            MultiStepHealthJourneyItemScreen(
                onCloseClick = {},
                onDoneClick = {},
                doneText = "Done",
                steps = listOf("Step 1", "Step 2", "Step 3"),
                lifecycleCoroutineScope = null,
                onPageChange = {},
                onNextPage = {},
                onPreviousPage = {},
                showDoneButton = true
            )
        }
    }
}
