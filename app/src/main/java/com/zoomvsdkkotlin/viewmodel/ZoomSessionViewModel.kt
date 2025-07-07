package com.zoomvsdkkotlin.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.zoomvsdkkotlin.activities.Config
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import us.zoom.sdk.ZoomVideoSDK
import us.zoom.sdk.ZoomVideoSDKAudioHelper
import us.zoom.sdk.ZoomVideoSDKAudioStatus
import us.zoom.sdk.ZoomVideoSDKErrors
import us.zoom.sdk.ZoomVideoSDKInitParams
import us.zoom.sdk.ZoomVideoSDKSession
import us.zoom.sdk.ZoomVideoSDKSessionContext
import us.zoom.sdk.ZoomVideoSDKUser
import us.zoom.sdk.ZoomVideoSDKVideoAspect
import us.zoom.sdk.ZoomVideoSDKVideoCanvas
import us.zoom.sdk.ZoomVideoSDKVideoResolution
import us.zoom.sdk.ZoomVideoSDKVideoView
import kotlin.math.ceil

data class ZoomSessionUIState(
    val selfView: ZoomVideoSDKVideoView? = null,
    val currentUsersInViewCount: Int = 0,
    val sessionName: String = "",
    val userName: String = "",
    val password: String? = "",
    val sessionLoader: Boolean = true,
    val isVideoOn: Boolean = false,
    val muted: Boolean = false,
    val audioConnected: Boolean = false,
    val pageNumber: Int = 1,
    val maxPages: Int = 1,
    val participantVideoOn: List<Boolean> = listOf(false, false, false, false),
    val participantMuted: List<Boolean> = listOf(false, false, false, false)
)

class ZoomSessionViewModel(application: Application): AndroidViewModel(application) {
    @SuppressLint("StaticFieldLeak")
    private val context: Context = getApplication<Application>().applicationContext
    private val _zoomSessionUIState = MutableStateFlow(ZoomSessionUIState())
    private var currentUsersInView: List<ZoomVideoSDKUser> = emptyList()
    val zoomSessionUIState: StateFlow<ZoomSessionUIState> = _zoomSessionUIState.asStateFlow()

