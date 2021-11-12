package life.league.healthjourney.programs.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import com.airbnb.epoxy.EpoxyController
import life.league.core.analytics.AnalyticsTracker
import life.league.core.base.RootFragment
import life.league.core.extension.displayErrorDialog
import life.league.core.extension.popBackStackOrFinish
import life.league.core.extension.takeIfNotEmpty
import life.league.core.extension.withEach
import life.league.core.observable.Failed
import life.league.core.observable.Loaded
import life.league.core.observable.Loading
import life.league.core.util.featureflags.FeatureFlagsUtils
import life.league.genesis.widget.banner.basicIconBanner
import life.league.genesis.widget.banner.cobrandingBanner
import life.league.genesis.widget.button.Button.Companion.BUTTON_STYLE_SECONDARY
import life.league.genesis.widget.button.button
import life.league.genesis.widget.dialog.BottomSheetDialog
import life.league.genesis.widget.dialog.ContentConfirmDialog
import life.league.genesis.widget.dialog.ContentProviderInfo
import life.league.genesis.widget.header.Header
import life.league.genesis.widget.header.header
import life.league.genesis.widget.header.progressHeader
import life.league.genesis.widget.loading.loadingSpinner
import life.league.genesis.widget.model.SpacingAttrRes
import life.league.genesis.widget.row.ActionRow
import life.league.genesis.widget.row.actionRow
import life.league.healthjourney.R
import life.league.healthjourney.analytics.*
import life.league.healthjourney.databinding.FragmentHealthProgramDetailsV2Binding
import life.league.healthjourney.featureflags.HealthJourneyFeatureFlags
import life.league.healthjourney.journey.models.CampaignMode
import life.league.healthjourney.navigation.HealthJourneyDeepLinker
import life.league.healthjourney.programs.models.Goal
import life.league.healthjourney.programs.models.HealthProgramDetails
import life.league.healthjourney.utils.DataPointsUtil
import life.league.healthjourney.utils.generateProgressBars
import life.league.healthjourney.utils.getOverline
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class HealthProgramDetailsFragment : RootFragment() {

    companion object {
        const val IS_WEARABLES_CONNECTED = "isWearablesConnected"
    }
    private val viewModel: HealthProgramDetailsViewModel by viewModel {
        parametersOf(args.programId)
    }
    private val args: HealthProgramDetailsFragmentArgs by navArgs()
    private val analyticsTracker: AnalyticsTracker by inject()
    private val featureFlagsUtils: FeatureFlagsUtils by inject()

    private var showWearableFlow = false
    private var automaticProgramCTA: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentHealthProgramDetailsV2Binding.inflate(inflater, container, false).apply {
        toolbar.setNavigationOnClickListener {
            findNavControllerSafely()?.popBackStackOrFinish(requireActivity())
        }
        viewModel.healthProgram.observe(viewLifecycleOwner, { healthProgram ->

            layout.withModels {
                when (healthProgram) {
                    is Failed -> context?.displayErrorDialog { viewModel.getHealthProgram() }
                    is Loaded -> {
                        action.apply {
                            setOnClick {
                                analyticsTracker.trackEnrollInProgram(
                                    buttonCta = getText().toString(),
                                    programId = healthProgram().id,
                                    programName = healthProgram().name
                                )
                                healthProgram().campaignContentConfig?.dataFields?.takeIfNotEmpty()
                                    ?.also {
                                        automaticProgramCTA =
                                            healthProgram().campaignContentConfig?.ctaUrl
                                        showDialogToGetWearablesConsent(it as List<String>, healthProgram().id)
                                    } ?: viewModel.addProgramToJourney()
                            }
                        }
                        renderHealthProgram(healthProgram())

                        action.isVisible = healthProgram().status == HealthProgramDetails.AVAILABLE
                        disabledFooter.isVisible =
                            healthProgram().status == HealthProgramDetails.UNAVAILABLE
                        /**
                         * Remove from journey button is added to the EpoxyController in
                         * renderHealthProgram
                         */
                    }
                    is Loading -> loadingSpinner { id("loading_spinner") }
                }
            }
            viewModel.programAdded.observe(viewLifecycleOwner, { programStarted ->
                when (programStarted) {
                    is Failed -> action.apply {
                        setLoadingStateOff()
                    }
                    is Loaded -> action.apply {
                        setLoadingStateOff()
                            programStarted().contentForUsersState?.also {
                                findNavControllerSafely()?.navigate(
                                    HealthProgramDetailsFragmentDirections.actionHealthProgramDetailsFragmentV2ToFirstGoalTomorrowFragment(
                                        it
                                    )
                                )
                            } ?: findNavControllerSafely()?.navigate(
                                HealthJourneyDeepLinker.HealthJourneyPaths.HealthJourney.construct(
                                    HealthJourneyDeepLinker.HealthJourneyPaths.HealthJourney.HealthJourneyTab.Timeline
                                ).toUri()
                            )
                    }
                    is Loading -> action.apply {
                        setLoadingStateOn()
                    }
                }
            })
        })

        viewModel.isWearablesConsentGiven.observe(viewLifecycleOwner, { isWearablesConsentGiven ->
            when (isWearablesConsentGiven) {
                is Failed -> action.apply {
                    setLoadingStateOff()
                }
                is Loaded -> action.apply {
                    setLoadingStateOff()
                    showWearableFlow = !isWearablesConsentGiven.data
                    if(isWearablesConsentGiven.data) {
                        viewModel.addProgramToJourney(CampaignMode.AUTOMATIC)
                    } else {
                        automaticProgramCTA?.let {
                            rootActivity?.navigateToDeeplink(
                                it,
                                true,
                                setRootNavigation = false,
                                navController = findNavControllerSafely()
                            )
                        }
                    }
                }
                is Loading -> action.apply {
                    setLoadingStateOn()
                }
            }
        })
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(showWearableFlow) {
            findNavControllerSafely()?.apply {
                currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>(
                    IS_WEARABLES_CONNECTED
                )?.observe(viewLifecycleOwner) { result ->
                    if (result) {
                        viewModel.addProgramToJourney(CampaignMode.AUTOMATIC)
                    }
                    currentBackStackEntry?.savedStateHandle?.remove<Boolean>(
                        IS_WEARABLES_CONNECTED
                    )
                }
            }
        }
    }

    private fun showDialogToGetWearablesConsent(dataPoints: List<String>, programId: String) {
        analyticsTracker.viewAppsAndDevicesSettings(programId)
        BottomSheetDialog.Builder().apply {
            setContentView(ContentConfirmDialog(requireContext()).apply {
                setCenteredContent(true)
                setImageAttr(R.attr.drawable_wearable_connect)
                setTitle(getString(R.string.track_progress_automatically))
                setDescription(getString(R.string.track_progress_automatically_description))
                setPrimaryButtonText(getString(R.string.connect_your_apps_and_devices))
                setPrimaryOnClickListener {
                    analyticsTracker.trackConnectAppsAndDevices(programId)
                    viewModel.getWearableConsentForDataPoints(dataPoints)
                    dismiss()
                }
                if(featureFlagsUtils.getValue(HealthJourneyFeatureFlags.wearableProgramManualFlow)) {
                    setSecondaryButtonText(getString(R.string.health_journey_not_now))
                    setSecondaryOnClickListener {
                        analyticsTracker.trackSkipConnectAppsAndDevices(programId)
                        showWearableFlow = false
                        viewModel.addProgramToJourney(CampaignMode.MANUAL)
                        dismiss()
                    }
                }
            })
                .show(
                    requireActivity().supportFragmentManager,
                    "AlertDialogView"
                )
        }
    }

    private fun EpoxyController.renderHealthProgram(healthProgram: HealthProgramDetails) {
        if (healthProgram.status == HealthProgramDetails.ACTIVE) {
            analyticsTracker.viewActiveHealthProgramDetails(healthProgram.name)
        } else {
            analyticsTracker.viewHealthProgramDetails(healthProgram.name)
        }

        progressHeader {
            id("program_header")
            active(healthProgram.status == HealthProgramDetails.ACTIVE)
            titleText(healthProgram.name)
            statusText(R.string.active_all_caps)
            captionText(healthProgram.getOverline(requireContext(), verbose = true))
            progressBars(healthProgram.generateProgressBars())
            imageUrl(healthProgram.imageUrl)
        }

        healthProgram.campaignContentConfig?.dataFields?.takeIfNotEmpty()?.also {
            basicIconBanner {
                id("wearable banner")
                iconImageResource(R.drawable.ic_check)
                title(getString(R.string.connected_health_program))
                descriptionText(
                    getString(
                        R.string.connect_your_data,
                        DataPointsUtil.getDataPointString(requireContext(), healthProgram.campaignContentConfig.dataFields)
                    ).lowercase()
                )
                marginRes(
                    SpacingAttrRes(
                        topSpacingResId = R.attr.spacing_three,
                        leftSpacingResId = R.attr.spacing_one_and_half,
                        rightSpacingResId = R.attr.spacing_one_and_half
                    )
                )
                onClick { _ -> }
            }

        }

        header {
            id("details_header")
            headerText(getString(R.string.details_title))
            descriptionStyle(Header.DESCRIPTION_STYLE_BODY1)
            descriptionText(healthProgram.longDescription)
            marginRes(
                SpacingAttrRes(
                    topSpacingResId = R.attr.spacing_three,
                    leftSpacingResId = R.attr.spacing_one_and_half,
                    rightSpacingResId = R.attr.spacing_one_and_half
                )
            )
        }

        if (featureFlagsUtils.getValue(HealthJourneyFeatureFlags.achievements)) {
            actionRow {
                id("achievement_name")
                titleText(getString(R.string.health_journey_earn_achievement))
                iconSize(ActionRow.IconSize.SEMI_LARGE)
                titleStyleAttr(R.attr.typography_subtitle2)
                iconUrl(healthProgram.achievementImage)
                showDivider(false)
                showChevron(false)
                marginRes(
                    SpacingAttrRes(
                        topSpacingResId = R.attr.spacing_one_and_half,
                        leftSpacingResId = R.attr.spacing_one_and_half,
                        rightSpacingResId = R.attr.spacing_one_and_half
                    )
                )
            }
        }

        healthProgram.contentProviderModal?.run {
            cobrandingBanner {
                id(id)
                titleText(button.text)
                iconUrl(button.imageAsset.fields.file?.url ?: "")
                onClick { _ ->
                    analyticsTracker.trackViewInfoModal(
                        programId = healthProgram.id,
                        programName = healthProgram.name,
                        heading = button.text
                    )
                    BottomSheetDialog.Builder()
                        .setContentView(ContentProviderInfo(requireContext()).apply {
                            setHeading(info.heading)
                            setDescription(info.content)
                            setImageUrlWithFitCenterScale(info.imageAsset.fields.file?.url ?: "")
                        })
                        .show(requireActivity().supportFragmentManager, "ContentProviderDialogView")
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

        if (healthProgram.status == HealthProgramDetails.AVAILABLE || healthProgram.status == HealthProgramDetails.UNAVAILABLE) {
            renderProgramGoals(healthProgram.goals)
        }

        if (healthProgram.status == HealthProgramDetails.ACTIVE) {
            button {
                id("remove_button")
                text(R.string.remove_from_journey)
                buttonStyle(BUTTON_STYLE_SECONDARY)
                onClick { _ ->
                    analyticsTracker.trackLeaveProgram(
                        buttonCta = getString(R.string.remove_from_journey),
                        programId = healthProgram.id,
                        programName = healthProgram.name
                    )
                    findNavControllerSafely()?.navigate(
                        HealthProgramDetailsFragmentDirections.actionHealthProgramDetailsFragmentV2ToHealthJourneyRemovalConfirmationBottomSheetDialog(
                            healthProgram = healthProgram
                        )
                    )
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


    }

    private fun EpoxyController.renderProgramGoals(goals: List<Goal>) {
        header {
            id("activities_header")
            headerText(R.string.activities_title_with_count, goals.size)
            descriptionStyle(Header.DESCRIPTION_STYLE_BODY1)
            marginRes(
                SpacingAttrRes(
                    topSpacingResId = R.attr.spacing_three,
                    bottomSpacingResId = R.attr.spacing_one,
                    leftSpacingResId = R.attr.spacing_one_and_half,
                    rightSpacingResId = R.attr.spacing_one_and_half
                )
            )
        }

        goals.withEach {
            actionRow {
                id(id)
                titleText(name)
                iconSize(ActionRow.IconSize.SMALL)
                titleStyleAttr(R.attr.typography_subtitle1)
                iconUrl(iconUrl)
                showDivider(true)
                showChevron(false)
                marginRes(
                    SpacingAttrRes(
                        leftSpacingResId = R.attr.spacing_one_and_half,
                        rightSpacingResId = R.attr.spacing_one_and_half
                    )
                )
            }
        }
    }

}