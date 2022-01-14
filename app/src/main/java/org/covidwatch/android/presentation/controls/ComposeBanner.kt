package org.covidwatch.android.presentation.controls

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.covidwatch.android.R
import life.league.genesis.compose.theme.HalfVerticalSpacer
import life.league.genesis.compose.theme.OneAndHalfHorizontalSpacer
import life.league.genesis.compose.theme.OneAndHalfVerticalSpacer

@Preview
@Composable
fun ComposeBannerPreview() {
    MaterialTheme() {
        ComposeBanner(
            modifier = Modifier.fillMaxWidth(),
            title = "Asset Banner Title", body = "Asset banner body") {
            Image(
                modifier = Modifier.size(48.dp),
                painter = painterResource(id = R.drawable.ic_logo),
                contentDescription = null
            )
        }
    }
}

@Composable
fun ComposeBanner(
    modifier: Modifier = Modifier,
    title: String,
    body: String,
    backgroundColor: Color = MaterialTheme.colors.background,
    backgroundShape: Shape = MaterialTheme.shapes.small,
    onClick: (() -> Unit)? = null,
    imageContent: @Composable RowScope.() -> Unit
) {

    Row(modifier = modifier
        .height(IntrinsicSize.Min)
        .background(
            color = backgroundColor,
            shape = backgroundShape
        )
        .clickable { onClick?.invoke() },
        verticalAlignment = Alignment.CenterVertically
    ) {

        OneAndHalfHorizontalSpacer()

        Column(modifier = Modifier
            .fillMaxHeight()
            .weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            OneAndHalfVerticalSpacer()
            Text(text = title, style = MaterialTheme.typography.subtitle1)
            HalfVerticalSpacer()
            Text(text = body, style = MaterialTheme.typography.caption)
            OneAndHalfVerticalSpacer()
        }

        OneAndHalfHorizontalSpacer()

        imageContent()

        OneAndHalfHorizontalSpacer()
    }

}
