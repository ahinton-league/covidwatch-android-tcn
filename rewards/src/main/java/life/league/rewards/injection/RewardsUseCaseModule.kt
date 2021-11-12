package life.league.rewards.injection

import life.league.rewards.usecase.AchievementProgressUseCase
import life.league.rewards.usecase.AchievementUseCase
import life.league.rewards.usecase.RecentAchievementUseCase
import org.koin.dsl.module

object RewardsUseCaseModule {
    val module = module {
        single {
            AchievementUseCase(repository = get())
        }
        single {
            AchievementProgressUseCase(repository = get())
        }
        single {
            RecentAchievementUseCase(repository = get())
        }
    }
}