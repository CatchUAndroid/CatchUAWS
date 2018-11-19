package com.uren.catchu.GeneralUtils.VideoUtil;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.Log;

import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.InfoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.UriAdapter;
import com.uren.catchu.R;

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
                DialogBoxUtil.showErrorDialog(context, context.getResources().getString(R.string.videoSelectTypeUnknown), new InfoDialogBoxCallback() {
                    @Override
                    public void okClick() {
                    }
                });
                break;
        }
    }

    public void onSelectFromCameraResult() {
        //setBitmapFromUriForVideo();
        try {
            videoRealPath = UriAdapter.getRealPathFromURI(videoUri, context);
            setBitmapFromUriForVideo();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void onSelectFromGalleryResult() {
        videoRealPath = UriAdapter.getPathFromGalleryUri(context, videoUri);
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
}
