package life.league.healthjourney.navigation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import life.league.core.navigation.DeepLink
import life.league.core.navigation.DeepLinker
import life.league.healthjourney.articles.ArticlesActivity
import life.league.healthjourney.main.HealthProgramsNavHostActivity
import life.league.healthjourney.navigation.HealthJourneyDeepLinker.HealthJourneyPaths.*
import life.league.healthjourney.settings.HealthJourneySettings

class HealthJourneyDeepLinker : DeepLinker {

    companion object {
        private const val TAG = "HealthJourneyDeepLinker"
    }

    sealed class HealthJourneyPaths(path: Regex) : DeepLink(path) {
        object Health : HealthJourneyPaths(Regex("^/member/health")) {
            override val construct = { "/member/health" }
        }

        object HealthJourneyItemLink :
            HealthJourneyPaths(Regex("^(/app)?/member/health-journey/activity/([^/^?]+)\$")) {
            override val construct =
                { hjItemId: String -> "${HealthJourneySettings.internalBaseUrl}/member/health-journey/activity/${hjItemId}" }
        }

        object HealthProgramsProgress : HealthJourneyPaths(Regex("^(/app)?/member/health-progress$")) {
            override val construct = { "/app/member/health-progress" }
        }

        object HealthJourney : HealthJourneyPaths(Regex("^(/app)?/member/health-journey$")) {
            enum class HealthJourneyTab {
                Timeline, Progress
            }

            override val construct =
                { tab: HealthJourneyTab -> "${HealthJourneySettings.internalBaseUrl}/member/health-journey?tab=${tab.ordinal}" }
        }

        object HealthPrograms : HealthJourneyPaths(Regex("^(/app)?/member/health-programs$")) {
            override val construct = { "/app/member/health-programs" }
        }

        object HealthProgramsCategory :
            HealthJourneyPaths(Regex("^(/app)?/member/health-programs-category/([^/^?]+)\$")) {
            override val construct =
                { categoryId: String -> "${HealthJourneySettings.internalBaseUrl}/app/member/health-programs-category/$categoryId" }
        }

        object HealthProgramDetails :
            HealthJourneyPaths(Regex("^(/app)?/member/health-programs/([^/^?]+)$")) {
            override val construct =
                { programSlug: String -> "${HealthJourneySettings.internalBaseUrl}/app/member/health-programs/$programSlug" }
        }

        object Articles : HealthJourneyPaths(Regex("^/member/health/articles")) {
            override val construct = { "/member/health/articles" }
        }

        object GoalDetails : HealthJourneyPaths(Regex("^(/app)?/member/health/goal-details")) {
            override val construct =
                { "${HealthJourneySettings.internalBaseUrl}/app/member/health/goal-details" }
        }

    }


    override fun navigateToDeepLink(
        context: Context,
        url: Uri,
        navController: NavController?,
        navOptions: NavOptions?
    ): Boolean {

        val path: String = "/" + TextUtils.join("/", url.pathSegments)
        return when {

            listOf(
                HealthProgramDetails.path,
                HealthProgramsCategory.path,
                GoalDetails.path,
                HealthJourneyItemLink.path
            ).any { path matches it } -> {
                context.startActivity(
                    HealthProgramsNavHostActivity.deepLinkTo(
                        context,
                        // Replace scheme to ensure the link is recognized by Jetpack navigation deeplinking
                        ("${HealthJourneySettings.internalBaseUrl}$path").toUri()
                    )
                )
                true
            }
            path matches Articles.path -> {
                context.startActivity(Intent(context, ArticlesActivity::class.java))
                true
            }
            path matches HealthPrograms.path -> false
            path matches Health.path -> false
            else -> false
        }
    }

}
