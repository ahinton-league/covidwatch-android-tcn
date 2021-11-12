package life.league.rewards.injection

import life.league.rewards.repository.AchievementsRepository
import org.koin.dsl.module

object RewardsRepositoryModule {
    val module = module {
        single {
            AchievementsRepository(api = get())
        }
    }
}