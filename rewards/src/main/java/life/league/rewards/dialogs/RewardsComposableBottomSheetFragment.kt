package life.league.rewards.dialogs

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import life.league.genesis.R
import life.league.genesis.compose.theme.GenesisTheme
import life.league.genesis.extension.setGenesisContent
import life.league.rewards.components.ActivityCompletionView
import life.league.rewards.components.BadgeDetail
import life.league.rewards.model.AchievementDetail
import life.league.rewards.model.UserAchievement
import life.league.rewards.utils.showAchievementCelebrationDialog

sealed class ComposableSheetContent {
    data class ActivityCompletionView(val userAchievement: UserAchievement) :
        ComposableSheetContent()

    data class BadgeDetailsView(val data: AchievementDetail) : ComposableSheetContent()
    object Empty : ComposableSheetContent()
}

/**
 * The RewardsComposableBottomSheet is a compose only bottom sheet that
 * requires the view type to be specified from the given options under ComposableSheetContent.
 * The design and associated actions will be handled by the RewardsComposableBottomSheet.
 * If any additional action needs to be performed by the end of this process, we can use setAdditionalAction()
 */
class RewardsComposableBottomSheetFragment : BottomSheetDialogFragment() {

    private var view: ComposableSheetContent = ComposableSheetContent.Empty

    private var navController: NavController? = null

    class Builder {
        private val rewardsComposableBottomSheet: RewardsComposableBottomSheetFragment = RewardsComposableBottomSheetFragment().apply {
            setStyle(DialogFragment.STYLE_NORMAL, R.style.Genesis_Dialog_BottomSheetTheme)
        }

        fun setView(contentView: ComposableSheetContent) = also {
            rewardsComposableBottomSheet.view = contentView
        }

        fun show(fragmentManager: FragmentManager, tag: String? = null) {
            rewardsComposableBottomSheet.show(fragmentManager, tag)
        }

        fun dismiss() {
            rewardsComposableBottomSheet.dismiss()
        }

        fun setNavController(navController: NavController?) = also {
            rewardsComposableBottomSheet.navController = navController
        }
    }

    override fun onStart() {
        super.onStart()
        //this forces the sheet to open fully at max height
        val behavior = BottomSheetBehavior.from(requireView().parent as View)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        when(view) {
            is ComposableSheetContent.ActivityCompletionView -> {
                val completedBadges = (view as ComposableSheetContent.ActivityCompletionView).userAchievement.getAllCompletedAchievements()
                if (completedBadges.isNotEmpty()) {
                    showAchievementCelebrationDialog(
                        completedBadges = completedBadges,
                        navController = navController,
                        fragmentManager = parentFragmentManager
                    )
                }
            }
            else -> { }
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setGenesisContent {
                when(view) {
                    is ComposableSheetContent.ActivityCompletionView -> {
                        ShowActivityCompletionView(
                            userAchievement = (view as ComposableSheetContent.ActivityCompletionView).userAchievement,
                            bottomSheet = this@RewardsComposableBottomSheetFragment,
                            fragmentManager = parentFragmentManager,
                            navController = navController
                        )
                    }

                    is ComposableSheetContent.BadgeDetailsView -> ShowBadgeDetailView(
                        achievementDetail = (view as ComposableSheetContent.BadgeDetailsView).data,
                        bottomSheet = this@RewardsComposableBottomSheetFragment,
                        navController = navController
                    )

                    ComposableSheetContent.Empty -> {}
                }
            }
        }
    }
}

@Composable
private fun ShowBadgeDetailView(
    achievementDetail: AchievementDetail,
    bottomSheet: RewardsComposableBottomSheetFragment,
    navController: NavController?
) {
    BadgeDetail(
        modifier = Modifier.padding(
            top = GenesisTheme.spacing.twoAndHalf,
            start = GenesisTheme.spacing.oneAndHalf,
            end = GenesisTheme.spacing.oneAndHalf,
            bottom = GenesisTheme.spacing.oneAndHalf
        ),
        achievementDetail = achievementDetail,
        navController = navController,
        onCloseClick = {
            bottomSheet.dismiss()
        }
    )
}

@ExperimentalPagerApi
@Composable
private fun ShowActivityCompletionView(
    userAchievement: UserAchievement,
    bottomSheet: RewardsComposableBottomSheetFragment,
    fragmentManager: FragmentManager,
    navController: NavController?
) {
    ActivityCompletionView(
        userAchievement = userAchievement,
        modifier = Modifier.padding(top = GenesisTheme.spacing.twoAndHalf, bottom = GenesisTheme.spacing.oneAndHalf)
    ) {
        //Get the completed badges alone to be passed to the Celebration Modal View
        val completedBadges = userAchievement.getAllCompletedAchievements()

        //If completed badge is available show Celebration Modal
        if (completedBadges.isNotEmpty()) {
            showAchievementCelebrationDialog(completedBadges = completedBadges, navController = navController, fragmentManager = fragmentManager)
        }

        bottomSheet.dismiss()
    }
}