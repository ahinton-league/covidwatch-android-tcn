package life.league.rewards.utils

import androidx.annotation.PluralsRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.platform.LocalContext

internal fun getGreyScale(): ColorMatrix {
    val matrix = ColorMatrix()
    matrix.setToSaturation(0F)
    return matrix
}


@Composable
fun pluralResource(
    @PluralsRes resId: Int,
    quantity: Int
): String {
    return LocalContext.current.resources.getQuantityString(resId, quantity, quantity)
}