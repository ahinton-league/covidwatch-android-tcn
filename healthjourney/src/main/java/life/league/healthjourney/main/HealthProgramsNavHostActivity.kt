package life.league.healthjourney.main

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.navigation.findNavController
import life.league.core.base.RootActivity
import life.league.core.extension.popBackStackOrFinish
import life.league.healthjourney.R
import life.league.healthjourney.databinding.FragmentHealthProgramsNavHostBinding


class HealthProgramsNavHostActivity: RootActivity() {

    companion object {
        fun deepLinkTo(context: Context, uri: Uri): Intent =
                Intent(context, HealthProgramsNavHostActivity::class.java).apply { data = uri }
    }

    private lateinit var binding: FragmentHealthProgramsNavHostBinding
    /**
     * This is to prevent the graph from being reset when the app returns from the background.
     * For some reason if the call to setupGraph is moved earlier in the lifecycle triggering
     * the start destination listener doesn't work correctly
     * All of this messiness should go away if/when we move to single nav host :)
     */
    var graphSet = false

    override fun setupViews(savedInstanceState: Bundle?) {
        binding = FragmentHealthProgramsNavHostBinding.inflate(layoutInflater).apply {
            setContentView(root)
        }
    }

    override fun onStart() {
        super.onStart()
        if (!graphSet) {
            setupGraph()
        }
    }

    private fun setupGraph() {
        binding.healthNavHostFragment.findNavController().apply {
            setGraph(R.navigation.health_journey_nav_graph, Bundle())
            onBackPressedDispatcher.addCallback(this@HealthProgramsNavHostActivity, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    binding.healthNavHostFragment.findNavController().popBackStackOrFinish(this@HealthProgramsNavHostActivity)
                }
            })
        }
        graphSet = true
    }

}
