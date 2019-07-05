package com.uren.catchu.MainPackage.MainFragments.Share.SubFragments;


import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.deep.videotrimmer.DeepVideoTrimmer;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.Interfaces.InfoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.UriAdapter;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Share.Interfaces.VideoTrimmedCallback;
import com.uren.catchu.R;

import butterknife.ButterKnife;

import static com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread;
import static com.uren.catchu.Constants.NumericConstants.MAX_VIDEO_DURATION;
import static com.uren.catchu.Constants.NumericConstants.MAX_VIDEO_SIZE_IN_MB;

@SuppressLint("ValidFragment")
public class VideoTrimmerFragment extends BaseFragment implements com.deep.videotrimmer.interfaces.OnTrimVideoListener {

    View mView;
    Uri videoUri;
    DeepVideoTrimmer mVideoTrimmer;
    VideoTrimmedCallback videoTrimmedCallback;

    public VideoTrimmerFragment(Uri videoUri, VideoTrimmedCallback videoTrimmedCallback) {
        this.videoUri = videoUri;
        this.videoTrimmedCallback = videoTrimmedCallback;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_video_trimmer, container, false);
        ButterKnife.bind(this, mView);
        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initValues();
        setVideoTrimmer();
    }

    public void initValues() {
        mVideoTrimmer = mView.findViewById(R.id.mVideoTrimmer);
    }

    private void setVideoTrimmer() {
        if (mVideoTrimmer != null && videoUri != null) {
            String path = UriAdapter.getPathFromGalleryUri(getContext(), videoUri);

            if (path != null && !path.isEmpty()) {
                mVideoTrimmer.setMaxDuration(MAX_VIDEO_DURATION);
                mVideoTrimmer.setOnTrimVideoListener(this);
                mVideoTrimmer.setVideoURI(Uri.parse(path));
                mVideoTrimmer.setMaxFileSize(MAX_VIDEO_SIZE_IN_MB);
                mVideoTrimmer.setDestinationPath("/storage/emulated/0/CatchU/Movies/");
            }
        }
    }

    @Override
    public void getResult(final Uri uri) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                videoTrimmedCallback.onTrimmed(uri, uri.getPath());

                DialogBoxUtil.showInfoDialogWithLimitedTime(getContext(), null,
                        getContext().getResources().getString(R.string.PLEASE_WAIT),
                        2000, new InfoDialogBoxCallback() {
                            @Override
                            public void okClick() {
                                getActivity().onBackPressed();
                            }
                        });
            }
        });
    }

    @Override
    public void cancelAction() {
        mVideoTrimmer.destroy();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });
        getActivity().onBackPressed();
    }
}