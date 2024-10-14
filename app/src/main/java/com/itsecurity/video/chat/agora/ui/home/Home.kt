package com.itsecurity.video.chat.agora.ui.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import com.itsecurity.video.chat.agora.model.Component
import com.itsecurity.video.chat.agora.model.Subpage
import com.itsecurity.video.chat.agora.ui.common.APIScaffold
import com.itsecurity.video.chat.agora.ui.page.PageItem

@Composable
fun Home(
    components: List<Component>,
    onSubpageClick: (subPage: Subpage, component: Component) -> Unit,
    onSettingClick: () -> Unit,
) {
    val ltr = LocalLayoutDirection.current
    APIScaffold(
        topBarTitle = "Agora API Test",
        showSettingIcon = true,
        onSettingClick = onSettingClick,
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.consumeWindowInsets(paddingValues),
            contentPadding = PaddingValues(
                start = paddingValues.calculateStartPadding(ltr) + ComponentPadding,
                top = paddingValues.calculateTopPadding() + ComponentPadding,
                end = paddingValues.calculateEndPadding(ltr) + ComponentPadding,
                bottom = paddingValues.calculateBottomPadding() + ComponentPadding
            )
        ) {
            components.forEach { component ->
                item {
                    Text(
                        text = component.name,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Spacer(modifier = Modifier.height(ComponentPadding))
                }
                items(component.subpages) { example ->
                    PageItem (
                        subPage = example,
                        onClick = { onSubpageClick(example, component) }
                    )
                    Spacer(modifier = Modifier.height(ExampleItemPadding))
                }
            }

        }
    }
}

private val ComponentPadding = 16.dp
private val ExampleItemPadding = 8.dp