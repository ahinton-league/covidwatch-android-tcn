package life.league.rewards.viewallachievements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import life.league.core.observable.Failed
import life.league.core.observable.Loaded
import life.league.core.observable.Loading
import life.league.core.observable.Uninitialized
import life.league.genesis.compose.component.appbar.GenesisTopAppBar
import life.league.genesis.compose.component.progress.GenesisCenteredIntermittentProgressBar
import life.league.genesis.compose.component.widget.GenesisEmptySectionView
import life.league.genesis.compose.theme.GenesisTheme
import life.league.genesis.compose.theme.HalfVerticalSpacer
import life.league.genesis.extension.isNotNullOrEmpty
import life.league.rewards.R
import life.league.rewards.components.AchievementSection
import life.league.rewards.model.Category
import life.league.rewards.utils.navigateToDynamicDeeplink

@Composable
internal fun ViewAllAchievementsScreen(
    viewModel: ViewAllAchievementsViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    navController: NavController? = rememberNavController()
) {
    Column(modifier = Modifier
        .fillMaxSize()
        .background(GenesisTheme.colors.backgroundSecondary)) {

        //Top App Bar
        GenesisTopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.rewards_achievements),
                    style = GenesisTheme.typography.h3
                )
            },
            backgroundColor = GenesisTheme.colors.fillLight,
            navigationOnClick = onBackClick,
            contentColor = Color.Black,
            elevation = 0.dp
        )

        when (val userAchievementState = viewModel.userAchievement) {
            is Loading -> GenesisCenteredIntermittentProgressBar()
            is Loaded -> DrawSections(userAchievementState.data.achievementCategories, navController)
            is Failed -> {
                // To be discussed still
            }
            is Uninitialized -> { }
        }
    }
}

@Composable
private fun DrawSections(userAchievements: List<Category>, navController: NavController?) {
    LazyColumn {
        items(userAchievements) { category ->
            HalfVerticalSpacer()
            Row(modifier = Modifier.background(GenesisTheme.colors.fillLight)) {
                val categoryName = category.name
                val categoryAchievements = category.allAchievements

                if (category.shouldShowEmptyState) {
                    GenesisEmptySectionView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                top = GenesisTheme.spacing.two,
                                bottom = GenesisTheme.spacing.two,
                                start = GenesisTheme.spacing.oneAndHalf,
                                end = GenesisTheme.spacing.oneAndHalf
                            ),
                        sectionHeader = categoryName,
                        title = category.emptyState?.title.orEmpty(),
                        description = category.emptyState?.description.orEmpty(),
                        ctaText = category.emptyState?.cta?.title.orEmpty(),
                        ctaAction = { navController?.navigateToDynamicDeeplink(category.emptyState?.cta?.url.orEmpty())}
                    )
                } else {
                    AchievementSection(
                        titleText = categoryName,
                        achievements = categoryAchievements,
                        modifier = Modifier
                            .padding(
                                start = GenesisTheme.spacing.oneAndHalf,
                                end = GenesisTheme.spacing.half,
                                top = GenesisTheme.spacing.two,
                                bottom = GenesisTheme.spacing.oneAndHalf
                            ),
                        navController = navController
                    )
                }
            }
        }
    }
}