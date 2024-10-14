package com.itsecurity.video.chat.agora.ui.subpages

import android.os.Build
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.itsecurity.video.chat.agora.BuildConfig
import com.itsecurity.video.chat.agora.data.SettingPreferences
import com.itsecurity.video.chat.agora.model.UiStore
import com.itsecurity.video.chat.agora.ui.common.ActionButtons
import com.itsecurity.video.chat.agora.ui.common.VideoCell
import com.itsecurity.video.chat.agora.ui.common.VideoList
import com.itsecurity.video.chat.agora.ui.common.VideoStatsInfo
import io.agora.rtc2.AgoraMediaRecorder
import io.agora.rtc2.AgoraMediaRecorder.CONTAINER_MP4
import io.agora.rtc2.AgoraMediaRecorder.STREAM_TYPE_BOTH
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IMediaRecorderCallback
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RecorderInfo
import io.agora.rtc2.RecorderStreamInfo
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig
import io.agora.rtc2.video.VideoCanvas
import io.agora.rtc2.video.VideoEncoderConfiguration


var recorder: AgoraMediaRecorder? = null

/**
 * This is a video meeting page.
 */
@Composable
fun MeetingHall(back: () -> Unit = {}) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val keyboard = LocalSoftwareKeyboardController.current
    var isJoined by rememberSaveable { mutableStateOf(false) }
    var channelName by rememberSaveable { mutableStateOf("test-channel-please-change-it") }
    var token by rememberSaveable { mutableStateOf("007eJxTYChd+3yN7tLQjSx2omGd3+QvTBBcL2zvWxqQ+XxT498PibcVGJLNjE2NDVMTk1OMTE1STZKTzNMMTFIMjA0sjJJSTNIMYht40hsCGRnWVjCzMjJAIIgvy1CSWlyim5yRmJeXmqNbkJOaWJwK5qan6maWMDAAAOGzKHI=") }
    var localUid by rememberSaveable { mutableIntStateOf(0) }
    var videoIdList by rememberSaveable { mutableStateOf(listOf<Int>()) }
    val statsMap = remember { mutableStateMapOf(0 to VideoStatsInfo()) }
    var activeUid by remember { mutableIntStateOf(localUid) }
    var recording = UiStore.recordingState
    BackHandler(true) {
        if (recording.value) {
            UiStore.stopRecording()
        } else {
            back()
        }
    }
    if (recording.value) {
        var config = AgoraMediaRecorder.MediaRecorderConfiguration(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES
            ).absolutePath + "/" + System.currentTimeMillis() + ".mp4",
            CONTAINER_MP4, STREAM_TYPE_BOTH, 3_600_000, 1_000
        )
        Log.i("MM", "Path=${config.storagePath}")
        recorder!!.setMediaRecorderObserver(object : IMediaRecorderCallback {
            override fun onRecorderStateChanged(
                channelId: String?,
                uid: Int,
                state: Int,
                reason: Int
            ) {
                TODO("Not yet implemented")
            }

            override fun onRecorderInfoUpdated(
                channelId: String?,
                uid: Int,
                info: RecorderInfo?
            ) {
                TODO("Not yet implemented")
            }
        })
        val startCode = recorder!!.startRecording(config)
        Log.i("MM", "Start recording = $startCode")
        if(startCode == 0) {
            Toast.makeText(LocalContext.current, "Recording started on path: ${config.storagePath}", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(LocalContext.current, "Recording failed!", Toast.LENGTH_SHORT).show()
        }
    } else {
        Log.i("MM", "Stop recording.")
        if (recorder != null) {
            Log.i("MM", "Stop recording2.")
            if(recorder!!.stopRecording() == 0) {
                Toast.makeText(LocalContext.current, "Recording saved!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(LocalContext.current, "Recording failed!", Toast.LENGTH_SHORT).show()
            }
            recorder = null
            back()
        }
    }

    val rtcEngine = remember {
        RtcEngine.create(RtcEngineConfig().apply {
            mAreaCode = SettingPreferences.getArea()
            mContext = context
            mAppId = BuildConfig.AGORA_APP_ID
            mEventHandler = object : IRtcEngineEventHandler() {
                override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
                    super.onJoinChannelSuccess(channel, uid, elapsed)
                    isJoined = true
                    localUid = uid
                    activeUid = uid
                    videoIdList = videoIdList + uid
                    statsMap[uid] = VideoStatsInfo()
                    UiStore.startRecording()
                }

                override fun onLeaveChannel(stats: RtcStats?) {
                    super.onLeaveChannel(stats)
                    isJoined = false
                    videoIdList = emptyList()
                    statsMap.clear()
                }

                override fun onUserJoined(uid: Int, elapsed: Int) {
                    super.onUserJoined(uid, elapsed)
                    videoIdList = videoIdList + uid
                    statsMap[uid] = VideoStatsInfo()
                }

                override fun onUserOffline(uid: Int, reason: Int) {
                    super.onUserOffline(uid, reason)
                    videoIdList = videoIdList - uid
                    statsMap.remove(uid)
                }

                override fun onRtcStats(stats: RtcStats?) {
                    super.onRtcStats(stats)
                    statsMap[localUid]?.copy(rtcStats = stats)?.let {
                        statsMap[localUid] = it
                    }
                }

                override fun onLocalVideoStats(
                    source: Constants.VideoSourceType?,
                    stats: LocalVideoStats?
                ) {
                    super.onLocalVideoStats(source, stats)
                    statsMap[localUid]?.copy(localVideoStats = stats)?.let {
                        statsMap[localUid] = it
                    }
                }

                override fun onLocalAudioStats(stats: LocalAudioStats?) {
                    super.onLocalAudioStats(stats)
                    statsMap[localUid]?.copy(localAudioStats = stats)?.let {
                        statsMap[localUid] = it
                    }
                }

                override fun onRemoteVideoStats(stats: RemoteVideoStats?) {
                    super.onRemoteVideoStats(stats)
                    val uid = stats?.uid ?: return
                    statsMap[uid]?.copy(remoteVideoStats = stats)?.let {
                        statsMap[uid] = it
                    }
                }

                override fun onRemoteAudioStats(stats: RemoteAudioStats?) {
                    super.onRemoteAudioStats(stats)
                    val uid = stats?.uid ?: return
                    statsMap[uid]?.copy(remoteAudioStats = stats)?.let {
                        statsMap[uid] = it
                    }
                }
            }
        }).apply {
            setVideoEncoderConfiguration(
                VideoEncoderConfiguration(
                    SettingPreferences.getVideoDimensions(),
                    SettingPreferences.getVideoFrameRate(),
                    VideoEncoderConfiguration.STANDARD_BITRATE,
                    SettingPreferences.getOrientationMode()
                )
            )
            enableVideo()
        }
    }
    DisposableEffect(lifecycleOwner) {
        onDispose {
            if (isJoined) {
                rtcEngine.leaveChannel()
            }
            RtcEngine.destroy()
        }
    }
    val permissionLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestMultiplePermissions()) { grantedMap ->
            val allGranted = grantedMap.values.all { it }
            if (allGranted) {
                // Permission is granted
                Toast.makeText(context, "Permission Granted", Toast.LENGTH_LONG).show()
                val mediaOptions = ChannelMediaOptions()
                mediaOptions.channelProfile = Constants.CHANNEL_PROFILE_LIVE_BROADCASTING
                mediaOptions.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
                rtcEngine.joinChannel(token, channelName, 0, mediaOptions)

                // To record local camera, just pass nothing as parameters.
                val recorderInfo = RecorderStreamInfo()
                recorder = rtcEngine.createMediaRecorder(recorderInfo)
                Log.i("MM", "Recorder created.")
            } else {
                // Permission is denied
                Toast.makeText(context, "Permission Denied", Toast.LENGTH_LONG).show()
            }
        }

    MeetingHallView(
        channelName = channelName,
        localUid = localUid,
        activeUid = activeUid,
//        onJoinClick = { channel, t ->
//            channelName = channel
//            token = t
//            keyboard?.hide()
//            permissionLauncher.launch(
//                arrayOf(
//                    android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.CAMERA
//                )
//            )
//        },
//        onLeaveClick = {
//            rtcEngine.leaveChannel()
//        },
        videoIdList = videoIdList,
//        statsMap = statsMap.toMap(),
        setupVideo = { view, id, _ ->
            val canvas = VideoCanvas(view, Constants.RENDER_MODE_HIDDEN, id)
            if (id == localUid) {
                rtcEngine.setupLocalVideo(canvas)
            } else {
                rtcEngine.setupRemoteVideo(canvas)
            }
            view.setOnClickListener { v ->
                if (activeUid == id) {
                    Log.i("MM", "$id was already set to main view.")
                } else {
                    Log.i("MM", "Participant clicked, $id")
                    activeUid = id
                }
            }
        },
        actionLabel = if (isJoined) {
            "Leave this channel"
        } else {
            "Press this button to join..."
        },
        action = {
            Log.i("MM", "Action is clicked.")
            if (isJoined) {
                // For now, stop recording performs back action together.
                UiStore.stopRecording()
            } else {
                keyboard?.hide()
                permissionLauncher.launch(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        arrayOf(
                            android.Manifest.permission.RECORD_AUDIO,
                            android.Manifest.permission.CAMERA,
                            android.Manifest.permission.MANAGE_EXTERNAL_STORAGE
                        )
                    } else {
                        arrayOf(
                            android.Manifest.permission.RECORD_AUDIO,
                            android.Manifest.permission.CAMERA,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                    }
                )
            }
        }
    )
}

