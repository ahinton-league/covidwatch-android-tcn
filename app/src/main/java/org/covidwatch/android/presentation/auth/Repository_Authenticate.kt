package org.covidwatch.android.presentation.auth

import life.league.core.api.setPushToken
import life.league.core.model.user.UserFlags
import life.league.core.repository.UserRepository
import life.league.networking.callback.Failure
import life.league.networking.callback.Outcome
import life.league.networking.callback.Success
import life.league.networking.socket.API

suspend fun UserRepository.authenticateAuth0Connection(
    api: API,
    jwt: String?,
    pushNotificationToken: String?
): Outcome<UserFlags> =
    jwt?.let {
        when (val result = api.authenticateJWT(jwt)) {
            is Success -> {
                pushNotificationToken?.let {
                    api.setPushToken(pushNotificationToken)
                }
                Success(UserFlags().apply {
                    isHealthProfileEnabled = true
                    isHealthGoalsEnabled = true
                    isNewMarketplace = true
                    cachedUserFlags = this
                })
            }
            is Failure -> Failure(result.errorResponse)
        }
    } ?: Failure("Cannot authenticate connection, jwt is null")

