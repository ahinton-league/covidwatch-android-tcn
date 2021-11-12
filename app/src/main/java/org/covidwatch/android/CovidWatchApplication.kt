package org.covidwatch.android

import android.app.Application
import com.google.common.eventbus.EventBus
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import life.league.core.analytics.AnalyticsTracker
import life.league.core.analytics.trackUserProfile
import life.league.core.api.*
import life.league.core.cache.Cache
import life.league.core.cache.dao.UserConfigDao
import life.league.core.cache.dao.UserDao
import life.league.core.cache.dao.UserFlagsDao
import life.league.core.cache.table.UserConfigTable
import life.league.core.cache.table.UserFlagsTable
import life.league.core.cache.table.UserTable
import life.league.core.model.user.User
import life.league.core.model.user.UserConfig
import life.league.core.model.user.UserFlags
import life.league.core.model.user.UserProfile
import life.league.core.repository.Repository
import life.league.core.repository.UserRepository
import life.league.core.util.Log
import life.league.core.util.SessionUtils
import life.league.genesis.configuration.Genesis
import life.league.healthjourney.injection.HealthJourneyModule
import life.league.healthjourney.journey.HealthJourney
import life.league.healthjourney.journey.HealthJourneyDrawables
import life.league.healthjourney.journey.HealthJourneyStrings
import life.league.networking.callback.*
import life.league.networking.socket.API
import org.covidwatch.android.data.signedreport.SignedReportsDownloader
import org.covidwatch.android.data.signedreport.firestore.SignedReportsUploader
import org.covidwatch.android.di.appModule
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.tcncoalition.tcnclient.TcnClient
import kotlin.coroutines.resume

class CovidWatchApplication : Application() {

    private val tcnManager: CovidWatchTcnManager by inject()
    private val signedReportsUploader: SignedReportsUploader by inject()
    private val signedReportsDownloader: SignedReportsDownloader by inject()

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(applicationContext)
            modules(appModule, *(HealthJourneyModule.modules))
        }

        Genesis.initialize(imageViewLoader = get())

        HealthJourney.initialize(
            api = get(),
            userRepository = get(),
            featureFlagsUtils = get(),
            analytics = get(),

            drawables = HealthJourneyDrawables(
                exitActivityConfirmation = R.drawable.il_health_journey_empty,
                healthJourneyDayCompleteCelebration = R.drawable.il_high_five,
                healthJourneyCurrentDayEmptyProgramsAvailable = R.drawable.il_health_journey_empty,
                healthJourneyCurrentDayEmptyNoProgramsAvailable = R.drawable.il_high_five, // Todo: get correct asset
                healthJourneyFutureDayEmpty = R.drawable.il_health_journey_empty,
                healthJourneyPastDayEmpty = R.drawable.il_health_journey_empty,
            ),
            strings = HealthJourneyStrings(
                pointsSystemError = R.string.health_journey_points_system_error
            ),
            achievementsEnabled = true
        )

        TcnClient.init(tcnManager)
        signedReportsUploader.startUploading()
        signedReportsDownloader.schedulePeriodicPublicSignedReportsRefresh()
    }
}

object CovidWatchAnalyticsTracker: AnalyticsTracker() {
    override fun trackDebugEvent(eventName: String, params: Map<String, String>) {

    }

    override fun trackDeepLinkUrl(url: String) {
    }

    override fun trackEcommerceEvent(
        transactionId: String,
        category: String?,
        action: String?,
        label: String?,
        id: String?,
        name: String?,
        list: String?,
        brand: String?,
        itemCategory: String?,
        variant: String?,
        price: Double?,
        currency: String?
    ) {
    }

    override fun trackEvent(
        category: String,
        action: String?,
        label: String?,
        value: Long?,
        parameters: Map<String, Any?>?
    ) {
    }

    override fun trackMessageUrlClick(url: String?, conversationId: String?, messageId: String?) {
    }

    override fun viewScreen(screenName: String, parameters: Map<String, Any?>?) {
    }

}

