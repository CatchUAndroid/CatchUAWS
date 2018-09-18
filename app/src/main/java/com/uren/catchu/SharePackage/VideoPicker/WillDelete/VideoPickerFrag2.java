package com.uren.catchu.SharePackage.VideoPicker.WillDelete;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;
import android.widget.VideoView;

import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.FileAdapter;
import com.uren.catchu.Permissions.PermissionModule;
import com.uren.catchu.R;
import com.uren.catchu.SharePackage.MainShareActivity;
import com.uren.catchu.SharePackage.VideoPicker.WillDelete.AudioRecorderActivity;
import com.uren.catchu.SharePackage.VideoPicker.WillDelete.Camera2Activity;
import com.uren.catchu.SharePackage.VideoPicker.WillDelete.VideoTestActivity;

import java.io.IOException;
import java.util.Arrays;

import butterknife.ButterKnife;

import static android.content.Context.WINDOW_SERVICE;
import static android.hardware.Camera.open;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
import static com.uren.catchu.Constants.NumericConstants.MAX_VIDEO_DURATION;
import static com.uren.catchu.Constants.StringConstants.CAMERA_TEXT;
import static com.uren.catchu.Constants.StringConstants.GALLERY_TEXT;


@SuppressLint("ValidFragment")
public class VideoPickerFrag2 extends Fragment implements MediaRecorder.OnInfoListener {

    //, TextureView.SurfaceTextureListener

    private MediaRecorder mMediaRecorder;
    //private Camera mCamera;
    //private SurfaceView mSurfaceView;
    //private SurfaceHolder mHolder;
    CameraManager cameraManager;
    CameraDevice mCameraDevice;
    private Surface mSurface;
    TextureView textureView;
    private ToggleButton mToggleButton;
    private ImageView galleryImgv;
    ImageView cancelImageView;
    PermissionModule permissionModule;
    private View mView;
    private Uri videoUri;
    private MediaController mediaController;
    private VideoView videoView;
    private RelativeLayout videoViewRelLayout;
    private RelativeLayout surfaceViewLayout;
    private boolean videoGallerySelected = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.video_picker_deneme, container, false);
        ButterKnife.bind(this, mView);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getActivity().getWindow().setFormat(PixelFormat.UNKNOWN);
        initUI();
        /*setHolderProperties();*/
        addListeners();
        /*textureView.setSurfaceTextureListener(this);*/
    }

    private void initUI() {
        //textureView = (TextureView) mView.findViewById(R.id.textureView);
        cancelImageView = mView.findViewById(R.id.cancelImageView);
        mToggleButton = (ToggleButton) mView.findViewById(R.id.toggleRecordingButton);
        galleryImgv = mView.findViewById(R.id.galleryImgv);
        videoView = mView.findViewById(R.id.videoView);
        videoViewRelLayout = mView.findViewById(R.id.videoViewRelLayout);
        surfaceViewLayout = mView.findViewById(R.id.surfaceViewLayout);
        mediaController = new MediaController(getActivity());
        permissionModule = new PermissionModule(getActivity());
    }

