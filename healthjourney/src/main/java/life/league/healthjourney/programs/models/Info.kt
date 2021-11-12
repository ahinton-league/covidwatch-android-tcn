package life.league.healthjourney.programs.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class Info(
    val heading: String = "",
    val content: String = "",
    @Json(name = "icon_url") val iconUrl: String = "",
    @Json(name = "contentful_image_asset") val imageAsset: ImageAsset = ImageAsset()
) : Serializable
