package life.league.healthjourney.injection

import life.league.healthjourney.articles.ArticlesViewModel
import life.league.healthjourney.journey.*
import life.league.healthjourney.journey.timeline.HealthJourneyDayPagerViewModel
import life.league.healthjourney.programs.details.HealthProgramDetailsViewModel
import life.league.healthjourney.programs.library.HealthProgramCategoryViewModel
import life.league.healthjourney.programs.library.HealthProgramLibraryViewModel
import life.league.healthjourney.programs.preview.HealthJourneyPreviewViewModel
import life.league.healthjourney.programs.progress.HealthProgramsProgressAchievementsViewModel
import life.league.healthjourney.programs.progress.HealthProgramsProgressViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object HealthJourneyViewModelModule {

    val module = module {
        viewModel { HealthJourneyPreviewViewModel(healthJourneyRepository = get()) }


        viewModel { ArticlesViewModel(userRepository = get()) }

        viewModel { HealthJourneyViewModel() }
        viewModel { HealthJourneyTimelineViewModel(healthJourneyRepository = get()) }
        viewModel { HealthJourneyDayPagerViewModel(getHealthJourneyItemsForDay = get()) }

        viewModel { (healthJourneyItemId: String?, campaignId: String?, activityId: String?) ->
            HealthJourneyItemViewModel(
                healthJourneyItemId = healthJourneyItemId,
                campaignId = campaignId,
                activityId = activityId,
                healthJourneyRepository = get(),
                pointsRepository = get(),
                featureFlagsUtils = get(),
                useCase = if (HealthJourney.configuration.achievementsEnabled) get() else null
            )
        }
        viewModel { (healthJourneyItemId: String?, healthProgramId: String?) ->
            HealthJourneyRemovalConfirmationViewModel(
                healthJourneyItemId = healthJourneyItemId,
                healthProgramId = healthProgramId,
                healthJourneyRepository = get(),
                healthProgramsRepository = get(),
            )
        }
        viewModel { HealthProgramsProgressViewModel(healthProgramsRepository = get()) }
        viewModel { HealthProgramsProgressAchievementsViewModel(progressInfoUseCase = get()) }
        viewModel { HealthProgramLibraryViewModel(healthProgramsRepository = get()) }
        viewModel { HealthProgramCategoryViewModel(healthProgramsRepository = get()) }
        viewModel { (programId: String) ->
            HealthProgramDetailsViewModel(programId = programId, healthProgramsRepository = get())
        }

    }
}