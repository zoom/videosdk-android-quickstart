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

data class ZoomSessionUIState(
    val selfView: ZoomVideoSDKVideoView? = null,
    val userList: List<ZoomVideoSDKUser> = emptyList(),
    val sessionName: String = "",
    val userName: String = "",
    val password: String? = "",
    val sessionLoader: Boolean = false,
    val isVideoOn: Boolean = true,
    val muted: Boolean = false,
    val audioConnected: Boolean = false,
    val pageNumber: Int = 1,
    val participantVideoOn1: Boolean = false,
    val participantVideoOn2: Boolean = false,
    val participantVideoOn3: Boolean = false
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
                userList = emptyList(),
                sessionName = "",
                userName = "",
                password = "",
                sessionLoader = false,
                isVideoOn = true,
                muted = false,
                audioConnected = false,
                pageNumber = 1,
                participantVideoOn1 = false,
                participantVideoOn2 = false,
                participantVideoOn3 = false
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
    private fun stopVideo() {
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
        val start: Int = (page - 1) * 3
        val size: Int = userList.size

        if (userList.isNotEmpty()) {
            newState.add(userList[start])
            if (size > 1) newState.add(userList[start + 1])
            if (size > 2) newState.add(userList[start + 2])
            this.currentUsersInView = newState.toList()
            println(this.currentUsersInView.size)
        } else {
            this.currentUsersInView = emptyList()
        }
        _zoomSessionUIState.update { it.copy( pageNumber = page) }
    }
    fun renderView(user: ZoomVideoSDKUser, view: ZoomVideoSDKVideoView) {
        val canvas: ZoomVideoSDKVideoCanvas = user.videoCanvas
        canvas.subscribe(
            view,
            ZoomVideoSDKVideoAspect.ZoomVideoSDKVideoAspect_Original,
            ZoomVideoSDKVideoResolution.VideoResolution_720P
        )
    }

    fun stopRenderSelfView(user: ZoomVideoSDKUser) {
        val canvas: ZoomVideoSDKVideoCanvas = user.videoCanvas
        canvas.unSubscribe(_zoomSessionUIState.value.selfView)
    }
}