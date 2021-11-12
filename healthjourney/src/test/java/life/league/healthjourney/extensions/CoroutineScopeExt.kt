package life.league.healthjourney.extensions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

fun <T> CoroutineScope.stateFlowTest(stateFlow: StateFlow<T>, action: () -> Unit, assertions: (List<T>) -> Unit) {
    val results = mutableListOf<T>()
    val job = launch {
        stateFlow.toList(results)
    }

    action()

    assertions(results)

    job.cancel()
}