package com.uren.catchu.SharePackage.GalleryPicker.Interfaces;

import android.net.Uri;

public interface PhotoSelectCallback {
    void onSelect(Uri uri, boolean portraitMode);
}
