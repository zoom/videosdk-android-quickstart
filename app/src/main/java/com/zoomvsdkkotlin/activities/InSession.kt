package com.zoomvsdkkotlin.activities

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
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
import androidx.compose.ui.window.Dialog
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
import com.zoomvsdkkotlin.viewmodel.OnHoldParticipant
import com.zoomvsdkkotlin.viewmodel.ZoomSessionViewModel
import us.zoom.sdk.ZoomVideoSDKUser
import us.zoom.sdk.ZoomVideoSDKVideoView

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@SuppressLint("ResourceType")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun InSession(navController: NavController, zoomSessionViewModel: ZoomSessionViewModel) {
    var visible by remember { mutableStateOf(true) }
    var showHostOptions by remember { mutableStateOf(false) }
    var showParticipantsDialog by remember { mutableStateOf(false) }
    var showAdmitDialog by remember { mutableStateOf(false) }
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
    val allSessionUsers = remember(zoomSessionViewModel) {{
        zoomSessionViewModel.getAllSessionUsers()
    }}
    val refreshOnHoldUsersFromHelper = remember(zoomSessionViewModel) {{
        zoomSessionViewModel.refreshOnHoldUsersFromHelper()
    }}
    val setHoldEnabled = remember(zoomSessionViewModel) {{
        enabled: Boolean -> zoomSessionViewModel.setHoldEnabled(enabled)
    }}
    val admitOnHoldUser = remember(zoomSessionViewModel) {{
        userId: String -> zoomSessionViewModel.admitOnHoldUser(userId)
    }}
    val admitAllOnHoldUsers = remember(zoomSessionViewModel) {{
        zoomSessionViewModel.admitAllOnHoldUsers()
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
            isHost = zoomSessionUIState.isHost,
            page = zoomSessionUIState.pageNumber,
            maxPages = zoomSessionUIState.maxPages,
            setVisible = { visible = !visible },
            updateUsersInView = updateUsersInView,
            showHostOptions = showHostOptions,
            onHostOptionsDismiss = { showHostOptions = false },
            onParticipantsClick = {
                showHostOptions = false
                showParticipantsDialog = true
            },
            onAdmitParticipantsClick = {
                showHostOptions = false
                refreshOnHoldUsersFromHelper()
                showAdmitDialog = true
            },
            onHostOptionsClick = { showHostOptions = true },
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

    if (zoomSessionUIState.sessionLoader && !zoomSessionUIState.waitingForHostAdmission) {
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

    if (zoomSessionUIState.waitingForHostAdmission) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.9f)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                IndeterminateCircleLoader(modifier = Modifier.size(64.dp))
                Text(
                    text = "Waiting for host to admit you...",
                    color = Color.White
                )
            }
        }
    }

    if (showParticipantsDialog) {
        ParticipantsDialog(
            users = allSessionUsers(),
            onDismiss = { showParticipantsDialog = false }
        )
    }

    if (showAdmitDialog) {
        AdmitParticipantsDialog(
            holdEnabled = zoomSessionUIState.holdEnabled,
            participants = zoomSessionUIState.onHoldParticipants,
            onDismiss = { showAdmitDialog = false },
            onToggleHold = { enabled -> setHoldEnabled(enabled) },
            onAdmit = { userId -> admitOnHoldUser(userId) },
            onAdmitAll = { admitAllOnHoldUsers() }
        )
    }
}

@Composable
private fun ParticipantsDialog(
    users: List<ZoomVideoSDKUser>,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Participants")
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close participants")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                if (users.isEmpty()) {
                    Text("No participants")
                } else {
                    LazyColumn(modifier = Modifier.fillMaxWidth().height(280.dp)) {
                        items(users, key = { it.userID }) { user ->
                            Text(
                                text = user.userName,
                                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdmitParticipantsDialog(
    holdEnabled: Boolean,
    participants: List<OnHoldParticipant>,
    onDismiss: () -> Unit,
    onToggleHold: (Boolean) -> Unit,
    onAdmit: (String) -> Unit,
    onAdmitAll: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Enable Hold")
                        Switch(
                            checked = holdEnabled,
                            onCheckedChange = onToggleHold
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close admit participants")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                if (participants.isEmpty()) {
                    Text("No users waiting")
                } else {
                    LazyColumn(modifier = Modifier.fillMaxWidth().height(280.dp)) {
                        items(participants, key = { it.userId }) { user ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = user.userName,
                                    modifier = Modifier.weight(1f)
                                )
                                Button(onClick = { onAdmit(user.userId) }) {
                                    Text("Admit")
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onAdmitAll,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Admit All")
                }
            }
        }
    }
}