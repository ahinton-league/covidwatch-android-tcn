package life.league.rewards.viewallachievements

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import life.league.core.observable.Loading
import life.league.core.observable.State
import life.league.core.observable.Uninitialized
import life.league.rewards.model.UserAchievement
import life.league.rewards.usecase.AchievementUseCase

class ViewAllAchievementsViewModel(private val achievementUseCase: AchievementUseCase) : ViewModel() {

    var userAchievement by mutableStateOf<State<UserAchievement>>(Uninitialized())
        private set

    fun getUserAchievements() {
        userAchievement = Loading()

        viewModelScope.launch {
            achievementUseCase.fetchAllAchievements().collect {
                userAchievement = it
            }
        }
    }

}