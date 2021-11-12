package life.league.healthjourney.journey

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyRecyclerView
import life.league.core.analytics.AnalyticsTracker
import life.league.core.base.RootFragment
import life.league.core.extension.displayErrorDialog
import life.league.core.observable.Failed
import life.league.core.observable.Loaded
import life.league.core.observable.Loading
import life.league.genesis.widget.banner.AssetBanner
import life.league.genesis.widget.banner.TagBanner
import life.league.genesis.widget.banner.assetBanner
import life.league.genesis.widget.banner.tagBanner
import life.league.genesis.widget.header.header
import life.league.genesis.widget.model.SpacingAttrRes
import life.league.healthjourney.R
import life.league.healthjourney.analytics.trackActivitySelection
import life.league.healthjourney.analytics.trackPreviewUpcomingActivities
import life.league.healthjourney.analytics.trackSelectActivitiesTab
import life.league.healthjourney.analytics.viewHealthJourneyActivities
import life.league.healthjourney.databinding.FragmentHealthJourneyTimelineBinding
import life.league.healthjourney.journey.models.HealthActivitiesCategory
import life.league.healthjourney.journey.models.HealthJourneyItem
import life.league.healthjourney.programs.models.HealthJourneyItemDetail
import life.league.healthjourney.utils.getCaption
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class HealthJourneyTimelineFragment : RootFragment() {

    private val viewModel: HealthJourneyTimelineViewModel by viewModel()
    private lateinit var binding: FragmentHealthJourneyTimelineBinding
    private val analyticsTracker: AnalyticsTracker by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentHealthJourneyTimelineBinding.inflate(inflater, container, false)
            .apply { binding = this }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        analyticsTracker.viewHealthJourneyActivities()
        viewModel.state.observe(viewLifecycleOwner, { state ->
            binding.apply {
                when (state) {
                    is Loaded -> renderDataState(state().activityCategories, state().previewAvailable)
                    is Loading -> setViewState(loading = true)
                    is Failed -> context?.displayErrorDialog(
                        state.getErrorMessage(
                            requireContext()
                        )
                    )
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        analyticsTracker.trackSelectActivitiesTab()
        viewModel.getActivities()
    }

    private fun FragmentHealthJourneyTimelineBinding.renderDataState(
        activityCategories: List<HealthActivitiesCategory>, previewAvailable: Boolean) {
        if (activityCategories.isNotEmpty() || previewAvailable) {
            setViewState(populated = true)
            journey.buildJourney(activityCategories, previewAvailable)
        } else {
            setViewState(empty = true)
        }
    }

    private fun EpoxyRecyclerView.buildJourney(
        activityCategories: List<HealthActivitiesCategory>, previewAvailable: Boolean) {
        withModels {
            activityCategories.forEachIndexed { index, category ->
                category.apply {
                    header {
                        id("category_$index")
                        headerText(category.header.titleResId)
                        header.descriptionResId?.also { descriptionText(it) }
                        marginRes(
                            SpacingAttrRes(
                                leftSpacingResId = R.attr.spacing_one_and_half,
                                rightSpacingResId = R.attr.spacing_one_and_half,
                                topSpacingResId = R.attr.spacing_one
                            )
                        )
                    }
                    activities.forEach { activity ->
                        activity.apply {
                            tagBanner {
                                id(id)
                                title(name)
                                tagline(cardTagline)
                                caption(getCaption(context))
                                iconUrl(iconUrl)
                                style(if (status == HealthJourneyItem.Status.COMPLETED.text) TagBanner.Style.COMPLETE else TagBanner.Style.ACTIVE)
                                if (activity.status != HealthJourneyItemDetail.Status.UPCOMING.text) {
                                    onClick { _ ->
                                        analyticsTracker.trackActivitySelection(
                                            activity.type,
                                            activity.name,
                                            activity.id
                                        )
                                        findNavControllerSafely()?.navigate(
                                            HealthJourneyFragmentDirections.actionHealthJourneyFragmentToHealthJourneyActivityFragment(
                                                healthJourneyItemId = activity.id
                                            )
                                        )
                                    }
                                }
                                if (suggested && status != HealthJourneyItem.Status.COMPLETED.text) footer(R.string.suggested)
                                marginRes(
                                    SpacingAttrRes(
                                        leftSpacingResId = R.attr.spacing_one_and_half,
                                        rightSpacingResId = R.attr.spacing_one_and_half,
                                        topSpacingResId = R.attr.spacing_one
                                    )
                                )
                            }
                        }
                    }
                }
            }
            if (previewAvailable) {
                header {
                    id("preview_header")
                    headerText(R.string.preview)
                    marginRes(
                        SpacingAttrRes(
                            leftSpacingResId = R.attr.spacing_one_and_half,
                            rightSpacingResId = R.attr.spacing_one_and_half,
                            topSpacingResId = R.attr.spacing_one
                        )
                    )
                }
                assetBanner {
                    id("preview_section")
                    titleText(R.string.see_whats_coming_up_next)
                    descriptionText(R.string.preview_description)
                    style(AssetBanner.Style.PADDED)
                    imageAttr(R.attr.drawable_health_journey_preview)
                    backgroundImageResource(R.drawable.background_card_primary_with_border)
                    onClick { _ ->
                        analyticsTracker.trackPreviewUpcomingActivities()
                        findNavControllerSafely()?.navigate(
                            HealthJourneyFragmentDirections.actionHealthJourneyFragmentToHealthJourneyPreviewFragment()
                        )
                    }
                    marginRes(
                        SpacingAttrRes(
                            leftSpacingResId = R.attr.spacing_one_and_half,
                            rightSpacingResId = R.attr.spacing_one_and_half,
                            topSpacingResId = R.attr.spacing_one
                        )
                    )
                }
            }
        }
    }

    private fun FragmentHealthJourneyTimelineBinding.setViewState(
        loading: Boolean = false,
        empty: Boolean = false,
        populated: Boolean = false
    ) {
        emptyState.isVisible = empty
        journey.isVisible = populated
        progressBar.isVisible = loading
    }
}
