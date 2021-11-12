package org.covidwatch.android.presentation.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import life.league.core.extension.findNavControllerSafely
import life.league.core.observable.Failed
import life.league.core.observable.Loaded
import life.league.core.util.Log
import life.league.core.util.SessionUtils
import life.league.networking.callback.Failure
import life.league.networking.callback.Success
import org.covidwatch.android.R
import org.covidwatch.android.databinding.FragmentLaunchBinding
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class LaunchFragment : Fragment() {

    companion object {
        private const val TAG = "LaunchFragment"
    }

    private val authenticator: Auth0Authenticator by inject()
    private val sessionUtils: SessionUtils by inject()
//    private val environmentUtils: EnvironmentUtils by inject()
    private val viewModel: LaunchViewModel by viewModel()

//    private val snackbarHostState: SnackbarHostState by lazy { SnackbarHostState() }


    private var _binding: FragmentLaunchBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        setupObservables()
        viewModel.refreshAndAuthenticateSession()
        _binding = FragmentLaunchBinding.inflate(inflater, container, false).apply {

            login.setOnClickListener { login() }
        }
        return binding.root

    }

    private fun setupObservables() {
        viewModel.loginSucceeded.observe(this) { shouldNavigate ->
            when (shouldNavigate) {
                is Loaded -> if (shouldNavigate.data) findNavControllerSafely()?.navigate(R.id.action_launchFragment_to_health_journey_nav_graph)
                is Failed -> lifecycleScope.launch {

                    handleLoginFailure(shouldNavigate.errorMessage)
                }
                else -> {
                    // loading state handled in mutableState for jetpack compose
                }
            }
        }

        viewModel.clearSessionState.observe(this) { clearState ->
            context?.let {
                if (clearState) {
                    lifecycleScope.launch {
                        sessionUtils.clearState(it, null)
                    }
                }
            }
        }
    }

//    private fun logout() {
//        lifecycleScope.launch {
//            context?.let {
//                sessionUtils.signOut(it, SignOutReason.UserLogOut)
//                Log.i(TAG, "Signed out")
//            }
//        }
//    }

    private fun login() {
        lifecycleScope.launch {
            context?.let {
                when (val result = authenticator.login(context = it)) {
                    is Success -> {
                        viewModel.authenticateSession()
                    }
                    is Failure -> {
                        handleLoginFailure(result.errorResponse)
                    }
                }
            }
        }
    }

    private suspend fun handleLoginFailure(errorMessage: String) {
        Log.e(TAG, "Login failed $errorMessage")

        context?.let {
            sessionUtils.clearState(it)
        }
//        snackbarHostState.showSnackbar(
//            message = "Login failed",
//            actionLabel = "Dismiss",
//            duration = Snackbar.LENGTH_SHORT
//        )
    }


}
