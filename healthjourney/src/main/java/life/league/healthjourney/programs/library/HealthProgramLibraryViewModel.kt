package life.league.healthjourney.programs.library

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import life.league.core.observable.Failed
import life.league.core.observable.Loaded
import life.league.core.observable.Loading
import life.league.core.observable.State
import life.league.healthjourney.programs.models.*
import life.league.healthjourney.programs.repository.HealthProgramsRepository
import life.league.networking.callback.Failure
import life.league.networking.callback.Success


class HealthProgramLibraryViewModel(
    private val healthProgramsRepository: HealthProgramsRepository
) : ViewModel() {

    data class HealthProgramLibraryViewState(
        val enrollmentLimitModal: ProgramEnrollmentLimitModal? = null,
        val subheading: String? = null,
        val curatedCarousels: State<HealthProgramsCarousels> = Loading(),
        val categories: State<HealthProgramsCategories> = Loading(),
        val suggestedCarousels: State<HealthProgramsCarousels> = Loading(),
        val disclaimer: Modal? = null,
        val allPrograms: State<HealthPrograms> = Loading(),
    ) {
        fun hasData() =
            suggestedCarousels is Loaded || curatedCarousels is Loaded || allPrograms is Loaded
    }

    private val curatedCarousels: Flow<State<HealthProgramsCarousels>> =
        healthProgramsRepository.getCuratedHealthProgramsCarouselsForLibrary()
            .map { response ->
                when (response) {
                    is Success -> Loaded(response())
                    is Failure -> Failed(response.errorResponse)
                }
            }
            .onStart { emit(Loading()) }

    private val suggestedCarousels: Flow<State<HealthProgramsCarousels>> =
        healthProgramsRepository.getSuggestedCarousels()
            .map { response ->
                when (response) {
                    is Success -> Loaded(response())
                    is Failure -> Failed(response.errorResponse)
                }
            }
            .onStart { emit(Loading()) }

    private val healthProgramsCategories: Flow<State<HealthProgramsCategories>> =
        healthProgramsRepository.getHealthProgramsCategories()
            .map { response ->
                when (response) {
                    is Success -> Loaded(response().copy(categories = response().categories.sortedBy { it.name }))
                    is Failure -> Failed(response.errorResponse)
                }
            }
            .onStart { emit(Loading()) }

    private val allPrograms: Flow<State<HealthPrograms>> =
        healthProgramsRepository.getAllHealthPrograms()
            .map { response ->
                when (response) {
                    is Success -> Loaded(response().copy(programs = response().programs.sortedBy { it.name }))
                    is Failure -> Failed(response.errorResponse)
                }
            }
            .onStart { emit(Loading()) }

    @Suppress("Deprecation")
    val state: LiveData<HealthProgramLibraryViewState> = combine(
        curatedCarousels,
        suggestedCarousels,
        allPrograms,
        healthProgramsCategories
    ) { curatedCarousel, suggestedCarousels, allPrograms, categories ->
        HealthProgramLibraryViewState(
            allPrograms = allPrograms,
            enrollmentLimitModal = (allPrograms as? Loaded)?.data?.programLimitModal.takeIf { (allPrograms as? Loaded)?.data?.numberOfAvailablePrograms == 0 },
            subheading = (allPrograms as? Loaded)?.data?.subheading,
            disclaimer = (allPrograms as? Loaded)?.data?.disclaimer,
            suggestedCarousels = suggestedCarousels,
            curatedCarousels = curatedCarousel,
            categories = categories,
        )
    }.asLiveData()

}
