package org.covidwatch.android.di

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.work.WorkManager
import life.league.core.analytics.AnalyticsTracker
import life.league.core.api.UriProvider
import life.league.core.api.uriProvider
import life.league.core.cache.CoreDatabase
import life.league.core.cache.RoomCache
import life.league.core.extension.getUriForFile
import life.league.core.image.CoreImageLoader
import life.league.core.image.ImageLoader
import life.league.core.repository.UserRepository
import life.league.core.repository.UserRepositoryImpl
import life.league.core.util.BaseJsonUtils
import life.league.core.util.EnvironmentUtils
import life.league.core.util.Log
import life.league.core.util.SessionUtils
import life.league.core.util.featureflags.FeatureFlagsUtils
import life.league.core.util.featureflags.remotefeatureflags.FirebaseRemoteFeatureFlagsUtils
import life.league.genesis.imageloader.GenesisImageViewLoader
import life.league.healthjourney.journey.api.ApplyHealthJourneyAdapters
import life.league.networking.content.ContentAPI
import life.league.networking.content.ContentAPIEnvironmentProvider
import life.league.networking.json.JsonAdapter
import life.league.networking.rest.LeagueRestAPIAuthenticator
import life.league.networking.socket.API
import life.league.networking.socket.LeagueSocketAPIAuthenticator
import life.league.networking.socket.LeagueSocketAPIEnvironmentProvider
import okhttp3.OkHttpClient
import org.covidwatch.android.*
import org.covidwatch.android.data.*
import org.covidwatch.android.data.signedreport.firestore.SignedReportsUploader
import org.covidwatch.android.data.signedreport.SignedReportsDownloader
import org.covidwatch.android.domain.TestedRepository
import org.covidwatch.android.domain.UserFlowRepository
import org.covidwatch.android.presentation.MainActivity
import org.covidwatch.android.presentation.auth.Auth0Authenticator
import org.covidwatch.android.presentation.auth.LaunchViewModel
import org.covidwatch.android.presentation.home.EnsureTcnIsStartedUseCase
import org.covidwatch.android.presentation.home.HomeViewModel
import org.covidwatch.android.presentation.settings.SettingsViewModel
import org.json.JSONObject
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.tcncoalition.tcnclient.TcnKeys
import java.io.File
import java.util.*

@Suppress("USELESS_CAST")
val appModule = module {

    single<AnalyticsTracker> {
        CovidWatchAnalyticsTracker
    }
    single<UserRepository> {
        UserRepositoryImpl(
            api = get(),
            cache = RoomCache(CoreDatabase.getDatabase(androidContext())),
            analyticsTracker = get(),
            sessionUtils = get()
        )
    }

    single<API> {
        API(
            tenantId = "league",
            appVersion = BuildConfig.VERSION_NAME,
            authenticator = get(),
            environmentProvidor = get(),
            locale = Locale.getDefault(),
            messageListener = get(),
            context = androidContext(),
            log = Log,
            jsonAdapter = get(),
            traceRequestsUsingFirebase = true
        )
    }

    single {
        val loginIntent = Intent(androidContext(), MainActivity::class.java)
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)

        SessionUtils(
            androidContext(),
            loginIntent,
            finishActivityOnLogout = true
        )
    }

    //ContentAPI
    single {
        // Set the UriProvider
        ContentAPI.uriProvider = object : UriProvider {
            override fun getUriForFile(context: Context, file: File): Uri {
                return try {
                    context.getUriForFile(BuildConfig.APPLICATION_ID + ".fileprovider", file)
                } catch (e: Exception) {
                    Log.e("ContentAPI", e.toString())
                    Uri.EMPTY
                }
            }
        }
        ContentAPI(
            authenticator = get(),
            environmentProvider = get(),
            logger = Log,
            api = get()
        )
    }

    single<JsonAdapter> {
        get<BaseJsonUtils>()
    }
    single<BaseJsonUtils> {
        get<LeagueJsonUtils>()
    }
    single {
        LeagueJsonUtils(ApplyHealthJourneyAdapters)
    }

    single<ImageLoader> {
        CoreImageLoader(androidContext(), contentAPI = get(), contentAPIAuthenticator = get())
    }

    single<GenesisImageViewLoader> {
        get<ImageLoader>()
    }

    single<API.MessageListener> {
        object: API.MessageListener {
            override fun handleMessagesWithoutCallback(messageType: String, info: JSONObject) {

            }
        }
    }

    single<LeagueSocketAPIEnvironmentProvider> {
        get<EnvironmentUtils>()
    }

    single<ContentAPIEnvironmentProvider> {
        get<EnvironmentUtils>()
    }

    single {
        EnvironmentUtils(
            context = androidContext(),
            defaultEnvironment = EnvironmentConfiguration.environments[0],
            jsonAdapter = get()
        )
    }

    single {
        CompositeLeagueAuthenticator(
            legacyAuthenticator = get(),
            context = androidContext()
        )
    }

    single {
        LegacyLeagueAuthenticator(
            context = androidContext(),
            environmentUtils = get(),
            sessionUtils = get(),
            jsonAdapter = get()
        )
    }

    single<LeagueSocketAPIAuthenticator> {
        get<CompositeLeagueAuthenticator>()
    }

    single<LeagueRestAPIAuthenticator> {
        get<CompositeLeagueAuthenticator>()
    }

