package com.uren.catchu.GeneralUtils.VideoUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

import com.uren.catchu.GeneralUtils.UriAdapter;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

public class VideoSelectUtil {
    Context context;
    Uri videoUri;
    Bitmap videoBitmap;
    String videoRealPath;
    boolean isDeletable;

    public VideoSelectUtil(Context context, Uri videoUri, String videoRealPath, boolean isDeletable) {
        this.context = context;
        this.videoUri = videoUri;
        this.videoRealPath = videoRealPath;
        this.isDeletable = isDeletable;
        videoUriProcess();
    }

    public void videoUriProcess() {
        if (videoRealPath == null || videoRealPath.isEmpty())
            videoRealPath = UriAdapter.getPathFromGalleryUri(context, videoUri);

        if (videoRealPath == null || videoRealPath.isEmpty())
            videoRealPath = UriAdapter.getRealPathFromURI(videoUri, context);

        if (videoRealPath == null || videoRealPath.isEmpty())
            videoRealPath = UriAdapter.getFilePathFromURI(context, videoUri, MEDIA_TYPE_VIDEO);

        if (videoRealPath == null || videoRealPath.isEmpty())
            videoRealPath = videoUri.getPath();

        if (videoRealPath != null && !videoRealPath.isEmpty())
            setBitmapFromUriForVideo();
    }

    public void setBitmapFromUriForVideo() {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(videoRealPath);
        videoBitmap = retriever.getFrameAtTime(100);
    }

    public Uri getVideoUri() {
        return videoUri;
    }

    public void setVideoUri(Uri videoUri) {
        this.videoUri = videoUri;
    }

    public Bitmap getVideoBitmap() {
        return videoBitmap;
    }

    public void setVideoBitmap(Bitmap videoBitmap) {
        this.videoBitmap = videoBitmap;
    }

    public String getVideoRealPath() {
        return videoRealPath;
    }

    public void setVideoRealPath(String videoRealPath) {
        this.videoRealPath = videoRealPath;
    }

    public boolean isDeletable() {
        return isDeletable;
    }

    public void setDeletable(boolean deletable) {
        isDeletable = deletable;
    }
}
