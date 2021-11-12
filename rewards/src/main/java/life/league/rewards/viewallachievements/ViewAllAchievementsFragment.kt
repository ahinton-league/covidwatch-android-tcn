package life.league.rewards.viewallachievements

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import life.league.core.base.RootFragment
import life.league.genesis.extension.setGenesisContent
import org.koin.androidx.viewmodel.ext.android.viewModel

class ViewAllAchievementsFragment : RootFragment() {

    private val viewModel by viewModel<ViewAllAchievementsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel.getUserAchievements()

        return ComposeView(requireContext()).apply {
            setGenesisContent {
                ViewAllAchievementsScreen(
                    viewModel = viewModel,
                    navController = findNavControllerSafely(),
                    onBackClick = {
                        activity?.onBackPressed()
                    }
                )
            }
        }
    }
}
