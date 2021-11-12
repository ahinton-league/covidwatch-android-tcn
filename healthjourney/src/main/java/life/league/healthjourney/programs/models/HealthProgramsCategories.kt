package life.league.healthjourney.programs.models

import com.squareup.moshi.JsonClass
import java.io.Serializable


@JsonClass(generateAdapter = true)
data class HealthProgramsCategories(
    val title: String = "",
    val subtitle: String = "",
    val categories: List<HealthProgramsCategory> = emptyList()
): Serializable
