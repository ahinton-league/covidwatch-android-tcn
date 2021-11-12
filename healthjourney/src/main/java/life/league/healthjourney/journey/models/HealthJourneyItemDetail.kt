package life.league.healthjourney.programs.models

import android.content.Context
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import life.league.core.util.DateUtils
import life.league.core.util.LocaleUtils
import life.league.healthjourney.R
import life.league.healthjourney.journey.models.*
import life.league.healthjourney.settings.HealthJourneySettings
import java.io.Serializable
import java.util.*


/**
 * This is what product is referring to as a health journey activity. Renamed to HealthJourneyItem
 * so that it doesn't get confused with an actual Activity
 */
@JsonClass(generateAdapter = true)
data class HealthJourneyItemDetail(
    val id: String = "",
    val name: String = "",
    val tagline: String = "",
    val description: String = "",
    @Json(name = "rich_text_description") val richTextDescription: String? = null,
    @Json(name = "icon_url") val iconUrl: String = "",
    @Json(name = "suggestion_banner") val suggestionBanner: ActivityBanner? = null,
    @Json(name = "information_banner") val informationBanner: ActivityBanner? = null,
    @Json(name = "disclaimer_banner") val disclaimerBanner: ActivityBanner? = null,
    @Json(name = "cta") val cta: CTA = CTA(),
    @Json(name = "completion_message") val completionMessage: String = "",
    @Json(name = "activity_points") val activityPoints: Int = 0,
    @Json(name = "points_earned") val pointsEarned: Int = 0,
    val status: String = "",
    @Json(name = "completion_disabled_timer_ms") val completionDisabledTimerMs: Long? = null,
    val suggested: Boolean = false,
    @Json(name = "start_date") val startDate: Date = Date(0),
    @Json(name = "end_date") val endDate: Date = Date(0),
    @Json(name = "complete_date") val completeDate: Date? = null,
    @Json(name = "helpful_tips") val helpfulTips: List<HelpfulTip> = listOf(),
    val type: String = "",
    @Json(name = "card_tagline") val cardTagline: String = "",
    @Json(name = "custom_fields") val customFields: CustomFields? = null,
    @Json(name = "activity_verification_progress") val activityVerificationProgress: ActivityVerificationProgress? = null
) : Serializable {

    data class ActivityVerificationProgress(
        @Json(name = "helpful_tips_completed_status") var helpfulTipsCompletedStatus: Map<String, Boolean>? = null
    )

    data class ActivityVerificationProgressResponse(
        @Json(name = "activity_verification_progress") val activityVerificationProgress: ActivityVerificationProgress
    )

    companion object {
        const val MODULE_ACTIVITY = "healthJourney_moduleActivity"
        const val VERIFIABLE_ACTIVITY = "healthJourney_verifiableActivity"
    }

    val needsVerifying: Boolean get() = cta.completionMethod !is CompletionMethod.MultiStep

    fun helpfulTipComplete(helpfulTip: HelpfulTip): Boolean = activityVerificationProgress?.helpfulTipsCompletedStatus?.getOrElse(helpfulTip.id, { false }) ?: false
    val helpfulTipsComplete: Boolean get() = helpfulTips.all { helpfulTipComplete(it) }
    val isComplete: Boolean get() = status == Status.COMPLETED.text

    fun isModuleActivity(): Boolean = type == MODULE_ACTIVITY

    fun isVerifiableActivity(): Boolean = type == VERIFIABLE_ACTIVITY

    fun isAutomaticMode(): Boolean = customFields?.campaignMode == CampaignMode.AUTOMATIC.string

    enum class Status(val text: String) {
        ACTIVE("active"), UPCOMING("upcoming"), COMPLETED("completed")
    }

    fun toHealthJourneyItem() =
        HealthJourneyItem(
            id = id,
            name = name,
            tagline = tagline,
            description = description,
            iconUrl = iconUrl,
            activityPoints = activityPoints,
            status = status,
            suggested = suggested,
            startDate = startDate,
            endDate = endDate,
            type = type,
            cardTagline = cardTagline
        )
}

@JsonClass(generateAdapter = true)
data class ImageAsset(
    val sys: Sys = Sys(),
    val fields: Fields = Fields(),
    val file: File? = null
) : Serializable

@JsonClass(generateAdapter = true)
data class Fields(
    val title: String = "",
    val file: File? = null
) : Serializable

@JsonClass(generateAdapter = true)
data class ActivityBanner(
    @Json(name = "icon_url") val iconUrl: String = "",
    val title: String = "",
    val description: String = ""
) : Serializable

@JsonClass(generateAdapter = true)
data class CTA(
    val text: String = "",
    val url: String = "",
    @Json(name = "is_external") val isExternal: Boolean = false,
    @Json(name = "mark_as_complete") val markAsComplete: Boolean = false,
    @Json(name = "completion_method") val completionMethod: CompletionMethod = CompletionMethod.Unsupported

) : Serializable

@JsonClass(generateAdapter = true)
data class ContentType(
    val sys: Sys? = null
) : Serializable

@JsonClass(generateAdapter = true)
data class Space(
    val sys: Sys? = null
) : Serializable

@JsonClass(generateAdapter = true)
data class File(
    @Json(name = "fileName") val name: String = "",
    val contentType: String = "",
    @Json(name = "url") val path: String = "", // For some reason contentful doesn't include https
    @Json(name = "upload") val uploadURL: String = "",
    @Json(name = "details") val detail: FileDetail? = null
) : Serializable {
    val url: String get() = "${if (path.startsWith("https:")) "" else "https:"}$path"
}

@JsonClass(generateAdapter = true)
data class Sys(
    @Json(name = "id") val iD: String = "",
    val type: String = "",
    val linkType: String = "",
    val createdAt: String = "",
    val updatedAt: String = "",
    val revision: Int = 0,
    val contentType: ContentType? = null,
    val space: Space? = null
) : Serializable

@JsonClass(generateAdapter = true)
data class FileDetail(
    val size: Int = 0,
    val image: FileImage? = null
) : Serializable

@JsonClass(generateAdapter = true)
data class FileImage(
    val width: Int = 0,
    val height: Int = 0
) : Serializable

@JsonClass(generateAdapter = true)
data class CustomFields(
    @Json(name = "campaign_mode") val campaignMode: String? = null
) : Serializable