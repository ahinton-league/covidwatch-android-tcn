package life.league.healthjourney.injection

import life.league.healthjourney.journey.repository.DefaultHealthJourneyRepository
import life.league.healthjourney.journey.repository.HealthJourneyRepository
import life.league.healthjourney.programs.repository.DefaultHealthProgramsRepository
import life.league.healthjourney.programs.repository.HealthProgramsRepository
import org.koin.dsl.module

object HealthJourneyRepositoryModule {

    val module = module {

        single<HealthProgramsRepository> {
            DefaultHealthProgramsRepository(healthProgramsAPI = get())
        }

        single<HealthJourneyRepository> {
            DefaultHealthJourneyRepository(healthJourneyAPI = get())
        }

    }
}
