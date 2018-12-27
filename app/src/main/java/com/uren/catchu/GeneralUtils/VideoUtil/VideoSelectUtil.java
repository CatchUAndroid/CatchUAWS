package com.uren.catchu.GeneralUtils.VideoUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

import com.uren.catchu.GeneralUtils.FirebaseHelperModel.ErrorSaveHelper;
import com.uren.catchu.GeneralUtils.UriAdapter;

import static com.uren.catchu.Constants.StringConstants.CAMERA_TEXT;
import static com.uren.catchu.Constants.StringConstants.GALLERY_TEXT;

public class VideoSelectUtil {
    Context context;
    Uri videoUri;
    Bitmap videoBitmap;
    String videoRealPath;
    String selectType;

    public VideoSelectUtil(Context context, Uri videoUri, String videoRealPath, String selectType) {
        this.context = context;
        this.videoUri = videoUri;
        this.selectType = selectType;
        this.videoRealPath = videoRealPath;
        routeVideoSelection();
    }

    public void routeVideoSelection() {
        switch (selectType) {
            case CAMERA_TEXT:
                onSelectFromCameraResult();
                break;
            case GALLERY_TEXT:
                onSelectFromGalleryResult();
                break;
            default:
                break;
        }
    }

    public void onSelectFromCameraResult() {
        try {
            videoRealPath = UriAdapter.getRealPathFromURI(videoUri, context);
            setBitmapFromUriForVideo();
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    public void onSelectFromGalleryResult() {
        try {
            videoRealPath = UriAdapter.getPathFromGalleryUri(context, videoUri);
            setBitmapFromUriForVideo();
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    public void setBitmapFromUriForVideo() {
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(videoRealPath);
            videoBitmap = retriever.getFrameAtTime(100);
        } catch (IllegalArgumentException e) {
            ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    public String getSelectType() {
        return selectType;
    }

    public void setSelectType(String selectType) {
        this.selectType = selectType;
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
}
