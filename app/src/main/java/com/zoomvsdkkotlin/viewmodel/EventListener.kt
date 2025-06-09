package com.zoomvsdkkotlin.viewmodel

import com.tylerthrailkill.helpers.prettyprint.pp
import us.zoom.sdk.IncomingLiveStreamStatus
import us.zoom.sdk.UVCCameraStatus
import us.zoom.sdk.ZoomVideoSDK
import us.zoom.sdk.ZoomVideoSDKAnnotationHelper
import us.zoom.sdk.ZoomVideoSDKAudioHelper
import us.zoom.sdk.ZoomVideoSDKAudioRawData
import us.zoom.sdk.ZoomVideoSDKAudioStatus
import us.zoom.sdk.ZoomVideoSDKCRCCallStatus
import us.zoom.sdk.ZoomVideoSDKCameraControlRequestHandler
import us.zoom.sdk.ZoomVideoSDKCameraControlRequestType
import us.zoom.sdk.ZoomVideoSDKChatHelper
import us.zoom.sdk.ZoomVideoSDKChatMessage
import us.zoom.sdk.ZoomVideoSDKChatMessageDeleteType
import us.zoom.sdk.ZoomVideoSDKChatPrivilegeType
import us.zoom.sdk.ZoomVideoSDKDelegate
import us.zoom.sdk.ZoomVideoSDKFileTransferStatus
import us.zoom.sdk.ZoomVideoSDKLiveStreamHelper
import us.zoom.sdk.ZoomVideoSDKLiveStreamStatus
import us.zoom.sdk.ZoomVideoSDKLiveTranscriptionHelper
import us.zoom.sdk.ZoomVideoSDKMultiCameraStreamStatus
import us.zoom.sdk.ZoomVideoSDKNetworkStatus
import us.zoom.sdk.ZoomVideoSDKPasswordHandler
import us.zoom.sdk.ZoomVideoSDKPhoneFailedReason
import us.zoom.sdk.ZoomVideoSDKPhoneStatus
import us.zoom.sdk.ZoomVideoSDKProxySettingHandler
import us.zoom.sdk.ZoomVideoSDKRawDataPipe
import us.zoom.sdk.ZoomVideoSDKReceiveFile
import us.zoom.sdk.ZoomVideoSDKRecordingConsentHandler
import us.zoom.sdk.ZoomVideoSDKRecordingStatus
import us.zoom.sdk.ZoomVideoSDKSSLCertificateInfo
import us.zoom.sdk.ZoomVideoSDKSendFile
import us.zoom.sdk.ZoomVideoSDKSession
import us.zoom.sdk.ZoomVideoSDKSessionLeaveReason
import us.zoom.sdk.ZoomVideoSDKShareAction
import us.zoom.sdk.ZoomVideoSDKShareHelper
import us.zoom.sdk.ZoomVideoSDKShareStatus
import us.zoom.sdk.ZoomVideoSDKTestMicStatus
import us.zoom.sdk.ZoomVideoSDKUser
import us.zoom.sdk.ZoomVideoSDKUserHelper
import us.zoom.sdk.ZoomVideoSDKVideoCanvas
import us.zoom.sdk.ZoomVideoSDKVideoHelper
import us.zoom.sdk.ZoomVideoSDKVideoSubscribeFailReason
import us.zoom.sdk.ZoomVideoSDKVideoView

