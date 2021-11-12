package life.league.healthjourney.journey.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable


@JsonClass(generateAdapter = true)
data class VerifiableActivityProgressDetail(
    @Json(name = "current_progress") val currentProgress: Float = 0f,
    @Json(name = "goal_total") val grandTotal: Float = 0f,
    @Json(name = "data_type") val dataType: String = "",
    val unit: String = "",
    @Json(name = "last_updated") val lastUpdated: String = ""
) : Serializable {

    fun getGoalUnit(): String {
        return if(unit == "count") "" else unit
    }

}