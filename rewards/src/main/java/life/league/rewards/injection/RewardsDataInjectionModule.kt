package life.league.rewards.injection

object RewardsDataInjectionModule {
    val modules = arrayOf(
        RewardsRepositoryModule.module,
        RewardsViewModelModule.module,
        RewardsUseCaseModule.module
    )
}