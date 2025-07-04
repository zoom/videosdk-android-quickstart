package com.zoomvsdkkotlin.activities

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.zoomvsdkkotlin.utils.ApiClient
import com.zoomvsdkkotlin.utils.Routes
import com.zoomvsdkkotlin.utils.TokenGenerator
import com.zoomvsdkkotlin.viewmodel.ZoomSessionViewModel
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.launch
import retrofit2.awaitResponse


data class JWTOptions(
    @SerializedName("sessionName") val sessionName: String,
    @SerializedName("role") val role: Int,
    @SerializedName("userIdentity") val userIdentity: String,
    @SerializedName("sessionkey") val sessionkey: String,
    @SerializedName("geo_regions") val geo_regions: String,
    @SerializedName("cloud_recording_option") val cloud_recording_option: Int,
    @SerializedName("cloud_recording_election") val cloud_recording_election: Int,
    @SerializedName("telemetry_tracking_id") val telemetry_tracking_id: String,
    @SerializedName("video_webrtc_mode") val video_webrtc_mode: Int,
    @SerializedName("audio_webrtc_mode") val audio_webrtc_mode: Int,
)

data class Signature(val signature: String)
data class Config(val sessionName: String, val userName: String, val password: String?, val jwt: String )

@Composable
fun JoinSession(navController: NavController, zoomSessionViewModel: ZoomSessionViewModel) {
    val dotenv = dotenv {
        directory = "/assets"
        filename = "env"
    }

    //place generated JWT here, if no value is provided the APIClient will be used
    //to retrieve a token from your specified Endpoint
    val sdkKey: String = dotenv["SDK_KEY"]
    val sdkSecret: String = dotenv["SDK_SECRET"]

    val scope = rememberCoroutineScope()

    var sessionName by remember {
        mutableStateOf("testSession")
    }
    var userName by remember {
        mutableStateOf("testUser")
    }
    var password by remember {
        mutableStateOf("123")
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,

        ) {
        OutlinedTextField(
            value = sessionName,
            onValueChange = {
                sessionName = it
            },
            label = {
                Text("Session Name")
            },
            placeholder = {
                Text("test")
            }
        )
        OutlinedTextField(
            value = userName,
            onValueChange = {
                userName = it
            },
            label = {
                Text("User Name")
            },
            placeholder = {
                Text("user1")
            })
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
            },
            label = {
                Text("Password")
            },
            placeholder = {
                Text("12345")
            })

        Row {
            Button(onClick = {
                val body = JWTOptions(
                    sessionName = sessionName,
                    role = 1,
                    userIdentity = null.toString(),
                    sessionkey = null.toString(),
                    geo_regions = null.toString(),
                    cloud_recording_option = 0,
                    cloud_recording_election = 0,
                    telemetry_tracking_id = "internal-dev5",
                    video_webrtc_mode = 0,
                    audio_webrtc_mode = 0
                )

                if (sdkKey.isNotEmpty() && sdkSecret.isNotEmpty()) {
                    val signature: String = TokenGenerator.generateToken(body, sdkKey, sdkSecret)
                    val config = Config(sessionName, userName, password, signature)
                    println(signature)
                    zoomSessionViewModel.initZoomSDK()
                    zoomSessionViewModel.joinSession(config)
                    navController.navigate(Routes.INSESSION)
                }
                else {
                    scope.launch() {
                        val response = ApiClient.apiService.getJWT(sessionName, userName, password, body).awaitResponse()
                        if (response.isSuccessful) {
                            val jwt = Gson().fromJson(response.body(), Signature::class.java)
                            val config = Config(sessionName, userName, password, jwt.signature)
                            println(jwt.signature)

                            zoomSessionViewModel.initZoomSDK()
                            zoomSessionViewModel.joinSession(config)
                            navController.navigate(Routes.INSESSION)
                        } else {
                            println("error")
                        }
                    }
                }
            }) {
                Text(text = "Join Session")
            }
        }
    }
}