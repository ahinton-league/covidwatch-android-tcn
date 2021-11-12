package life.league.healthjourney.injection

import life.league.healthjourney.journey.api.DefaultHealthJourneyAPI
import life.league.healthjourney.journey.api.HealthJourneyAPI
import life.league.healthjourney.programs.api.DefaultHealthProgramsAPI
import life.league.healthjourney.programs.api.HealthProgramsAPI
import org.koin.dsl.module

object HealthJourneyAPIModule {

    val module = module {
        single<HealthJourneyAPI> { DefaultHealthJourneyAPI(api = get()) }
        single<HealthProgramsAPI> { DefaultHealthProgramsAPI(api = get()) }
    }
}