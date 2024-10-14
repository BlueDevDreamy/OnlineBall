package com.itsecurity.video.chat.agora.model

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

object UiStore : ViewModel() {
    private val _recordingState = mutableStateOf(false)
    val recordingState: State<Boolean> = _recordingState

    fun startRecording() {
        _recordingState.value = true
    }

    fun stopRecording() {
        _recordingState.value = false
    }
}