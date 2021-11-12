package life.league.healthjourney.programs.models

import com.squareup.moshi.Json

data class ProgramDetailsResponse(
    @Json(name = "program_details") val programDetails: HealthProgramDetails = HealthProgramDetails(),
    val status: String = ""
)
