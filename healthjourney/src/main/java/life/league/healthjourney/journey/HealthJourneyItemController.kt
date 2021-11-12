package life.league.healthjourney.journey

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.lifecycle.LifecycleCoroutineScope
import com.airbnb.epoxy.EpoxyController
import life.league.core.analytics.AnalyticsTracker
import life.league.core.observable.Failed
import life.league.core.observable.Loaded
import life.league.core.observable.Loading
import life.league.core.util.featureflags.FeatureFlagsUtils
import life.league.genesis.widget.banner.basicIconBanner
import life.league.genesis.widget.header.header
import life.league.genesis.widget.loading.loadingSpinner
import life.league.genesis.widget.model.SpacingAttrRes
import life.league.genesis.widget.row.ActionRow
import life.league.genesis.widget.row.actionRow
import life.league.genesis.widget.text.richText
import life.league.healthjourney.R
import life.league.healthjourney.analytics.trackSelectContentFromActivity
import life.league.healthjourney.featureflags.HealthJourneyFeatureFlags
import life.league.healthjourney.journey.models.CompletionMethod
import life.league.healthjourney.journey.models.HelpfulTip
import life.league.healthjourney.programs.models.ActivityBanner
import life.league.healthjourney.programs.models.HealthJourneyItemDetail
import life.league.healthjourney.settings.HealthJourneySettings
import life.league.healthjourney.utils.getPointsString

