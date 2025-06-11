package com.zoomvsdkkotlin.sessionviews

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil3.compose.AsyncImage
import us.zoom.sdk.ZoomVideoSDKUser
import us.zoom.sdk.ZoomVideoSDKVideoView
import kotlin.math.roundToInt

@SuppressLint("ResourceType")
@Composable
fun DraggableSelfView(
    user: () -> ZoomVideoSDKUser,
    isVideoOn: Boolean,
    renderView: (ZoomVideoSDKUser, ZoomVideoSDKVideoView) -> Unit,
    rotateVideo: (Int) -> Unit
) {
    val isPortrait = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT
    val rotation: Int = LocalContext.current.display.rotation
    val width = if (isPortrait) 120.dp else 236.dp
    val height = if (isPortrait) 236.dp else 120.dp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxWidth(),
    ) {
        var offsetX by remember { mutableFloatStateOf(0f) }
        var offsetY by remember { mutableFloatStateOf(0f) }

        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .width(width + 10.dp)
                .height(height + 10.dp)
                .background(
                    color = Color.LightGray,
                    shape = RoundedCornerShape(10.dp)
                )
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    }
                }
                .align(Alignment.CenterEnd),
            contentAlignment = Alignment.Center
        ) {
            if (isVideoOn) {
                Box(
                    modifier = Modifier
                        .width(width)
                        .height(height)
                ) {
                    AndroidView(
                        factory = { context: Context ->
                            ZoomVideoSDKVideoView(context).apply {
                                setId(4)
                            }
                        },
                        modifier = Modifier.clip(RoundedCornerShape(20.dp)),
                        update = {
                            val myselfView: ZoomVideoSDKVideoView = it.findViewById(4)
                            myselfView.setZOrderOnTop(true)
                            rotateVideo(rotation)
                            renderView(user(), myselfView)
                        }
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .width(width)
                        .height(height)
                        .background(
                            color = Color.LightGray,
                            shape = RoundedCornerShape(20.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = "https://upload.wikimedia.org/wikipedia/commons/7/7c/Profile_avatar_placeholder_large.png",
                        contentDescription = "Profile Pic Placeholder when video off",
                    )
                }
            }
        }
    }
}