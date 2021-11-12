package life.league.healthjourney.journey.models

import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class StatusCounts(
        val active: Int = 0,
        val upcoming: Int = 0,
        val completed: Int = 0,
        val removed: Int = 0,
        val expired: Int = 0
) : Serializable
