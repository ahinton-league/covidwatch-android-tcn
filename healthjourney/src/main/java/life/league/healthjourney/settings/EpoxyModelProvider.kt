package life.league.healthjourney.settings

import androidx.lifecycle.LiveData
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyModel

/**
 * Intermediate class that allows an epoxy controller to inject views/models into it
 */
abstract class EpoxyModelsProvider {
    /** Call this function when the data required to construct the control is loaded. It will
     * trigger a refresh of the control.
     */
    var requestBuildModel : (() -> Unit) = { }
    internal set

    /** Triggered when the view is first loaded. Put any logic required to load the data for your
     * custom control here. Eg, load data from a database or server. Call dataLoaded() to notify
     * the view to update the contexts of the control.
     */
    abstract fun fetchData()

    /** Triggered when the view is being constructed or when dataLoaded() is called
     */
    abstract fun buildModels(controller: EpoxyController, deeplinkHandler: (url: String) -> Unit)
}