class HealthJourneyItemController(
    private val context: Context,
    private val analyticsTracker: AnalyticsTracker,
    private val lifecycleCoroutineScope: LifecycleCoroutineScope,
    private val navigateToPointsSystemSignUp: (HealthJourneyItemDetail) -> Unit,
    private val navigateToUnsupportedActivity: (HealthJourneyItemDetail) -> Unit,
    private val navigateToHelpfulTip: (HelpfulTip) -> Unit,
    private val featureFlagsUtils: FeatureFlagsUtils,
    ) : EpoxyController() {

    var state: HealthJourneyItemViewModel.HealthJourneyItemViewState? = null
        set(value) {
            field = value
            requestModelBuild()
        }

    override fun buildModels() {
        state?.run {
            when (val healthJourneyItem = healthJourneyItem) {
                is Loaded -> displayHjItemDetails(healthJourneyItem())
                is Loading -> displayLoadingState()
                is Failed -> Unit
            }
        }
    }

    private fun displayLoadingState() {
        loadingSpinner {
            id("loading_spinner")
        }
    }

    private fun displayHjItemDetails(healthJourneyItem: HealthJourneyItemDetail) {
        healthJourneyItem.apply {

            if (cta.completionMethod is CompletionMethod.Unsupported) {
                renderUpdateAppBanner(healthJourneyItem)
            }

            suggestionBanner?.takeIf { !isVerifiableActivity() || isAutomaticMode() }?.also {
                //Should not show suggestion banner for Verifiable activity with non `automatic` mode. todo: down the road this would be handled by contentful
                displayBanner(it)
            }

            renderDescription(healthJourneyItem)

            informationBanner?.also { displayBanner(it) }

            renderHelpfulTips(healthJourneyItem)

            disclaimerBanner?.also { displayBanner(it, R.drawable.background_card_emergency) }

            if (activityPoints > 0 && !HealthJourneySettings.pointsSystem.canEarnPoints) {
                renderMissingPointsBanner(healthJourneyItem)
            }

        }
    }

    private fun EpoxyController.renderMissingPointsBanner(healthJourneyItem: HealthJourneyItemDetail) {
        basicIconBanner {
            id("missing_out_on_points_notice")
            title(R.string.missing_out_on_points)
            descriptionText(R.string.join_pco_points_for_health_activities)
            background(R.drawable.background_card_emergency)
            iconImageResource(R.drawable.ic_bulb)
            isCollapsed(false)
            marginRes(
                SpacingAttrRes(
                    topSpacingResId = R.attr.spacing_one_and_half,
                    leftSpacingResId = R.attr.spacing_one_and_half,
                    rightSpacingResId = R.attr.spacing_one_and_half
                )
            )
            onClick { _ ->
                this@HealthJourneyItemController.navigateToPointsSystemSignUp(healthJourneyItem)
            }
        }
    }

    private fun renderUpdateAppBanner(healthJourneyItem: HealthJourneyItemDetail) {
        basicIconBanner {
            id("updated_app_banner")
            title(R.string.health_journey_heads_up)
            descriptionText(R.string.health_journey_unsupported_completion_type)
            expandActionText(R.string.see_more)
            background(R.drawable.background_card_emergency)
            iconImageResource(R.drawable.ic_warning_alert)
            onClick { _ ->
                this@HealthJourneyItemController.navigateToUnsupportedActivity(healthJourneyItem)
            }
            marginRes(
                SpacingAttrRes(
                    topSpacingResId = R.attr.spacing_one_and_half,
                    leftSpacingResId = R.attr.spacing_one_and_half,
                    rightSpacingResId = R.attr.spacing_one_and_half
                )
            )
        }
    }

    private fun renderHelpfulTips(healthJourneyItem: HealthJourneyItemDetail) = with(healthJourneyItem) {
        helpfulTips.forEach { tip ->
            actionRow {
                id(tip.id)
                showDivider(false)
                overlineText(tip.type)
                titleText(tip.title)
                if (this@HealthJourneyItemController.featureFlagsUtils.getValue(HealthJourneyFeatureFlags.activityCompletionVerification)) {
                    bodyText(if (helpfulTipComplete(tip)) R.string.done else R.string.open_to_continue)
                }
                iconSize(ActionRow.IconSize.MEDIUM)
                tip.assetUrl?.also { url -> iconUrl(url) } ?: iconContentId(tip.imageContentId)
                titleStyleAttr(R.attr.typography_subtitle1)
                marginRes(
                    SpacingAttrRes(
                        topSpacingResId = R.attr.spacing_two_and_half,
                        leftSpacingResId = R.attr.spacing_one_and_half,
                        rightSpacingResId = R.attr.spacing_one_and_half
                    )
                )
                onClick { _ ->
                    this@HealthJourneyItemController.analyticsTracker.trackSelectContentFromActivity(
                        type,
                        name,
                        id,
                        tip.type,
                        tip.url,
                        tip.title
                    )
                    this@HealthJourneyItemController.navigateToHelpfulTip(tip)
                }
            }
        }
    }

    private fun renderDescription(healthJourneyItem: HealthJourneyItemDetail) = with(healthJourneyItem) {
        if (richTextDescription != null) {
            richText {
                id("rich_text_description")
                lifecycleScope(this@HealthJourneyItemController.lifecycleCoroutineScope)
                textFromHtml(richTextDescription)
                marginRes(
                    SpacingAttrRes(
                        topSpacingResId = R.attr.spacing_two,
                        leftSpacingResId = R.attr.spacing_one_and_half,
                        rightSpacingResId = R.attr.spacing_one_and_half
                    )
                )
            }
        } else {
            // This is used for the description text and not an actual header
            header {
                id("description")
                descriptionText(description)
                marginRes(
                    SpacingAttrRes(
                        topSpacingResId = R.attr.spacing_two,
                        leftSpacingResId = R.attr.spacing_one_and_half,
                        rightSpacingResId = R.attr.spacing_one_and_half
                    )
                )
            }
        }
    }

    private fun displayBanner(
        banner: ActivityBanner,
        @DrawableRes backgroundRes: Int = R.drawable.background_card_primary_highlight
    ) {
        banner.run {
            basicIconBanner {
                id(title)
                descriptionText(description)
                expandActionText(R.string.see_more)
                title(title)
                background(backgroundRes)
                iconUrl(iconUrl)
                marginRes(
                    SpacingAttrRes(
                        topSpacingResId = R.attr.spacing_one_and_half,
                        leftSpacingResId = R.attr.spacing_one_and_half,
                        rightSpacingResId = R.attr.spacing_one_and_half
                    )
                )
            }
        }
    }


}