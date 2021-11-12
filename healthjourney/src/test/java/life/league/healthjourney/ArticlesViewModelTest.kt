package life.league.healthjourney

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf
import life.league.core.model.user.User
import life.league.core.model.user.UserProfile
import life.league.core.observable.Loaded
import life.league.core.observable.Loading
import life.league.core.observable.State
import life.league.core.repository.UserRepository
import life.league.core.test.rule.TestCoroutineRule
import life.league.healthjourney.articles.ArticlesViewModel
import life.league.networking.callback.Failure
import life.league.networking.callback.Success
import org.junit.Rule
import org.junit.Test


class ArticlesViewModelTest {

    // This rule is needed to mock observers on MutableLiveData
    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineRule = TestCoroutineRule()

    @MockK
    private lateinit var userRepository: UserRepository


    @MockK
    private lateinit var articlesObserver: Observer<State<Int>>


    private lateinit var articlesViewModel: ArticlesViewModel

    companion object {
        private const val USER_ID = "UserId"
    }

    private fun mockedUserProfile(isHrAdmin: Boolean) = mockk<UserProfile>().also {
        every {
            it.user
        } returns mockedUser(isHrAdmin)
    }

    private fun mockedUser(isHrAdmin: Boolean) = mockk<User>().also {
        every {
            it.isHrAdmin
        } returns isHrAdmin
    }


    fun resetMocks() {
        MockKAnnotations.init(this, relaxUnitFun = true)

        articlesViewModel = ArticlesViewModel(userRepository)
        articlesViewModel.articlesUrlStringRes.observeForever(articlesObserver)
    }

    data class TestCase(
        val isHrAdmin: Boolean,
        val isError: Boolean,
        val expected: State<Int>
    )

    private val memberUrlRes = R.string.get_inspired_member_url
    private val adminUrlRes = R.string.get_inpsired_admin_url

    private val testCases = arrayOf(
        TestCase(isHrAdmin = true, isError = false, expected = Loaded(adminUrlRes)),
        TestCase(isHrAdmin = false, isError = false, expected = Loaded(memberUrlRes)),
        TestCase(isHrAdmin = true, isError = true, expected = Loaded(memberUrlRes)),
        TestCase(isHrAdmin = false, isError = true, expected = Loaded(memberUrlRes))
    )

    @Test
    fun `Articles - load url - cached, url only loaded once`() {

        resetMocks()
        every {
            userRepository.getUser()
        } returns flowOf(
            Success(mockedUserProfile(true)),
            Success(mockedUserProfile(true))
        )

        articlesViewModel.fetchArticlesUrl()

        verifySequence {
            articlesObserver.onChanged(Loading())
            articlesObserver.onChanged(any())
        }

    }

    @Test
    fun `Articles - load url - not cached`() {
        testCases.forEach { test ->

            resetMocks()

            every {
                userRepository.getUser()
            } answers {
                flowOf(
                    // extension methods set the extension object to the first argument, that's why callback is the second
                    if (test.isError) {
                        Failure("")
                    } else {
                        Success(mockedUserProfile(test.isHrAdmin))
                    }
                )
            }

            articlesViewModel.fetchArticlesUrl()

            verifyOrder {
                articlesObserver.onChanged(Loading())
                articlesObserver.onChanged(test.expected)
            }
        }

    }
}
