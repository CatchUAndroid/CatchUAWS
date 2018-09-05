package com.uren.catchu.SharePackage.VideoPicker;

import android.Manifest;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ToggleButton;

import com.uren.catchu.GeneralUtils.FileAdapter;
import com.uren.catchu.R;
import com.uren.catchu.SharePackage.MainShareActivity;

import java.io.IOException;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

public class VideoTestActivity extends Activity implements SurfaceHolder.Callback {
    private final String VIDEO_PATH_NAME = "/sdcard/VGA_30fps_512vbrate.mp4";

    private MediaRecorder mMediaRecorder;
    private Camera mCamera;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mHolder;
    private View mToggleButton;
    private boolean mInitSuccesful;

    private static final int CODE_SURFACE_STARTED = 0;
    private static final int CODE_SURFACE_UPDATED = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_test);

        // we shall take the video in landscape orientation
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mToggleButton = (ToggleButton) findViewById(R.id.toggleRecordingButton);
        mToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            // toggle video recording
            public void onClick(View v) {
                if (((ToggleButton) v).isChecked()) {


                    mMediaRecorder.start();
                    try {
                        // Thread.sleep(10 * 1000); // This will recode for 10 seconds, if you don't want then just remove it.
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //finish();
                } else {
                    mMediaRecorder.stop();
                    mMediaRecorder.reset();
                    mCamera.stopPreview();
                    mCamera.unlock();

                    try {
                        initRecorder(mHolder.getSurface(), CODE_SURFACE_UPDATED);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    /* Init the MediaRecorder, the order the methods are called is vital to
     * its correct functioning */
    private void initRecorder(Surface surface, int type) throws IOException {
        // It is very important to unlock the camera before doing setCamera
        // or it will results in a black preview
        if (mCamera == null) {
            mCamera = Camera.open();
            checkCameraRotation();
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
            mCamera.unlock();
        }

        //mMediaRecorder = new MediaRecorder();
        if (mMediaRecorder == null)
            mMediaRecorder = new MediaRecorder();

        mMediaRecorder.setPreviewDisplay(surface);
        mMediaRecorder.setCamera(mCamera);

        /*if(type == CODE_SURFACE_STARTED)
            mCamera.startPreview();*/

        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMediaRecorder.setVideoEncodingBitRate(512 * 1000);
        mMediaRecorder.setVideoFrameRate(30);
        mMediaRecorder.setVideoSize(640, 480);
        mMediaRecorder.setOutputFile(FileAdapter.getOutputMediaFile(MEDIA_TYPE_VIDEO).getAbsolutePath());

        Log.i("Info", "FileAdapter.getOutputMediaFile(MEDIA_TYPE_VIDEO).getAbsolutePath():" +
                FileAdapter.getOutputMediaFile(MEDIA_TYPE_VIDEO).getAbsolutePath());

        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            // This is thrown if the previous calls are not called with the
            // proper order
            e.printStackTrace();
        }

        mInitSuccesful = true;
    }

    private void checkCameraRotation() {

        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();

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
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            if (!mInitSuccesful)
                initRecorder(mHolder.getSurface(), CODE_SURFACE_STARTED);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        shutdown();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    private void shutdown() {
        // Release MediaRecorder and especially the Camera as it's a shared
        // object that can be used by other applications
        mMediaRecorder.reset();
        mMediaRecorder.release();
        mCamera.release();

        // once the objects have been released they can't be reused
        mMediaRecorder = null;
        mCamera = null;
    }
}
