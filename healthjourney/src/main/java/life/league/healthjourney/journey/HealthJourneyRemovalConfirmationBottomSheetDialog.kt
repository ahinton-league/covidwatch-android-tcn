package life.league.healthjourney.journey

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import life.league.core.analytics.AnalyticsTracker
import life.league.core.observable.Failed
import life.league.core.observable.Loaded
import life.league.core.observable.Loading
import life.league.genesis.widget.button.Button
import life.league.healthjourney.R
import life.league.healthjourney.analytics.trackCancelRemoval
import life.league.healthjourney.analytics.trackCloseRemoval
import life.league.healthjourney.analytics.trackConfirmRemoval
import life.league.healthjourney.databinding.FragmentHealthJourneyRemovalConfirmationBinding
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class HealthJourneyRemovalConfirmationBottomSheetDialog : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentHealthJourneyRemovalConfirmationBinding
    private val args: HealthJourneyRemovalConfirmationBottomSheetDialogArgs by navArgs()
    private val viewModel: HealthJourneyRemovalConfirmationViewModel by viewModel {
        parametersOf(args.healthJourneyItem?.id, args.healthProgram?.userProgramId)
    }
    private val analyticsTracker: AnalyticsTracker by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentHealthJourneyRemovalConfirmationBinding.inflate(inflater, container, false)
            .apply {
                binding = this
                message.text = getString(
                    if (args.healthProgram != null)
                        R.string.remove_program_confirmation_message
                    else R.string.remove_activity_confirmation_message)

                submessage.apply {
                    isVisible = args.healthProgram != null
                    text = getString(R.string.leave_program_disclaimer)
                }

                close.setOnClickListener {
                    args.healthJourneyItem?.run {
                        analyticsTracker.trackCloseRemoval(type, name, id)
                    }
                    dismiss()
                }
                cancel.setOnClick {
                    args.healthJourneyItem?.run {
                        analyticsTracker.trackCancelRemoval(type, name, id)
                    }
                    dismiss()
                }
                button.setOnClickListener {
                    args.healthJourneyItem?.run {
                        analyticsTracker.trackConfirmRemoval(type, name, id)
                    }
                    viewModel.removeActivity()
                }
            }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.healthJourneyRemovalComplete.observe(viewLifecycleOwner, {
            when (it) {
                is Loaded -> {
                    findNavController().navigate(
                        HealthJourneyRemovalConfirmationBottomSheetDialogDirections
                            .actionHealthJourneyRemovalConfirmationBottomSheetDialogToHealthJourneyRemovalSuccessBottomSheetDialog(
                                healthJourneyItem = args.healthJourneyItem,
                                healthProgram = args.healthProgram
                            )
                    )
                    binding.button.setLoadingStateOff()
                    binding.cancel.setButtonStyle(Button.BUTTON_STYLE_MINIMAL)
                }
                is Loading -> {
                    binding.button.setLoadingStateOn()
                    binding.cancel.setButtonStyle(Button.BUTTON_STYLE_DISABLED)
                }
                is Failed -> {
                    binding.button.setLoadingStateOff()
                    binding.cancel.setButtonStyle(Button.BUTTON_STYLE_MINIMAL)
                }
            }
        })
    }
}


