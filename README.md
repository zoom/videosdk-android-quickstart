# Video SDK Android Quickstart 

This is a Sample App of the Android Zoom Video SDK using Jetpack Compose. Compose is a popular, modern UI toolkit from Android for building native Android UIs with Kotlin. With the use of this app, developers can quickly start
their Zoom Video SDK projects, integrate the SDK into their projects, or use as reference for best coding practices for our Android Video SDK. 

Use of this Sample App is subject to our [Terms of Use](https://www.zoom.com/en/trust/terms/).

You can find the corresponding Developer Blog for this app [here](https://developers.zoom.us/blog/videosdk-android-quickstart/)

## Installation

Clone this repo into your local enviroment:
```
$ git clone https://github.com/zoom/VideoSDK-Android-Quickstart.git
```

Once cloned, navigate to the 'videosdk-android-quickstart' directory

You can use the `studio .` command to open it in Android Studio.

In the `env-sample` file found in `app/src/main/assets`, you can choose to either enter an endpoint url to a backend server which handles JWT Token generation or you can leave the field blank and use the below steps for manually generating a token in the Android Studio terminal. Once your data is entered, rename this file to `env`. 

> :warning: **Do not store credentials in plain text on production environments**

## Configuration

For manually generating a JWT Token:
1. Ensure you are using JDK 16+ and your project language level is set to JDK 16+
2. In the terminal navigate to the utils folder with this command `cd app/src/main/java/com/zoomvideosdkkotlin/utils`
3. Compile the `TokenGenerator.java` with this command: `javac -cp "lib/*"  TokenGenerator.java`
4. Execute the file with this command with command line argument in this order: `java -cp "lib/*" TokenGenerator.java [topic] [role(0 or 1)] [sdk key] [sdk secret]`
5. A JWT Token will output to the console. From there Build and run the application and input the JWT into the app when prompted.

If you choose to use the apps APIClient, the request query parameters and body structure can be edited to match your servers requirements in the `ApiService.kt` file. The current request structure is as follows:
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
After entering either a JWTToken or Endpoint URL and configuring your Endpoint,  you can start the Sample App by clicking Build and Run! 

<img width="755" alt="Image" src="https://github.com/user-attachments/assets/d49a4c37-60d3-471a-b3e4-0b6a13947c41" />
