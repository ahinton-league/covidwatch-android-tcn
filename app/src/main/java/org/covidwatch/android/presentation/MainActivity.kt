package org.covidwatch.android.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.covidwatch.android.R
import org.covidwatch.android.data.LegacyLeagueAuthenticator
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private val authenticator: LegacyLeagueAuthenticator by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        authenticator.signIn(sessionId = "d97845c3cab45ce7fdf87e1aa411defa", deviceToken = "263e8560c6c9b353a3e6d4e3038570eb", userId = "366528200e42c7df924ff86ac87d17d2")

    }
}
