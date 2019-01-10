package com.uren.catchu.MainPackage.MainFragments.Share.SubFragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.InfoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.FileAdapter;
import com.uren.catchu.GeneralUtils.FirebaseHelperModel.ErrorSaveHelper;
import com.uren.catchu.GeneralUtils.VideoUtil.VideoSelectUtil;
import com.uren.catchu.Interfaces.ReturnCallback;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Share.Models.AutoFitTextureView;
import com.uren.catchu.Permissions.PermissionModule;
import com.uren.catchu.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
import static com.uren.catchu.Constants.NumericConstants.MAX_VIDEO_DURATION;
import static com.uren.catchu.Constants.NumericConstants.VERIFY_PHONE_NUM_DURATION;
import static com.uren.catchu.Constants.StringConstants.CAMERA_TEXT;
import static com.uren.catchu.Constants.StringConstants.CHAR_HYPHEN;

@SuppressLint("ValidFragment")
public class VideoRecordFragment extends BaseFragment implements View.OnClickListener, MediaRecorder.OnInfoListener {

    private static final int SENSOR_ORIENTATION_DEFAULT_DEGREES = 90;
    private static final int SENSOR_ORIENTATION_INVERSE_DEGREES = 270;
    private static final SparseIntArray DEFAULT_ORIENTATIONS = new SparseIntArray();
    private static final SparseIntArray INVERSE_ORIENTATIONS = new SparseIntArray();

    public static final String CAMERA_FRONT = "1";
    public static final String CAMERA_BACK = "0";
    private String cameraId = CAMERA_BACK;

    private boolean isFlashSupported;
    private boolean isTorchOn;
    private static Context context;

