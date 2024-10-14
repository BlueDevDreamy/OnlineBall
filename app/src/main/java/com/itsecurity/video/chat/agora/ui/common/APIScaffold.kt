
package com.itsecurity.video.chat.agora.ui.common

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun APIScaffold(
    topBarTitle: String? = null,
    showBackNavigationIcon: Boolean = false,
    showSettingIcon: Boolean = false,
    onBackClick: () -> Unit = {},
    onSettingClick: () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    if(topBarTitle != null) {
        Scaffold(
            topBar = {
                APITopAppBar(
                    title = topBarTitle,
                    showBackNavigationIcon = showBackNavigationIcon,
                    showSettingIcon = showSettingIcon,
                    scrollBehavior = scrollBehavior,
                    onBackClick = onBackClick,
                    onSettingClick = onSettingClick
                )
            },
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            content = content
        )
    } else {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            content = content
        )
    }
}
