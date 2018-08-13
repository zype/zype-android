# UI customization

## Video placeholders

The placeholder is used for videos that doesn't have thumbnails.

To set your custom placeholder image:

1. Upload your placeholder image file (.png or .jpg) to **[template/res/drawable-xxhdpi](https://github.com/zype/zype-android/blob/master/app/src/template/res/drawable-xxhdpi)** folder.
2. Update **[template/res/drawable/placeholder_video.xml](https://github.com/zype/zype-android/blob/master/app/src/template/res/drawable/placeholder_video.xml)** file:

In the line `android:src="@drawable/outline_play_circle_filled_white_white_48"` change default placeholder resource name `outline_play_circle_filled_white_white_48` to your custom one uploaded in the step 1.

## Lock icons

Lock icon is displayed on video thumbnails of paywall videos.

Update values of following color resources to change lock icon colors:

####  **[colors.xml](https://github.com/zype/zype-android/blob/master/app/src/template/res/values/colors.xml)**, **[values-night/colors.xml](https://github.com/zype/zype-android/blob/master/app/src/template/res/values-night/colors.xml)**

`icon_locked` - Color of the locked icon

`icon_unlocked` - Color of the unlocked icon. If set to transparent color, the unlocked icon will not display.
