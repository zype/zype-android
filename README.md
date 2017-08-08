Don't know what Zype is? Check this [overview](http://www.zype.com/).

# Zype Android Template

This SDK allows you to set up an eye-catching, easy to use Android video streaming app integrated with the Zype platform with minimal coding and configuration. The app is built with Java and Zype API. With minimal setup you can have your Android up and running.
An example of fully functional app that is using a Zype sandbox account.


## Prerequisites

```
Android Studio
```

## Installing

```
1. Clone repo
2. Open Android Studio and create new Project from Version Control
3. Build project and run
```

## Supported Features

- Populates your app with content from enhanced playlists
- Video Search
- Live Streaming videos
- Downloading videos
- Video Favorites
- Dynamic theme colors
- Resume watch functionality

## Unsupported Features

- Closed Caption Support
- Native SVOD via In App Purchases

## Monetizations Supported

- Pre-roll Ads (VAST)
- Midroll ads
- Universal SVOD via login

## Creating new Android app based on Zype Template

Steps you need to perform to get a new app running:

### Mandatory updates

1. In the app level ```build.gradle``` file change ```applicationId``` to your app package name

2. Change app name, launch icon, accent color and other resources to customize app appearance

3. API keys:

    Update following constants in ```ZypeSettings``` class with values applicable to your Zype account:
* ```APP_KEY```
* ```CLIENT_ID```
* ```CLIENT_SECRET```
* ```ROOT_PLAYLIST_ID```

4. Setup app features:

    Update feature flags in ```ZypeSettings``` class to customize functinality and UI of your app
* ```BACKGROUND_PLAYBACK_ENABLED```
* ```DOWNLOADS_ENABLED```
* ```DOWNLOADS_ENABLED_FOR_GUESTS```
* ```NATIVE_SUBSCRIPTION_ENABLED```
* ```SHARE_VIDEO_ENABLED```
* ```THEME_LIGHT```
* ```UNIVERSAL_SUBSCRIPTION_ENABLED```

### Optional

5. In 'ZypeSettings' class provide your social network ids
6. To use Google Analytics in your app provide your GA id in 'ZypeSettings' class and uncomment init of Google Analytics in 'ZypeApp' class
7. To use Fabric in your app provide your fabric api key in 'AndroidManifest.xml' and uncomment init of fabric in 'ZypeApp' class
8. To use OneSignal in your app uncomment init of OneSignal in 'ZypeApp' class



## Built With

* [Java](https://en.wikipedia.org/wiki/java) - Language Java
* [Gradle](https://gradle.org) - Build Tool
* [Fabric](https://get.fabric.io/) - Analytics and Crashlitics
* [OneSignal](https://onesignal.com/) - Multiplatform push notifications
* [Zype API](http://dev.zype.com/api_docs/intro/) - Zype API docs

## App Architecture

Coming soon

## Contributing

Please submit pull requests to us.

## Versioning

For the versions available, see the [tags on this repository](https://github.com/zype/zype-android/tags).

## Authors

See also the list of [contributors](https://github.com/zype/zype-android/graphs/contributors) who participated in this project.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details

