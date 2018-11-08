package com.uren.catchu.UgurDeneme;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.common.FirebaseVisionPoint;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.InfoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.IntentUtil.IntentSelectUtil;
import com.uren.catchu.GeneralUtils.PhotoUtil.PhotoSelectUtil;
import com.uren.catchu.GeneralUtils.ProgressDialogUtil.ProgressDialogUtil;
import com.uren.catchu.Permissions.PermissionModule;
import com.uren.catchu.R;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import static com.uren.catchu.Constants.StringConstants.GALLERY_TEXT;
import static com.uren.catchu.Permissions.PermissionModule.PERMISSION_WRITE_EXTERNAL_STORAGE;

public class FirebaseMLActivity extends AppCompatActivity {

    // High-accuracy landmark detection and face classification
    FirebaseVisionFaceDetectorOptions faceDetectorOptions;

    // Real-time contour detection of multiple faces
    FirebaseVisionFaceDetectorOptions faceDetectorRealTimeOptions;

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    Button faceButton;
    Button textButton;
    Button barcodeButton;
    Button labelButton;
    Button landmarkButton;

    FirebaseVisionImage visionImage;

    PermissionModule permissionModule;
    PhotoSelectUtil photoSelectUtil;

    LinearLayout faceDetectLayout;
    ImageView selectFaceImgv;
    TextView facedescTv;

