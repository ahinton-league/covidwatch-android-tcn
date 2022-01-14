package org.covidwatch.android.presentation.controls

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import life.league.healthjourney.settings.ComposeContentProvider
import org.covidwatch.android.R

class ComposeTestResultsBannerProvider : ComposeContentProvider() {

    // Ideally we'd use a view model to manage state so that it can survive
    // onConfiguration changes
    private var dataLoaded by mutableStateOf(false)
    private var testResult by mutableStateOf("")

    override fun refreshData() {
        // this data would be loaded via a ViewModel/API rather than hard coded
        testResult = "NEGATIVE - Jan 11, 2022"
        dataLoaded = true
    }

    @Composable
    override fun Content(deeplinkHandler: (url: String) -> Unit) {
        if (dataLoaded) {
            // Can apply any theme desired, of it nothing is applied the genesis theme will be used
            MaterialTheme {
                TestResultsBanner(testResult = testResult, deeplinkHandler = deeplinkHandler)
            }
        }
    }
}

@Preview
@Composable
fun TestResultsBannerPreview() {
    MaterialTheme() {
        TestResultsBanner(testResult = "UNKNOWN", deeplinkHandler = {})
    }
}

@Composable
fun TestResultsBanner(testResult: String, deeplinkHandler: (url: String) -> Unit) {
    ComposeBanner(
        modifier = Modifier.fillMaxWidth(),
        title = "Update COVID Test Results", body = "Previous Result: $testResult",
        onClick = {
            deeplinkHandler("https://covidwatch.com/testresults")
        }
    ) {
        Image(
            modifier = Modifier.size(48.dp),
            painter = painterResource(id = R.drawable.ic_logo),
            contentDescription = null
        )
    }
}