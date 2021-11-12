package life.league.healthjourney.journey.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import life.league.healthjourney.programs.models.ImageAsset
import java.io.Serializable

@JsonClass(generateAdapter = true)
class HelpfulTip(
    val id: String,
    var type: String = "",
    var title: String = "",
    var description: String = "",
    var url: String = "",
    @Json(name = "image_content_id") var imageContentId: String = "",
    @Json(name = "image_url") var imageUrl: String = "",
    @Json(name = "contentful_image_asset") val contentfulImageAsset: ImageAsset = ImageAsset()
): Serializable {

    val assetUrl: String? =
         contentfulImageAsset.fields.file?.url?.takeUnless { it.isEmpty() } ?: imageUrl.takeUnless { it.isEmpty() }

}
