package com.itsecurity.video.chat.agora.model

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import com.itsecurity.video.chat.agora.R
import com.itsecurity.video.chat.agora.ui.subpages.JoinChannelVideoToken
import com.itsecurity.video.chat.agora.ui.subpages.MeetingHall

data class Subpage(
    @StringRes val name: Int,
    val description: String = "",
    val content: @Composable (back: () -> Unit) -> Unit
)

val BasicSubpageLists = listOf(
//    Subpage(R.string.page_join_channel_video_token) { JoinChannelVideoToken() },
    Subpage(R.string.page_customized_video_channel) { back -> MeetingHall(back) },
)