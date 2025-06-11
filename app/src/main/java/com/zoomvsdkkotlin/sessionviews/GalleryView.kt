package com.zoomvsdkkotlin.sessionviews

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
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
    val isPortrait = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT
    val getUser = remember(currentUsersInView) {{ i: Int  ->  currentUsersInView()[i] }}

    BoxWithConstraints() {
        val boxWidth = maxWidth

        if (isPortrait) {
            //portrait
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val width = if (currentUsersInViewCount > 1) boxWidth / 2 else boxWidth

                Row(
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (currentUsersInViewCount > 0) {
                        UserView(
                            modifier = Modifier
                                .size(width)
                                .padding(horizontal = 5.dp),
                            user = getUser,
                            renderView = renderView,
                            id = 0,
                            participantVideoOn = participantVideoOn1
                        )
                    }

                    if (currentUsersInViewCount > 1) {
                        UserView(
                            modifier = Modifier
                                .size(width)
                                .padding(horizontal = 5.dp),
                            user = getUser,
                            renderView = renderView,
                            id = 1,
                            participantVideoOn = participantVideoOn2
                        )
                    }
                }
                if (currentUsersInViewCount > 2) {
                    UserView(
                        modifier = Modifier
                            .size(width)
                            .padding(horizontal = 5.dp, vertical = 10.dp),
                        user = getUser,
                        renderView = renderView,
                        id = 2,
                        participantVideoOn = participantVideoOn3
                    )
                }
            }
        } else {
            //Landscape
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val width = if (currentUsersInViewCount > 2) boxWidth / 3 else boxWidth / 2

                if (currentUsersInViewCount > 0) {
                    UserView(
                        modifier = Modifier
                            .width(width)
                            .height(width)
                            .padding(horizontal = 5.dp),
                        user = getUser,
                        renderView = renderView,
                        id = 0,
                        participantVideoOn = participantVideoOn1
                    )
                }
                if (currentUsersInViewCount > 1) {
                    UserView(
                        modifier = Modifier
                            .width(width)
                            .height(width)
                            .padding(horizontal = 5.dp),
                        user = getUser,
                        renderView = renderView,
                        id = 1,
                        participantVideoOn = participantVideoOn2
                    )
                }
                if (currentUsersInViewCount > 2) {
                    UserView(
                        modifier = Modifier
                            .width(width)
                            .height(width)
                            .padding(horizontal = 5.dp),
                        user = getUser,
                        renderView = renderView,
                        id = 2,
                        participantVideoOn = participantVideoOn3
                    )
                }
            }
        }
    }
}