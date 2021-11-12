package life.league.healthjourney.programs.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import life.league.core.base.RootFragment
import life.league.core.extension.popBackStackOrFinish
import life.league.genesis.compose.component.button.GenesisButton
import life.league.genesis.compose.component.widget.GenesisEmptyStateRemoteImageWidget
import life.league.genesis.extension.setGenesisContent
import life.league.healthjourney.R

class FullScreenContentFragment : RootFragment() {

    private val args: FullScreenContentFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return ComposeView(this.requireContext()).apply {
            setGenesisContent {
                Column(modifier = Modifier.fillMaxWidth()) {

                    with(args.content) {
                        GenesisEmptyStateRemoteImageWidget(
                                title = title,
                                description = description,
                                imageId = imageId,
                                contentScale = ContentScale.Fit,
                                descriptionAlign = TextAlign.Center,
                                imageModifier = Modifier.size(180.dp),
                                modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth()
                        )
                    }
                    GenesisButton(
                            text = stringResource(id = R.string.health_journey_ok),
                            modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth()
                    ) {
                        findNavController().popBackStackOrFinish(requireActivity())
                    }
                }
            }
        }
    }
}