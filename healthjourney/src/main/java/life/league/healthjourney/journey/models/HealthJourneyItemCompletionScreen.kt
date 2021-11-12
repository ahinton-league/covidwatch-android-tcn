package life.league.healthjourney.journey.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class HealthJourneyItemCompletionScreen(
        val image: String = "",
        val title: String = "",
        @Json(name = "eyebrow_headline") val eyebrowHeadline: String = "",
        @Json(name = "rewards_message") val rewardsMessage: String = "",
        @Json(name = "description_one") val descriptionOne: String = "",
        @Json(name = "description_two") val descriptionTwo: String = "") : Serializable

@JsonClass(generateAdapter = true)
data class HealthJourneyItemCompletionResponse(
        @Json(name = "completion_screen") val completionScreen: HealthJourneyItemCompletionScreen =
                HealthJourneyItemCompletionScreen()
) : Serializable

