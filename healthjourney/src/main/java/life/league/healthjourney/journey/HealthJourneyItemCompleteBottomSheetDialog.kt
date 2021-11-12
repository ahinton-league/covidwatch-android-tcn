package life.league.healthjourney.journey

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import life.league.core.analytics.AnalyticsTracker
import life.league.genesis.extension.loadUrlAndSetVisibility
import life.league.genesis.extension.setTextAndVisibility
import life.league.healthjourney.R
import life.league.healthjourney.databinding.FragmentHealthJourneyItemCompleteBinding
import life.league.healthjourney.analytics.trackCloseActivityComplete
import life.league.healthjourney.analytics.viewHealthJourneyActivityCompleted
import life.league.healthjourney.main.HealthProgramsNavHostActivity
import org.koin.android.ext.android.inject

class HealthJourneyItemCompleteBottomSheetDialog : BottomSheetDialogFragment() {

    private val args: HealthJourneyItemCompleteBottomSheetDialogArgs by navArgs()
    private lateinit var binding: FragmentHealthJourneyItemCompleteBinding
    private val analyticsTracker: AnalyticsTracker by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHealthJourneyItemCompleteBinding.inflate(inflater)
        analyticsTracker.viewHealthJourneyActivityCompleted()
        binding.apply {
            title.setTextAndVisibility(args.healthJourneyItemComplete.title)
            overline.setTextAndVisibility(args.healthJourneyItemComplete.eyebrowHeadline)
            belowline.setTextAndVisibility(args.healthJourneyItemComplete.descriptionOne)
            alert.setTextAndVisibility(args.healthJourneyItemComplete.rewardsMessage)
            description.setTextAndVisibility(args.healthJourneyItemComplete.descriptionTwo)
            image.loadUrlAndSetVisibility(args.healthJourneyItemComplete.image)
            button.setText(getString(R.string.close))
            button.setOnClick {
                args.healthJourneyItem?.run {
                    analyticsTracker.trackCloseActivityComplete(type, name, id)
                }
                dismiss()
            }
            close.setOnClickListener {
                args.healthJourneyItem?.run {
                    analyticsTracker.trackCloseActivityComplete(type, name, id)
                }
                dismiss()
            }
        }
        return binding.root
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        args.healthJourneyItem?.run {
            analyticsTracker.trackCloseActivityComplete(type, name, id)
        }
        if (activity is HealthProgramsNavHostActivity) {
            activity?.finish()
        } else {
            findNavController().navigate(
                    HealthJourneyItemCompleteBottomSheetDialogDirections
                            .actionHealthJourneyItemCompleteBottomSheetDialogPop())
        }
    }
}


