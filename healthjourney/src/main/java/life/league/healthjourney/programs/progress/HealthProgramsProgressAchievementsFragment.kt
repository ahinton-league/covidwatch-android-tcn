package life.league.healthjourney.programs.progress

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import life.league.core.analytics.AnalyticsTracker
import life.league.genesis.extension.setGenesisContent
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class HealthProgramsProgressAchievementsFragment : Fragment() {

    private val viewModel: HealthProgramsProgressAchievementsViewModel by viewModel()
    private val analyticsTracker: AnalyticsTracker by inject()
    private val navController: NavController by lazy { findNavController() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setGenesisContent {
                HealthProgramsProgressInfoScreen(
                    healthProgramsInfo = viewModel.programProgressInfo,
                    navController = navController,
                    analyticsTracker = analyticsTracker
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getHealthProgramsProgressInfo()
    }

}