    private static final String TAG = "VideoRecordFragment";

    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
    };

    static {
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_0, 90);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_90, 0);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_180, 270);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    static {
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_0, 270);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_90, 180);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_180, 90);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_270, 0);
    }

    private AutoFitTextureView mTextureView;

    private ToggleButton toggleRecordingButton;
    private ImageView flashModeImgv;
    private ImageView switchCamImgv;
    private TextView remainingTimeTv;

    private CameraDevice mCameraDevice;
    private CameraCaptureSession mPreviewSession;

    private Size mPreviewSize;
    private Size mVideoSize;
    private MediaRecorder mMediaRecorder;
    private PermissionModule permissionModule;
    private boolean mIsRecordingVideo;
    private HandlerThread mBackgroundThread;
    private Handler mBackgroundHandler;
    private Semaphore mCameraOpenCloseLock = new Semaphore(1);

    private TextureView.SurfaceTextureListener mSurfaceTextureListener
            = new TextureView.SurfaceTextureListener() {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture,
                                              int width, int height) {
            if (!hasPermissions(getContext(), PERMISSIONS)) {
                requestPermissions(PERMISSIONS, PERMISSION_ALL);
            } else
                openCamera(width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture,
                                                int width, int height) {
            configureTransform(width, height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        }

    };

    private CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            mCameraDevice = cameraDevice;
            startPreview();
            mCameraOpenCloseLock.release();
            if (null != mTextureView) {
                configureTransform(mTextureView.getWidth(), mTextureView.getHeight());
            }
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int error) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
            Activity activity = getActivity();
            if (null != activity) {
                activity.finish();
            }
        }

    };
    private Integer mSensorOrientation;
    private String mNextVideoAbsolutePath;
    private CaptureRequest.Builder mPreviewBuilder;
    private ReturnCallback returnCallback;

    public VideoRecordFragment(ReturnCallback returnCallback) {
        this.returnCallback = returnCallback;
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private static Size chooseVideoSize(Size[] choices) {
        for (Size size : choices) {
            if (size.getWidth() == size.getHeight() * 4 / 3 && size.getWidth() <= 1080) {
                return size;
            }
        }
        Log.e(TAG, "Couldn't find any suitable video size");
        return choices[choices.length - 1];
    }

    private static Size chooseOptimalSize(Size[] choices, int width, int height, Size aspectRatio) {
        try {
            // Collect the supported resolutions that are at least as big as the preview Surface
            List<Size> bigEnough = new ArrayList<>();
            int w = aspectRatio.getWidth();
            int h = aspectRatio.getHeight();
            for (Size option : choices) {
                if (option.getHeight() == option.getWidth() * h / w &&
                        option.getWidth() >= width && option.getHeight() >= height) {
                    bigEnough.add(option);
                }
            }

            // Pick the smallest of those, assuming we found any
            if (bigEnough.size() > 0) {
                return Collections.min(bigEnough, new CompareSizesByArea());
            } else {
                Log.e(TAG, "Couldn't find any suitable preview size");
                return choices[0];
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, VideoRecordFragment.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
        return choices[0];
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_video_record, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        mTextureView = (AutoFitTextureView) view.findViewById(R.id.texture);
        toggleRecordingButton = (ToggleButton) view.findViewById(R.id.toggleRecordingButton);
        flashModeImgv = (ImageView) view.findViewById(R.id.flashModeImgv);
        switchCamImgv = (ImageView) view.findViewById(R.id.switchCamImgv);
        remainingTimeTv = (TextView) view.findViewById(R.id.remainingTimeTv);
        permissionModule = new PermissionModule(getActivity());
        toggleRecordingButton.setOnClickListener(this);
        flashModeImgv.setOnClickListener(this);
        switchCamImgv.setOnClickListener(this);
        context = getContext();
    }

    @Override
    public void onResume() {
        super.onResume();
        startBackgroundThread();
        startOpenCamera();
    }

    @Override
    public void onPause() {
        closeCamera();
        stopBackgroundThread();
        super.onPause();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toggleRecordingButton:
                if (mIsRecordingVideo) {
                    stopRecordingVideo();
                } else {
                    startRecordingVideo();
                }
                break;

            case R.id.flashModeImgv:
                switchFlash();
                break;
            case R.id.switchCamImgv:
                switchCamera();
                break;
            default:
                break;
        }
    }

    public void startOpenCamera() {
        try {

            if (mTextureView.isAvailable()) {
                if (!hasPermissions(getContext(), PERMISSIONS)) {
                    requestPermissions(PERMISSIONS, PERMISSION_ALL);
                } else
                    openCamera(mTextureView.getWidth(), mTextureView.getHeight());
            } else
                mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);

        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(), this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    private void startBackgroundThread() {
        try {
            mBackgroundThread = new HandlerThread("CameraBackground");
            mBackgroundThread.start();
            mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(), this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    private void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(), this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_ALL) {
            if (grantResults.length > 0 && checkGrantResults(grantResults) ) {
                startOpenCamera();
            } else
                getActivity().onBackPressed();
        }
    }

    public boolean checkGrantResults(int[] grantResults){
        for(int result : grantResults){
            if(result != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    @SuppressWarnings("MissingPermission")
    private void openCamera(int width, int height) {

        final Activity activity = getActivity();
        if (null == activity || activity.isFinishing()) {
            return;
        }
        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            /*if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }*/
            // Choose the sizes for camera preview and video recording
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics
                    .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
            if (map == null) {
                throw new RuntimeException("Cannot get available preview/video sizes");
            }
            mVideoSize = chooseVideoSize(map.getOutputSizes(MediaRecorder.class));
            mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
                    width, height, mVideoSize);

            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                mTextureView.setAspectRatio(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            } else {
                mTextureView.setAspectRatio(mPreviewSize.getHeight(), mPreviewSize.getWidth());
            }
            configureTransform(width, height);
            Boolean available = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
            isFlashSupported = available == null ? false : available;
            setupFlashButton();
            mMediaRecorder = new MediaRecorder();
            manager.openCamera(cameraId, mStateCallback, null);
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(), this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    public void switchFlash() {
        try {
            if (cameraId.equals(CAMERA_BACK)) {
                if (isFlashSupported) {
                    if (isTorchOn) {
                        mPreviewBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_OFF);
                        mPreviewSession.setRepeatingRequest(mPreviewBuilder.build(), null, null);
                        Glide.with(getActivity()).load(R.drawable.ic_flash_off).into(flashModeImgv);
                        isTorchOn = false;
                    } else {
                        mPreviewBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_TORCH);
                        mPreviewSession.setRepeatingRequest(mPreviewBuilder.build(), null, null);
                        Glide.with(getActivity()).load(R.drawable.ic_flash_on).into(flashModeImgv);
                        isTorchOn = true;
                    }
                }
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(), this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    public void setupFlashButton() {
        try {
            if (cameraId.equals(CAMERA_BACK) && isFlashSupported) {
                flashModeImgv.setVisibility(View.VISIBLE);

                if (isTorchOn) {
                    Glide.with(getActivity()).load(R.drawable.ic_flash_off).into(flashModeImgv);
                } else {
                    Glide.with(getActivity()).load(R.drawable.ic_flash_on).into(flashModeImgv);
                }

            } else {
                flashModeImgv.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(), this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    private void closeCamera() {
        try {
            mCameraOpenCloseLock.acquire();
            closePreviewSession();
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
            if (null != mMediaRecorder) {
                mMediaRecorder.release();
                mMediaRecorder = null;
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(), this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        } finally {
            mCameraOpenCloseLock.release();
        }
    }

    private void startPreview() {
        if (null == mCameraDevice || !mTextureView.isAvailable() || null == mPreviewSize) {
            return;
        }
        try {
            closePreviewSession();
            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

            Surface previewSurface = new Surface(texture);
            mPreviewBuilder.addTarget(previewSurface);

            mCameraDevice.createCaptureSession(Collections.singletonList(previewSurface),
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {
                            mPreviewSession = session;
                            updatePreview();
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                            Activity activity = getActivity();
                            if (null != activity) {
                                Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, mBackgroundHandler);
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(), this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    private void updatePreview() {
        if (null == mCameraDevice) {
            return;
        }
        try {
            setUpCaptureRequestBuilder(mPreviewBuilder);
            HandlerThread thread = new HandlerThread("CameraPreview");
            thread.start();
            mPreviewSession.setRepeatingRequest(mPreviewBuilder.build(), null, mBackgroundHandler);
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(), this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    private void setUpCaptureRequestBuilder(CaptureRequest.Builder builder) {
        try {
            builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(), this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    private void configureTransform(int viewWidth, int viewHeight) {
        try {
            Activity activity = getActivity();
            if (null == mTextureView || null == mPreviewSize || null == activity) {
                return;
            }
            int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
            Matrix matrix = new Matrix();
            RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
            RectF bufferRect = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
            float centerX = viewRect.centerX();
            float centerY = viewRect.centerY();
            if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
                bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
                matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
                float scale = Math.max(
                        (float) viewHeight / mPreviewSize.getHeight(),
                        (float) viewWidth / mPreviewSize.getWidth());
                matrix.postScale(scale, scale, centerX, centerY);
                matrix.postRotate(90 * (rotation - 2), centerX, centerY);
            }
            mTextureView.setTransform(matrix);
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(), this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    private void setUpMediaRecorder() {
        try {
            final Activity activity = getActivity();
            if (null == activity) {
                return;
            }
            mNextVideoAbsolutePath = FileAdapter.getOutputMediaFile(MEDIA_TYPE_VIDEO).getAbsolutePath();
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mMediaRecorder.setOutputFile(mNextVideoAbsolutePath);
            mMediaRecorder.setVideoEncodingBitRate(1700000);
            mMediaRecorder.setVideoFrameRate(30);
            mMediaRecorder.setVideoSize(mVideoSize.getWidth(), mVideoSize.getHeight());
            mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mMediaRecorder.setMaxDuration(MAX_VIDEO_DURATION * 1000);

            int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();

            switch (mSensorOrientation) {
                case SENSOR_ORIENTATION_DEFAULT_DEGREES:
                    mMediaRecorder.setOrientationHint(DEFAULT_ORIENTATIONS.get(rotation));
                    break;
                case SENSOR_ORIENTATION_INVERSE_DEGREES:
                    mMediaRecorder.setOrientationHint(INVERSE_ORIENTATIONS.get(rotation));
                    break;
            }
            mMediaRecorder.setOnInfoListener(this);

            mMediaRecorder.prepare();
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(), this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    private void startRecordingVideo() {
        if (null == mCameraDevice || !mTextureView.isAvailable() || null == mPreviewSize) {
            return;
        }
        try {
            closePreviewSession();
            setUpMediaRecorder();
            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            List<Surface> surfaces = new ArrayList<>();

            // Set up Surface for the camera preview
            Surface previewSurface = new Surface(texture);
            surfaces.add(previewSurface);
            mPreviewBuilder.addTarget(previewSurface);

            // Set up Surface for the MediaRecorder
            Surface recorderSurface = mMediaRecorder.getSurface();
            surfaces.add(recorderSurface);
            mPreviewBuilder.addTarget(recorderSurface);

            // Start a capture session
            // Once the session starts, we can update the UI and start recording
            mCameraDevice.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {

                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    final String methodName = new Object() {
                    }.getClass().getEnclosingMethod().getName();
                    mPreviewSession = cameraCaptureSession;
                    updatePreview();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                toggleRecordingButton.setBackgroundResource(R.drawable.btn_capture_photo);
                                mIsRecordingVideo = true;
                                mMediaRecorder.start();
                                setTimer();
                            } catch (Exception e) {
                                ErrorSaveHelper.writeErrorToDB(getContext(), this.getClass().getSimpleName(),
                                        methodName + CHAR_HYPHEN + new Object() {
                                        }.getClass().getEnclosingMethod().getName(), e.getMessage());
                                e.printStackTrace();
                            }
                        }
                    });
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Activity activity = getActivity();
                    if (null != activity) {
                        ErrorSaveHelper.writeErrorToDB(getContext(), this.getClass().getSimpleName(),
                                new Object() {
                                }.getClass().getEnclosingMethod().getName(), cameraCaptureSession.toString());
                    }
                }
            }, mBackgroundHandler);
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(), this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }

    }

    private void closePreviewSession() {
        try {
            if (mPreviewSession != null) {
                mPreviewSession.close();
                mPreviewSession = null;
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(), this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    private void stopRecordingVideo() {
        // UI
        try {
            mIsRecordingVideo = false;
            toggleRecordingButton.setBackgroundResource(R.drawable.btn_capture_video);
            // Stop recording
            mMediaRecorder.stop();
            mMediaRecorder.reset();

            VideoSelectUtil videoSelectUtil = new VideoSelectUtil(getActivity(), Uri.parse(mNextVideoAbsolutePath),
                    mNextVideoAbsolutePath, CAMERA_TEXT);
            returnCallback.onReturn(videoSelectUtil);

            mNextVideoAbsolutePath = null;

            DialogBoxUtil.showInfoDialogWithLimitedTime(getActivity(), null,
                    getContext().getResources().getString(R.string.PLEASE_WAIT), 3000, new InfoDialogBoxCallback() {
                        @Override
                        public void okClick() {
                            getActivity().onBackPressed();
                        }
                    });


            //startPreview();
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(), this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onInfo(MediaRecorder mr, int what, int extra) {
        try {
            if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                stopRecordingVideo();
                toggleRecordingButton.setChecked(false);
                remainingTimeTv.setText(checkDigit(0));
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(), this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    static class CompareSizesByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }
    }

    public void switchCamera() {
        try {
            if (cameraId.equals(CAMERA_FRONT)) {
                cameraId = CAMERA_BACK;
                closeCamera();
                reopenCamera();

            } else if (cameraId.equals(CAMERA_BACK)) {
                cameraId = CAMERA_FRONT;
                closeCamera();
                reopenCamera();
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(), this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    public void reopenCamera() {
        try {
            if (mTextureView.isAvailable()) {
                openCamera(mTextureView.getWidth(), mTextureView.getHeight());
            } else {
                mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(), this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    public void setTimer() {
        try {
            new CountDownTimer(MAX_VIDEO_DURATION * 1000, 1000) {

                int duration = MAX_VIDEO_DURATION;

                public void onTick(long millisUntilFinished) {
                    if(mIsRecordingVideo) {
                        remainingTimeTv.setText(checkDigit(duration));
                        duration--;
                    }
                }

                public void onFinish() {
                    remainingTimeTv.setText(checkDigit(0));
                }
            }.start();
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(),this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    public String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }
}
