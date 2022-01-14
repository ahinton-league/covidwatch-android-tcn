package life.league.healthjourney.settings

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.lifecycle.LiveData
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyModel

/**
 * Intermediate class that allows a parent application to inject a jetpack compose view
 * into a feature module. Legacy views are also supported via interop-apis:
 * https://developer.android.com/jetpack/compose/interop/interop-apis#views-in-compose
 */
abstract class ComposeContentProvider {
    /** Triggered when the view is resumed. Put any logic required to load the data for your
     * custom control here. Eg, load data from a database or server. Use
     * compose states to automatically trigger a view refresh when data changes
     */
    abstract fun refreshData()

    /** Navigation to deeplinks is handled by the provided [deeplinkHandler] function
     */
    @Composable
    abstract fun Content(deeplinkHandler : ((url : String) -> Unit))
}