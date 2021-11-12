package life.league.healthjourney.programs.models

import com.squareup.moshi.Json

data class HealthProgramDetailsResponse(
    @Json(name = "program") val healthProgram: HealthProgramDetails = HealthProgramDetails(),
)
