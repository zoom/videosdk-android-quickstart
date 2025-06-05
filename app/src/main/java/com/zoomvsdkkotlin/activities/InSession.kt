package com.zoomvsdkkotlin.activities

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
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
import com.zoomvsdkkotlin.viewmodel.ZoomSessionViewModel
import us.zoom.sdk.ZoomVideoSDKUser
import us.zoom.sdk.ZoomVideoSDKVideoView
import kotlin.math.roundToInt

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
    //https://multithreaded.stitchfix.com/blog/2022/08/05/jetpack-compose-recomposition/
    //https://stackoverflow.com/questions/77275283/avoiding-compose-recompositions-when-ui-data-class-variables-change
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
    val toggleCamera = remember(zoomSessionViewModel) {{
        zoomSessionViewModel.toggleCamera()
    }}
    val toggleMicrophone = remember(zoomSessionViewModel) {{
        zoomSessionViewModel.toggleMicrophone()
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
//            .background(Color.Black)
            .fillMaxSize()
            .fillMaxWidth()
    ) {
        if (currentUsersInView().isEmpty()) {
            BigSelfView(
                user = user,
                isVideoOn = zoomSessionUIState.isVideoOn,
                renderView = renderView
            )
        } else {
            GalleryView(
                currentUsersInView = currentUsersInView,
                currentUsersInViewCount = zoomSessionUIState.currentUsersInViewCount,
                participantVideoOn1 = zoomSessionUIState.participantVideoOn1,
                participantVideoOn2 = zoomSessionUIState.participantVideoOn2,
                participantVideoOn3 = zoomSessionUIState.participantVideoOn3,
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
            setVisible = { visible = !visible },
            microphonePermission = microphonePermission,
            cameraPermission = cameraPermission,
            toggleMicrophone = toggleMicrophone,
            toggleCamera = toggleCamera,
            closeSession = closeSession,
            launchMultiplePermissionRequest = launchMultiplePermissionRequest,
            navigate = navigate,
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxWidth()
    ){
        if (currentUsersInView().isNotEmpty()) {
            var offsetX by remember { mutableFloatStateOf(0f) }
            var offsetY by remember { mutableFloatStateOf(0f) }

            DraggableSelfView(
                modifier = Modifier
                    .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                    .width(124.dp)
                    .height(236.dp)
                    .clip(shape = RoundedCornerShape(15.dp, 15.dp, 15.dp, 15.dp))
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            offsetX += dragAmount.x
                            offsetY += dragAmount.y
                        }
                    }
                    .align(Alignment.CenterEnd),
                user = user,
                isVideoOn = zoomSessionUIState.isVideoOn,
                renderView = renderView
            )
        }
    }
}