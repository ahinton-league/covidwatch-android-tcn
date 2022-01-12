package org.covidwatch.android.presentation

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import org.covidwatch.android.R
import org.covidwatch.android.data.LegacyLeagueAuthenticator
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        CovidWatchDeeplinkHandler.deeplinkListener = {
            findNavController(R.id.nav_host_fragment).setGraph(R.navigation.navigation_main)
            findNavController(R.id.nav_host_fragment).navigate(Uri.parse(it))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        CovidWatchDeeplinkHandler.deeplinkListener = null
    }

}
