package com.uren.catchu.SharePackage.VideoPicker.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;
import android.widget.VideoView;

import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.FileAdapter;
import com.uren.catchu.Permissions.PermissionModule;
import com.uren.catchu.R;
import com.uren.catchu.SharePackage.Utils.CameraUtil;
import com.uren.catchu.Singleton.ShareItems;

import java.io.IOException;

import butterknife.ButterKnife;

import static android.content.Context.WINDOW_SERVICE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
import static com.uren.catchu.Constants.NumericConstants.MAX_VIDEO_DURATION;

@SuppressLint("ValidFragment")
public class VideoPickerFrag extends Fragment implements MediaRecorder.OnInfoListener, TextureView.SurfaceTextureListener {

    private MediaRecorder mMediaRecorder;
    private Camera mCamera;
    private TextureView textureView;
    SurfaceTexture surfaceTexture;
    private ToggleButton mToggleButton;
    private ImageView galleryImgv;
    ImageView cancelImageView;
    PermissionModule permissionModule;
    private View mView;
    private Uri videoUri;
    private RelativeLayout texttureViewLayout;
    private RelativeLayout videoViewRelLayout;
    private boolean videoGallerySelected = false;
    private int mCurrentFlash;
    LinearLayout camParamsLayout;
    ImageView flashModeImgv;
    ImageView switchCamImgv;
    String currentFlashMode;
    int currentSwitchMode;
    boolean isCameraPreviewing = false;
    VideoView videoView;
    String videoFilePath;
    MediaPlayer mediaPlayer;
    ImageView playVideoImgv;
    int mediaLen;
    int cameraOrientation;

    private int RESULT_CODE_VIDEO_GALLERY_SELECT = 1001;

    private static final String[] FLASH_OPTIONS = {
            Camera.Parameters.FLASH_MODE_AUTO,
            Camera.Parameters.FLASH_MODE_OFF,
            Camera.Parameters.FLASH_MODE_TORCH,
    };

