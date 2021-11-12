package life.league.healthjourney.programs.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import life.league.healthjourney.journey.models.StatusCounts
import java.io.Serializable


/**
 * Represents a set of [HealthProgram]. This grouping is used to represent several things including:
 *     1. Programs that a user is enrolled in
 *     2. Programs for a particular program category (in this case title/subtitle may be populated)
 *     3. A grouping of programs to be displayed in a carousel
 *     4. All programs
 */
@JsonClass(generateAdapter = true)
data class HealthPrograms(
    val name: String? = null,
    val description: String? = null,
    val subheading: String? = null,
    val disclaimer: Modal? = null,
    @Json(name = "number_of_available_programs") val numberOfAvailablePrograms: Int? = null,
    @Json(name = "program_limit_modal") val programLimitModal: ProgramEnrollmentLimitModal? = null,
    val programs: List<HealthProgram> = emptyList(),
    @Json(name = "total_activities_count") val totalActivitiesCount: Int? = null,
    @Json(name = "activities_status_counts") val activitiesStatusCounts: StatusCounts? = null
): Serializable {

    val programEnrollmentLimit: Int get() = (numberOfAvailablePrograms ?: 0) + programs.size

}
