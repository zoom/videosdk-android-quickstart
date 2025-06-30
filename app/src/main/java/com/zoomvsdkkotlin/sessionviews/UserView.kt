package com.zoomvsdkkotlin.sessionviews

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil3.compose.AsyncImage
import us.zoom.sdk.ZoomVideoSDKUser
import us.zoom.sdk.ZoomVideoSDKVideoView

@SuppressLint("ResourceType")
@Composable
fun UserView(
    modifier: Modifier,
    user: (Int) -> ZoomVideoSDKUser,
    renderView: (ZoomVideoSDKUser,ZoomVideoSDKVideoView) -> Unit,
    id: Int,
    participantVideoOn: Boolean,
    participantMuted: Boolean
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        if (participantVideoOn) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(
                        width = 4.dp,
                        color = Color.hsl(212F, .6f, .17f),
                        shape = RoundedCornerShape(20.dp)
                    )
            ) {
                AndroidView(
                    factory = { context: Context ->
                        ZoomVideoSDKVideoView(context).apply {
                            setId(id)
                        }
                    },
                    modifier = Modifier.clip(RoundedCornerShape(20.dp)),
                    update = {
                        val zoomView: ZoomVideoSDKVideoView = it.findViewById(id)
                        renderView(user(id), zoomView)
                    }
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = Color.hsl(212F, .6f, .17f),
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

        Nametag(
            modifier = Modifier
                .padding(5.dp, 0.dp, 0.dp, 5.dp)
                .sizeIn(maxWidth = 200.dp)
                .background(
                    color = Color.DarkGray.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(0.dp, 0.dp, 6.dp, 0.dp)
                .align(Alignment.BottomStart),
            username = user(id).userName,
            muted = participantMuted
        )
    }
}
