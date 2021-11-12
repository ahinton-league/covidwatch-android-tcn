package life.league.healthjourney.journey.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import life.league.healthjourney.programs.models.ImageAsset
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class Button(
    val text: String = "",
    @Json(name = "icon_url") val iconUrl: String = "",
    @Json(name = "contentful_icon_asset") val imageAsset: ImageAsset = ImageAsset()
) : Serializable


