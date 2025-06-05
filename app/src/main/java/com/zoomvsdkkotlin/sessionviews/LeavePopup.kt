package com.zoomvsdkkotlin.sessionviews


import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Popup
import com.zoomvsdkkotlin.utils.Routes
import us.zoom.sdk.ZoomVideoSDKUser

@SuppressLint("ResourceType")
@Composable
fun LeavePopup(
    modifier: Modifier,
    user: () -> ZoomVideoSDKUser,
    closeSession: (Boolean) -> Unit,
    navigate: (String) -> Unit
) {
    Box(
        modifier = modifier
    ) {
        val color = ButtonDefaults.buttonColors(containerColor = Color.Red)

        if (user().isHost) {
            Popup(alignment = Alignment.Center) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        colors = color,
                        onClick = {
                        closeSession(false)
                        navigate(Routes.JOINSESSION)
                    }) { Text("Leave Session") }
                    Button(
                        colors = color,
                        onClick = {
                        closeSession(true)
                        navigate(Routes.JOINSESSION)
                    }) { Text("End Session") }
                }
            }
        } else {
            Popup(alignment = Alignment.Center) {
                Button(
                    colors = color,
                    onClick = {
                    closeSession(false)
                    navigate(Routes.JOINSESSION)
                }) { Text("Leave Session") }
            }
        }
    }
}