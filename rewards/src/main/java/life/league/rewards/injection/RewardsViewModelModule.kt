package life.league.rewards.injection
import life.league.rewards.viewallachievements.ViewAllAchievementsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object RewardsViewModelModule {
    val module = module {
        viewModel { ViewAllAchievementsViewModel(achievementUseCase = get()) }
    }
}