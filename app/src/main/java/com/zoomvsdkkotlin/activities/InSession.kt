package com.zoomvsdkkotlin.activities

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.zoomvsdkkotlin.sessionviews.BigSelfView
import com.zoomvsdkkotlin.sessionviews.Controls
import com.zoomvsdkkotlin.sessionviews.DraggableSelfView
import com.zoomvsdkkotlin.sessionviews.GalleryView
import com.zoomvsdkkotlin.sessionviews.IndeterminateCircleLoader
import com.zoomvsdkkotlin.viewmodel.ZoomSessionViewModel
import us.zoom.sdk.ZoomVideoSDKUser
import us.zoom.sdk.ZoomVideoSDKVideoView

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@SuppressLint("ResourceType")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun InSession(navController: NavController, zoomSessionViewModel: ZoomSessionViewModel) {
    var visible by remember { mutableStateOf(true) }
    val zoomSessionUIState by zoomSessionViewModel.zoomSessionUIState.collectAsState()
    val permissionState: MultiplePermissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.CAMERA
        )
    )
    val user = remember(zoomSessionViewModel) {{
        zoomSessionViewModel.getMyself()
    }}
    val currentUsersInView = remember(zoomSessionViewModel) {{
        zoomSessionViewModel.getCurrentUsersInView()
    }}
    val renderView = remember(zoomSessionViewModel) {{
            zoomuser: ZoomVideoSDKUser, view: ZoomVideoSDKVideoView ->
        zoomSessionViewModel.renderView(zoomuser, view)
    }}
    val rotateVideo = remember(zoomSessionViewModel) {{
            rotation: Int -> zoomSessionViewModel.rotateVideo(rotation)
    }}
    val toggleCamera = remember(zoomSessionViewModel) {{
        zoomSessionViewModel.toggleCamera()
    }}
    val toggleMicrophone = remember(zoomSessionViewModel) {{
        zoomSessionViewModel.toggleMicrophone()
    }}
    val updateUsersInView = remember(zoomSessionViewModel) {{
            page: Int -> zoomSessionViewModel.updateUsersInView(page)
    }}
    val closeSession = remember(zoomSessionViewModel) {{
        end: Boolean -> zoomSessionViewModel.closeSession(end)
    }}
    val launchMultiplePermissionRequest = remember(permissionState) {{
        permissionState.launchMultiplePermissionRequest()
    }}
    val navigate = remember(navController) {{
       route: String -> navController.navigate(route)
    }}
    val cameraPermission = remember(permissionState){{
        permissionState.permissions[1].status.isGranted
    }}
    val microphonePermission = remember(permissionState){{
        permissionState.permissions[0].status.isGranted
    }}

    LaunchedEffect(key1 = permissionState) {
        if (!permissionState.allPermissionsGranted) {
            permissionState.launchMultiplePermissionRequest()
        }
    }

    Box(
        modifier = Modifier
            .background(Color.Black)
            .fillMaxSize()
            .fillMaxWidth()
    ) {
        if (currentUsersInView().isEmpty()) {
            BigSelfView(
                user = user,
                isVideoOn = zoomSessionUIState.isVideoOn,
                renderView = renderView,
                rotateVideo = rotateVideo
            )
        } else {
            GalleryView(
                currentUsersInView = currentUsersInView,
                currentUsersInViewCount = zoomSessionUIState.currentUsersInViewCount,
                participantVideoOn = zoomSessionUIState.participantVideoOn,
                participantMuted = zoomSessionUIState.participantMuted,
                renderView = renderView
            )
        }

        Controls(
            user = user,
            visible = visible,
            sessionName = zoomSessionUIState.sessionName,
            muted = zoomSessionUIState.muted,
            audioConnected = zoomSessionUIState.audioConnected,
            isVideoOn = zoomSessionUIState.isVideoOn,
            page = zoomSessionUIState.pageNumber,
            maxPages = zoomSessionUIState.maxPages,
            setVisible = { visible = !visible },
            updateUsersInView = updateUsersInView,
            microphonePermission = microphonePermission,
            cameraPermission = cameraPermission,
            toggleMicrophone = toggleMicrophone,
            toggleCamera = toggleCamera,
            closeSession = closeSession,
            launchMultiplePermissionRequest = launchMultiplePermissionRequest,
            navigate = navigate,
        )
    }

    if (currentUsersInView().isNotEmpty()) {
        DraggableSelfView(
            user = user,
            isVideoOn = zoomSessionUIState.isVideoOn,
            muted = zoomSessionUIState.muted,
            renderView = renderView,
            rotateVideo = rotateVideo
        )
    }

    if (zoomSessionUIState.sessionLoader) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            IndeterminateCircleLoader(
                modifier = Modifier.width(64.dp),
            )
        }
    }
}