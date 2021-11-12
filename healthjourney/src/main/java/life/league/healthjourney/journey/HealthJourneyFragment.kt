package life.league.healthjourney.journey

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import life.league.core.analytics.AnalyticsTracker
import life.league.core.base.RootFragment
import life.league.core.util.Log
import life.league.core.util.featureflags.FeatureFlagsUtils
import life.league.genesis.extension.getColorFromAttr
import life.league.healthjourney.R
import life.league.healthjourney.analytics.trackGoToProgramLibrary
import life.league.healthjourney.analytics.trackSelectActivitiesTab
import life.league.healthjourney.analytics.trackSelectProgressTab
import life.league.healthjourney.databinding.HealthJourneyFragmentBinding
import life.league.healthjourney.featureflags.HealthJourneyFeatureFlags
import life.league.healthjourney.journey.timeline.HealthJourneyDayPagerFragment
import life.league.healthjourney.programs.progress.HealthProgramsProgressAchievementsFragment
import life.league.healthjourney.programs.progress.HealthProgramsProgressFragment
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


@ExperimentalPagerApi
@Suppress("DEPRECATION")
class HealthJourneyFragment : RootFragment() {

    private lateinit var binding: HealthJourneyFragmentBinding
    private val args: HealthJourneyFragmentArgs by navArgs()
    private val viewModel: HealthJourneyViewModel by sharedViewModel()
    private val analyticsTracker: AnalyticsTracker by inject()
    private val featureFlagsUtils: FeatureFlagsUtils by inject()

    companion object {
        private const val TAG = "HealthJourneyFragment"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View =
            HealthJourneyFragmentBinding.inflate(inflater, container, false).apply {
                binding = this

                jumpToTodayButton.apply {
                    isVisible = featureFlagsUtils.getValue(HealthJourneyFeatureFlags.healthJourneyRevamp)
                    DrawableCompat.setTint(
                        drawable,
                        requireContext().getColorFromAttr(R.attr.color_fill_tertiary)
                    )
                    setOnClickListener {
                        viewModel.onJumpToTodayClicked()
                    }
                }

                pager.adapter = JourneyAdapter(this@HealthJourneyFragment)
                pager.currentItem =
                    try {
                        args.tabIndex
                    } catch (e: Exception) { 
                        Log.e(TAG, "HealthJourneyFragment arguments were null")
                        0
                    }
                pager.isUserInputEnabled = false
                addButton.apply {
                    val buttonCta = getString(R.string.add_all_caps)
                    setText(buttonCta)
                    setLoadingStateOff()
                    setBackgroundColorAttr(R.attr.color_background_button_primary)
                    setTextColorAttr(R.attr.color_text_light)
                    setOnClick {
                        analyticsTracker.trackGoToProgramLibrary(buttonCtaText = buttonCta)
                        findNavControllerSafely()?.navigate(HealthJourneyFragmentDirections.actionHealthJourneyFragmentToHealthProgramLibraryFragment())
                    }
                }

            }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.setupTabLayout()
    }

    private fun HealthJourneyFragmentBinding.setupTabLayout() {

        TabLayoutMediator(tabLayout, pager) { tab, position ->
            tab.text = getString(when (position) {
                0 -> R.string.activities
                else -> R.string.progress
            })
        }.attach()

        tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.position?.trackTabSelection()
            }
            override fun onTabReselected(tab: TabLayout.Tab?) {
                tab?.position?.trackTabSelection()
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) = Unit
        })
    }

    private fun Int.trackTabSelection() {
        analyticsTracker.run {
            when (this@trackTabSelection) {
                0 -> trackSelectActivitiesTab()
                1 -> trackSelectProgressTab()
            }
        }
    }

    @ExperimentalPagerApi
    private inner class JourneyAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment = when (position) {
            0 -> if (featureFlagsUtils.getValue(HealthJourneyFeatureFlags.healthJourneyRevamp)) HealthJourneyDayPagerFragment() else HealthJourneyTimelineFragment()
            else -> {
                if (featureFlagsUtils.getValue(HealthJourneyFeatureFlags.achievements)) {
                    HealthProgramsProgressAchievementsFragment()
                } else {
                    HealthProgramsProgressFragment()
                }
            }
        }
    }

}