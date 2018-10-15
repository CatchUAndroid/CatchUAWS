package com.uren.catchu.SharePackage.VideoPicker.WillDelete;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.FileAdapter;
import com.uren.catchu.Permissions.PermissionModule;
import com.uren.catchu.R;
import com.uren.catchu.SharePackage.VideoPicker.WillDelete.AudioRecorderActivity;
import com.uren.catchu.SharePackage.VideoPicker.WillDelete.VideoTestActivity;

import java.io.IOException;

import butterknife.ButterKnife;

import static android.content.Context.WINDOW_SERVICE;
import static android.hardware.Camera.open;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;


@SuppressLint("ValidFragment")
public class VideoTemp extends Fragment implements SurfaceHolder.Callback {

    Camera camera;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    boolean previewing = false;
    ImageView mBtnTakeVideo;
    CheckBox checkBox;

    private View mView;
    PermissionModule permissionModule;

    private MediaRecorder mMediaRecorder;
    private boolean mInitSuccesful;
    private final String VIDEO_PATH_NAME = "/mnt/sdcard/VGA_30fps_512vbrate.mp4";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.video_picker, container, false);
        ButterKnife.bind(this, mView);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        getActivity().getWindow().setFormat(PixelFormat.UNKNOWN);
        //surfaceView = (SurfaceView) view.findViewById(R.id.surfaceview);
        //mBtnTakeVideo = view.findViewById(R.id.mBtnTakeVideo);
        //checkBox = view.findViewById(R.id.checkbox);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        permissionModule = new PermissionModule(getActivity());
        addListeners();
    }

    private void addListeners() {

        mBtnTakeVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!checkBox.isChecked())
                    checkBox.setChecked(true);
                else
                    checkBox.setChecked(false);

                startActivity(new Intent(getActivity(), VideoTestActivity.class));


                /*if (checkBox.isChecked()) {
                    try {
                        mMediaRecorder.start();
                        Thread.sleep(10 * 1000); // This will recode for 10 seconds, if you don't want then just remove it.
                    } catch (Exception e) {
                        Log.i("Info", "mMediaRecorder.start() e:" + e.getMessage());
                        e.printStackTrace();
                    }
                }
                else {
                    try {
                        mMediaRecorder.stop();
                        mMediaRecorder.reset();
                        initRecorder(surfaceHolder.getSurface());
                    } catch (IOException e) {
                        Log.i("Info", "mMediaRecorder.stopreset() e:" + e.getMessage());
                        e.printStackTrace();
                    }
                }*/
            }
        });
    }


    public void openCamera() {
        if (permissionModule.checkCameraPermission()) {
            /*if (!previewing) {*/

            // TODO: 4.09.2018 - Yukaridaki if blogunu ekledigimizde tablar arasi gecislerden sonra surface holder siyah kaliyor
            // TODO: 4.09.2018 - Tab degisimden sonra tekrar camera open yapiyoruz. buna bakalim...

            if (surfaceHolder.getSurface().isValid()) {

                camera = Camera.open();
                if (camera != null) {
                    try {
                        checkCameraRotation();
                        camera.setPreviewDisplay(surfaceHolder);
                        camera.startPreview();
                        previewing = true;
                    } catch (IOException e) {
                        CommonUtils.showToastLong(getActivity(), getActivity().getResources().getString(R.string.error)
                                + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }


            /*}*/
        } else
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CAMERA},
                    permissionModule.PERMISSION_CAMERA);
    }

    private void checkCameraRotation() {

        Display display = ((WindowManager) getActivity().getSystemService(WINDOW_SERVICE)).getDefaultDisplay();

        if (display.getRotation() == Surface.ROTATION_0) {
            camera.setDisplayOrientation(90);
        }

        if (display.getRotation() == Surface.ROTATION_90) {
            camera.setDisplayOrientation(0);
        }

        if (display.getRotation() == Surface.ROTATION_180) {
            camera.setDisplayOrientation(270);
        }

        if (display.getRotation() == Surface.ROTATION_270) {
            camera.setDisplayOrientation(180);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == permissionModule.PERMISSION_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        try {
            if(!mInitSuccesful)
                initRecorder(surfaceHolder.getSurface());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

       /* if (!previewing) {
            openCamera();
        }*/

        /*
        Camera.Parameters parameters = camera.getParameters();
        Display display = ((WindowManager)getActivity().getSystemService(WINDOW_SERVICE)).getDefaultDisplay();

        if(display.getRotation() == Surface.ROTATION_0) {
            parameters.setPreviewSize(height, width);
            camera.setDisplayOrientation(90);
        }

        if(display.getRotation() == Surface.ROTATION_90) {
            parameters.setPreviewSize(width, height);
        }

        if(display.getRotation() == Surface.ROTATION_180) {
            parameters.setPreviewSize(height, width);
        }

        if(display.getRotation() == Surface.ROTATION_270) {
            parameters.setPreviewSize(width, height);
            camera.setDisplayOrientation(180);
        }

        camera.setParameters(parameters);
        previewCamera();*/


        /*Camera.Parameters parameters = camera.getParameters();
        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();

        Camera.Size previewSize = previewSizes.get(11);

        //parameters.setPreviewSize(previewSize.width, previewSize.height);
        parameters.setPreviewSize(176, 144);
        camera.setParameters(parameters);
        camera.startPreview();*/
    }

    public void previewCamera() {
        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
            previewing = true;
        } catch (Exception e) {
            CommonUtils.showToastLong(getActivity(), getActivity().getResources().getString(R.string.error)
                    + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        shutdown();
    }

    private void shutdown() {
        /*mMediaRecorder.reset();
        mMediaRecorder.release();
        camera.release();
        mMediaRecorder = null;
        camera = null;*/
    }

    private void initRecorder(Surface surface) throws IOException {
        // It is very important to unlock the camera before doing setCamera
        // or it will results in a black preview
        if(camera == null) {
            camera = Camera.open();
            camera.unlock();
        }

        if(mMediaRecorder == null)
            mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setPreviewDisplay(surface);
        mMediaRecorder.setCamera(camera);

        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMediaRecorder.setVideoEncodingBitRate(512 * 1000);
        mMediaRecorder.setVideoFrameRate(30);
        mMediaRecorder.setVideoSize(640, 480);
        mMediaRecorder.setOutputFile(FileAdapter.getOutputMediaFile(MEDIA_TYPE_VIDEO).getAbsolutePath());

        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            // This is thrown if the previous calls are not called with the
            // proper order
            e.printStackTrace();
        }

        mInitSuccesful = true;
    }
}
