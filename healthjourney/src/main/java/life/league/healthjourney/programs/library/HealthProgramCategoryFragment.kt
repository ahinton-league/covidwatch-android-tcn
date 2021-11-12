package life.league.healthjourney.programs.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.airbnb.epoxy.EpoxyController
import life.league.core.analytics.AnalyticsTracker
import life.league.core.extension.findNavControllerSafely
import life.league.core.extension.popBackStackOrFinish
import life.league.core.extension.withEach
import life.league.core.observable.Failed
import life.league.core.observable.Loaded
import life.league.core.observable.Loading
import life.league.genesis.widget.header.header
import life.league.genesis.widget.loading.loadingSpinner
import life.league.genesis.widget.model.SpacingAttrRes
import life.league.healthjourney.R
import life.league.healthjourney.analytics.viewHealthProgramLibraryCategory
import life.league.healthjourney.databinding.HealthProgramCategoryFragmentBinding
import life.league.healthjourney.programs.healthProgramCardView
import life.league.healthjourney.programs.models.HealthPrograms
import life.league.healthjourney.utils.getOverline
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class HealthProgramCategoryFragment : Fragment() {

    private val viewModel: HealthProgramCategoryViewModel by viewModel()
    private val args: HealthProgramCategoryFragmentArgs by navArgs()
    private val analyticsTracker: AnalyticsTracker by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = HealthProgramCategoryFragmentBinding.inflate(inflater, container, false)
        .apply {
            toolbar.setNavigationOnClickListener {
                findNavControllerSafely()?.popBackStackOrFinish(requireActivity())
            }
            args.healthProgramsCarousel?.let { viewModel.initializeFromCarousel(it) }
                ?: args.categoryId?.let { viewModel.getCategory(it) }

            viewModel.healthPrograms.observe(viewLifecycleOwner, { healthPrograms ->
                layout.withModels {
                    when (healthPrograms) {
                        is Failed -> Unit // TODO
                        is Loaded -> renderCategory(healthPrograms())
                        is Loading -> loadingSpinner { id("loading_spinner") }
                    }
                }
            })
        }.root

    private fun EpoxyController.renderCategory(healthPrograms: HealthPrograms) {
        analyticsTracker.viewHealthProgramLibraryCategory(healthPrograms.name)

        header {
            id("program_category_header")
            headerText(healthPrograms.name.orEmpty())
            descriptionText(healthPrograms.description.orEmpty())
            marginRes(
                SpacingAttrRes(
                    topSpacingResId = R.attr.spacing_one_and_half,
                    leftSpacingResId = R.attr.spacing_one_and_half,
                    rightSpacingResId = R.attr.spacing_one_and_half
                )
            )
        }

        healthPrograms.programs.withEach {
            healthProgramCardView {
                id(id)
                title(name)
                description(description)
                imageUrl(imageUrl)
                overline(getOverline(requireContext(), verbose = true))
                applyMargins(true)
                clickListener { _ ->
                    findNavControllerSafely()?.navigate(HealthProgramCategoryFragmentDirections.actionHealthProgramCategoryFragmentToHealthProgramDetailsFragmentV2(id))
                }
            }
        }
    }

}