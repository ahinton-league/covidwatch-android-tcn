package life.league.healthjourney.programs.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class Goal(
    @Json(name = "goal_id") val id: String = "",
    val name: String = "",
    @Json(name = "icon_url") val iconUrl: String = ""): Serializable
