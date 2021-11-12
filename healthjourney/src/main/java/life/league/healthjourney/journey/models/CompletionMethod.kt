package life.league.healthjourney.journey.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


enum class CompletionMethodType {
    base,
    unsupported,
    step
}

sealed class CompletionMethod(val completionMethodType: CompletionMethodType): Parcelable {

    @Parcelize
    object Unsupported: CompletionMethod(CompletionMethodType.unsupported)

    @Parcelize
    data class Base(val text: String): CompletionMethod(CompletionMethodType.base)

    @Parcelize
    data class MultiStep(val text: String, val steps: List<String>): CompletionMethod(
        CompletionMethodType.step
    )

}
