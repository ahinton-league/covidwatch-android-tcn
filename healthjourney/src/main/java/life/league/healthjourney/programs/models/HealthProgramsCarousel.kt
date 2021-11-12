package life.league.healthjourney.programs.models

import com.squareup.moshi.JsonClass
import java.io.Serializable
import java.util.*


@JsonClass(generateAdapter = true)
data class HealthProgramsCarousel(

    val id: String = "",
    val title: String = "",
    val description: String = "",
    val programs: List<HealthProgram> = emptyList()
): Serializable {

    /**
     * We have cases where the id may not be passed (e.g. suggested carousels do not have ids) so
     * a we use ids from the programs in the carousel
     */
    val carouselId: String get() = id.takeIf { it.isNotEmpty() } ?: programs.take(12).joinToString(separator = "_") { it.id }

    fun toHealthPrograms() = HealthPrograms(name = title, description = description, programs = programs)

}
