package com.uren.catchu.MainPackage.MainFragments.Share.SubFragments;

import android.annotation.SuppressLint;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.VideoView;

import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.Share.ShareItems;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("ValidFragment")
public class VideoViewFragment extends BaseFragment {

    View mView;

    @BindView(R.id.useButton)
    Button useButton;
    @BindView(R.id.playVideoImgv)
    ImageView playVideoImgv;
    @BindView(R.id.videoView)
    VideoView videoView;

    Uri videoUri;
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
            mView = inflater.inflate(R.layout.fragment_video_view, container, false);
            ButterKnife.bind(this, mView);
            return mView;
        }
        return mView;
    }

    private void setShapes() {
        GradientDrawable playVideoImgvShape = ShapeUtil.getShape(getActivity().getResources().getColor(R.color.transparentBlack, null),
                getActivity().getResources().getColor(R.color.White, null), GradientDrawable.OVAL, 50, 3);
        playVideoImgv.setBackground(playVideoImgvShape);
        useButton.setBackground(ShapeUtil.getShape(getActivity().getResources().getColor(R.color.Black, null),
                getActivity().getResources().getColor(R.color.White, null), GradientDrawable.RECTANGLE, 20, 3));
    }

    @SuppressLint("ClickableViewAccessibility")
    private void addListeners() {
        useButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView.stopPlayback();
                mediaPlayer = null;
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
        setShapes();
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

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                mediaPlayer = mp;
                mediaPlayer.start();
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