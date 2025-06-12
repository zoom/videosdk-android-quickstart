This is a Sample App of the Android Zoom Video SDK using Jetpack Compose. Compose is a popular, modern UI toolkit from Android for building native Android UIs with Kotlin. With the use of this app, developers can quickly start
their Zoom Video SDK projects, integrate the SDK into their projects, or use as reference for best coding practices for our Android Video SDK. 

Use of this Sample App is subject to our [Terms of Use](https://www.zoom.com/en/trust/terms/).

## Installation

Clone this repo into your local enviroment:
```
$ git clone https://github.com/zoom/VideoSDK-Android-Quickstart.git
```

Once cloned, open Android Studio and build your project.

In the `.env` file found in `app/src/main/assets`, you can either enter a JWTToken retrieved from your local environment or enter an Endpoint URL to a server of your choice, which the app will use to query for a JWTToken. 

> :warning: **Do not store credentials in plain text on production environments**

## Configuration
The request query parameters and body structure can be edited to match your servers requirements in the `ApiService.kt` file. The current request structure is as follows:
```
curl --location --request POST 'http://ENDPOINT_URL/zoomtoken?token=&name=&password=' \
--header 'Content-Type: application/json' \
--data '{                        
    "body": {                    
        "sessionName" = "",
        "role" = 0,
        "userIdentity" = "",
        "sessionkey" = "",
        "geo_regions" = "",
        "cloud_recording_option" = 0,
        "cloud_recording_election" = 0,
        "telemetry_tracking_id" = "",
        "video_webrtc_mode" = 0,
        "audio_webrtc_mode" = 0
    }
}'
```

## Usage
After building the app and entering either a JWTToken or Endpoint URL, you can start the Sample App!  

<img width="755" alt="Image" src="https://github.com/user-attachments/assets/d49a4c37-60d3-471a-b3e4-0b6a13947c41" />
