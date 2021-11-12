@file:Suppress("UnusedImport", "UnusedImport", "UnusedImport", "UnusedImport", "UnusedImport",
    "UnusedImport", "UnusedImport", "UnusedImport", "UnusedImport", "UnusedImport", "UnusedImport",
    "UnusedImport", "UnusedImport", "UnusedImport", "UnusedImport", "UnusedImport", "UnusedImport",
    "UnusedImport", "UnusedImport", "UnusedImport", "UnusedImport", "UnusedImport", "UnusedImport",
    "UnusedImport", "UnusedImport", "UnusedImport", "UnusedImport", "UnusedImport", "UnusedImport",
    "UnusedImport", "UnusedImport", "UnusedImport", "UnusedImport", "UnusedImport", "UnusedImport",
    "UnusedImport", "UnusedImport", "UnusedImport", "UnusedImport", "UnusedImport", "UnusedImport",
    "UnusedImport", "UnusedImport", "UnusedImport", "UnusedImport", "UnusedImport", "UnusedImport",
    "UnusedImport", "UnusedImport", "UnusedImport", "UnusedImport", "UnusedImport", "UnusedImport",
    "UnusedImport"
)

package life.league.rewards.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.DialogFragment
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import life.league.genesis.extension.setGenesisContent
import life.league.rewards.components.AchievementCelebrationView
import life.league.rewards.model.AchievementDetail

class AchievementCelebrationDialogFragment(
    val data: List<AchievementDetail>,
    val navController: NavController?
) : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, life.league.genesis.R.style.Genesis_Dialog_FullScreen)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setGenesisContent {
                AchievementCelebrationView(
                    achievements = data,
                    onCloseClick = { dismiss() },
                    onViewAchievementClick = { dismiss() },
                    navController = navController
                )
            }
        }
    }
}
