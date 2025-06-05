package com.zoomvsdkkotlin

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.zoomvsdkkotlin.activities.InSession
import com.zoomvsdkkotlin.activities.JoinSession
import com.zoomvsdkkotlin.utils.Routes
import com.zoomvsdkkotlin.viewmodel.ZoomSessionViewModel

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        val zoomSessionViewModel by viewModels<ZoomSessionViewModel>()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = Routes.JOINSESSION, builder = {
                composable(Routes.JOINSESSION){  JoinSession(navController, zoomSessionViewModel) }
                composable(Routes.INSESSION){ InSession(navController, zoomSessionViewModel) }
            })
        }
    }
}
