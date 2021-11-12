package life.league.healthjourney

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import life.league.healthjourney.articles.ArticlesActivity
import life.league.healthjourney.main.HealthProgramsNavHostActivity
import life.league.healthjourney.navigation.HealthJourneyDeepLinker
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config


@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.M])
class HealthJourneyDeepLinkerTest {

    @MockK(relaxed = true)
    private lateinit var context: Context

    private val deeplinker = HealthJourneyDeepLinker()

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)
    }

    
    @Test
    fun Articles() {
        val path = HealthJourneyDeepLinker.HealthJourneyPaths.Articles.construct()
        assert(path matches HealthJourneyDeepLinker.HealthJourneyPaths.Articles.path)

        deeplinker.navigateToDeepLink(context, Uri.parse(path))

        val intentSlot = slot<Intent>()
        verify {
            context.startActivity(capture(intentSlot))
        }
        assertEquals(ArticlesActivity::class.java.name, intentSlot.captured.component?.className)
    }


    @Test
    fun `HealthProgramDetails - Construct`() {
        val programId = "program_id"
        val uri = Uri.parse(HealthJourneyDeepLinker.HealthJourneyPaths.HealthProgramDetails.construct(programId))
        deeplinker.navigateToDeepLink(context, uri)

        val intentSlot = slot<Intent>()
        verify {
            context.startActivity(capture(intentSlot))
        }
        assertEquals(uri, intentSlot.captured.data)
        assertEquals(
            HealthProgramsNavHostActivity::class.java.name,
            intentSlot.captured.component?.className
        )
    }

    @Test
    fun `HealthProgramDetails - No base url`() {
        val programId = "program_id"
        val uri = Uri.parse("/app/member/health-programs/$programId")
        deeplinker.navigateToDeepLink(context, uri)

        val intentSlot = slot<Intent>()
        verify {
            context.startActivity(capture(intentSlot))
        }
        assert(intentSlot.captured.data?.scheme != null && intentSlot.captured.data?.host != null)
        assertEquals(
            HealthProgramsNavHostActivity::class.java.name,
            intentSlot.captured.component?.className
        )
    }

    @Test
    fun `HealthProgramsCategory - Construct`() {
        val categoryId = "category_id"
        val uri =
            Uri.parse(HealthJourneyDeepLinker.HealthJourneyPaths.HealthProgramsCategory.construct(categoryId))
        deeplinker.navigateToDeepLink(context, uri)

        val intentSlot = slot<Intent>()
        verify {
            context.startActivity(capture(intentSlot))
        }
        assertEquals(uri, intentSlot.captured.data)
        assertEquals(
            HealthProgramsNavHostActivity::class.java.name,
            intentSlot.captured.component?.className
        )
    }

    @Test
    fun `HealthProgramsCategory - No base url`() {
        val categoryId = "category_id"
        val uri = Uri.parse("/app/member/health-programs-category/$categoryId")
        deeplinker.navigateToDeepLink(context, uri)

        val intentSlot = slot<Intent>()
        verify {
            context.startActivity(capture(intentSlot))
        }
        assert(intentSlot.captured.data?.scheme != null && intentSlot.captured.data?.host != null)
        assertEquals(
            HealthProgramsNavHostActivity::class.java.name,
            intentSlot.captured.component?.className
        )
    }

    @Test
    fun `GoalDetails - Construct`() {
        val uri = Uri.parse(HealthJourneyDeepLinker.HealthJourneyPaths.GoalDetails.construct())
        deeplinker.navigateToDeepLink(context, uri)

        val intentSlot = slot<Intent>()
        verify {
            context.startActivity(capture(intentSlot))
        }
        assertEquals(uri, intentSlot.captured.data)
        assertEquals(
            HealthProgramsNavHostActivity::class.java.name,
            intentSlot.captured.component?.className
        )
    }

    @Test
    fun `GoalDetails - No base url`() {
        val uri = Uri.parse("/app/member/health/goal-details")
        deeplinker.navigateToDeepLink(context, uri)

        val intentSlot = slot<Intent>()
        verify {
            context.startActivity(capture(intentSlot))
        }
        assert(intentSlot.captured.data?.scheme != null && intentSlot.captured.data?.host != null)
        assertEquals(
            HealthProgramsNavHostActivity::class.java.name,
            intentSlot.captured.component?.className
        )
    }

}