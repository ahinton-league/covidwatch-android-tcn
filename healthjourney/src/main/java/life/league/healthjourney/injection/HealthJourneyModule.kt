package life.league.healthjourney.injection

object HealthJourneyModule {

    val modules = arrayOf(
        HealthJourneyRepositoryModule.module,
        HealthJourneyAPIModule.module,
        HealthJourneyViewModelModule.module,
        HealthJourneyUseCaseModule.module
    )

}
