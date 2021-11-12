package life.league.healthjourney.programs.preview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import life.league.core.analytics.AnalyticsTracker
import life.league.core.base.RootFragment
import life.league.core.extension.popBackStackOrFinish
import life.league.core.extension.withEach
import life.league.core.observable.Failed
import life.league.core.observable.Loaded
import life.league.core.observable.Loading
import life.league.genesis.widget.banner.tagBanner
import life.league.genesis.widget.header.header
import life.league.genesis.widget.model.SpacingAttrRes
import life.league.healthjourney.R
import life.league.healthjourney.analytics.viewHealthJourneyActivitiesPreview
import life.league.healthjourney.databinding.HealthJourneyPreviewFragmentBinding
import life.league.healthjourney.utils.getCaption
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class HealthJourneyPreviewFragment : RootFragment() {

    private val viewModel: HealthJourneyPreviewViewModel by viewModel()
    private val analyticsTracker: AnalyticsTracker by inject()

    override fun onResume() {
        super.onResume()
        analyticsTracker.viewHealthJourneyActivitiesPreview()
    }
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = HealthJourneyPreviewFragmentBinding.inflate(inflater, container, false).apply {
        toolbar.setNavigationOnClickListener {
            findNavControllerSafely()?.popBackStackOrFinish(requireActivity())
        }
        viewModel.previewItems.observe(viewLifecycleOwner, { items ->
            loadingSpinner.isVisible = items is Loading
            preview.isVisible = items is Loaded

            when (items) {
                is Failed -> { /* TODO(HJ) */ }
                is Loaded -> {
                    preview.withModels {
                        header {
                            id("preview_header")
                            headerText(R.string.preview)
                            descriptionText(R.string.preview_description)
                            marginRes(
                                SpacingAttrRes(
                                    leftSpacingResId = R.attr.spacing_one_and_half,
                                    rightSpacingResId = R.attr.spacing_one_and_half,
                                    topSpacingResId = R.attr.spacing_one
                                )
                            )
                        }
                        items().withEach {
                            tagBanner {
                                id(id)
                                title(name)
                                tagline(cardTagline)
                                caption(getCaption(requireContext()))
                                iconUrl(iconUrl)
                                if (suggested) footer(R.string.suggested)
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
                is Loading -> Unit
            }
        })
        viewModel.getPreview()
    }.root
}