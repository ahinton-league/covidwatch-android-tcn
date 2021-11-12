@file:Suppress("unused", "unused", "unused", "unused", "unused", "unused", "unused", "unused",
    "unused", "unused", "unused", "unused", "unused", "unused", "unused", "unused", "unused",
    "unused", "unused", "unused", "unused", "unused", "unused", "unused", "unused", "unused",
    "unused", "unused", "unused", "unused", "unused", "unused", "unused", "unused", "unused",
    "unused", "unused", "unused", "unused", "unused", "unused", "unused", "unused", "unused",
    "unused", "unused", "unused", "unused", "unused", "unused", "unused", "unused", "unused",
    "unused", "unused", "unused", "unused", "unused", "unused", "unused", "unused", "unused",
    "unused", "unused", "unused", "unused", "unused", "unused", "unused", "unused", "unused",
    "unused", "unused", "unused", "unused", "unused", "unused", "unused", "unused", "unused",
    "unused", "unused", "unused", "unused"
)

package life.league.rewards.interop

import android.content.Context
import android.util.AttributeSet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.AbstractComposeView
import com.google.accompanist.pager.ExperimentalPagerApi
import life.league.genesis.compose.theme.GenesisTheme
import life.league.rewards.components.ActivityCompletionView
import life.league.rewards.previewdata.GetAllAchievementsTestData

/**
 * Refrain from using the introp Component. It is easier to adopt Full Compose
 * This Component and the interop folder is to be deleted once we achieve full compose
 * This Component is only to be used when we are bound by a non-jetpack compose module
 */
class ActivityCompletionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0): AbstractComposeView(context, attrs, defStyle) {

    var userAchievement by mutableStateOf(GetAllAchievementsTestData.createUserAchievementData())

    @Composable
    override fun Content() {
        GenesisTheme {
            ActivityCompletionView(userAchievement = userAchievement)
        }
    }
}