package life.league.healthjourney.programs.progress

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import life.league.core.analytics.AnalyticsTracker
import life.league.genesis.R
import life.league.healthjourney.analytics.viewHealthProgramLimitReached
import life.league.healthjourney.databinding.FragmentHealthProgramsLimitMesssageDialogBinding
import org.koin.android.ext.android.inject


class HealthProgramsLimitMessageDialog : BottomSheetDialogFragment() {

    private val args: HealthProgramsLimitMessageDialogArgs by navArgs()
    private val analyticsTracker: AnalyticsTracker by inject()

    override fun onResume() {
        super.onResume()
        analyticsTracker.viewHealthProgramLimitReached()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentHealthProgramsLimitMesssageDialogBinding.inflate(inflater, container, false).apply {
        header.text = args.programEnrollmentLimitModal.title
        message.text = args.programEnrollmentLimitModal.description
        button.setOnClickListener { findNavController().popBackStack() }
    }.root

}