    private static final int[] FLASH_ICONS = {
            R.drawable.ic_flash_auto,
            R.drawable.ic_flash_off,
            R.drawable.ic_flash_on,
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.video_picker_deneme, container, false);
            ButterKnife.bind(this, mView);
            return mView;
        }
        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        initUI();
        addListeners();
        checkCameraFlash();
    }

    private void initUI() {
        textureView = (TextureView) mView.findViewById(R.id.texture_view);
        cancelImageView = mView.findViewById(R.id.cancelImageView);
        mToggleButton = (ToggleButton) mView.findViewById(R.id.toggleRecordingButton);
        galleryImgv = mView.findViewById(R.id.galleryImgv);
        texttureViewLayout = mView.findViewById(R.id.texttureViewLayout);
        permissionModule = new PermissionModule(getActivity());
        camParamsLayout = mView.findViewById(R.id.camParamsLayout);
        flashModeImgv = mView.findViewById(R.id.flashModeImgv);
        switchCamImgv = mView.findViewById(R.id.switchCamImgv);
        videoView = mView.findViewById(R.id.videoView);
        videoViewRelLayout = mView.findViewById(R.id.videoViewRelLayout);
        playVideoImgv = mView.findViewById(R.id.playVideoImgv);
        textureView.setSurfaceTextureListener(this);
    }

    private void addListeners() {
        cancelImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelImageView.setVisibility(View.GONE);
                camParamsLayout.setVisibility(View.VISIBLE);

                if(videoViewRelLayout.getVisibility() == View.VISIBLE){
                    videoViewRelLayout.setVisibility(View.GONE);
                    texttureViewLayout.setVisibility(View.VISIBLE);
                }

                previewCamera();
            }
        });

        mToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (permissionModule.checkCameraPermission()) {
                    if (mToggleButton.isChecked()) {
                        if(permissionModule.checkRecordAudioPermission()) {
                            mToggleButton.setBackgroundResource(R.drawable.btn_capture_photo);
                            cancelImageView.setVisibility(View.GONE);
                            camParamsLayout.setVisibility(View.VISIBLE);

                            try {
                                Surface surface = new Surface(surfaceTexture);
                                initRecorder(surface);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            mMediaRecorder.start();
                        }else
                            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},
                                    permissionModule.getRecordAudioPermissionCode());
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

        flashModeImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCamera != null) {
                    mCurrentFlash = (mCurrentFlash + 1) % FLASH_OPTIONS.length;
                    flashModeImgv.setImageResource(FLASH_ICONS[mCurrentFlash]);
                    Camera.Parameters p = mCamera.getParameters();
                    p.setFlashMode(FLASH_OPTIONS[mCurrentFlash]);
                    mCamera.setParameters(p);
                    currentFlashMode = FLASH_OPTIONS[mCurrentFlash];
                }
            }
        });

        switchCamImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCameraPreviewing)
                    mCamera.stopPreview();
                mCamera.release();

                if (currentSwitchMode == Camera.CameraInfo.CAMERA_FACING_BACK)
                    currentSwitchMode = Camera.CameraInfo.CAMERA_FACING_FRONT;
                else
                    currentSwitchMode = Camera.CameraInfo.CAMERA_FACING_BACK;

                changeCameraSwitchMode(currentSwitchMode);
            }
        });

        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (playVideoImgv.getVisibility() == View.GONE) {
                    playVideoImgv.setVisibility(View.VISIBLE);
                    mediaPlayer.pause();
                    mediaLen = mediaPlayer.getCurrentPosition();
                }
                else {
                    playVideoImgv.setVisibility(View.GONE);
                    mediaPlayer.seekTo(mediaLen);
                    mediaPlayer.start();
                }
                return false;
            }
        });
    }

    public void checkCameraFlash(){
        if(!CameraUtil.hasDeviceFlush(getActivity()))
            flashModeImgv.setVisibility(View.GONE);
    }

    /*private void getOptimalPreviewSize() {
        Camera.Parameters parameters = mCamera.getParameters();
        List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
        Camera.Size optimalSize = CameraUtil.getOptimalPreviewSize(sizes, getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels);
        parameters.setPreviewSize(optimalSize.width, optimalSize.height);
        mCamera.setParameters(parameters);
    }*/

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
        }else if (requestCode == permissionModule.getRecordAudioPermissionCode()) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Surface surface = new Surface(surfaceTexture);
                try {
                    initRecorder(surface);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void startGalleryForVideos() {
        setFlashModeOff();
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Video"), RESULT_CODE_VIDEO_GALLERY_SELECT);
    }

    public void changeCameraSwitchMode(int switchMode) {
        mCamera = Camera.open(switchMode);
        checkCameraRotation();
        //setCameraOrientation();
        previewCamera();
        currentFlashMode = FLASH_OPTIONS[0];
        currentSwitchMode = switchMode;
        //getOptimalPreviewSize();
    }

    public void openCamera() {
        if (mCamera == null)
            changeCameraSwitchMode(Camera.CameraInfo.CAMERA_FACING_BACK);
    }

    private void initRecorder(Surface surface) throws IOException {
        mCamera.unlock();
        videoFilePath = FileAdapter.getOutputMediaFile(MEDIA_TYPE_VIDEO).getAbsolutePath();
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setPreviewDisplay(surface);
        mMediaRecorder.setCamera(mCamera);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMediaRecorder.setVideoEncodingBitRate(512 * 1000);
        mMediaRecorder.setVideoFrameRate(30);
        mMediaRecorder.setVideoSize(640, 480);
        mMediaRecorder.setOutputFile(videoFilePath);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mMediaRecorder.setMaxDuration(MAX_VIDEO_DURATION * 1000);
        mMediaRecorder.setOrientationHint(cameraOrientation);
        mMediaRecorder.setOnInfoListener(this);
        //mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_480P));

        Log.i("Info", "FileAdapter.getOutputMediaFile(MEDIA_TYPE_VIDEO).getAbsolutePath():" + videoFilePath);

        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            CommonUtils.showToastLong(getActivity(),
                    getResources().getString(R.string.error) + e.getMessage());
            e.printStackTrace();
        }
    }

    private void checkCameraRotation() {
        Display display = ((WindowManager) getActivity().getSystemService(WINDOW_SERVICE)).getDefaultDisplay();

        if (display.getRotation() == Surface.ROTATION_0) {
            cameraOrientation = 90;
            mCamera.setDisplayOrientation(cameraOrientation);
        }

        if (display.getRotation() == Surface.ROTATION_90) {
            cameraOrientation = 0;
            mCamera.setDisplayOrientation(cameraOrientation);
        }

        if (display.getRotation() == Surface.ROTATION_180) {
            cameraOrientation = 270;
            mCamera.setDisplayOrientation(cameraOrientation);
        }

        if (display.getRotation() == Surface.ROTATION_270) {
            cameraOrientation = 180;
            mCamera.setDisplayOrientation(cameraOrientation);
        }
    }

    /*public void setCameraOrientation() {
        Camera.Parameters p = mCamera.getParameters();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            p.set("orientation", "portrait");
            p.set("rotation", 90);
        }
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            p.set("orientation", "landscape");
            p.set("rotation", 90);
        }
        mCamera.setParameters(p);
    }*/

    public void previewCamera() {
        try {
            mCamera.setPreviewTexture(surfaceTexture);
            mCamera.startPreview();
            isCameraPreviewing = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopRecorderProcess() {
        try {
            mToggleButton.setBackgroundResource(R.drawable.btn_capture_video);
            cancelImageView.setVisibility(View.VISIBLE);
            camParamsLayout.setVisibility(View.GONE);

            if (mMediaRecorder != null) {
                mMediaRecorder.stop();
                mMediaRecorder.reset();
            }

            if (mCamera != null) {
                mCamera.stopPreview();
                isCameraPreviewing = false;
            }

            texttureViewLayout.setVisibility(View.GONE);
            videoViewRelLayout.setVisibility(View.VISIBLE);
            playRecordedVideo();


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onInfo(MediaRecorder mr, int what, int extra) {
        if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
            stopRecorderProcess();
            mToggleButton.setChecked(false);
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.i("Info", "onSurfaceTextureAvailable");
        surfaceTexture = textureView.getSurfaceTexture();
        if (permissionModule.checkCameraPermission()) {
            if (!videoGallerySelected)
                openCamera();
        } else
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    permissionModule.getCameraPermissionCode());
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        Log.i("Info", "onSurfaceTextureSizeChanged");
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        Log.i("Info", "onSurfaceTextureDestroyed");
        shutdown();
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        Log.i("Info", "onSurfaceTextureUpdated");
    }

    public void setFlashModeOff() {
        if (mCamera != null) {
            if (currentFlashMode != FLASH_OPTIONS[1]) {
                try {
                    mCurrentFlash = 1;
                    flashModeImgv.setImageResource(FLASH_ICONS[mCurrentFlash]);
                    Camera.Parameters p = mCamera.getParameters();
                    p.setFlashMode(FLASH_OPTIONS[mCurrentFlash]);
                    mCamera.setParameters(p);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void shutdown() {
        try {
            if (mMediaRecorder != null) {
                mMediaRecorder.reset();
                mMediaRecorder.release();
                mMediaRecorder = null;
            }

            if (mCamera != null) {
                setFlashModeOff();
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
                isCameraPreviewing = false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RESULT_CODE_VIDEO_GALLERY_SELECT) {
                VideoViewFragment nextFrag = new VideoViewFragment(data);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.videoPickerMainLayout, nextFrag, VideoPickerFrag.class.getName())
                        .addToBackStack(null)
                        .commit();
            }
        }
    }

    public void playRecordedVideo() {
        videoUri = Uri.parse(videoFilePath);
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
                cancelImageView.setVisibility(View.VISIBLE);
                playVideoImgv.setVisibility(View.VISIBLE);
            }
        });
    }
}