class UserRepositoryImpl(
    api: API,
    cache: Cache,
    analyticsTracker: AnalyticsTracker,
    val sessionUtils: SessionUtils
) : Repository(api, cache, analyticsTracker), UserRepository {
    companion object {
        private const val TAG = "CoreRepository"
    }

    // region ftux
    override fun setFtuxViewed(ftuxViewed: Boolean, callback: RequestCallback<Empty>) {
        api.setFtuxViewed(ftuxViewed, requestCallback { result ->
            when (result) {
                is Success -> {
                    val userFlags = cache.getDao<UserFlagsDao>(UserFlagsTable::class.java)
                        ?.getTable()
                        ?.userFlags
                        ?: UserFlags()

                    userFlags.isFtuxViewed = ftuxViewed
                    cache.insertOrUpdate(UserFlagsTable(userFlags))
                    callback.onSuccess(result.response)
                }
                is Failure -> callback.onFailure(result.errorResponse)
            }
        })
    }

    override fun markAccountSetupFtuxCompleted() {
        @Suppress("DEPRECATION")
        cachedUser?.let {
            api.markFtux(true, it.isFtuxCreateBookingVersionOneComplete)
            it.isFtuxVersionOneComplete = true
            cache.insertOrUpdate(UserTable(it))
        }
    }

    override fun setClaimsAssistantFtuxViewed(ftuxViewed: Boolean) {
        api.setClaimsAssistantFtuxViewed(ftuxViewed, requestCallback { result ->
            when (result) {
                is Success -> {
                    val userFlags = cache.getDao<UserFlagsDao>(UserFlagsTable::class.java)
                        ?.getTable()
                        ?.userFlags
                        ?: UserFlags()

                    userFlags.isClaimsAssistantFtuxViewed = ftuxViewed
                    cache.insertOrUpdate(UserFlagsTable(userFlags))
                }
                is Failure -> Log.e(TAG, result.errorResponse)
            }
        })
    }

    // endregion

    // region push notifications

    override fun updatePushNotificationSettings(isHealthAtWorkEnabled: Boolean) {
        @Suppress("DEPRECATION")
        cachedUser?.apply {
            isHealthAtWorkPushNotifications = isHealthAtWorkEnabled
            cache.insertOrUpdate(this)
        }
        api.updatePushNotificationSettings(isHealthAtWorkEnabled)
    }

    override fun updateEmailNotificationSettings(isHealthAtWorkEnabled: Boolean) {
        @Suppress("DEPRECATION")
        cachedUser?.apply {
            isHealthAtWorkEmails = isHealthAtWorkEnabled
            cache.insertOrUpdate(this)
        }
        api.updateEmailNotificationSettings(isHealthAtWorkEnabled)
    }

    // endregion

    // region user

    // None of these values do their work on the background thread, prefer the flow/coroutine versions

    @Deprecated(message = "Use getUser(): Flow<Outcome<UserProfile>> instead")
    override val cachedUser: User?
        get() {
            return cache.getDao<UserDao>(UserTable::class.java)?.getTable()?.user
        }

    @Deprecated(message = "Use getUserConfig(): Flow<Outcome<UserConfig>> instead")
    override val cachedUserConfig: UserConfig?
        get() = cache.getDao<UserConfigDao>(UserConfigTable::class.java)?.getTable()?.userConfig

    @Deprecated(message = "Use suspend fun getUserFlags(): UserFlags? instead")
    override var cachedUserFlags: UserFlags?
        get() = cache.getDao<UserFlagsDao>(UserFlagsTable::class.java)?.getTable()?.userFlags
        set(value) {
            value?.let {
                val table = UserFlagsTable(value)
                cache.insertOrUpdate(table)
            }
        }

    override suspend fun getUserFlags(): UserFlags? = coroutineScope {
        withContext(repositoryScope.coroutineContext) {
            cache.getDao<UserFlagsDao>(UserFlagsTable::class.java)?.getTable()?.userFlags
        }
    }

    override suspend fun setUserProfile(user: User): Outcome<UserProfile> =
        suspendCancellableCoroutine { cont ->
            @Suppress("DEPRECATION")
            setUserProfile(user, requestCallback(executeOnBackgroundThread = true) { result ->
                cont.resume(result)
            })
        }

    @Deprecated(message = "Use setUserProfile(user: User): Flow<Outcome<UserProfile>> instead")
    override fun setUserProfile(user: User, callback: RequestCallback<UserProfile>?) {
        api.setUserProfile(user, requestCallback { result ->
            when (result) {
                is Success -> {
                    @Suppress("DEPRECATION")
                    getUser(callback)
                }
                is Failure -> {
                    callback?.onFailure(result.errorResponse)
                }
            }
        })
    }

    override fun getUser(): Flow<Outcome<UserProfile>> = requestCallbackFlow {
        @Suppress("DEPRECATION")
        getUser(it)
    }

    @Deprecated(message = "Use getUser(): Flow<Outcome<UserProfile>> instead")
    override fun getUser(callback: RequestCallback<UserProfile>?): User? {
        // Update cache, and update UI with result
        api.getUserProfile(requestCallback { result ->
            when (result) {
                is Success -> {
                    if (userId != null && result.response.user.userId != userId) {
                        Log.d(
                            TAG,
                            "Received user profile from different user, not storing in cache"
                        )
                        analyticsTracker.trackDebugEvent("Received user profile from different user, not storing in cache")
                        callback?.onFailure("Received user profile from different user")
                    } else {
                        sessionUtils.lastUserName =
                            result.response.user.preferredFirstName()
                        cache.insertOrUpdate(UserTable(result.response.user))
//                        EventBus.getDefault().post(result.response.user)
                        analyticsTracker.trackUserProfile(result.response.user)
                        callback?.onSuccess(result.response)
                    }
                }
                is Failure -> {
                    callback?.onFailure(result.errorResponse)
                }
            }
        })
        @Suppress("DEPRECATION")
        return cachedUser
    }

    override fun getUserConfig(): Flow<Outcome<UserConfig>> = requestCallbackFlow {
        @Suppress("DEPRECATION")
        getUserConfig(it)
    }

    @Deprecated(message = "Use getUserConfig(): Flow<Outcome<UserConfig>> instead")
    override fun getUserConfig(callback: RequestCallback<UserConfig>?): UserConfig? {
        api.getUserConfig(requestCallback { result ->
            when (result) {
                is Success -> {
                    val table = UserConfigTable(result.response)
                    cache.insertOrUpdate(table)

                    if (callback != null) {
                        callback.onSuccess(result.response)
                    } else {
                        // TODO: update getUserConfig calls so that it does not use the event bus
//                        EventBus.getDefault().post(result.response)
                    }
                }
                is Failure -> {
                    Log.e(TAG, "getUserConfig failed: ${result.errorResponse}")
                    callback?.onFailure(result.errorResponse)
                }
            }
        })
        @Suppress("DEPRECATION")
        return cachedUserConfig
    }

    // endregion
}
