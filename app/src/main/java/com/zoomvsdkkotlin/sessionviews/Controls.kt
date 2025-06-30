package com.zoomvsdkkotlin.sessionviews

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.zoomvsdkkotlin.R
import us.zoom.sdk.ZoomVideoSDKUser

fun Modifier.bottomBorder(
    color: Color,
    height: Float,
) = this.drawWithContent {
    drawContent()
    drawLine(
        color = color,
        start = Offset(0f, size.height),
        end = Offset(size.width, size.height),
        strokeWidth = height,
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Controls(
    user: () -> ZoomVideoSDKUser,
    visible: Boolean,
    sessionName: String,
    muted: Boolean,
    audioConnected: Boolean,
    isVideoOn: Boolean,
    page: Int,
    maxPages: Int,
    setVisible: () -> Unit,
    updateUsersInView: (Int) -> Unit,
    cameraPermission: () -> Boolean,
    microphonePermission: () -> Boolean,
    launchMultiplePermissionRequest: () -> Unit,
    toggleMicrophone: () -> Unit,
    toggleCamera: () -> Unit,
    closeSession: (Boolean) -> Unit,
    navigate: (String) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    var showLeave by remember { mutableStateOf(false) }

    //Controls and Title
    Box(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxWidth()
            .clickable(
                enabled = true,
                interactionSource = interactionSource,
                indication = null,
                onClick = setVisible
            ),
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .height(25.dp)
                .background(Color.Black)
                .offset(0.dp, 65.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .bottomBorder(Color.Gray, 5.toFloat()),
            ) {
                Text(
                    text = sessionName,
                    color = Color.White
                )
            }
        }
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .offset(0.dp, (-50).dp),
        ) {
            ConstraintLayout {
                val (controls, leftChevron, rightChevron) = createRefs()
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(controls) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                ) {
                    //Audio
                    IconButton(
                        onClick = {
                            if (!microphonePermission()) {
                                launchMultiplePermissionRequest()
                            } else {
                                toggleMicrophone()
                            }
                        },
                        modifier = Modifier
                            .then(Modifier.size(75.dp))
                            .clip(CircleShape)
                            .background(Color.Blue)
                    ) {
                        if (microphonePermission()) {
                            if (audioConnected) {
                                if (muted) {
                                    Icon(
                                        painter = painterResource(R.drawable.mic_off_24px),
                                        tint = Color.White,
                                        contentDescription = null
                                    )
                                } else {
                                    Icon(
                                        painter = painterResource(R.drawable.mic_24px),
                                        tint = Color.White,
                                        contentDescription = null
                                    )
                                }
                            } else {
                                Icon(
                                    painter = painterResource(R.drawable.headset_mic_24px),
                                    tint = Color.White,
                                    contentDescription = null
                                )
                            }
                        } else {
                            Icon(
                                painter = painterResource(R.drawable.mic_alert_24px),
                                tint = Color.White,
                                contentDescription = null
                            )
                        }
                    }

                    //Video
                    IconButton(
                        onClick = {
                            if (!cameraPermission()) {
                                launchMultiplePermissionRequest()
                            } else {
                                toggleCamera()
                            }
                        },
                        modifier = Modifier
                            .then(Modifier.size(75.dp))
                            .clip(CircleShape)
                            .background(Color.Blue)
                    ) {
                        if (cameraPermission()) {
                            if (isVideoOn) {
                                Icon(
                                    painter = painterResource(R.drawable.videocam_24px),
                                    tint = Color.White,
                                    contentDescription = null
                                )
                            } else {
                                Icon(
                                    painter = painterResource(R.drawable.videocam_off_24px),
                                    tint = Color.White,
                                    contentDescription = null
                                )
                            }
                        } else {
                            Icon(
                                painter = painterResource(R.drawable.videocam_alert_24px),
                                tint = Color.White,
                                contentDescription = null
                            )
                        }
                    }

                    //Leave/End Session
                    IconButton(
                        onClick = { showLeave = !showLeave },
                        modifier = Modifier
                            .then(Modifier.size(75.dp))
                            .clip(CircleShape)
                            .background(Color.Red)
                    ) {
                        Icon(
                            Icons.Default.Clear,
                            tint = Color.White,
                            contentDescription = null
                        )
                    }
                }

                //Pagination
                if (maxPages > 1 && page != 1) {
                    IconButton(
                        onClick = {
                            updateUsersInView(page - 1)
                        },
                        modifier = Modifier
                            .then(Modifier.size(30.dp))
                            .clip(CircleShape)
                            .background(Color.DarkGray)
                            .constrainAs(leftChevron) {
                                bottom.linkTo(controls.top, margin = 25.dp)
                                start.linkTo(controls.start, margin = 30.dp)
                            }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.chevron_backward_24px),
                            tint = Color.White,
                            contentDescription = null
                        )
                    }
                }
                if ( maxPages > 1 && maxPages != page) {
                    IconButton(
                        onClick = {
                            updateUsersInView(page + 1)
                        },
                        modifier = Modifier
                            .then(Modifier.size(30.dp))
                            .clip(CircleShape)
                            .background(Color.DarkGray)
                            .constrainAs(rightChevron) {
                                bottom.linkTo(controls.top, margin = 25.dp)
                                end.linkTo(controls.end, margin = 30.dp)
                            }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.chevron_forward_24px),
                            tint = Color.White,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }

    if (showLeave) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxWidth()
                .clickable(
                    enabled = true,
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = { showLeave = !showLeave }),
        ) {
            LeavePopup(
                modifier = Modifier
                    .background(Color.White, RoundedCornerShape(10.dp))
                    .width(150.dp)
                    .height(100.dp)
                    .align(Alignment.Center),
                user = user,
                closeSession = closeSession,
                navigate = navigate
            )
        }
    }
}