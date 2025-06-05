package com.zoomvsdkkotlin.sessionviews

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import coil3.compose.AsyncImage
import us.zoom.sdk.ZoomVideoSDKUser
import us.zoom.sdk.ZoomVideoSDKVideoView

@SuppressLint("ResourceType")
@Composable
fun DraggableSelfView(
    modifier: Modifier,
    user: () -> ZoomVideoSDKUser,
    isVideoOn: Boolean,
    renderView: (ZoomVideoSDKUser, ZoomVideoSDKVideoView) -> Unit,
){
    Box(modifier = modifier) {
        if (isVideoOn) {
            AndroidView(
                modifier = Modifier
                    .fillMaxHeight(1f)
                    .fillMaxWidth(),
                factory = { context: Context ->
                    ZoomVideoSDKVideoView(context).apply {
                        setId(4)
                    }
                },
                update = {
                    val myselfView: ZoomVideoSDKVideoView = it.findViewById(4)
                    myselfView.setZOrderOnTop(true)
                    renderView(user(), myselfView)
                }
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxHeight(1f)
                    .fillMaxWidth()
                    .background(Color.LightGray),
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