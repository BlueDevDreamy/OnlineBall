package com.itsecurity.video.chat.agora.ui.page

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.itsecurity.video.chat.agora.model.Subpage
import com.itsecurity.video.chat.agora.ui.common.APIScaffold

@Composable
fun Subpage(
    subPage: Subpage,
    onBackClick: () -> Unit,
) {
    APIScaffold(
//        topBarTitle = stringResource(id = subPage.name),
        showBackNavigationIcon = true,
        onBackClick = onBackClick,
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .consumeWindowInsets(WindowInsets.safeDrawing)
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            subPage.content(onBackClick)
        }
    }
}