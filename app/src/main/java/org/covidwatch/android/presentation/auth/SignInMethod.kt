package org.covidwatch.android.presentation.auth

sealed class SignInMethod(val value: String) {
    object Google : SignInMethod("google")
    object Password : SignInMethod("password")
}