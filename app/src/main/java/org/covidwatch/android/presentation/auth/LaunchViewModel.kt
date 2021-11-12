package org.covidwatch.android.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import life.league.core.base.SingleLiveEvent
import life.league.core.observable.Failed
import life.league.core.observable.Loaded
import life.league.core.observable.State
import life.league.core.repository.UserRepository
import life.league.networking.callback.Failure
import life.league.networking.callback.Success
import life.league.networking.socket.API

class LaunchViewModel(
    val authenticator: Auth0Authenticator,
    val api: API,
    val userRepository: UserRepository,
    val backgroundDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

//    var isLoggedIn by mutableStateOf(false)
//    var currentEnvironmentName by mutableStateOf("")
//    var isLoading by mutableStateOf(false)
//    var canUseBiometrics by mutableStateOf(false)

    var clearSessionState = SingleLiveEvent<Boolean>()
    var loginSucceeded = SingleLiveEvent<State<Boolean>>()


    fun refreshAndAuthenticateSession() {
        viewModelScope.launch {
//            isLoading = true
//            clearSessionState.postValue(
//                !authenticator.isLoggedIn
//                        || !authenticator.refreshAndAuthenticateSession(api = api)
//            )
//            isLoading = false
//            isLoggedIn = authenticator.isLoggedIn
        }
    }

    fun authenticateSession() {

        viewModelScope.launch(backgroundDispatcher) {
//            isLoading = true
            when (userRepository.authenticateAuth0Connection(
                api = api,
                jwt = authenticator.jwt,
                pushNotificationToken = authenticator.pushNotificationToken
            )) {
                is Success -> {
                    var receivedResult = false
                    combine(
                        userRepository.getUser(),
                        userRepository.getUserConfig()
                    ) { user, userConfig ->
                        if (!receivedResult) {
                            receivedResult = true
                            when {
                                (user is Success && userConfig is Success) -> {
                                    // force on biometric logon so we can test it, eventually we'll toggle this from within the presenter app
                                    loginSucceeded.postValue(
                                        Loaded(true)
                                    )
                                }
                                else -> loginSucceeded.postValue(Failed("Error getting user or user config"))
                            }
                        }
                    }.launchIn(viewModelScope)
                }
                is Failure -> {
                    loginSucceeded.postValue(Failed("Error CoreMenuFragmentgetting user or user config"))
                }
            }
        }
    }


}