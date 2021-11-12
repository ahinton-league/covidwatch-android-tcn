package life.league.healthjourney.programs.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

/**
 * Response from start_health_goal_program V1
 */
@JsonClass(generateAdapter = true)
data class HealthProgramStart(
    @Json(name = "content_for_users_state") val contentForUsersState: FullScreenContent? = null)  :
    Serializable