//    single<LeagueSocketAPIAuthenticator> {
//        object: LeagueSocketAPIAuthenticator {
//            override val canAuthenticateWithBiometrics: Boolean
//                get() = false
//            override val isLoggedIn: Boolean
//                get() = true
//            override var pushNotificationToken: String?
//                get() = null
//                set(value) {}
//            override val signOutListeners: MutableSet<(Boolean) -> Unit>
//                get() = mutableSetOf()
//            override val userId: String?
//                get() = null
//
//            override suspend fun authenticateSocket(api: API): Boolean = false
//
//            override suspend fun authenticateUsingBiometrics(
//                activity: FragmentActivity,
//                promptTitle: String
//            ): Boolean = false
//
//            override fun disconnect() {
//            }
//
//            override suspend fun refreshAndAuthenticateSession(api: API): Boolean = false
//
//        }
//    }

    single {
        FeatureFlagsUtils(
            context = androidContext(),
            prefFileName = "life.league.preferences.local.flags",
            remoteFeatureFlagsUtils = FirebaseRemoteFeatureFlagsUtils(),
        )
    }

    factory {
        UserFlowRepositoryImpl(
            preferences = get()
        ) as UserFlowRepository
    }

    factory {
        val context = androidContext()

        context.getSharedPreferences(
            "org.covidwatch.android.PREFERENCE_FILE_KEY",
            Context.MODE_PRIVATE
        )
    }

    factory {
        EnsureTcnIsStartedUseCase(
            context = androidContext(),
            tcnManager = get()
        )
    }

    single {
        Auth0Authenticator(
            clientId = androidContext().getString(R.string.com_auth0_client_id),
            domain = androidContext().getString(R.string.com_auth0_domain),
            scheme = androidContext().getString(R.string.com_auth0_scheme),
            sessionUtils = get(),
            context = androidContext()
        )
    }

    viewModel {
        HomeViewModel(
            userFlowRepository = get(),
            testedRepository = get(),
            signedReportsDownloader = get(),
            ensureTcnIsStartedUseCase = get(),
            tcnDao = get()
        )
    }

    viewModel {
        LaunchViewModel(
            authenticator = get(),
            api = get(),
            userRepository = get(),
        )
    }

    factory {
        val context = androidContext()
        val workManager = WorkManager.getInstance(context)
        SignedReportsDownloader(workManager)
    }

    viewModel {
        SettingsViewModel(androidApplication())
    }

    single {
        CovidWatchDatabase.getInstance(androidContext())
    }

    single {
        val database: CovidWatchDatabase = get()
        database.signedReportDAO()
    }

    single {
        val database: CovidWatchDatabase = get()
        database.temporaryContactNumberDAO()
    }

    single { TcnKeys(androidApplication()) }

    single { OkHttpClient() }

    single { SignedReportsUploader(okHttpClient = get(), signedReportDAO = get()) }

    factory {
        TestedRepositoryImpl(
            preferences = get(),
            covidWatchTcnManager = get()
        ) as TestedRepository
    }

    single {
        CovidWatchTcnManager(
            context = androidApplication(),
            tcnKeys = get(),
            tcnDao = get(),
            signedReportDAO = get()
        )
    }
}