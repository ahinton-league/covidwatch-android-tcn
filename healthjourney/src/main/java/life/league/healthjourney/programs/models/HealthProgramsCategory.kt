package life.league.healthjourney.programs.models

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Json
import java.io.Serializable


@JsonClass(generateAdapter = true)
data class HealthProgramsCategory(
    val id: String = "",
    val name: String = "",
    @Json(name = "icon_url") val iconUrl: String = "",
): Serializable
