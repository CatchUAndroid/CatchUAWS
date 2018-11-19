package com.uren.catchu.SharePackage.VideoPicker.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.VideoView;

import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.GeneralUtils.VideoUtil.VideoSelectUtil;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.R;
import com.uren.catchu.SharePackage.Models.VideoShareItemBox;
import com.uren.catchu.Singleton.Share.ShareItems;

import butterknife.ButterKnife;
import catchu.model.Media;

import static com.uren.catchu.Constants.StringConstants.GALLERY_TEXT;

@SuppressLint("ValidFragment")
public class VideoViewFragment extends BaseFragment {

    ImageView cancelImageView;
    ImageView playVideoImgv;
    private View mView;
    private Uri videoUri;
    private VideoView videoView;
    MediaPlayer mediaPlayer;
    boolean mediaPlayerPlayFinished = false;
    int mediaPlayerTotalLen;
    boolean mediaPlayerIsPlaying = false;

    public VideoViewFragment(Uri videoUri) {
        this.videoUri = videoUri;
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
        setShapes();
    }

    private void setShapes() {
        GradientDrawable playVideoImgvShape = ShapeUtil.getShape(getActivity().getResources().getColor(R.color.transparentBlack, null),
                getActivity().getResources().getColor(R.color.White, null), GradientDrawable.OVAL, 50, 3);
        playVideoImgv.setBackground(playVideoImgvShape);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void addListeners() {
        cancelImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelImageView.setVisibility(View.GONE);
                videoView.stopPlayback();
                mediaPlayer = null;
                ShareItems.getInstance().clearVideoShareItemBox();
                getActivity().onBackPressed();
            }
        });

        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(mediaPlayer != null) {
                    if (mediaPlayerPlayFinished) {
                        playVideoImgv.setVisibility(View.GONE);
                        mediaPlayer.seekTo(mediaPlayerTotalLen);
                        mediaPlayer.start();
                        mediaPlayerIsPlaying = true;
                        mediaPlayerPlayFinished = false;
                    } else {
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
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        android.widget.RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) videoView.getLayoutParams();
        params.width = metrics.widthPixels;
        params.height = metrics.heightPixels;
        params.leftMargin = 0;
        videoView.setLayoutParams(params);
        videoView.setVideoURI(videoUri);
        videoView.requestFocus();

        Log.i("Info", "VideoViewFragment: videoUri :" + videoUri.toString());

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                mediaPlayer = mp;
                mediaPlayer.start();
                cancelImageView.setVisibility(View.VISIBLE);
                playVideoImgv.setVisibility(View.GONE);
                mediaPlayerIsPlaying = true;
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