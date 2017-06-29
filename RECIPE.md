# Zype Android Mobile Recipe

This document outlines step-by-step instructions for creating and publishing an Android mobile app powered by Zype's Endpoint API service and app production software and SDK template.

## Requirements and Prerequisites

#### Technical Contact
IT or developer support strongly recommended. Completing app submission and publishing requires working with app bundles and IDE.

#### Mac with Android Studio installed
In order to compile, run, and package an app you need the latest version of Android Studio to be installed on your computer. Android Studio can be downloaded from the [App Store](https://developer.android.com/studio/index.html). 

#### Mac with ADB installed
You'll need to have ADB (Android Debug Bridge) installed in order to perform adb installs in terminal. To install them on your computer, follow the [ADB documentation](https://developer.android.com/studio/command-line/adb.html).

## Creating a New App with the SDK Template

#### Generating the bundle and running the app

1. In order to generate an Android bundle using this SDK, you must first pull the latest source code from Zype's github repository. This can be found at "https://github.com/zype/zype-android". 

<a href="https://drive.google.com/uc?export=view&id=0BzMPADAfOuPQVXpVS3pPTGxlYU0"><img src="https://drive.google.com/uc?export=view&id=0BzMPADAfOuPQVXpVS3pPTGxlYU0" style="width: 500px; max-width: 100%; height: auto" title="Click for the larger version." /></a>

Select the green __"Clone or download"__ button on the right. 

<a href="https://drive.google.com/uc?export=view&id=0BzMPADAfOuPQU3V0N1NmaFBra3M"><img src="https://drive.google.com/uc?export=view&id=0BzMPADAfOuPQU3V0N1NmaFBra3M" style="width: 500px; max-width: 100%; height: auto" title="Click for the larger version." /></a>

From here, there are two options to copy the files:

a. Click the __"Download ZIP"__ button on the bottom right. Then pick a folder to save the zipped files to. 

<a href="https://drive.google.com/uc?export=view&id=0BzMPADAfOuPQWnlZTzRKV201SVU"><img src="https://drive.google.com/uc?export=view&id=0BzMPADAfOuPQWnlZTzRKV201SVU" style="width: 500px; max-width: 100%; height: auto" title="Click for the larger version." /></a>

<a href="https://drive.google.com/uc?export=view&id=0BzMPADAfOuPQYVZmT0RqQjVzTkU"><img src="https://drive.google.com/uc?export=view&id=0BzMPADAfOuPQYVZmT0RqQjVzTkU" style="width: 500px; max-width: 100%; height: auto" title="Click for the larger version." /></a>

Once the ZIP file is downloaded, open the file to reveal the contents. 

<a href="https://drive.google.com/uc?export=view&id=0BzMPADAfOuPQMUFUcTBoSXlfMVU"><img src="https://drive.google.com/uc?export=view&id=0BzMPADAfOuPQMUFUcTBoSXlfMVU" style="width: 500px; max-width: 100%; height: auto" title="Click for the larger version." /></a>

__OR__

b.  Click the __"Git web URL"__ to highlight it and copy the URL. 

<a href="https://drive.google.com/uc?export=view&id=0BzMPADAfOuPQdXZySmRRLXRyZUk"><img src="https://drive.google.com/uc?export=view&id=0BzMPADAfOuPQdXZySmRRLXRyZUk" style="width: 500px; max-width: 100%; height: auto" title="Click for the larger version." /></a>

Open terminal and __"cd"__ into the folder you want to save the files to.

##### Helpful command line tips for Terminal

    ```
    ls  ---> shows folders in current directory
    cd Downloads  ---> goes into downloads if available (see ls)
    cd Downloads/myproject  ---> goes into downloads/myproject if available (see ls)
    cd ..  ---> goes back one directory level up 
    ```

Clone the files into this folder by using the command __"git clone ***"__ and replace the "***" with the copied url. Press enter.

<a href="https://drive.google.com/uc?export=view&id=0BzMPADAfOuPQMFAtOV9hV0hwTDA"><img src="https://drive.google.com/uc?export=view&id=0BzMPADAfOuPQMFAtOV9hV0hwTDA" style="width: 500px; max-width: 100%; height: auto" title="Click for the larger version." /></a>

2. Now you have the file containing the SDK. This must be packaged into an APK in order to run on your device. Open the application folder in Android Studio. (File > Open > *application folder*)

<a href="https://drive.google.com/uc?export=view&id=0BzMPADAfOuPQeWRjWFhHZjUxVG8"><img src="https://drive.google.com/uc?export=view&id=0BzMPADAfOuPQeWRjWFhHZjUxVG8" style="width: 500px; max-width: 100%; height: auto" title="Click for the larger version." /></a>

3. Build APK by selecting Build>Build APK.

<a href="https://drive.google.com/uc?export=view&id=0BzMPADAfOuPQTHhsbjlZODM0cms"><img src="https://drive.google.com/uc?export=view&id=0BzMPADAfOuPQTHhsbjlZODM0cms" style="width: 500px; max-width: 100%; height: auto" title="Click for the larger version." /></a>

4. Once the APK is packaged, you can find it by navigating to the application folder where it is stored (*Application Folder* > app > build > outputs > apk > *new app package*) or by clicking the __"Reveal in Finder"__ link toward the bottom of the event log. 

<a href="https://drive.google.com/uc?export=view&id=0BzMPADAfOuPQYlRadmllUDNaTkU"><img src="https://drive.google.com/uc?export=view&id=0BzMPADAfOuPQYlRadmllUDNaTkU" style="width: 500px; max-width: 100%; height: auto" title="Click for the larger version." /></a>

5. Open terminal. CD into the folder with the APK. An easy way to do this is to type __"cd"__, hit the spacebar, then drag the folder containing the APK into terminal. Then hit the enter key. 

<a href="https://drive.google.com/uc?export=view&id=0BzMPADAfOuPQNW5rM3JwSlZ4Wkk"><img src="https://drive.google.com/uc?export=view&id=0BzMPADAfOuPQNW5rM3JwSlZ4Wkk" style="width: 500px; max-width: 100%; height: auto" title="Click for the larger version." /></a>

6. Turn on developer's mode on the Android device. You can do this by navigating to Settings>Developer Options. Turn the toggle at the top of the screen to __"on"__.

7. Connect the device to your computer by either using a USB cable or terminal. To connect the device using terminal, enter the command __"adb connect ***"__ where the asterisks are the IP address of the device. The IP address can be found in Settings>Connections>Wi-Fi>Advanced. The IP address is the last listed item. 

<a href="https://drive.google.com/uc?export=view&id=0BzMPADAfOuPQbU1FakZnU2ZJeTg"><img src="https://drive.google.com/uc?export=view&id=0BzMPADAfOuPQbU1FakZnU2ZJeTg" style="width: 500px; max-width: 100%; height: auto" title="Click for the larger version." /></a>

8. Once the device is connected, return to terminal and enter the command __"adb devices"__. This should all Android devices the computer is currently connected to. If you see your devices listed, proceed to the next step. If not, reconnect device until it appears at this step. 

<a href="https://drive.google.com/uc?export=view&id=0BzMPADAfOuPQLVdBYVhrQ3lWM2c"><img src="https://drive.google.com/uc?export=view&id=0BzMPADAfOuPQLVdBYVhrQ3lWM2c" style="width: 500px; max-width: 100%; height: auto" title="Click for the larger version." /></a>

9. While in terminal, enter the command __"adb install ***"__ and replace the asterisks with the .apk file. 

<a href="https://drive.google.com/uc?export=view&id=0BzMPADAfOuPQbHhfVENpQjJzbkk"><img src="https://drive.google.com/uc?export=view&id=0BzMPADAfOuPQbHhfVENpQjJzbkk" style="width: 500px; max-width: 100%; height: auto" title="Click for the larger version." /></a>

10. The app will be on your device.






