package com.zoomvsdkkotlin.sessionviews

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil3.compose.AsyncImage
import us.zoom.sdk.ZoomVideoSDKUser
import us.zoom.sdk.ZoomVideoSDKVideoView

@SuppressLint("ResourceType")
@Composable
fun GalleryView(
    currentUsersInView: () -> List<ZoomVideoSDKUser>,
    currentUsersInViewCount: Int,
    renderView: (ZoomVideoSDKUser,ZoomVideoSDKVideoView) -> Unit,
    participantVideoOn1: Boolean,
    participantVideoOn2: Boolean,
    participantVideoOn3: Boolean
) {
    val height: Float = if (currentUsersInViewCount > 1) .2f else .3f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (currentUsersInViewCount > 0) {
                if (participantVideoOn1) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(1f)
                            .fillMaxHeight(height)
                            .weight(1f)
                            .padding(horizontal = 5.dp)
                    ) {
                        AndroidView(
                            factory = { context: Context ->
                                ZoomVideoSDKVideoView(context).apply {
                                    setId(1)
                                }
                            },
                            update = {
                                val zoomView: ZoomVideoSDKVideoView = it.findViewById(1)
                                renderView(currentUsersInView()[0], zoomView)
                            }
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(1f)
                            .fillMaxHeight(height)
                            .weight(1f)
                            .padding(horizontal = 5.dp)
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = "https://upload.wikimedia.org/wikipedia/commons/7/7c/Profile_avatar_placeholder_large.png",
                            contentDescription = "Profile Pic Placeholder when video off",
                        )
                    }
                }
            }
            if (currentUsersInViewCount > 1) {
                if (participantVideoOn2) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(1f)
                            .fillMaxHeight(.2f)
                            .weight(1f)
                            .padding(horizontal = 5.dp)
                    ) {
                        AndroidView(
                            factory = { context: Context ->
                                ZoomVideoSDKVideoView(context).apply {
                                    setId(2)
                                }
                            },
                            update = {
                                val zoomView: ZoomVideoSDKVideoView = it.findViewById(2)
                                renderView(currentUsersInView()[1], zoomView)
                            }
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(1f)
                            .fillMaxHeight(.2f)
                            .weight(1f)
                            .padding(horizontal = 5.dp)
                            .background(Color.Black),
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
        if (currentUsersInViewCount > 2) {
            if (participantVideoOn3) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(.5f)
                        .fillMaxHeight(.27f)
                        .padding(horizontal = 5.dp)
                ) {
                    AndroidView(
                        factory = { context: Context ->
                            ZoomVideoSDKVideoView(context).apply {
                                setId(3)
                            }
                        },
                        update = {
                            val zoomView: ZoomVideoSDKVideoView = it.findViewById(3)
                            renderView(currentUsersInView()[2], zoomView)
                        }
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(.5f)
                        .fillMaxHeight(.27f)
                        .padding(horizontal = 5.dp)
                        .background(Color.Black),
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