package org.covidwatch.android.presentation.controls

import com.airbnb.epoxy.EpoxyController
import life.league.genesis.widget.model.SpacingAttrRes
import life.league.healthjourney.settings.EpoxyModelsProvider
import org.covidwatch.android.R

class EpoxyTestResultsBannerProvider : EpoxyModelsProvider() {
    private var dataLoaded : Boolean = false
    private var testResult : String = ""

    override fun fetchData() {
        // the value test result would typically be loaded via a ViewModel/API rather than hard coded
        testResult = "NEGATIVE - Jan 11, 2022"
        dataLoaded = true
        requestBuildModel()
    }

    override fun buildModels(controller: EpoxyController, deeplinkHandler: (url: String) -> Unit) {
        if (dataLoaded) {
            controller.normalBanner {
                id("covidwatch_test_results") // id is a required field
                titleText("Your last covid test result:")
                descriptionText(testResult)
                actionText("Add new test results")
                onClick{ _->
                    deeplinkHandler("https://covidwatch.com/testresults")
                }
                backgroundImageResource(R.drawable.bg_splash_fragment)
                marginRes(
                    SpacingAttrRes(
                        leftSpacingResId = life.league.healthjourney.R.attr.spacing_one_and_half,
                        rightSpacingResId = life.league.healthjourney.R.attr.spacing_one_and_half,
                        topSpacingResId = life.league.healthjourney.R.attr.spacing_one
                    )
                )
            }
        }
    }
}