class EventListener(zoomViewModel: ZoomSessionViewModel) {
    val listener = object : ZoomVideoSDKDelegate {
        override fun onSessionJoin() {
            pp("onSessionJoin")
            val sdkSession: ZoomVideoSDKSession = ZoomVideoSDK.getInstance().session
            val remoteUsers: List<ZoomVideoSDKUser> = sdkSession.remoteUsers
            val state = zoomViewModel.getState()

            if (remoteUsers.size < 4)
                zoomViewModel.updateUsersInView(1)

            zoomViewModel.updateState( state.copy(sessionLoader = false))
        }

        override fun onSessionLeave() {
            pp("onSessionLeave")
        }

        override fun onSessionLeave(reason: ZoomVideoSDKSessionLeaveReason?) {
            pp("onSessionLeaveWithReason")
        }

        override fun onError(errorCode: Int) {
            pp("onError")
        }

        override fun onUserJoin(
            userHelper: ZoomVideoSDKUserHelper?,
            userList: MutableList<ZoomVideoSDKUser>?
        ) {
            pp("onUserJoin")
            val sdkSession: ZoomVideoSDKSession = ZoomVideoSDK.getInstance().session
            val state = zoomViewModel.getState()
            val remoteUsers: List<ZoomVideoSDKUser> = sdkSession.remoteUsers

            zoomViewModel.updateUsersInView(state.pageNumber)

            if (remoteUsers.size == 1) {
                val user: ZoomVideoSDKUser = zoomViewModel.getMyself()
                zoomViewModel.stopRenderSelfView(user)
                if (state.isVideoOn) zoomViewModel.startVideo()
            }
        }

        override fun onUserLeave(
            userHelper: ZoomVideoSDKUserHelper?,
            userList: MutableList<ZoomVideoSDKUser>?
        ) {
            pp("onUserLeave")
            val sdkSession: ZoomVideoSDKSession = ZoomVideoSDK.getInstance().session
            val remoteUsers: List<ZoomVideoSDKUser> = sdkSession.remoteUsers
            val state = zoomViewModel.getState()

            zoomViewModel.updateUsersInView(state.pageNumber)

            if (remoteUsers.isEmpty()) {
                val user: ZoomVideoSDKUser = zoomViewModel.getMyself()
                zoomViewModel.stopRenderSelfView(user)
                if (state.isVideoOn) zoomViewModel.startVideo()
            }
        }

        override fun onUserVideoStatusChanged(
            videoHelper: ZoomVideoSDKVideoHelper?,
            userList: MutableList<ZoomVideoSDKUser>?
        ) {
            pp("onUserVideoStatusChanged")

            val state = zoomViewModel.getState()
            val currentUsersInView: List<ZoomVideoSDKUser> = zoomViewModel.getCurrentUsersInView()
            val user: ZoomVideoSDKUser = zoomViewModel.getMyself()

            if (userList?.get(0)?.userID != user.userID) {
                val size: Int = currentUsersInView.size

                val participantVideoOn1: Boolean =
                    (size > 0) && currentUsersInView[0].videoCanvas.videoStatus.isOn
                val participantVideoOn2: Boolean =
                    (size > 1) && currentUsersInView[1].videoCanvas.videoStatus.isOn
                val participantVideoOn3: Boolean =
                    (size > 2) && currentUsersInView[2].videoCanvas.videoStatus.isOn

                zoomViewModel.updateState( state.copy(
                    participantVideoOn1 = participantVideoOn1,
                    participantVideoOn2 = participantVideoOn2,
                    participantVideoOn3 = participantVideoOn3
                ))
            }
        }

        override fun onUserAudioStatusChanged(
            audioHelper: ZoomVideoSDKAudioHelper?,
            userList: MutableList<ZoomVideoSDKUser>?
        ) {
            pp("onUserAudioStatusChanged")
            for (user in userList!!) {
                val state = zoomViewModel.getState()
                val myself: ZoomVideoSDKUser = zoomViewModel.getMyself()

                if (user.userID == myself.userID) {
                    val audioType: ZoomVideoSDKAudioStatus.ZoomVideoSDKAudioType? =
                        user.audioStatus?.audioType

                    if (audioType == ZoomVideoSDKAudioStatus
                            .ZoomVideoSDKAudioType.ZoomVideoSDKAudioType_None) {
                        zoomViewModel.updateState(state.copy(audioConnected = false))
                    } else {
                        val muted: Boolean = user.audioStatus?.isMuted == true
                        zoomViewModel.updateState(state.copy(audioConnected = true, muted = muted))
                    }
                }
            }
        }

        //--------------------------------------------------
        override fun onUserShareStatusChanged(
            shareHelper: ZoomVideoSDKShareHelper?,
            userInfo: ZoomVideoSDKUser?,
            status: ZoomVideoSDKShareStatus?
        ) {
            pp("onUserShareStatusChanged")
        }

        override fun onUserShareStatusChanged(
            shareHelper: ZoomVideoSDKShareHelper?,
            userInfo: ZoomVideoSDKUser?,
            shareAction: ZoomVideoSDKShareAction?
        ) {
            pp("onUserShareStatusChanged2")
        }

        override fun onShareContentChanged(
            shareHelper: ZoomVideoSDKShareHelper?,
            userInfo: ZoomVideoSDKUser?,
            shareAction: ZoomVideoSDKShareAction?
        ) {
            pp("onShareContentChanged")
        }

        override fun onLiveStreamStatusChanged(
            liveStreamHelper: ZoomVideoSDKLiveStreamHelper?,
            status: ZoomVideoSDKLiveStreamStatus?
        ) {
            pp("onLiveStreamStatusChanged")
        }

        override fun onChatNewMessageNotify(
            chatHelper: ZoomVideoSDKChatHelper?,
            messageItem: ZoomVideoSDKChatMessage?
        ) {
            pp("onChatNewMessageNotify")
        }

        override fun onChatDeleteMessageNotify(
            chatHelper: ZoomVideoSDKChatHelper?,
            msgID: String?,
            deleteBy: ZoomVideoSDKChatMessageDeleteType?
        ) {
            pp("onChatDeleteMessageNotify")
        }

        override fun onChatPrivilegeChanged(
            chatHelper: ZoomVideoSDKChatHelper?,
            currentPrivilege: ZoomVideoSDKChatPrivilegeType?
        ) {
            pp("onChatPrivilegeChanged")
        }

        override fun onUserHostChanged(
            userHelper: ZoomVideoSDKUserHelper?,
            userInfo: ZoomVideoSDKUser?
        ) {
            pp("onUserHostChanged")
        }

        override fun onUserManagerChanged(user: ZoomVideoSDKUser?) {
            pp("onUserManagerChanged")
        }

        override fun onUserNameChanged(user: ZoomVideoSDKUser?) {
            pp("onUserNameChanged")
        }

        override fun onUserActiveAudioChanged(
            audioHelper: ZoomVideoSDKAudioHelper?,
            list: MutableList<ZoomVideoSDKUser>?
        ) {
//            pp("onUserActiveAudioChanged")
        }

        override fun onSessionNeedPassword(handler: ZoomVideoSDKPasswordHandler?) {
            pp("onSessionNeedPassword")
        }

        override fun onSessionPasswordWrong(handler: ZoomVideoSDKPasswordHandler?) {
            pp("onSessionPasswordWrong")
        }

        override fun onMixedAudioRawDataReceived(rawData: ZoomVideoSDKAudioRawData?) {
            pp("onMixedAudioRawDataReceived")
        }

        override fun onOneWayAudioRawDataReceived(
            rawData: ZoomVideoSDKAudioRawData?,
            user: ZoomVideoSDKUser?
        ) {
            pp("onOneWayAudioRawDataReceived")
        }

        override fun onShareAudioRawDataReceived(rawData: ZoomVideoSDKAudioRawData?) {
            pp("onShareAudioRawDataReceived")
        }

        override fun onCommandReceived(sender: ZoomVideoSDKUser?, strCmd: String?) {
            pp("onCommandReceived")
        }

        override fun onCommandChannelConnectResult(isSuccess: Boolean) {
            pp("onCommandChannelConnectResult")
        }

        override fun onCloudRecordingStatus(
            status: ZoomVideoSDKRecordingStatus?,
            handler: ZoomVideoSDKRecordingConsentHandler?
        ) {
            pp("onCloudRecordingStatus")
        }

        override fun onHostAskUnmute() {
            pp("onHostAskUnmute")
        }

        override fun onInviteByPhoneStatus(
            status: ZoomVideoSDKPhoneStatus?,
            reason: ZoomVideoSDKPhoneFailedReason?
        ) {
            pp("onInviteByPhoneStatus")
        }

        override fun onMultiCameraStreamStatusChanged(
            status: ZoomVideoSDKMultiCameraStreamStatus?,
            user: ZoomVideoSDKUser?,
            videoPipe: ZoomVideoSDKRawDataPipe?
        ) {
            pp("onMultiCameraStreamStatusChanged")
        }

        override fun onMultiCameraStreamStatusChanged(
            status: ZoomVideoSDKMultiCameraStreamStatus?,
            user: ZoomVideoSDKUser?,
            canvas: ZoomVideoSDKVideoCanvas?
        ) {
            pp("onMultiCameraStreamStatusChanged")
        }

        override fun onLiveTranscriptionStatus(status: ZoomVideoSDKLiveTranscriptionHelper.ZoomVideoSDKLiveTranscriptionStatus?) {
            pp("onLiveTranscriptionStatus")
        }

        override fun onOriginalLanguageMsgReceived(messageInfo: ZoomVideoSDKLiveTranscriptionHelper.ILiveTranscriptionMessageInfo?) {
            pp("onOriginalLanguageMsgReceived")
        }

        override fun onLiveTranscriptionMsgInfoReceived(messageInfo: ZoomVideoSDKLiveTranscriptionHelper.ILiveTranscriptionMessageInfo?) {
            pp("onLiveTranscriptionMsgInfoReceived")
        }

        override fun onLiveTranscriptionMsgError(
            spokenLanguage: ZoomVideoSDKLiveTranscriptionHelper.ILiveTranscriptionLanguage?,
            transcriptLanguage: ZoomVideoSDKLiveTranscriptionHelper.ILiveTranscriptionLanguage?
        ) {
            pp("onLiveTranscriptionMsgError")
        }

        override fun onProxySettingNotification(handler: ZoomVideoSDKProxySettingHandler?) {
            pp("onProxySettingNotification")
        }

        override fun onSSLCertVerifiedFailNotification(info: ZoomVideoSDKSSLCertificateInfo?) {
            pp("onSSLCertVerifiedFailNotification")
        }

        override fun onCameraControlRequestResult(user: ZoomVideoSDKUser?, isApproved: Boolean) {
            pp("onCameraControlRequestResult")
        }

        override fun onCameraControlRequestReceived(
            user: ZoomVideoSDKUser?,
            requestType: ZoomVideoSDKCameraControlRequestType?,
            requestHandler: ZoomVideoSDKCameraControlRequestHandler?
        ) {
            pp("onCameraControlRequestReceived")
        }

        override fun onUserVideoNetworkStatusChanged(
            status: ZoomVideoSDKNetworkStatus?,
            user: ZoomVideoSDKUser?
        ) {
            pp("onUserVideoNetworkStatusChanged")
        }

        override fun onUserRecordingConsent(user: ZoomVideoSDKUser?) {
            pp("onUserRecordingConsent")
        }

        override fun onCallCRCDeviceStatusChanged(status: ZoomVideoSDKCRCCallStatus?) {
            pp("onCallCRCDeviceStatusChanged")
        }

        override fun onVideoCanvasSubscribeFail(
            fail_reason: ZoomVideoSDKVideoSubscribeFailReason?,
            pUser: ZoomVideoSDKUser?,
            view: ZoomVideoSDKVideoView?
        ) {
            pp("onVideoCanvasSubscribeFail")
        }

        override fun onShareCanvasSubscribeFail(
            fail_reason: ZoomVideoSDKVideoSubscribeFailReason?,
            pUser: ZoomVideoSDKUser?,
            view: ZoomVideoSDKVideoView?
        ) {
            pp("onShareCanvasSubscribeFail")
        }

        override fun onShareCanvasSubscribeFail(
            pUser: ZoomVideoSDKUser?,
            view: ZoomVideoSDKVideoView?,
            shareAction: ZoomVideoSDKShareAction?
        ) {
            pp("onShareCanvasSubscribeFail")
        }

        override fun onAnnotationHelperCleanUp(helper: ZoomVideoSDKAnnotationHelper?) {
            pp("onAnnotationHelperCleanUp")
        }

        override fun onAnnotationPrivilegeChange(
            shareOwner: ZoomVideoSDKUser?,
            shareAction: ZoomVideoSDKShareAction?
        ) {
            pp("onAnnotationPrivilegeChange")
        }

        override fun onTestMicStatusChanged(status: ZoomVideoSDKTestMicStatus?) {
            pp("onTestMicStatusChanged")
        }

        override fun onMicSpeakerVolumeChanged(micVolume: Int, speakerVolume: Int) {
//            pp("onMicSpeakerVolumeChanged")
        }

        override fun onCalloutJoinSuccess(user: ZoomVideoSDKUser?, phoneNumber: String?) {
            pp("onCalloutJoinSuccess")
        }

        override fun onSendFileStatus(
            file: ZoomVideoSDKSendFile?,
            status: ZoomVideoSDKFileTransferStatus?
        ) {
            pp("onSendFileStatus")
        }

        override fun onReceiveFileStatus(
            file: ZoomVideoSDKReceiveFile?,
            status: ZoomVideoSDKFileTransferStatus?
        ) {
            pp("onReceiveFileStatus")
        }

        override fun onUVCCameraStatusChange(cameraId: String?, status: UVCCameraStatus?) {
            pp("onUVCCameraStatusChange")
        }

        override fun onVideoAlphaChannelStatusChanged(isAlphaModeOn: Boolean) {
            pp("onVideoAlphaChannelStatusChanged")
        }

        override fun onSpotlightVideoChanged(
            videoHelper: ZoomVideoSDKVideoHelper?,
            userList: MutableList<ZoomVideoSDKUser>?
        ) {
            pp("onSpotlightVideoChanged")
        }

        override fun onFailedToStartShare(
            shareHelper: ZoomVideoSDKShareHelper?,
            user: ZoomVideoSDKUser?
        ) {
            pp("onFailedToStartShare")
        }

        override fun onBindIncomingLiveStreamResponse(bSuccess: Boolean, streamKeyID: String?) {
            pp("onBindIncomingLiveStreamResponse")
        }

        override fun onUnbindIncomingLiveStreamResponse(bSuccess: Boolean, streamKeyID: String?) {
            pp("onUnbindIncomingLiveStreamResponse")
        }

        override fun onIncomingLiveStreamStatusResponse(
            bSuccess: Boolean,
            streamsStatusList: MutableList<IncomingLiveStreamStatus>?
        ) {
            pp("onIncomingLiveStreamStatusResponse")
        }

        override fun onStartIncomingLiveStreamResponse(bSuccess: Boolean, streamKeyID: String?) {
            pp("onStartIncomingLiveStreamResponse")
        }

        override fun onStopIncomingLiveStreamResponse(bSuccess: Boolean, streamKeyID: String?) {
            pp("onStopIncomingLiveStreamResponse")
        }

        override fun onShareContentSizeChanged(
            shareHelper: ZoomVideoSDKShareHelper?,
            user: ZoomVideoSDKUser?,
            shareAction: ZoomVideoSDKShareAction?
        ) {
            pp("onShareContentSizeChanged")
        }
    }
}