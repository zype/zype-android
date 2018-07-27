# Fastlane Tutorial

Fastlane is a set of command line tools which allow you to automate parts of your app building workflow. With Fastlane you can automate screenshots, build your app and upload to the Google Play Store. **Note: For the Google Play Store, you will not be able to use Fastlane to automate first time uploads.**

## Requirements

In order to use Fastlane, you need to first install Fastlane along with it's dependencies on your development computer. You can install Fastlane for the Zype template by:

1. Open __Terminal__ and navigate to the app folder with the following command: `cd <PATH TO APP FOLDER>`. **Note: all commands in the Terminal should be entered from the base of your app folder.**
2. Enter the following command to download Fastlane: `bundle install`

## Setup

Before you can build your Android app, you will need to configure some files.

1. In order to configure Fastlane to build your Android app, you will need to update **fastlane/Fastfile**. You will need to update the `keystore_name`, `alias_name`, `store_password`, and `key_password` in order to create a Java keystore which is used for code signing in Android apps. It is not recommended to use special characters besides `.`, `-`, or `_`.
- Your `keystore_name` is the filename for your `.jks` file. The Java keystore used for code signing uses the `.jks` file extension.
- Your `alias_name` identifies who/what the keystore is for. You can put your company name or app name as the alias.
- Your `store_password` is the password used for accessing the keystore file. Set this to anything you want.
- Your `key_password` is the password used for reading the keys inside your keystore file. Set this to anything you want.

<a href="https://drive.google.com/uc?export=view&id=1Ac8wVykIP6YbE-blxXxe_F59SiTDNq7-"><img src="https://drive.google.com/uc?export=view&id=1Ac8wVykIP6YbE-blxXxe_F59SiTDNq7-" style="width: 500px; max-width: 100%; height: auto" title="Click for the larger version." /></a>

### Automating Screenshots with Fastlane

2. In order to automatically take screenshots with Fastlane, you will need to have an Android device open with Android Studio. This can be a physical Android device connected via USB cable or it can be an Android virtual device (emulator). After you have a device connected, you can automatically take screenshots of your app by running the following command in the Terminal within your project directory:

```
fastlane screenshots
```

When you run this command, Fastlane will generate the APK for the tests and the debug version of your app. These will be installed on your device and automatically run. After Fastlane takes the screenshots of the app, they will be stored inside a **fastlane/metadata/en-US/images/phoneScreenshots** folder.

### Code Signing with Fastlane

3. You can create your Java keystore using Fastlane. A Java keystore is used for code signing Android apps and you will need this to create a Release version of your app. If you completed the setup in **Step 1**, you can create your keystore by entering the following command in the Terminal:

```
fastlane create_keystore
```

Your `.jks` file will be saved inside the **fastlane** folder. When creating apps for the Google Play Store the same keystore needs to be used every time for the same app, so remember to keep this file along with the alias and passwords saved. If you do not have them, you will not be able to update your app going forward.

### Build your Release app with Fastlane

4. After your app is properly configured (i.e. placeholders, assets, feature flags, app id, version number, etc.) and you have created your keystore, you can build the Release version of your app. You can create your app package (`.apk` file), by entering the following command:

```
fastlane build_apk
```

This will create your APK file which you can upload to the Google Play Store. Usually Android Studio will build your Release APK file in the **app/build/outputs/apk/template/release** folder.

### Uploading your APK to Google Play Store

**Note:** You cannot use Fastlane to upload the first version of your APK to the Google Play Store. Google Play does not allow automated updates for apps that do not exist yet. The only way to create your app is to upload an initial APK to get an app id. If you have created your app under your Google Play account, you can follow these steps to automate future uploads of your app.

1. Follow the steps at [https://docs.fastlane.tools/actions/supply/#setup](https://docs.fastlane.tools/actions/supply/#setup) in order to create to create a service account which can upload for your dev team.
2. After creating the service account and downloading the JSON file for your service account, place your service account JSON within the app folder. After you have placed your JSON in the app folder, you should update the `json_key_file` inside **fastlane/Appfile**. This allows Fastlane to upload to the correct account.
3. Before your service account can upload apps to your account, your app needs to be added under your service accounts permissions.

<a href="https://drive.google.com/uc?export=view&id=1ZJwFE-t5ezi0ryfdIa-eq-adwMHQ0PxF"><img src="https://drive.google.com/uc?export=view&id=1ZJwFE-t5ezi0ryfdIa-eq-adwMHQ0PxF" style="width: 500px; max-width: 100%; height: auto" title="Click for the larger version." /></a>

<a href="https://drive.google.com/uc?export=view&id=1Rgt7mpNTgghFd8JDiN1qxXfPAamkmSjI"><img src="https://drive.google.com/uc?export=view&id=1Rgt7mpNTgghFd8JDiN1qxXfPAamkmSjI" style="width: 500px; max-width: 100%; height: auto" title="Click for the larger version." /></a>

<a href="https://drive.google.com/uc?export=view&id=1Gz0LAoN5LDQkNTu2TTlOZl1LfWjuQ9ut"><img src="https://drive.google.com/uc?export=view&id=1Gz0LAoN5LDQkNTu2TTlOZl1LfWjuQ9ut" style="width: 500px; max-width: 100%; height: auto" title="Click for the larger version." /></a>

<a href="https://drive.google.com/uc?export=view&id=1i0mPy21Vz1Olb2Aq4clcp7_f163Vlot-"><img src="https://drive.google.com/uc?export=view&id=1i0mPy21Vz1Olb2Aq4clcp7_f163Vlot-" style="width: 500px; max-width: 100%; height: auto" title="Click for the larger version." /></a>

4. Once you have created your service account, downloaded the service account JSON and added your app to the service account's permissions. You can upload your release app to the Google Play Store with the following command:

```
fastlane upload_apk
```

You can determine which track you upload it to by adding the `track`. The track option accepts: `production`, `beta`, `alpha`, and `internal` (for internal testing).

```
// Example
fastlane upload_apk track:production
```

This command tells Fastlane to upload your Release app to your Google Play account. It will look for the APK file in the path specified in `release_apk_path` in **fastlane/Fastfile**. This path should be set to the folder where Android Studio builds your APKs. **Note:** this may not be the same depending on what you have configured in Android Studio. If you cannot locate your release APK, check **app/build/outputs/apk/template/release** or **app/template/release**.

<a href="https://drive.google.com/uc?export=view&id=14c237yF2VpJZnqjV3Tt185vTzh8wEji5"><img src="https://drive.google.com/uc?export=view&id=14c237yF2VpJZnqjV3Tt185vTzh8wEji5" style="width: 500px; max-width: 100%; height: auto" title="Click for the larger version." /></a>
