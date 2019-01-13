package com.uren.catchu.MainPackage.MainFragments.Share.SubFragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.uren.catchu.GeneralUtils.FileAdapter;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.Interfaces.PermissionCallback;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.Permissions.PermissionModule;
import com.uren.catchu.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import life.knowledge4.videotrimmer.K4LVideoTrimmer;
import life.knowledge4.videotrimmer.interfaces.OnTrimVideoListener;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
import static com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread;
import static com.uren.catchu.Constants.NumericConstants.MAX_VIDEO_DURATION;

@SuppressLint("ValidFragment")
public class TrimVideoFragment extends BaseFragment implements OnTrimVideoListener{

    View mView;

    @BindView(R.id.videoTrimmer)
    K4LVideoTrimmer videoTrimmer;

    Uri videoUri;

    public TrimVideoFragment(Uri videoUri) {
        this.videoUri = videoUri;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_trim_video, container, false);
            ButterKnife.bind(this, mView);
            return mView;
        }
        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initValues();
        addListeners();
        setVideoTrimmer();
    }

    private void addListeners() {

    }


    public void initValues() {

    }

    private void setVideoTrimmer() {
        if (videoTrimmer != null) {
            videoTrimmer.setVideoURI(videoUri);
            //videoTrimmer.setDestinationPath("/storage/emulated/0/CatchU/Movies/");
            //videoTrimmer.setOnTrimVideoListener(this);
            videoTrimmer.setMaxDuration(MAX_VIDEO_DURATION);
        }
    }


    @Override
    public void getResult(final Uri uri) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(),  uri.getPath(), Toast.LENGTH_SHORT).show();
            }
        });
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setDataAndType(uri, "video/mp4");
        startActivity(intent);

        /*runOnUiThread(new Runnable() {
            public void run() {
                System.out.println("getResult uri:" + uri);

                mFragmentNavigation.pushFragment(new VideoViewFragment(uri, new PermissionCallback() {
                    @Override
                    public void OnPermGranted() {

                    }

                    @Override
                    public void OnPermNotAllowed() {

                    }
                }));
            }
        });*/

    }

    @Override
    public void cancelAction() {

    }
}