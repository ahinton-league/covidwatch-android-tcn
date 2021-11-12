package life.league.healthjourney.articles

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import life.league.core.model.user.User
import life.league.core.observable.Loaded
import life.league.core.observable.Loading
import life.league.core.observable.State
import life.league.core.repository.UserRepository
import life.league.healthjourney.R
import life.league.networking.callback.Failure
import life.league.networking.callback.Success

class ArticlesViewModel(private val userRepository: UserRepository) : ViewModel() {

    val articlesUrlStringRes: LiveData<State<Int>> = MutableLiveData()

    fun fetchArticlesUrl() {
        viewModelScope.launch {
            (articlesUrlStringRes as MutableLiveData).postValue(Loading())
            userRepository.getUser().collect { result ->
                if (articlesUrlStringRes.value !is Loaded<Int>) {
                    when (result) {
                        is Success -> {
                            handleUser(result.response.user)
                        }
                        is Failure -> {
                            articlesUrlStringRes.postValue(Loaded(R.string.get_inspired_member_url))
                        }
                    }
                }
            }
        }
    }

    private fun handleUser(user: User) {
        val state: State<Int> =
            if (user.isHrAdmin) {
                Loaded(R.string.get_inpsired_admin_url)
            } else {
                Loaded(R.string.get_inspired_member_url)
            }
        (articlesUrlStringRes as MutableLiveData).postValue(state)
    }
}