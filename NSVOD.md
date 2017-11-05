# Native Subscription

This document outlines step-by-step instructions for setting up native subscription for your Android mobile app powered by Zype's Endpoint API service and app production software and SDK template.

## Requirements and Prerequisites

#### Technical Contact
IT or developer support strongly recommended. Completing app submission and publishing requires working with Google Play Console, app bundles and IDE.

#### Google Play Console
A [Google Account](https://accounts.google.com/SignUp) and [Google Developer Account](https://play.google.com/apps/publish/signup) will be needed.

#### Android Studio

## Google Play Console

#### Create Your App
1. Log in to your Google Play Console and either select your app, or create one. You can follow this simple [Google documentation](https://support.google.com/googleplay/android-developer/answer/113469) if you need to create one.

#### Subscription Options
2. After you hit create, look towards the left menu and pull down `Store presence` menu group and then select `In-app products` option. Then switch to `Subscriptions` tab.

    <a href="https://drive.google.com/uc?export=view&id=17MFW-IbnerIabvaX7z8mLLut8SWXI2Jl"><img src="https://drive.google.com/uc?export=view&id=17MFW-IbnerIabvaX7z8mLLut8SWXI2Jl" style="width: 500px; max-width: 100%; height: auto" title="Click for the larger version." /></a>

3. Click `Create subscription` button to create new subscription. Follow [this guide](https://support.google.com/googleplay/android-developer/answer/140504?hl=en) for details of the creating subscription process. Take a note of subscription `Product ID`. You will need it further.

    <a href="https://drive.google.com/uc?export=view&id=1N1xLCNzSxePE6egqSnP2zwj02nwdkDA1"><img src="https://drive.google.com/uc?export=view&id=1N1xLCNzSxePE6egqSnP2zwj02nwdkDA1" style="width: 500px; max-width: 100%; height: auto" title="Click for the larger version." /></a>

4. Repeat the process for any other subscription options you would like to offer - yearly, weekly, etc.

    _Note: To add In-app products you need to set up a merchant account. Follow [this guide](https://support.google.com/googleplay/android-developer/answer/3092739?hl=en) for details._

## Android Studio project
5. Open your Android Studio project and switch to the Android view.

#### Turn on Native subscription feature
6. Expand the `app` folder and go to `java/com.zype.android` folder. Then open the `ZypeSettings.java` file.

    <a href="https://drive.google.com/uc?export=view&id=1Vf1rilVpuTTJBeEQD59HPerl4Q2ZctBe"><img src="https://drive.google.com/uc?export=view&id=1Vf1rilVpuTTJBeEQD59HPerl4Q2ZctBe" style="width: 500px; max-width: 100%; height: auto" title="Click for the larger version." /></a>

7. Set following constants as stated below::
    
    ```
    public static final boolean NATIVE_SUBSCRIPTION_ENABLED = true;
    ...
    public static final boolean UNIVERSAL_SUBSCRIPTION_ENABLED = false;
    ```

#### Map In-App items
8. Expand the `app` folder and go to `java/com.zype.android/Billing` folder. Then open the `SubscriptionsHelper.java` file.

    <a href="https://drive.google.com/uc?export=view&id=1s9akox7SMLTju78BXi03hmmM5qh5ozOL"><img src="https://drive.google.com/uc?export=view&id=1s9akox7SMLTju78BXi03hmmM5qh5ozOL" style="width: 500px; max-width: 100%; height: auto" title="Click for the larger version." /></a>

9. In the `getSkuList()` method update the code where subscription SKUs are added to the array.

    For each your subscription option you should have following row:

    ```
        result.add("<Your product ID>");
    ```

    Use product ID of the subscription option that you have noted in step 3 as a string parameter of the `add` method.

    The result code of the method should look like this:
    ```
    public static List<String> getSkuList() {
        List<String> result = new ArrayList<>();
        result.add("com.zype.android.demo.monthly");
        result.add("com.zype.android.demo.yearly");
        return result;
    }
    ```


## Testing
Now that you’ve set up your in-app items and configured your app, it’s time to test out the integration and see how IAP interacts with your media.

10. Build a signed APK file of your app and publish it in Google Play Console into alpha or beta channel.

11. Enable testing in your alpha or beta channel and share opt-in URL with your testers.

    <a href="https://drive.google.com/uc?export=view&id=1IcXLNn5m1ZRkYmgo553GnwTWA9_K-EH2"><img src="https://drive.google.com/uc?export=view&id=1IcXLNn5m1ZRkYmgo553GnwTWA9_K-EH2" style="width: 500px; max-width: 100%; height: auto" title="Click for the larger version." /></a>

12. In left menu of Google Play Console select `Settings` option and the `Account details`. Add your tester accounts to the `License Testing` list. This will make testers to not charged when they test in-app purchases.

    <a href="https://drive.google.com/uc?export=view&id=13S5Nx34LopIV8z7_hRmNgPBDADxWHC4F"><img src="https://drive.google.com/uc?export=view&id=13S5Nx34LopIV8z7_hRmNgPBDADxWHC4F" style="width: 500px; max-width: 100%; height: auto" title="Click for the larger version." /></a>

