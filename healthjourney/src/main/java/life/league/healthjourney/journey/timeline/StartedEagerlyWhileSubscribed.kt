package life.league.healthjourney.journey.timeline

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

class StartedEagerlyWhileSubscribed(
    private val stopTimeout: Long = 0 ,
    private val replayExpiration: Long = Long.MAX_VALUE,
) : SharingStarted {
    init {
        require(stopTimeout >= 0) { "stopTimeout($stopTimeout ms) cannot be negative" }
        require(replayExpiration >= 0) { "replayExpiration($replayExpiration ms) cannot be negative" }
    }

    override fun command(subscriptionCount: StateFlow<Int>): Flow<SharingCommand> = subscriptionCount
        .transformLatest { count ->
            if (count > 0) {
                emit(SharingCommand.START)
            } else {
                delay(stopTimeout)
                if (replayExpiration > 0) {
                    emit(SharingCommand.STOP)
                    delay(replayExpiration)
                }
                emit(SharingCommand.STOP_AND_RESET_REPLAY_CACHE)
            }
        }
        .onStart { emit(SharingCommand.START) }
        .dropWhile { it != SharingCommand.START } // don't emit any STOP/RESET_BUFFER to start with, only START
        .distinctUntilChanged() // just in case somebody forgets it, don't leak our multiple sending of START

    @OptIn(ExperimentalStdlibApi::class)
    override fun toString(): String {
        val params = buildList(2) {
            if (stopTimeout > 0) add("stopTimeout=${stopTimeout}ms")
            if (replayExpiration < Long.MAX_VALUE) add("replayExpiration=${replayExpiration}ms")
        }
        return "SharingStarted.WhileSubscribed(${params.joinToString()})"
    }

    // equals & hashcode to facilitate testing, not documented in public contract
    override fun equals(other: Any?): Boolean =
        other is StartedEagerlyWhileSubscribed &&
                stopTimeout == other.stopTimeout &&
                replayExpiration == other.replayExpiration

    override fun hashCode(): Int = stopTimeout.hashCode() * 31 + replayExpiration.hashCode()
}