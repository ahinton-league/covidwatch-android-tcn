package life.league.healthjourney.programs.models

import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class ProgramEnrollmentLimitModal(
    val title: String = "",
    val description: String = ""
): Serializable