    ProgressDialogUtil progressDialogUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_ml);
        initVariables();
        setDetectors();
        addListeners();
    }

    private void initVariables() {
        faceButton = findViewById(R.id.faceButton);
        textButton = findViewById(R.id.textButton);
        barcodeButton = findViewById(R.id.barcodeButton);
        labelButton = findViewById(R.id.labelButton);
        landmarkButton = findViewById(R.id.landmarkButton);
        permissionModule = new PermissionModule(this);
        faceDetectLayout = findViewById(R.id.faceDetectLayout);
        selectFaceImgv = findViewById(R.id.selectFaceImgv);
        facedescTv = findViewById(R.id.facedescTv);
        progressDialogUtil = new ProgressDialogUtil(FirebaseMLActivity.this, null, false);
    }

    private void addListeners() {
        faceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                faceDetectLayout.setVisibility(View.VISIBLE);
                startGalleryProcess();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == permissionModule.PERMISSION_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startGalleryProcess();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == 1000) {
                photoSelectUtil = new PhotoSelectUtil(FirebaseMLActivity.this, data, GALLERY_TEXT);
                Glide.with(FirebaseMLActivity.this)
                        .load(photoSelectUtil.getBitmap())
                        .apply(RequestOptions.centerInsideTransform())
                        .into(selectFaceImgv);
                faceDetectorStart();
            }
        }
    }


    private void startGalleryProcess() {
        if (permissionModule.checkWriteExternalStoragePermission())
            startActivityForResult(Intent.createChooser(IntentSelectUtil.getGalleryIntent(),
                    getResources().getString(R.string.selectPicture)), 1000);
        else
            ActivityCompat.requestPermissions(FirebaseMLActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_WRITE_EXTERNAL_STORAGE);
    }

    private void faceDetectorStart() {

        progressDialogUtil.dialogShow();
        FirebaseVisionFaceDetector detector = FirebaseVision.getInstance()
                .getVisionFaceDetector(faceDetectorOptions);

        Task<List<FirebaseVisionFace>> result =
                detector.detectInImage(getVisionImageFromBitmap(photoSelectUtil.getBitmap()))
                        .addOnSuccessListener(
                                new OnSuccessListener<List<FirebaseVisionFace>>() {
                                    @Override
                                    public void onSuccess(List<FirebaseVisionFace> faces) {
                                        getFaces(faces);
                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialogUtil.dialogDismiss();
                                        DialogBoxUtil.showErrorDialog(FirebaseMLActivity.this, e.getMessage(), new InfoDialogBoxCallback() {
                                            @Override
                                            public void okClick() {

                                            }
                                        });
                                    }
                                });
    }

    public void getFaces(List<FirebaseVisionFace> faces){
        int faceCount = 0;
        for (FirebaseVisionFace face : faces) {
            Rect bounds = face.getBoundingBox();
            float rotY = face.getHeadEulerAngleY();  // Head is rotated to the right rotY degrees
            float rotZ = face.getHeadEulerAngleZ();  // Head is tilted sideways rotZ degrees

            faceCount ++;

            // If landmark detection was enabled (mouth, ears, eyes, cheeks, and
            // nose available):
            FirebaseVisionFaceLandmark leftEar = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EAR);
            if (leftEar != null) {
                FirebaseVisionPoint leftEarPos = leftEar.getPosition();
            }

            // If contour detection was enabled:
            List<FirebaseVisionPoint> leftEyeContour =
                    face.getContour(FirebaseVisionFaceContour.LEFT_EYE).getPoints();
            List<FirebaseVisionPoint> upperLipBottomContour =
                    face.getContour(FirebaseVisionFaceContour.UPPER_LIP_BOTTOM).getPoints();

            // If classification was enabled:
            if (face.getSmilingProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                float smileProb = face.getSmilingProbability();
            }
            if (face.getRightEyeOpenProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                float rightEyeOpenProb = face.getRightEyeOpenProbability();
            }

            // If face tracking was enabled:
            if (face.getTrackingId() != FirebaseVisionFace.INVALID_ID) {
                int id = face.getTrackingId();
            }
        }

        facedescTv.setText("Face count:" + Integer.toString(faceCount));
        progressDialogUtil.dialogDismiss();
    }

    public void setDetectors() {
        faceDetectorOptions =
                new FirebaseVisionFaceDetectorOptions.Builder()
                        .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                        .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                        .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                        .build();

        faceDetectorRealTimeOptions =
                new FirebaseVisionFaceDetectorOptions.Builder()
                        .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
                        .build();
    }

    //To create a FirebaseVisionImage object from a Bitmap object:
    public FirebaseVisionImage getVisionImageFromBitmap(Bitmap bitmap) {
        visionImage = FirebaseVisionImage.fromBitmap(bitmap);
        return visionImage;
    }

    //To create a FirebaseVisionImage object from a media.Image object,
    //such as when capturing an image from a device's camera, first determine
    //the angle the image must be rotated to compensate for both the device's
    //rotation and the orientation of camera sensor in the device:
    public void getVisionImageFromMedia(Image mediaImage, int rotation) {
        visionImage = FirebaseVisionImage.fromMediaImage(mediaImage, rotation);
    }

    //To create a FirebaseVisionImage object from a ByteBuffer or a byte array
    public void getVisionImageFromByteBuffer(int rotation, ByteBuffer buffer) {
        FirebaseVisionImageMetadata metadata = new FirebaseVisionImageMetadata.Builder()
                .setWidth(480)   // 480x360 is typically sufficient for
                .setHeight(360)  // image recognition
                .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
                .setRotation(rotation)
                .build();
        visionImage = FirebaseVisionImage.fromByteBuffer(buffer, metadata);
    }


    //To create a FirebaseVisionImage object from a file, pass the app context and file URI to FirebaseVisionImage.fromFilePath():
    public void getVisionImageFromFile(Context context, Uri uri) {
        try {
            visionImage = FirebaseVisionImage.fromFilePath(context, uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private int getRotationCompensation(String cameraId, Activity activity, Context context)
            throws CameraAccessException {
        // Get the device's current rotation relative to its "native" orientation.
        // Then, from the ORIENTATIONS table, look up the angle the image must be
        // rotated to compensate for the device's rotation.
        int deviceRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int rotationCompensation = ORIENTATIONS.get(deviceRotation);

        // On most devices, the sensor orientation is 90 degrees, but for some
        // devices it is 270 degrees. For devices with a sensor orientation of
        // 270, rotate the image an additional 180 ((270 + 270) % 360) degrees.
        CameraManager cameraManager = (CameraManager) context.getSystemService(CAMERA_SERVICE);
        int sensorOrientation = cameraManager
                .getCameraCharacteristics(cameraId)
                .get(CameraCharacteristics.SENSOR_ORIENTATION);
        rotationCompensation = (rotationCompensation + sensorOrientation + 270) % 360;

        // Return the corresponding FirebaseVisionImageMetadata rotation value.
        int result;
        switch (rotationCompensation) {
            case 0:
                result = FirebaseVisionImageMetadata.ROTATION_0;
                break;
            case 90:
                result = FirebaseVisionImageMetadata.ROTATION_90;
                break;
            case 180:
                result = FirebaseVisionImageMetadata.ROTATION_180;
                break;
            case 270:
                result = FirebaseVisionImageMetadata.ROTATION_270;
                break;
            default:
                result = FirebaseVisionImageMetadata.ROTATION_0;
                Log.i("Info", "Bad rotation value: " + rotationCompensation);
        }
        return result;
    }
}