@Composable
fun MeetingHallView(
    channelName: String,
    localUid: Int,
    activeUid: Int,
    videoIdList: List<Int>,
    setupVideo: (View, Int, Boolean) -> Unit,
    actionLabel: String,
    action: () -> Unit
//    statsMap: Map<Int, VideoStatsInfo> = emptyMap()
) {
    Column(modifier = Modifier.fillMaxSize()) {
        VideoList(
            modifier = Modifier
                .wrapContentWidth()
                .height(160.dp)
                .padding(5.dp),
            activeId = activeUid,
            setupVideo = setupVideo,
            videoIdList = videoIdList,
            overlay = { index, uid ->
                val description = if (uid == localUid) {
                    "Me"
                } else {
                    uid.toString()
                }
                Text(
                    text = description,
                    modifier = Modifier
                        .wrapContentSize()
                        .background(Color(0, 0, 0, 70))
                )
            }
        )
        VideoCell(modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
            .padding(7.5.dp),
            id = activeUid,
            isLocal = activeUid == localUid,
            isWindowMode = true,
            isSelected = true,
            setupVideo = setupVideo,
            overlay = {
                val description = if (activeUid == localUid) {
                    "Me"
                } else {
                    activeUid.toString()
                }
                Text(
                    text = description,
                    modifier = Modifier
                        .wrapContentSize()
                        .background(Color(0, 0, 0, 70))
                )
            })
        ActionButtons(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
                .wrapContentHeight(),
            text = actionLabel
        ) {
            action()
        }
    }
}