package com.uren.catchu.SharePackage.VideoPicker.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;
import android.widget.VideoView;

import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.FileAdapter;
import com.uren.catchu.Permissions.PermissionModule;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.ShareItems;

import java.io.IOException;

import butterknife.ButterKnife;

import static android.content.Context.WINDOW_SERVICE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
import static com.uren.catchu.Constants.NumericConstants.MAX_VIDEO_DURATION;

@SuppressLint("ValidFragment")
public class VideoViewFragment extends Fragment {

    ImageView cancelImageView;
    ImageView playVideoImgv;
    private View mView;
    private Uri videoUri;
    private VideoView videoView;
    Intent data;
    MediaPlayer mediaPlayer;
    boolean mediaPlayerPlayFinished = false;
    int mediaPlayerTotalLen;
    boolean mediaPlayerIsPlaying = false;

    @SuppressLint("ValidFragment")
    public VideoViewFragment(Intent data) {
        this.data = data;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.video_view_layout, container, false);
            ButterKnife.bind(this, mView);
            return mView;
        }
        return mView;
    }

    private void initUI() {
        cancelImageView = mView.findViewById(R.id.cancelImageView);
        videoView = mView.findViewById(R.id.videoView);
        playVideoImgv = mView.findViewById(R.id.playVideoImgv);
    }

    private void addListeners() {
        cancelImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelImageView.setVisibility(View.GONE);
                videoView.stopPlayback();
                ShareItems.getInstance().setVideoUri(null);
                getActivity().onBackPressed();
            }
        });

        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(mediaPlayerPlayFinished) {
                    playVideoImgv.setVisibility(View.GONE);
                    mediaPlayer.seekTo(mediaPlayerTotalLen);
                    mediaPlayer.start();
                    mediaPlayerIsPlaying = true;
                    mediaPlayerPlayFinished = false;
                }else {
                    if (mediaPlayerIsPlaying) {
                        playVideoImgv.setVisibility(View.VISIBLE);
                        mediaPlayer.pause();
                        mediaPlayerIsPlaying = false;
                    } else {
                        playVideoImgv.setVisibility(View.GONE);
                        mediaPlayer.start();
                        mediaPlayerIsPlaying = true;
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        initUI();
        addListeners();
        manageVideoFromGallery();
    }

    public void manageVideoFromGallery() {
        videoUri = data.getData();
        ShareItems.getInstance().setVideoUri(videoUri);
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        android.widget.RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) videoView.getLayoutParams();
        params.width = metrics.widthPixels;
        params.height = metrics.heightPixels;
        params.leftMargin = 0;
        videoView.setLayoutParams(params);
        videoView.setVideoURI(videoUri);
        videoView.requestFocus();

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                mediaPlayer = mp;
                mediaPlayer.start();
                cancelImageView.setVisibility(View.VISIBLE);
                playVideoImgv.setVisibility(View.GONE);
                mediaPlayerTotalLen = mediaPlayer.getCurrentPosition();

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        playVideoImgv.setVisibility(View.VISIBLE);
                        mediaPlayerPlayFinished = true;
                        mediaPlayerIsPlaying = false;
                    }
                });
            }
        });
    }
}