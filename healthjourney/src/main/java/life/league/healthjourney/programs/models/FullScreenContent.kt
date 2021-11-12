package life.league.healthjourney.programs.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import life.league.healthjourney.programs.models.ImageAsset
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class FullScreenContent(
    @Json(name = "image_id") val imageId: String = "",
    @Json(name = "contentful_image_asset") val contentfulImageAsset: ImageAsset,
    val title: String = "",
    val description: String = "") : Serializable