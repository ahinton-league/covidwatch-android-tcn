package life.league.rewards

import life.league.networking.socket.API
import life.league.rewards.injection.RewardsDataInjectionModule
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.component.KoinComponent
import org.koin.dsl.koinApplication
import org.koin.dsl.module

object RewardsModule {

    private const val PATH_PREFIX = "https://app.internal.league.com"

    private var rewardsConfiguration : RewardsConfiguration? = null

    internal val configuration: RewardsConfiguration
        get() = rewardsConfiguration ?: throw RuntimeException("Rewards Module Not Initialized")

    fun initialize(
        api: API,
        pathPrefixForDeeplink: String = PATH_PREFIX,
        placeHolderImageForBadges: Int? = null,
        celebrationModalCloseImage: Int = -1,
        activityStreakSwoopImage: Int = -1
    ) {
        rewardsConfiguration = RewardsConfiguration(
            pathPrefixForDeeplink = pathPrefixForDeeplink,
            drawables = RewardsDrawables(
                placeHolderImageForBadges = placeHolderImageForBadges,
                celebrationModalCloseImage = celebrationModalCloseImage,
                milestoneTrackerSwoopImage = activityStreakSwoopImage
            ),
            koinApplication = koinApplication {
                modules(
                    module {
                        single {
                            api
                        }
                    },
                    *(RewardsDataInjectionModule.modules)
                )
            })
    }
}

internal class RewardsConfiguration(
    val koinApplication: KoinApplication,
    val pathPrefixForDeeplink: String,
    val drawables: RewardsDrawables
)

data class RewardsDrawables(
    val placeHolderImageForBadges: Int?,
    val celebrationModalCloseImage: Int,
    val milestoneTrackerSwoopImage: Int
)

internal interface RewardsKoinComponent : KoinComponent {
    override fun getKoin(): Koin = RewardsModule.configuration.koinApplication.koin
}