//    public void setHolderProperties() {
//        mHolder = mSurfaceView.getHolder();
//        mHolder.addCallback(this);
//        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//    }

    private void addListeners() {
        cancelImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelImageView.setVisibility(View.GONE);

                if (videoGallerySelected) {
                    videoView.stopPlayback();
                    surfaceViewLayout.setVisibility(View.VISIBLE);
                    videoViewRelLayout.setVisibility(View.GONE);
                    openCamera();
                    videoGallerySelected = false;
                } /*else
                    previewCamera();*/
            }
        });

        mToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (permissionModule.checkCameraPermission()) {
                    if (mToggleButton.isChecked()) {

                        /*mToggleButton.setBackgroundResource(R.drawable.btn_capture_photo);
                        cancelImageView.setVisibility(View.GONE);

                        try {
                            initRecorder(mHolder.getSurface());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        mMediaRecorder.start();*/

                        startActivity(new Intent(getActivity(), Camera2Activity.class));


                    } else
                        stopRecorderProcess();
                } else
                    requestPermissions(new String[]{Manifest.permission.CAMERA},
                            permissionModule.getCameraPermissionCode());
            }
        });

        galleryImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (permissionModule.checkWriteExternalStoragePermission()) {
                    startGalleryForVideos();
                } else
                    requestPermissions(new String[]{Manifest.permission.CAMERA},
                            permissionModule.getWriteExternalStoragePermissionCode());
            }
        });
    }

    // TODO: 11.09.2018 - GalleryPicker dan baslatilan permission burayi tetikliyor.

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == permissionModule.getCameraPermissionCode()) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            }
        } else if (requestCode == permissionModule.getWriteExternalStoragePermissionCode()) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startGalleryForVideos();
            }
        }
    }

    public void startGalleryForVideos() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Video"), permissionModule.getVideoGalerySelectedPerm());
    }

    public void openCamera() {
        if (cameraManager == null) {
            cameraManager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);

            if (permissionModule.checkCameraPermission()) {
                try {
                    String cameraId = cameraManager.getCameraIdList()[0];
                    cameraManager.openCamera(cameraId, new CameraDevice.StateCallback() {
                        @Override
                        public void onOpened(@NonNull CameraDevice camera) {
                            mCameraDevice = camera;
                            startCameraPreview();
                        }

                        @Override
                        public void onDisconnected(@NonNull CameraDevice camera) {

                        }

                        @Override
                        public void onError(@NonNull CameraDevice camera, int error) {

                        }
                    }, null);

                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            } else {
                Log.i("Info", "Don't have permission to camera!");
            }
            Log.i("Info", "Open Camera end");
        }
    }

    private void startCameraPreview() {
        if (mSurface != null && mCameraDevice != null) {
            try {
                mCameraDevice.createCaptureSession(Arrays.asList(mSurface), cameraSessionStateCallback, null);
            } catch (CameraAccessException e) {
                Log.i("Info", "startCameraPreview exception:" + e);
            }
        }
    }

    private CameraCaptureSession.StateCallback cameraSessionStateCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(CameraCaptureSession session) {
            try {
                CaptureRequest.Builder captureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                captureRequestBuilder.addTarget(mSurface);
                CaptureRequest captureRequest = captureRequestBuilder.build();
                session.setRepeatingRequest(captureRequest, cameraSessionCaptureCallback, null);
            } catch (CameraAccessException e) {
                Log.w("Info", "CameraCaptureSession.StateCallback:" + e);
            }
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession session) {
        }
    };

    private CameraCaptureSession.CaptureCallback cameraSessionCaptureCallback = new CameraCaptureSession.CaptureCallback() {
    };


    private void initRecorder(Surface surface) throws IOException {
        //mCamera.unlock();
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setPreviewDisplay(surface);
        //mMediaRecorder.setCamera(mCamera);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMediaRecorder.setVideoEncodingBitRate(512 * 1000);
        mMediaRecorder.setVideoFrameRate(30);
        mMediaRecorder.setVideoSize(640, 480);
        mMediaRecorder.setOutputFile(FileAdapter.getOutputMediaFile(MEDIA_TYPE_VIDEO).getAbsolutePath());
        mMediaRecorder.setMaxDuration(MAX_VIDEO_DURATION * 1000);
        mMediaRecorder.setOnInfoListener(this);


        Log.i("Info", "FileAdapter.getOutputMediaFile(MEDIA_TYPE_VIDEO).getAbsolutePath():" +
                FileAdapter.getOutputMediaFile(MEDIA_TYPE_VIDEO).getAbsolutePath());

        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            CommonUtils.showToastLong(getActivity(),
                    getResources().getString(R.string.error) + e.getMessage());
            e.printStackTrace();
        }
    }

    /*private void checkCameraRotation() {

        Display display = ((WindowManager) getActivity().getSystemService(WINDOW_SERVICE)).getDefaultDisplay();

        if (display.getRotation() == Surface.ROTATION_0) {
            mCamera.setDisplayOrientation(90);
        }

        if (display.getRotation() == Surface.ROTATION_90) {
            mCamera.setDisplayOrientation(0);
        }

        if (display.getRotation() == Surface.ROTATION_180) {
            mCamera.setDisplayOrientation(270);
        }

        if (display.getRotation() == Surface.ROTATION_270) {
            mCamera.setDisplayOrientation(180);
        }
    }*/

    /*public void previewCamera() {
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    public void stopRecorderProcess() {
        mToggleButton.setBackgroundResource(R.drawable.btn_capture_video);
        cancelImageView.setVisibility(View.VISIBLE);

        if (mMediaRecorder != null) {
            mMediaRecorder.stop();
            mMediaRecorder.reset();
        }

        /*if (mCamera != null)
            mCamera.stopPreview();*/
    }

    /*@Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (permissionModule.checkCameraPermission()) {
            if (!videoGallerySelected)
                openCamera();
        } else
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    permissionModule.getCameraPermissionCode());
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        shutdown();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }*/

    public void shutdown() {
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
        }

        /*if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }*/
    }

    @Override
    public void onInfo(MediaRecorder mr, int what, int extra) {
        if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
            stopRecorderProcess();
            mToggleButton.setChecked(false);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == permissionModule.getVideoGalerySelectedPerm()) {
                manageVideoFromGallery(data);
            } else
                CommonUtils.showToast(getActivity(), getResources().getString(R.string.technicalError) + requestCode);
        }
    }

    public void manageVideoFromGallery(Intent data) {

        videoGallerySelected = true;
        videoUri = data.getData();
        stopRecorderProcess();
        shutdown();
        surfaceViewLayout.setVisibility(View.GONE);
        videoViewRelLayout.setVisibility(View.VISIBLE);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
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
                mp.start();
            }
        });
    }





   /* @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mSurface = new Surface(surface);
        startCameraPreview();
        if (permissionModule.checkCameraPermission()) {
            if (!videoGallerySelected)
                openCamera();
        } else
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    permissionModule.getCameraPermissionCode());
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        shutdown();
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mCameraDevice != null) {
            mCameraDevice.close();
        }
    }
}
