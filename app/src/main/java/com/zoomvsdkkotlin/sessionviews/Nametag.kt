package com.zoomvsdkkotlin.sessionviews

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.zoomvsdkkotlin.R

@Composable
fun Nametag(
    modifier: Modifier,
    username: String,
    muted: Boolean
) {
    Box (
        modifier = modifier,
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
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

            Text(
                text = username,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}