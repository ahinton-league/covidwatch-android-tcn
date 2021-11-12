package life.league.rewards.interop

import android.content.Context
import android.util.AttributeSet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import life.league.genesis.compose.theme.GenesisTheme
import life.league.rewards.components.MilestoneTracker
import life.league.rewards.model.MilestoneTrackerResult

/**
 * Refrain from using the introp Component. It is easier to adopt Full Compose
 * This Component and the interop folder is to be deleted once we achieve full compose
 * This Component is only to be used when we are bound by a non-jetpack compose module
 */
class MilestoneTrackerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0): AbstractComposeView(context, attrs, defStyle) {

    var data by mutableStateOf(MilestoneTrackerResult(milestones = emptyList()))
    var componentHeight by mutableStateOf(160.dp)

    @Composable
    override fun Content() {
        GenesisTheme {
            MilestoneTracker(milestoneData = data, componentHeight = componentHeight)
        }
    }
}