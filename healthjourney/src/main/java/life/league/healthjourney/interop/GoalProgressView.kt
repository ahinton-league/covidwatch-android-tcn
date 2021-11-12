package life.league.healthjourney.interop

import android.content.Context
import android.util.AttributeSet
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.unit.dp
import life.league.genesis.compose.theme.GenesisTheme
import life.league.healthjourney.components.GoalProgress

class GoalProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0): AbstractComposeView(context, attrs, defStyle) {

    var modifier by mutableStateOf(Modifier.padding(0.dp))
    var dataPoint by mutableStateOf("")
    var percentage by mutableStateOf(0f)
    var currentProgress by mutableStateOf(0f)
    var maxProgress by mutableStateOf(0f)
    var unit by mutableStateOf("")
    var animationDuration by mutableStateOf(2000)
    var isLoading by mutableStateOf(false)

    @Composable
    override fun Content() {
        GenesisTheme {
            GoalProgress(
                modifier = modifier,
                dataPoint = dataPoint,
                currentProgress = currentProgress,
                maxProgress = maxProgress,
                percentage = percentage,
                unit = unit,
                animationDuration = animationDuration,
                isLoading = isLoading)
        }
    }
}