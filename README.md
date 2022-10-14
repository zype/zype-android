Want to learn more about Zype’s solutions for OTT apps, video streaming and playout? Visit our [website](http://www.zype.com/).

# Zype Android Template

This legacy open source app template is no longer supported by Zype. If you are looking to build streaming applications for OTT, we recommend using [Zype Apps Creator](https://www.zype.com/product/apps-creator) for the latest app building features and functionality. 

The app is built with Java and Zype API. 


## Prerequisites

- Android Studio

## Installing

**1.** Clone the repo.

**2.** Open Android Studio and create new Project from Version Control

**3.** This SDK has two app variants - Zype demo app and template app.

   To build Zype demo app select `zypeDebug` build variant for `app` module

   <a href="https://drive.google.com/uc?export=view&id=1g7YOrIgbfIJLljrXsi4oRC8efrWygIly"><img src="https://drive.google.com/uc?export=view&id=1g7YOrIgbfIJLljrXsi4oRC8efrWygIly" style="width: auto; height: auto" title="Click for the larger version." /></a>

> **Note:** To build custom app based on the template see [this section](#template).

**4.** Build project and run the app on a connected device or in the emulator.

## Supported Features

- Populates your app with content from enhanced playlists
- Video Search
- Live Streaming videos
- Downloading videos
- Video Favorites
- Resume watch functionality
- Closed Caption Support

## Monetizations Supported

- Pre-roll and Mid-roll Advertising (VAST)
- Universal SVOD via Sign In
- Native SVOD via In-App Purchases
- Marketplace Connect (Native to Universal SVOD)

## Creating Custom App Based on the Template<a name="template"></a>

**1.** Select `templateDebug` (or `templateRelease`) build variant for `app` module

**2.** Replace following placeholders with actual values:

####  **[build.gradle](https://github.com/zype/zype-android/blob/master/app/build.gradle)**

   `<APPLICATION_ID>` - Package name for your app. Used to identify your app on the device and in the marketplace. Must be unique and usually is following `com.yourdomain.android` pattern.

####  **[ZypeSettings.java](https://github.com/zype/zype-android/blob/master/app/src/template/java/com/zype/android/ZypeSettings.java)**

   Use respective values from your account in Zype platform for:

   `<APP_KEY>`

   `<CLIENT_ID>`

   `<ROOT_PLAYLIST_ID>`

   Use `true` or `false` values for:

   `<NATIVE_SUBSCRIPTION_ENABLED>`

   `<NATIVE_TO_UNIVERSAL_SUBSCRIPTION_ENABLED>`

   `<SUBSCRIBE_TO_WATCH_AD_FREE_ENABLED>`

   `<UNIVERSAL_SUBSCRIPTION_ENABLED>`

   `<UNIVERSAL_TVOD>`

   `<AUTOPLAY>`
   
   `<BACKGROUND_AUDIO_PLAYBACK_ENABLED>`

   `<BACKGROUND_PLAYBACK_ENABLED>`

   `<DOWNLOADS_ENABLED>`

   `<DOWNLOADS_ENABLED_FOR_GUESTS>`

   `<DEVICE_LINKING>`

   `<DEVICE_LINKING_URL>` - Update with your url for device linking if you set DEVICE_LINKING to true. Otherwise set empty string.

   `<THEME>` - Use constants `ZypeConfiguration.THEME_LIGHT` or `ZypeConfiguration.THEME_DARK`

####  **[colors.xml](https://github.com/zype/zype-android/blob/master/app/src/template/res/values/colors.xml)**, **[values-night/colors.xml](https://github.com/zype/zype-android/blob/master/app/src/template/res/values-night/colors.xml)**

   `<BRAND_COLOR>` - Accent color in `##RRGGBB` format. Used for highlighting buttons and widgets.

####  **[strings.xml](https://github.com/zype/zype-android/blob/master/app/src/template/res/values/strings.xml)**

   `<APP_NAME>`

**3.** Update following resources:

####  **[splash_logo.png](https://github.com/zype/zype-android/blob/master/app/src/template/res/drawable-xxhdpi/splash_logo.png)**

   This image is used on the splash screen when app is starting.

####  **[mipmap-mdpi/ic_launcher.png](https://github.com/zype/zype-android/blob/master/app/src/template/res/mipmap-mdpi/ic_launcher.png)**, **[mipmap-hdpi/ic_launcher.png](https://github.com/zype/zype-android/blob/master/app/src/template/res/mipmap-hdpi/ic_launcher.png)**, **[mipmap-xhdpi/ic_launcher.png](https://github.com/zype/zype-android/blob/master/app/src/template/res/mipmap-xhdpi/ic_launcher.png)**, **[mipmap-xxhdpi/ic_launcher.png](https://github.com/zype/zype-android/blob/master/app/src/template/res/mipmap-xxhdpi/ic_launcher.png)**

   The icon of your app. You can use icon generator in Android Studio to produce icons with required dimensions from 512x512 source icon image.

> #### Optional

**4.** Check out [this guide](https://github.com/zype/zype-android/blob/master/UI.md) for additional UI customization options.

**5.** Update your social network ids in [ZypeSettings.java](https://github.com/zype/zype-android/blob/master/app/src/template/java/com/zype/android/ZypeSettings.java)

**6.** Analytics

**Google Analytics:**
 - Update your GA id in [ZypeSettings.java](https://github.com/zype/zype-android/blob/master/app/src/template/java/com/zype/android/ZypeSettings.java)
 - Uncomment init of Google Analytics in [ZypeApp.java](https://github.com/zype/zype-android/blob/master/app/src/main/java/com/zype/android/ZypeApp.java)

 **Segment Analytics:**
 
 In [ZypeSettings.java](https://github.com/zype/zype-android/blob/master/app/src/template/java/com/template/android/ZypeSettings.java)
 - Set `SEGMENT_ANALYTICS` to `true` 
 - Update `SEGMENT_ANALYTICS_WRITE_KEY` with a value from your Segment account 

**7.** To use Fabric:
 - In [AndroidManifest.xml](https://github.com/zype/zype-android/blob/master/app/src/main/AndroidManifest.xml) uncomment Fabric section and put you Fabric API key:  
 ```
         <meta-data
            android:name="io.fabric.ApiKey"
            android:value="<YOUR KEY HERE>" />
        -->
```
 - In [ZypeApp.java](https://github.com/zype/zype-android/blob/master/app/src/main/java/com/zype/android/ZypeApp.java) uncomment Fabric initialization:
 ```
 initFabric();
 ``` 
 - In [build.gradle](https://github.com/zype/zype-android/blob/master/app/build.gradle) uncomment following line: 
 ```
 apply plugin: 'io.fabric'
 ``` 
 
**8.** Integration of OneSignal push notification:
 - Set `OneSignal` attribute in [zype_app_configuration.json](https://github.com/zype/zype-android/blob/master/app/src/template/res/raw/zype_app_configuration.json) file to `true`
 - Set `onesignal_app_id` attribute in the following code in the [build.gradle](https://github.com/zype/zype-android/blob/master/app/build.gradle):
 ```
     productFlavors {
         ...
         template {
             ...
             manifestPlaceholders = [manifestApplicationId: "${applicationId}",
                                     // TODO: Provide valid app_id and google_project_number for OneSignal
                                     onesignal_app_id: '',
                                     onesignal_google_project_number: 'REMOTE']
         }
     }
```

**9.** Integration of Amazon Pinpoint push notification:
 - Uncomment `apply plugin: 'com.google.gms.google-services'` in [app/build.gradle](https://github.com/zype/zype-android/blob/master/app/build.gradle)
 - Set `AWSPinpoint` attribute in [zype_app_configuration.json](https://github.com/zype/zype-android/blob/master/app/src/template/res/raw/zype_app_configuration.json) file to `true`
 - Set up your project in the Amazon Mobile Hub, turn on Pinpoint service for the project and generate AWS configuration file for your app. Then put `awsconfiguration.json` file to the [app/src/template/res/raw](https://github.com/zype/zype-android/blob/master/app/src/template/res/raw/) folder


## Built With

* [Java](https://en.wikipedia.org/wiki/java) - Language Java
* [Gradle](https://gradle.org) - Build Tool
* [Zype API](http://dev.zype.com/api_docs/intro/) - Zype API docs
* [Fabric](https://get.fabric.io/) - Analytics and Crashlitics
* [OneSignal](https://onesignal.com/) - Multiplatform push notifications


## Versioning

For the versions available, see the [tags on this repository](https://github.com/zype/zype-android/tags).

## Authors

See also the list of [contributors](https://github.com/zype/zype-android/graphs/contributors) who participated in this project.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details

