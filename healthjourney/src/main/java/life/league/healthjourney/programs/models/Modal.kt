package life.league.healthjourney.programs.models

import com.squareup.moshi.JsonClass
import life.league.healthjourney.journey.models.Button
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class Modal(
    val id: String = "",
    val button: Button = Button(),
    val info: Info = Info()
) : Serializable
