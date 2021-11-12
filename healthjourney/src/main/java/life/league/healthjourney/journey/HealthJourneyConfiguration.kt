package life.league.healthjourney.journey

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import life.league.core.analytics.AnalyticsTracker
import life.league.core.repository.UserRepository
import life.league.core.util.featureflags.FeatureFlagsUtils
import life.league.healthjourney.featureflags.HealthJourneyFeatureFlags
import life.league.healthjourney.injection.HealthJourneyModule
import life.league.healthjourney.injection.HealthJourneyViewModelModule
import life.league.networking.socket.API
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.component.KoinComponent
import org.koin.dsl.koinApplication
import org.koin.dsl.module

object HealthJourney {
    // API uses the long lived application context, so this warning is not an issue
    @SuppressLint("StaticFieldLeak")
    private var nullableConfiguration: HealthJourneyConfiguration? = null

    internal val configuration: HealthJourneyConfiguration
        get() = nullableConfiguration ?: throw HealthJourneyNotInitializedException()

    fun initialize(
        featureFlagsUtils: FeatureFlagsUtils,
        drawables: HealthJourneyDrawables,
        strings: HealthJourneyStrings,
        userRepository: UserRepository,
        analytics: AnalyticsTracker,
        api: API,
        achievementsEnabled: Boolean = false
    ) {
        featureFlagsUtils.addFeatureFlagContainers(HealthJourneyFeatureFlags)
        nullableConfiguration =
            HealthJourneyConfiguration(
                achievementsEnabled = achievementsEnabled,
                drawables = drawables,
                strings = strings,
                koinApplication = koinApplication {
                    modules(
                        module {
                            single {
                                analytics
                            }
                            single {
                                api
                            }
                            single {
                                userRepository
                            }
                        },
                        *(HealthJourneyModule.modules)
                    )
                })
    }
}

internal class HealthJourneyConfiguration(
    val koinApplication: KoinApplication,
    val drawables: HealthJourneyDrawables,
    val strings: HealthJourneyStrings,
    val achievementsEnabled: Boolean
)


data class HealthJourneyDrawables(
    @DrawableRes val exitActivityConfirmation: Int,
    @DrawableRes val healthJourneyDayCompleteCelebration: Int,
    @DrawableRes val healthJourneyCurrentDayEmptyProgramsAvailable: Int,
    @DrawableRes val healthJourneyCurrentDayEmptyNoProgramsAvailable: Int,
    @DrawableRes val healthJourneyFutureDayEmpty: Int,
    @DrawableRes val healthJourneyPastDayEmpty: Int,
)

data class HealthJourneyStrings(
    @StringRes val pointsSystemError: Int
)

internal interface HealthJourneyKoinComponent : KoinComponent {
    override fun getKoin(): Koin = HealthJourney.configuration.koinApplication.koin
}


class HealthJourneyNotInitializedException :
    Exception("Error, the HealthJourney module has not been initialized. Please call HealthJourney.initialize(...) in your application's Application.onCreate() method.")