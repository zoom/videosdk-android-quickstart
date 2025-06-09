package com.zoomvsdkkotlin.sessionviews


import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun IndeterminateCircleLoader(
    modifier: Modifier,
) {
     CircularProgressIndicator(
         modifier = modifier,
         color = MaterialTheme.colorScheme.secondary,
         trackColor = MaterialTheme.colorScheme.surfaceVariant,
     )
}