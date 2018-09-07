package com.uren.catchu.SharePackage.VideoPicker.WillDelete;

import android.app.Activity;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.ToggleButton;

import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.FileAdapter;
import com.uren.catchu.R;

import java.io.IOException;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
import static com.uren.catchu.Constants.NumericConstants.MAX_VIDEO_DURATION;

public class VideoTestActivity extends Activity implements SurfaceHolder.Callback,
         MediaRecorder.OnInfoListener{
    private final String VIDEO_PATH_NAME = "/sdcard/VGA_30fps_512vbrate.mp4";

    private MediaRecorder mMediaRecorder;
    private Camera mCamera;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mHolder;
    private ToggleButton mToggleButton;
    ImageView cancelImageView;
    /*SeekBar seekBar;
    public boolean isRecording;
    int recordTime;*/
    Handler handler;

    // TODO: 6.09.2018 - Max video duration i gostermek icin seekbar ekleyelim.


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_test);

        initUI();
        setHolderProperties();
        addListeners();
    }

    private void initUI() {
        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        cancelImageView = findViewById(R.id.cancelImageView);
        mToggleButton = (ToggleButton) findViewById(R.id.toggleRecordingButton);
      /*  seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setMax(MAX_VIDEO_DURATION * 1000);*/
        handler = new Handler();
    }

    public void setHolderProperties(){
        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    private void addListeners() {
        cancelImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelImageView.setVisibility(View.GONE);
                previewCamera();
            }
        });

        mToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mToggleButton.isChecked()) {

                    mToggleButton.setBackgroundResource(R.drawable.btn_capture_photo);
                    cancelImageView.setVisibility(View.GONE);

                    try {
                        initRecorder(mHolder.getSurface());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    /*recordTime = 0;*/
                    mMediaRecorder.start();
                    /*isRecording = true;*/
                    /*handler.post(UpdateRecordTime);
                    processSeekBar();*/


                } else
                    stopRecorderProcess();
            }
        });
    }

    /*public void processSeekBar(){
        final Handler mHandler = new Handler();
        final Runnable mRunnable = new Runnable() {

            @Override
            public void run() {
                if(isRecording){
                    int mCurrentPosition = getCurrentPosition();
                    seekBar.setProgress(mCurrentPosition * 10 / MAX_VIDEO_DURATION);
                }
                mHandler.postDelayed(this, 1000);
            }
        };
    }
*/
    public void openCamera(){
        if (mCamera == null) {
            mCamera = Camera.open();
            checkCameraRotation();
            previewCamera();
        }
    }

    private void initRecorder(Surface surface) throws IOException {
        // It is very important to unlock the camera before doing setCamera
        // or it will results in a black preview
        mCamera.unlock();
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setPreviewDisplay(surface);
        mMediaRecorder.setCamera(mCamera);
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
            CommonUtils.showToastLong(VideoTestActivity.this,
                    getResources().getString(R.string.error) + e.getMessage());
            e.printStackTrace();
        }
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

    public void previewCamera() {
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (Exception e) {
            CommonUtils.showToastLong(VideoTestActivity.this,
                    getResources().getString(R.string.error) + e.getMessage());
            e.printStackTrace();
        }
    }

    public void stopRecorderProcess(){
        mToggleButton.setBackgroundResource(R.drawable.btn_capture_video);
        cancelImageView.setVisibility(View.VISIBLE);
        mMediaRecorder.stop();
        /*isRecording = false;*/
        mMediaRecorder.reset();
        mCamera.stopPreview();
        /*handler.removeCallbacks(UpdateRecordTime);*/
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        openCamera();
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

    @Override
    public void onInfo(MediaRecorder mr, int what, int extra) {

        Log.i("Info","what:" + what + " - extra:" + extra);

        if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
            Log.i("VIDEOCAPTURE","Maximum Duration Reached" );

            stopRecorderProcess();
            mToggleButton.setChecked(false);
        }
    }

    /*Runnable UpdateRecordTime = new Runnable(){
        public void run(){
            if(isRecording){
                recordTime++;
                handler.postDelayed(this, 1000);
            }
        }
    };*/

    /*public int getCurrentPosition(){
        return recordTime;
    };*/
}
