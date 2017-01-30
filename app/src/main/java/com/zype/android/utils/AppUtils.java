package com.zype.android.utils;

import com.zype.android.R;
import com.zype.android.ui.main.fragments.settings.WebActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

/**
 * @author vasya
 * @version 1
 *          date 9/17/15
 */
public class AppUtils {
    public static void openFacebook(Context context, String facebookUrl) {
        try {
            context.getPackageManager().getPackageInfo("com.facebook.katana", 0);
            Uri uri = Uri.parse("fb://facewebmodal/f?href=" + facebookUrl);
            context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
        } catch (Exception e) {
            Intent intent = new Intent(context, WebActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(BundleConstants.WEB_ACTIVITY_TITLE, "Facebook");
            bundle.putString(BundleConstants.WEB_ACTIVITY_URL, facebookUrl);
            intent.putExtras(bundle);
            context.startActivity(intent);
        }
    }

    public static void openTwitter(Context context, @Nullable String twitterId, @NonNull String twitterUrl) {
        Intent intent;
        try {
            // get the Twitter app if possible
            context.getPackageManager().getPackageInfo("com.twitter.android", 0);
            if (TextUtils.isEmpty(twitterId)) {
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitterUrl));
            } else {
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?user_id=" + twitterId));
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            // no Twitter app, revert to browser
            intent = new Intent(context, WebActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(BundleConstants.WEB_ACTIVITY_TITLE, "Twitter");
            bundle.putString(BundleConstants.WEB_ACTIVITY_URL, twitterUrl);
            intent.putExtras(bundle);
            context.startActivity(intent);
        }
    }

    public static void openWeb(Context context, @NonNull String webUrl) {
        Intent intent;
        intent = new Intent(context, WebActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(BundleConstants.WEB_ACTIVITY_TITLE, context.getString(R.string.web_title));
        bundle.putString(BundleConstants.WEB_ACTIVITY_URL, webUrl);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public static void openInstagram(Context context, @Nullable String instagramId, @NonNull String instagramUrl) {
        try {
            Uri uri = Uri.parse("http://instagram.com/_u/" + instagramId);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.instagram.android");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
        catch (Exception e) {
            Intent intent;
            intent = new Intent(context, WebActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(BundleConstants.WEB_ACTIVITY_TITLE, context.getString(R.string.instagram_title));
            bundle.putString(BundleConstants.WEB_ACTIVITY_URL, instagramUrl);
            intent.putExtras(bundle);
            context.startActivity(intent);
        }
    }

//    public static void openWebView(Activity activity, String title, String videoUrl) {
//        Intent intent = new Intent(activity.getApplicationContext(), WebActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putString(BundleConstants.WEB_ACTIVITY_TITLE, title);
//        bundle.putString(BundleConstants.WEB_ACTIVITY_URL, videoUrl);
//        intent.putExtras(bundle);
//        activity.startActivity(intent);
//    }
//
//    public static void openChromeTab(Activity activity, String videoUrl) {
//        final CustomTabsIntent tabsIntent = new CustomTabsIntent.Builder()
////                .setShowTitle(true)
//                .build();
//        tabsIntent.launchUrl(activity, Uri.parse(videoUrl));
//    }
}
