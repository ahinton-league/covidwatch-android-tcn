package org.covidwatch.android.data

import life.league.core.environment.CoreEnvironmentConfiguration
import life.league.core.model.Environment

object EnvironmentConfiguration: CoreEnvironmentConfiguration() {

    @JvmStatic
    val environments = getEnvironmentList("stage")

    @JvmStatic
    fun getEnvironment(name: String): Environment? {
        for (environment in environments) {
            if (name == environment.name) {
                return environment
            }
        }
        return null
    }
}