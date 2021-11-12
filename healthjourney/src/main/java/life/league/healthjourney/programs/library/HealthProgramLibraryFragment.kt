package life.league.healthjourney.programs.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import life.league.core.analytics.AnalyticsTracker
import life.league.core.extension.findNavControllerSafely
import life.league.core.extension.popBackStackOrFinish
import life.league.genesis.widget.dialog.BottomSheetDialog
import life.league.genesis.widget.dialog.ContentProviderInfo
import life.league.healthjourney.R
import life.league.healthjourney.analytics.viewHealthProgramLibrary
import life.league.healthjourney.databinding.HealthProgramLibraryFragmentBinding
import life.league.healthjourney.utils.getOverline
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class HealthProgramLibraryFragment : Fragment() {

    private val viewModel: HealthProgramLibraryViewModel by viewModel()
    private val spanCountLimit = 2
    private val analyticsTracker: AnalyticsTracker by inject()

    override fun onResume() {
        super.onResume()
        analyticsTracker.viewHealthProgramLibrary()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = HealthProgramLibraryFragmentBinding.inflate(inflater, container, false)
        .apply {
            toolbar.setNavigationOnClickListener {
                findNavControllerSafely()?.popBackStackOrFinish(requireActivity())
            }

            val epoxyController =  HealthProgramLibraryController(
                analyticsTracker = analyticsTracker,
                navController = findNavController(),
                getProgramOverline = { program -> program.getOverline(requireContext()) },
                getProgramVerboseOverline = { program -> program.getOverline(requireContext(), verbose = true) },
                programEnrollmentLimitMessage = getString(R.string.program_enrollment_limit_banner_message),
                displayInfoModal = { info ->
                    BottomSheetDialog.Builder()
                        .setContentView(ContentProviderInfo(requireContext()).apply {
                            setHeading(info.heading)
                            setDescription(info.content)
                            setImageUrl(info.imageAsset.fields.file?.url ?: "")
                        })
                        .show(requireActivity().supportFragmentManager, "ContentProviderDialogView")
                }
            )
            val layoutManager = GridLayoutManager(context, spanCountLimit)
            epoxyController.spanCount = spanCountLimit
            layoutManager.spanSizeLookup = epoxyController.spanSizeLookup

            libraryLayout.layoutManager = layoutManager
            libraryLayout.setController(epoxyController)

            viewModel.state.observe(viewLifecycleOwner, { state ->
                epoxyController.healthProgramLibraryViewState = state
            })
        }.root

}
