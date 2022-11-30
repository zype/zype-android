package com.zype.android.utils;

import com.google.android.material.snackbar.Snackbar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BaseTarget;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.zype.android.R;
import com.zype.android.core.provider.helpers.VideoHelper;
import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.webapi.model.video.VideoData;

/**
 * @author vasya
 * @version 1
 *          date 7/1/15
 */
public class UiUtils {

    public static boolean isLandscapeOrientation(Activity activity) {
        if (activity == null) {
            return false;
        }
        else {
            return (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
        }
    }

    public static void loadImage(@Nullable String url, int placeholderRes, @NonNull ImageView view) {
        Glide.with(view.getContext())
                .load(url)
                .apply(RequestOptions.centerCropTransform())
                .into(view);
    }

    public static void loadImage(Context context, @Nullable String url, int placeholderRes,
                                 @NonNull BaseTarget target) {
        Glide.with(context)
                .load(url)
                .into(target);
    }

    public static void loadImage(@Nullable final Context context, @Nullable final String imgUrl, @Nullable final ImageView img) {
        if (context == null) {
            Logger.e("Context is null");
            return;
        }
        if (TextUtils.isEmpty(imgUrl)) {
            Logger.e("Image url is empty");
            return;
        }
        if (img == null) {
            Logger.e("Image holder is empty");
            return;
        }
        Picasso.with(context).load(imgUrl).into(img);
    }

    public static void loadImage(@Nullable final Context context, @Nullable final String imgUrl,
                                 final int placeholderRes, @Nullable final ImageView img,
                                 @Nullable final ProgressBar progressBar, @Nullable final TextView title) {
        if (context == null) {
            Logger.e("Context is null");
            return;
        }
        if (TextUtils.isEmpty(imgUrl)) {
            Logger.e("Image url is empty");
            return;
        }
        if (img == null) {
            Logger.e("Image holder is empty");
            return;
        }

        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        else {
            Logger.w("loadImage(): Progress bar is empty");
        }
        Callback callback = new Callback() {
            @Override
            public void onSuccess() {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                if (title != null){
                    title.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError() {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                if (title != null){
                    title.setVisibility(View.VISIBLE);
                }
                if (placeholderRes > 0) {
                    img.setImageDrawable(ContextCompat.getDrawable(context, placeholderRes));
                }
            }
        };
        Picasso.with(context).load(imgUrl).into(img, callback);
    }

    public static void setImageColor(ImageView imageView, int color) {
        imageView.clearColorFilter();
        imageView.setColorFilter(color);
        imageView.setImageAlpha(0xFF & (color >> 24));
    }

    public static void showErrorSnackbar(@Nullable final View view, @NonNull final String text) {
        if (view == null) {
            return;
        }
        Snackbar snackbar = Snackbar.make(view, text, Snackbar.LENGTH_LONG);

        View snackView = snackbar.getView();
        snackView.setBackgroundColor(view.getContext().getResources().getColor(R.color.snackbar_error));
        snackbar.show();
    }

    public static void showErrorIndefiniteSnackbar(@Nullable final View view, @NonNull final String text) {
        if (view == null) {
            return;
        }
        Snackbar snackbar = Snackbar.make(view, text, Snackbar.LENGTH_INDEFINITE);

        View snackView = snackbar.getView();
        snackView.setBackgroundColor(view.getContext().getResources().getColor(R.color.snackbar_error));
        snackbar.show();
    }

    public static void showPositiveSnackbar(View view, String text) {
        if (view == null) {
            return;
        }
        Snackbar snackbar = Snackbar.make(view, text, Snackbar.LENGTH_LONG);

        View snackView = snackbar.getView();
        snackView.setBackgroundColor(view.getContext().getResources().getColor(R.color.snackbar_positive));
        snackbar.show();

    }

    public static void showWarningSnackbar(View view, String text) {
        if (view == null) {
            return;
        }
        Snackbar snackbar = Snackbar.make(view, text, Snackbar.LENGTH_LONG);

        View snackView = snackbar.getView();
        snackView.setBackgroundColor(view.getContext().getResources().getColor(R.color.snackbar_warning));
        snackbar.show();
    }


    public static void shareVideo(Activity activity, String videoId) {
        Intent sendIntent = new Intent();
        VideoData video = VideoHelper.getVideo(activity.getContentResolver(), videoId);
        String title = video.getTitle();
        String imageUrl = "";
        if (video.getThumbnails() != null && video.getThumbnails().size() > 0) {
            imageUrl = video.getThumbnails().get(video.getThumbnails().size() - 1).getUrl();
        }
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, SettingsProvider.getInstance().getShareSubject());
        String message = String.format(activity.getString(R.string.share_message), title, activity.getString(R.string.app_name));
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.setType("text/html");
        activity.startActivity(Intent.createChooser(sendIntent, activity.getResources().getText(R.string.menu_share)));
    }

    public static void showKeyboard(Context context, View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }
}