    fun initZoomSDK () {
        val initParams = ZoomVideoSDKInitParams().apply {
            domain = "https://zoom.us"
        }

        val sdk = ZoomVideoSDK.getInstance()
        val initResult = sdk.initialize(context, initParams)
        if (initResult == ZoomVideoSDKErrors.Errors_Success) {
            println("init success")
        } else {
            println("init fail")
        }

        val listener = EventListener(this).listener
        ZoomVideoSDK.getInstance().addListener(listener)
    }
    fun joinSession(config: Config) {
        val joinParams: ZoomVideoSDKSessionContext = ZoomVideoSDKSessionContext().apply {
            sessionName = config.sessionName
            userName = config.userName
            sessionPassword = config.password
            token = config.jwt
        }
        val session: ZoomVideoSDKSession? = ZoomVideoSDK.getInstance().joinSession(joinParams)

        if (session != null) {
            _zoomSessionUIState.update {
                it.copy(
                    sessionName = config.sessionName,
                    userName = config.userName,
                    password = config.password
                )
            }
        }
    }
    fun getMyself(): ZoomVideoSDKUser {
        return ZoomVideoSDK.getInstance().session.mySelf
    }
    fun getCurrentUsersInView(): List<ZoomVideoSDKUser> {
        return this.currentUsersInView
    }
    fun closeSession(end: Boolean) {
        this.stopVideo()
        this.stopAudio()
        _zoomSessionUIState.update {
            it.copy(
                selfView = null,
                currentUsersInViewCount = 0,
                sessionName = "",
                userName = "",
                password = "",
                sessionLoader = true,
                isVideoOn = false,
                muted = false,
                audioConnected = false,
                pageNumber = 1,
                participantVideoOn = listOf(false, false, false, false),
                participantMuted = listOf(false, false, false, false)
            )
        }
        this.currentUsersInView = emptyList()
        ZoomVideoSDK.getInstance().leaveSession(end)
    }
    fun updateState(state: ZoomSessionUIState) {
        _zoomSessionUIState.value = state
    }
    fun getState(): ZoomSessionUIState {
        return _zoomSessionUIState.value
    }
    fun rotateVideo(rotation: Int) {
        val videoHelper = ZoomVideoSDK.getInstance().videoHelper
        videoHelper.rotateMyVideo(rotation)
    }
    fun toggleCamera() {
        val user: ZoomVideoSDKUser = ZoomVideoSDK.getInstance().session.mySelf
        val videoHelper = ZoomVideoSDK.getInstance().videoHelper
        val isVideoOn: Boolean? = user.videoCanvas?.videoStatus?.isOn

        if (isVideoOn != null) {
            if (isVideoOn) {
                videoHelper.stopVideo()
                _zoomSessionUIState.update { it.copy(isVideoOn = false) }
            } else {
                videoHelper.startVideo()
                _zoomSessionUIState.update { it.copy(isVideoOn = true) }
            }
        }
    }
    fun startVideo() {
        val videoHelper = ZoomVideoSDK.getInstance().videoHelper

        videoHelper.startVideo()
        _zoomSessionUIState.update { it.copy(isVideoOn = true) }
    }
    fun stopVideo() {
        val videoHelper = ZoomVideoSDK.getInstance().videoHelper

        videoHelper.stopVideo()
        _zoomSessionUIState.update { it.copy(isVideoOn = false) }
    }
    fun toggleMicrophone() {
        val user: ZoomVideoSDKUser = ZoomVideoSDK.getInstance().session.mySelf
        val audioHelper: ZoomVideoSDKAudioHelper = ZoomVideoSDK.getInstance().audioHelper
        val audioType: ZoomVideoSDKAudioStatus.ZoomVideoSDKAudioType? = user.audioStatus?.audioType

        if (audioType == ZoomVideoSDKAudioStatus.ZoomVideoSDKAudioType.ZoomVideoSDKAudioType_None) {
            println("Starting Audio...")
            audioHelper.startAudio()
            _zoomSessionUIState.update { it.copy( audioConnected = true, muted = true )}
        } else {
            val muted: Boolean? = user.audioStatus?.isMuted
            if (muted != null) {
                if (muted) {
                    audioHelper.unMuteAudio(user)
                } else {
                    audioHelper.muteAudio(user)
                }
            }
        }
    }
    private fun stopAudio() {
        val user: ZoomVideoSDKUser = ZoomVideoSDK.getInstance().session.mySelf
        val audioHelper: ZoomVideoSDKAudioHelper = ZoomVideoSDK.getInstance().audioHelper
        val audioType: ZoomVideoSDKAudioStatus.ZoomVideoSDKAudioType? = user.audioStatus?.audioType

        if (audioType == ZoomVideoSDKAudioStatus.ZoomVideoSDKAudioType.ZoomVideoSDKAudioType_VOIP) {
            audioHelper.stopAudio()
            _zoomSessionUIState.update { it.copy(audioConnected = false) }
        }
    }
    fun updateUsersInView(page: Int) {
        val userList = ZoomVideoSDK.getInstance().session.remoteUsers
        val newState = ArrayList<ZoomVideoSDKUser>()
        val newParticipantVideoOn = ArrayList<Boolean>()
        val newParticipantMuted = ArrayList<Boolean>()
        val start: Int = (page - 1) * 4
        val size: Int = userList.size

        if (userList.isNotEmpty()) {
            for (i in 0..3) {
               if (start + i < size) {
                   newState.add(userList[start + i])
                   newParticipantVideoOn.add(userList[start + i].videoCanvas.videoStatus.isOn)
                   newParticipantMuted.add(userList[start + i].audioStatus.isMuted)
               }
               else break
            }
            this.currentUsersInView = newState.toList()
            _zoomSessionUIState.update {
                it.copy( currentUsersInViewCount = this.currentUsersInView.size)
            }
        } else {
            this.currentUsersInView = emptyList()
            _zoomSessionUIState.update {
                it.copy( currentUsersInViewCount = 0)
            }
        }
        _zoomSessionUIState.update { it.copy(
            pageNumber = page,
            maxPages = ceil((size.toDouble() / 4)).toInt(),
            participantVideoOn = newParticipantVideoOn.toList(),
            participantMuted = newParticipantMuted.toList()
        )}
    }
    fun renderView(user: ZoomVideoSDKUser, view: ZoomVideoSDKVideoView) {
        val canvas: ZoomVideoSDKVideoCanvas = user.videoCanvas
        canvas.subscribe(
            view,
            ZoomVideoSDKVideoAspect.ZoomVideoSDKVideoAspect_Full_Filled,
            ZoomVideoSDKVideoResolution.VideoResolution_720P
        )
    }
    fun stopRenderSelfView(user: ZoomVideoSDKUser) {
        val canvas: ZoomVideoSDKVideoCanvas = user.videoCanvas
        canvas.unSubscribe(_zoomSessionUIState.value.selfView)
    }
}