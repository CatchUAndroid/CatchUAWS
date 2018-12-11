package com.uren.catchu.GeneralUtils.PhotoUtil;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.graphics.BitmapCompat;

import com.uren.catchu.GeneralUtils.ExifUtil;
import com.uren.catchu.GeneralUtils.FirebaseHelperModel.ErrorSaveHelper;
import com.uren.catchu.GeneralUtils.UriAdapter;

import java.io.IOException;

import static com.uren.catchu.Constants.NumericConstants.MAX_IMAGE_SIZE;
import static com.uren.catchu.Constants.StringConstants.CAMERA_TEXT;
import static com.uren.catchu.Constants.StringConstants.FROM_FILE_TEXT;
import static com.uren.catchu.Constants.StringConstants.GALLERY_TEXT;

public class PhotoSelectUtil {

    Bitmap bitmap = null;
    Bitmap screeanShotBitmap = null;
    Bitmap resizedBitmap = null;
    Uri mediaUri = null;
    String imageRealPath = null;
    Context context;
    Intent data;
    String type;
    boolean portraitMode;
    float bitmapResizeValue = 0.8f;

    public PhotoSelectUtil() {

    }

    public PhotoSelectUtil(Context context, Intent data, String type) {
        this.context = context;
        this.data = data;
        this.type = type;
        routeSelection();
    }

    public PhotoSelectUtil(Context context, Uri uri, String type) {
        this.context = context;
        this.type = type;
        this.mediaUri = uri;
        routeSelection();
        setPortraitMode();
    }

    private void routeSelection() {
        switch (type) {
            case CAMERA_TEXT:
                onSelectFromCameraResult();
                break;
            case GALLERY_TEXT:
                onSelectFromGalleryResult();
                break;
            case FROM_FILE_TEXT:
                onSelectFromFileResult();
                break;
            default:
                break;
        }
    }

    public Bitmap getResizedBitmap() {
        Bitmap mBitmap = null;

        try {
            if (getScreeanShotBitmap() != null) {
                mBitmap = getScreeanShotBitmap();
                System.out.println("BitmapCompat.getAllocationByteCount(getScreeanShotBitmap):" + BitmapCompat.getAllocationByteCount(getScreeanShotBitmap()));
            } else if (getBitmap() != null) {
                mBitmap = getBitmap();
                System.out.println("BitmapCompat.getAllocationByteCount(getBitmap):" + BitmapCompat.getAllocationByteCount(getBitmap()));
            }

            if (BitmapCompat.getAllocationByteCount(mBitmap) > MAX_IMAGE_SIZE) {

                for (float i = 0.9f; i > 0; i = i - 0.1f) {
                    System.out.println("i_1:" + i);
                    resizedBitmap = Bitmap.createScaledBitmap(mBitmap,
                            (int) (mBitmap.getWidth() * i),
                            (int) (mBitmap.getHeight() * i), true);

                    System.out.println("BitmapCompat.getAllocationByteCount(resizedBitmap):" + BitmapCompat.getAllocationByteCount(resizedBitmap));

                    if (BitmapCompat.getAllocationByteCount(resizedBitmap) < MAX_IMAGE_SIZE) {
                        break;
                    }
                }
            } else {
                if (!type.equals(GALLERY_TEXT)) {
                    for (float i = 1.2f; i < 20f; i = i + 1.2f) {
                        System.out.println("i_2:" + i);
                        resizedBitmap = Bitmap.createScaledBitmap(mBitmap,
                                (int) (mBitmap.getWidth() * i),
                                (int) (mBitmap.getHeight() * i), true);

                        System.out.println("BitmapCompat.getAllocationByteCount(resizedBitmap):" + BitmapCompat.getAllocationByteCount(resizedBitmap));

                        if (BitmapCompat.getAllocationByteCount(resizedBitmap) > MAX_IMAGE_SIZE) {
                            break;
                        }
                    }
                } else
                    resizedBitmap = mBitmap;
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, PhotoSelectUtil.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            if (getScreeanShotBitmap() != null) {
                resizedBitmap = getScreeanShotBitmap();
            } else if (getBitmap() != null) {
                resizedBitmap = getBitmap();
            }
        }

        System.out.println("BitmapCompat.getAllocationByteCount(resizedBitmap):" + BitmapCompat.getAllocationByteCount(resizedBitmap));

        return resizedBitmap;
    }

    private void onSelectFromFileResult() {
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), mediaUri);
            imageRealPath = UriAdapter.getPathFromGalleryUri(context, mediaUri);
            bitmap = ExifUtil.rotateImageIfRequired(imageRealPath, bitmap);
        } catch (IOException e) {
            ErrorSaveHelper.writeErrorToDB(context, PhotoSelectUtil.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    public void onSelectFromGalleryResult() {
        try {
            mediaUri = data.getData();
            if (data != null) {
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), mediaUri);
                    imageRealPath = UriAdapter.getPathFromGalleryUri(context, mediaUri);
                    bitmap = ExifUtil.rotateImageIfRequired(imageRealPath, bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, PhotoSelectUtil.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    public void onSelectFromCameraResult() {
        try {
            bitmap = (Bitmap) data.getExtras().get("data");
            mediaUri = data.getData();
            imageRealPath = UriAdapter.getPathFromGalleryUri(context, mediaUri);
            bitmap = ExifUtil.rotateImageIfRequired(imageRealPath, bitmap);
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, PhotoSelectUtil.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }

    }

    public void setPortraitMode() {
        try {
            if (bitmap == null)
                return;

            int width = bitmap.getWidth();
            int heigth = bitmap.getHeight();

            if (heigth > width)
                portraitMode = true;
            else
                portraitMode = false;
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, PhotoSelectUtil.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Uri getMediaUri() {
        return mediaUri;
    }

    public void setMediaUri(Uri mediaUri) {
        this.mediaUri = mediaUri;
    }

    public String getImageRealPath() {
        return imageRealPath;
    }

    public void setImageRealPath(String imageRealPath) {
        this.imageRealPath = imageRealPath;
    }

    public boolean isPortraitMode() {
        return portraitMode;
    }

    public void setPortraitMode(boolean portraitMode) {
        this.portraitMode = portraitMode;
    }

    public Bitmap getScreeanShotBitmap() {
        return screeanShotBitmap;
    }

    public void setScreeanShotBitmap(Bitmap screeanShotBitmap) {
        this.screeanShotBitmap = screeanShotBitmap;
    }
}
