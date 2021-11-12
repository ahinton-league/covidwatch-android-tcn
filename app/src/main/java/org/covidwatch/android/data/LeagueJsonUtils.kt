package org.covidwatch.android.data

import life.league.core.util.BaseJsonUtils
import life.league.core.util.ApplyModuleAdapters

class LeagueJsonUtils(vararg applyModuleAdapters: ApplyModuleAdapters) : BaseJsonUtils(*applyModuleAdapters) {

    override fun getAPISpecificJsonAdapters(): List<Any> =
        listOf()

}