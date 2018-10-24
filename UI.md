# UI customization

## Playlist view

You can choose the view for browsing playlists:

- Normal view, that is set up as default

<a href="https://drive.google.com/uc?export=view&id=1ZMJizsTo8Hw-YZaIEKV7y0q-58b-GWqm"><img src="https://drive.google.com/uc?export=view&id=1ZMJizsTo8Hw-YZaIEKV7y0q-58b-GWqm" style="width: auto; height: 108px" title="Click for the larger version." /></a>

- Gallery view

<a href="https://drive.google.com/uc?export=view&id=1adBoFuFb8g_vOPwfNzzc8ZAGruNIQ8G4"><img src="https://drive.google.com/uc?export=view&id=1adBoFuFb8g_vOPwfNzzc8ZAGruNIQ8G4" style="width: auto; height: 108px" title="Click for the larger version." /></a>

Set `PLAYLIST_GALLERY_VIEW` in [ZypeSettings.java](https://github.com/zype/zype-android/blob/master/app/src/template/java/com/zype/android/ZypeSettings.java) to `true` to display playlists in this view.

In the gallery view you can manage following UI options:

- `PLAYLIST_GALLERY_ITEM_TITLES` - When set to `true` the video title will show on the video thumbnails in the gallery view. If the thumbnail images already has the title it is recommended to turn this option off.

- `PLAYLIST_GALLERY_HERO_IMAGES` - Hero images carousel will be displayed above the playlist rows, when this option is set ti `true`. Each hero image is clickable and linked to the specified playlist. The `Top Playlist` zobject in the platform is used to link playlists to hero images.

- **Poster style thumbnails** - When Gallery View is set, "poster" style thumbnails are available if they have been configured on the Zype Admin. For more information see **[here](https://support.zype.com/hc/en-us/articles/115011367848)**

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
