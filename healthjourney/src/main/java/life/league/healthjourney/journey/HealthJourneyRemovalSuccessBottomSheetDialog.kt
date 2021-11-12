package life.league.healthjourney.journey

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import life.league.genesis.databinding.WidgetSingleActionBottomSheetDialogBinding
import life.league.genesis.extension.getDrawableFromAttr
import life.league.healthjourney.R
import life.league.core.analytics.AnalyticsTracker
import life.league.core.base.RootActivity
import life.league.core.extension.findNavControllerSafely
import life.league.healthjourney.analytics.trackCloseActivityRemoved
import life.league.healthjourney.analytics.trackLeaveProgramFeedback
import life.league.healthjourney.analytics.trackSkipLeaveProgramFeedback
import life.league.healthjourney.analytics.viewHealthJourneyActivityRemoved
import life.league.healthjourney.main.HealthProgramsNavHostActivity
import life.league.healthjourney.journey.models.PulseCheckScenario
import org.koin.android.ext.android.inject

class HealthJourneyRemovalSuccessBottomSheetDialog : BottomSheetDialogFragment() {

    private val args: HealthJourneyRemovalSuccessBottomSheetDialogArgs by navArgs()
    private lateinit var binding: WidgetSingleActionBottomSheetDialogBinding
    private val analyticsTracker: AnalyticsTracker by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
            analyticsTracker.viewHealthJourneyActivityRemoved()
            binding = WidgetSingleActionBottomSheetDialogBinding.inflate(inflater)
            binding.apply {
                title.text = getString(if (args.healthProgram != null) R.string.program_removed else R.string.activity_removed)
                description.text = getString(R.string.no_longer_see_in_journey)
                image.setImageDrawable(requireContext().getDrawableFromAttr(R.attr.drawable_health_journey_item_removed))
                val pulseCheck = args.healthProgram?.campaignContentConfig?.pulseChecks?.filter { it.scenario == PulseCheckScenario.CAMPAIGN_REMOVED.value }?.getOrNull(0)
                button.setText(pulseCheck?.ctaText ?: getString(R.string.close))
                button.setOnClick {
                    args.healthProgram?.run {
                        analyticsTracker.trackCloseActivityRemoved("", name , id) //TODO Find a proper
                        pulseCheck?.ctaUrl?.let {
                            analyticsTracker.trackLeaveProgramFeedback(name, id)
                            (activity as? RootActivity)?.navigateToDeeplink(
                                it,
                                true,
                                false,
                                navController = findNavController()
                            )
                        }
                    }
                    dismiss()
                }
                close.setOnClickListener {
                    args.healthJourneyItem?.run {
                        analyticsTracker.trackCloseActivityRemoved(type, name, id)
                        pulseCheck?.ctaUrl?.let {
                            analyticsTracker.trackSkipLeaveProgramFeedback(name, id)
                        }
                    }
                    dismiss()
                }
            }
        return binding.root
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        args.healthJourneyItem?.run {
            analyticsTracker.trackCloseActivityRemoved(type, name, id)
        }

        if (activity is HealthProgramsNavHostActivity) {
            activity?.finish()
        } else {
            if (args.healthJourneyItem != null) {
                findNavController().navigate(
                    HealthJourneyRemovalSuccessBottomSheetDialogDirections
                        .actionHealthJourneyItemRemovedBottomSheetDialogPopToHealthJourneyActivityFragment())
            } else {
                findNavController().navigate(
                    HealthJourneyRemovalSuccessBottomSheetDialogDirections
                        .actionHealthJourneyItemRemovedBottomSheetDialogPopToHealthProgramDetailsFragmentV2())
            }

        }
    }
}


