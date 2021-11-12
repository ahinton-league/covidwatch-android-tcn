package life.league.healthjourney.injection

import life.league.healthjourney.journey.usecase.GetHealthJourneyItemsForDayUseCase
import life.league.healthjourney.journey.usecase.HealthJourneyActivityCompletionUseCase
import life.league.healthjourney.programs.progress.HealthJourneyProgressInfoUseCase
import org.koin.dsl.module

object HealthJourneyUseCaseModule {

    val module = module {

        single {
            HealthJourneyActivityCompletionUseCase(achievementUseCase = get(), healthJourneyRepository = get())
        }
        single {
            HealthJourneyProgressInfoUseCase(healthProgramsRepository = get(), achievementProgressUseCase = get(), recentAchievementUseCase = get())
        }
        single {
            GetHealthJourneyItemsForDayUseCase(healthJourneyRepository = get())
        }

    }
}
