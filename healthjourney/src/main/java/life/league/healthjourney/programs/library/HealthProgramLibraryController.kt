package life.league.healthjourney.programs.library

import androidx.navigation.NavController
import com.airbnb.epoxy.EpoxyController
import life.league.core.analytics.AnalyticsTracker
import life.league.core.extension.withEachIndexed
import life.league.core.observable.Loaded
import life.league.genesis.widget.banner.basicIconBanner
import life.league.genesis.widget.banner.cobrandingBanner
import life.league.genesis.widget.banner.stackedActionBanner
import life.league.genesis.widget.card.Card
import life.league.genesis.widget.card.CardModel_
import life.league.genesis.widget.card.card
import life.league.genesis.widget.carousel.GenesisCarousel
import life.league.genesis.widget.carousel.genesisCarousel
import life.league.genesis.widget.header.header
import life.league.genesis.widget.loading.loadingSpinner
import life.league.genesis.widget.model.SpacingAttrRes
import life.league.healthjourney.R
import life.league.healthjourney.analytics.*
import life.league.healthjourney.programs.HealthProgramCardViewModel_
import life.league.healthjourney.programs.models.*

class HealthProgramLibraryController(
    private val analyticsTracker: AnalyticsTracker,
    private val navController: NavController?,
    private val getProgramOverline: (HealthProgram) -> String,
    private val getProgramVerboseOverline: (HealthProgram) -> String,
    private val programEnrollmentLimitMessage: String,
    private val displayInfoModal: (Info) -> Unit
) : EpoxyController() {

    private var numberOfActivePrograms: Int? = null
    private var programLimit: Int? = null
    var healthProgramLibraryViewState: HealthProgramLibraryViewModel.HealthProgramLibraryViewState? =
        null
        set(value) {
            field = value
            requestModelBuild()
        }

    override fun buildModels() {
        healthProgramLibraryViewState?.takeIf { it.hasData() }?.run {
            enrollmentLimitModal?.also {
                this@HealthProgramLibraryController.renderEnrollmentLimitBanner(
                    it
                )
            }
            subheading?.also {
                this@HealthProgramLibraryController.header {
                    id(subheading)
                    descriptionText(subheading)
                    marginRes(
                        SpacingAttrRes(
                            topSpacingResId = R.attr.spacing_one,
                            bottomSpacingResId = R.attr.spacing_one,
                            leftSpacingResId = R.attr.spacing_one_and_half,
                            rightSpacingResId = R.attr.spacing_one_and_half
                        )
                    )
                }
            }
            (suggestedCarousels as? Loaded)?.also {
                this@HealthProgramLibraryController.renderHealthProgramsCarousels(it.data.carousels) { program, carouselIndex, carouselName ->
                    this@HealthProgramLibraryController.analyticsTracker.trackRecommendedHealthProgramFromLibrary(
                        programName = program.name,
                        programId = program.id,
                        carouselName = carouselName,
                        carouselIndex = carouselIndex,
                        numberOfActivePrograms = this@HealthProgramLibraryController.numberOfActivePrograms,
                        programLimit = this@HealthProgramLibraryController.programLimit,
                    )
                }
            }
            (categories as? Loaded)?.also {
                this@HealthProgramLibraryController.renderHealthProgramCategories(
                    it.data
                )
            }
            (curatedCarousels as? Loaded)?.also {
                this@HealthProgramLibraryController.renderHealthProgramsCarousels(it.data.carousels) { program, carouselIndex, carouselName ->
                    this@HealthProgramLibraryController.analyticsTracker.trackHealthProgramClickFromLibrary(
                        programName = program.name,
                        programId = program.id,
                        carouselName = carouselName,
                        carouselIndex = carouselIndex,
                        numberOfActivePrograms = this@HealthProgramLibraryController.numberOfActivePrograms,
                        programLimit = this@HealthProgramLibraryController.programLimit,
                    )
                }
            }
            disclaimer?.run {
                this@HealthProgramLibraryController.cobrandingBanner {
                    id(id)
                    titleText(button.text)
                    iconUrl(button.imageAsset.fields.file?.url ?: "")
                    onClick { _ ->
                        this@HealthProgramLibraryController.analyticsTracker.trackFinePrint(header = button.text)
                        this@HealthProgramLibraryController.displayInfoModal.invoke(info)
                    }
                    marginRes(
                        SpacingAttrRes(
                            topSpacingResId = R.attr.spacing_three,
                            leftSpacingResId = R.attr.spacing_one_and_half,
                            rightSpacingResId = R.attr.spacing_one_and_half
                        )
                    )
                }
            }
            (allPrograms as? Loaded)?.also {
                this@HealthProgramLibraryController.numberOfActivePrograms =
                    it.data.numberOfAvailablePrograms
                this@HealthProgramLibraryController.programLimit = it.data.programEnrollmentLimit
                this@HealthProgramLibraryController.renderAllProgramsSection(it.data)
            }
        } ?: renderLoadingState()
    }


    private fun renderEnrollmentLimitBanner(modal: ProgramEnrollmentLimitModal) {
        basicIconBanner {
            id("enrollment_limit_banner")
            descriptionText(this@HealthProgramLibraryController.programEnrollmentLimitMessage)
            isCollapsed(false)
            marginRes(
                SpacingAttrRes(
                    topSpacingResId = R.attr.spacing_one,
                    bottomSpacingResId = R.attr.spacing_one,
                    leftSpacingResId = R.attr.spacing_one_and_half,
                    rightSpacingResId = R.attr.spacing_one_and_half
                )
            )
            backgroundFromAttr(R.attr.color_background_secondary)
            onClick { _ ->
                this@HealthProgramLibraryController.analyticsTracker.trackViewBannerPrompt(
                    bannerCta = this@HealthProgramLibraryController.programEnrollmentLimitMessage,
                    numberOfActivePrograms = this@HealthProgramLibraryController.numberOfActivePrograms,
                    programLimit = this@HealthProgramLibraryController.programLimit
                )
                this@HealthProgramLibraryController.navController?.navigate(
                    HealthProgramLibraryFragmentDirections.actionHealthProgramLibraryFragmentToHealthProgramsLimitMessageDialog(
                        modal
                    )
                )
            }
        }
    }

    private fun renderHealthProgramCategories(categories: HealthProgramsCategories) {
        header {
            id("categories_header")
            headerText(categories.title)
            descriptionText(categories.subtitle)
            marginRes(
                SpacingAttrRes(
                    topSpacingResId = R.attr.spacing_one,
                    bottomSpacingResId = R.attr.spacing_one,
                    leftSpacingResId = R.attr.spacing_one_and_half,
                    rightSpacingResId = R.attr.spacing_one_and_half
                )
            )
        }
        categories.categories.withEachIndexed { index ->
            stackedActionBanner {
                id(this@withEachIndexed.id)
                titleText(this@withEachIndexed.name)
                iconUrl(this@withEachIndexed.iconUrl)
                spanSizeOverride { _, _, _ -> 1 }
                marginRes(
                    if (index.rem(2) == 0) {
                        SpacingAttrRes(
                            leftSpacingResId = R.attr.spacing_one_and_half,
                            rightSpacingResId = R.attr.spacing_half,
                            bottomSpacingResId = R.attr.spacing_half,
                        )
                    } else {
                        SpacingAttrRes(
                            rightSpacingResId = R.attr.spacing_one_and_half,
                            bottomSpacingResId = R.attr.spacing_half,
                        )
                    }

                )
                onClick { _ ->
                    this@HealthProgramLibraryController.analyticsTracker.trackViewFilteredCategory(
                        category = this@withEachIndexed.name,
                        numberOfActivePrograms = this@HealthProgramLibraryController.numberOfActivePrograms,
                        programLimit = this@HealthProgramLibraryController.programLimit
                    )
                    this@HealthProgramLibraryController.navController?.navigate(
                        HealthProgramLibraryFragmentDirections.actionHealthProgramLibraryFragmentToHealthProgramCategoryFragment(
                            this@withEachIndexed.id
                        )
                    )
                }
            }
        }
    }

    private fun renderHealthProgramsCarousels(
        healthProgramCarousels: List<HealthProgramsCarousel>,
        analyticsEvent: (HealthProgram, Int, String) -> Unit
    ) {
        healthProgramCarousels.withEachIndexed {
            header {
                id("${this@withEachIndexed.carouselId}_header")
                headerText(this@withEachIndexed.title)
                descriptionText(this@withEachIndexed.description)
                actionText(R.string.view_all)
                marginRes(
                    SpacingAttrRes(
                        topSpacingResId = R.attr.spacing_one,
                        bottomSpacingResId = R.attr.spacing_one,
                        leftSpacingResId = R.attr.spacing_one_and_half,
                        rightSpacingResId = R.attr.spacing_one_and_half
                    )
                )
                onActionClick { _ ->
                    this@HealthProgramLibraryController.analyticsTracker.trackViewFilteredCategory(
                        category = this@withEachIndexed.title,
                        numberOfActivePrograms = this@HealthProgramLibraryController.numberOfActivePrograms,
                        programLimit = this@HealthProgramLibraryController.programLimit
                    )
                    this@HealthProgramLibraryController.navController?.navigate(
                        HealthProgramLibraryFragmentDirections
                            .actionHealthProgramLibraryFragmentToHealthProgramCategoryFragment(
                                categoryId = this@withEachIndexed.id,
                                healthProgramsCarousel = this@withEachIndexed
                            )
                    )
                }
            }
            this@HealthProgramLibraryController.renderHealthProgramsInCarousel(
                "${carouselId}_carousel",
                programs
            ) { program: HealthProgram, carouselIndex: Int ->
                analyticsEvent(program, carouselIndex, title)
            }
        }
    }

    private fun renderHealthProgramsInCarousel(
        id: String,
        healthPrograms: List<HealthProgram>,
        analyticsEvent: (HealthProgram, Int) -> Unit
    ) {
        genesisCarousel {
            id(id)
            numViewsToShowOnScreen(1.1f)
            paddingAttr(
                GenesisCarousel.PaddingAttr(
                    R.attr.spacing_one_and_half,
                    R.attr.spacing_one,
                    R.attr.spacing_one_and_half,
                    R.attr.spacing_two,
                    R.attr.spacing_three_quarters
                )
            )
            models(
                healthPrograms.take(12).mapIndexed { index, program ->
                    CardModel_().apply {
                        id(program.id)
                        titleText(program.name)
                        descriptionText(program.description)
                        imageUrl(program.imageUrl)
                        titleMaxLines(Int.MAX_VALUE)
                        overlineText(
                            this@HealthProgramLibraryController.getProgramVerboseOverline(
                                program
                            )
                        )
                        onClick { _ ->
                            analyticsEvent(program, index)
                            this@HealthProgramLibraryController.navController?.navigate(
                                HealthProgramLibraryFragmentDirections.actionHealthProgramLibraryFragmentToHealthProgramDetailsFragmentV2(
                                    program.id
                                )
                            )
                        }
                    }
                }
            )
        }
    }

    private fun renderAllProgramsSection(programs: HealthPrograms) {
        header {
            id("all_programs_header")
            headerText(programs.name ?: "")
            descriptionText(programs.description.orEmpty())
            spanSizeOverride { _, _, _ -> 2 }
            marginRes(
                SpacingAttrRes(
                    topSpacingResId = R.attr.spacing_one,
                    bottomSpacingResId = R.attr.spacing_one,
                    leftSpacingResId = R.attr.spacing_one_and_half,
                    rightSpacingResId = R.attr.spacing_one_and_half
                )
            )
        }
        programs.programs.withEachIndexed { index ->
            card {
                id(this@withEachIndexed.id)
                titleText(this@withEachIndexed.name)
                titleMaxLines(Int.MAX_VALUE)
                captionText(this@HealthProgramLibraryController.getProgramOverline(this@withEachIndexed))
                imageUrl(this@withEachIndexed.imageUrl)
                spanSizeOverride { _, _, _ -> 1 }
                marginRes(
                    if (index.rem(2) == 0) {
                        SpacingAttrRes(
                            leftSpacingResId = R.attr.spacing_one_and_half,
                            rightSpacingResId = R.attr.spacing_half,
                            bottomSpacingResId = R.attr.spacing_one_and_half,
                        )
                    } else {
                        SpacingAttrRes(
                            rightSpacingResId = R.attr.spacing_one_and_half,
                            leftSpacingResId = R.attr.spacing_half,
                            bottomSpacingResId = R.attr.spacing_one_and_half,
                        )
                    }
                )
                onClick { _ ->
                    this@HealthProgramLibraryController.navController?.navigate(
                        HealthProgramLibraryFragmentDirections.actionHealthProgramLibraryFragmentToHealthProgramDetailsFragmentV2(
                            this@withEachIndexed.id
                        )
                    )
                }
            }
        }
    }

    private fun renderLoadingState() {
        loadingSpinner { id("loading_spinner") }
    }


}