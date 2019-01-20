package com.uren.catchu.GeneralUtils.PhotoUtil;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.graphics.BitmapCompat;

import com.uren.catchu.GeneralUtils.BitmapConversion;
import com.uren.catchu.GeneralUtils.ExifUtil;
import com.uren.catchu.GeneralUtils.FirebaseHelperModel.ErrorSaveHelper;
import com.uren.catchu.GeneralUtils.UriAdapter;

import java.io.File;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static com.uren.catchu.Constants.NumericConstants.IMAGE_RESOLUTION_480;
import static com.uren.catchu.Constants.NumericConstants.IMAGE_RESOLUTION_640;
import static com.uren.catchu.Constants.NumericConstants.IMAGE_RESOLUTION_800;
import static com.uren.catchu.Constants.NumericConstants.MAX_IMAGE_SIZE_1ANDHALFMB;
import static com.uren.catchu.Constants.NumericConstants.MAX_IMAGE_SIZE_1MB;
import static com.uren.catchu.Constants.NumericConstants.MAX_IMAGE_SIZE_2ANDHALFMB;
import static com.uren.catchu.Constants.NumericConstants.MAX_IMAGE_SIZE_2MB;
import static com.uren.catchu.Constants.NumericConstants.MAX_IMAGE_SIZE_5MB;
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

    public PhotoSelectUtil() {

    }

    public PhotoSelectUtil(Context context, Intent data, String type) {
        this.context = context;
        this.data = data;
        this.type = type;
        routeSelection();
        setPortraitMode();
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

    /*public Bitmap getImageResizedBitmap() {
        Bitmap mBitmap = null;
        int newWidth, newHeight;

        try {
            if (getScreeanShotBitmap() != null)
                mBitmap = getScreeanShotBitmap();
            else if (getBitmap() != null)
                mBitmap = getBitmap();

            System.out.println("BitmapCompat.getAllocationByteCount(mBitmap):" + BitmapCompat.getAllocationByteCount(mBitmap));

            int width = mBitmap.getWidth();
            int height = mBitmap.getHeight();

            if (isPortraitMode()) {
                newWidth = IMAGE_RESOLUTION_480;
                newHeight = IMAGE_RESOLUTION_800;
            } else {
                newWidth = IMAGE_RESOLUTION_800;
                newHeight = IMAGE_RESOLUTION_480;
            }

            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;

            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);

            resizedBitmap = Bitmap.createBitmap(
                    mBitmap, 0, 0, width, height, matrix, false);

            //bm.recycle();
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(null, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
            return mBitmap;
        }
        return resizedBitmap;
    }*/

    /*public Bitmap getImageResizedBitmap() {
        Bitmap mBitmap = null;
        int newWidth, newHeight;

        try {
            if (getScreeanShotBitmap() != null)
                mBitmap = getScreeanShotBitmap();
            else if (getBitmap() != null)
                mBitmap = getBitmap();

            System.out.println("BitmapCompat.getAllocationByteCount(mBitmap):" + BitmapCompat.getAllocationByteCount(mBitmap));

            if (BitmapCompat.getAllocationByteCount(mBitmap) < MAX_IMAGE_SIZE_1MB)
                return mBitmap;

            if (isPortraitMode()) {
                newWidth = IMAGE_RESOLUTION_480;
                newHeight = IMAGE_RESOLUTION_800;
            } else {
                newWidth = IMAGE_RESOLUTION_800;
                newHeight = IMAGE_RESOLUTION_480;
            }

            int width = mBitmap.getWidth();
            int height = mBitmap.getHeight();

            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;

            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);

            resizedBitmap = Bitmap.createBitmap(
                    mBitmap, 0, 0, width, height, matrix, false);

            System.out.println("BitmapCompat.getAllocationByteCount(resizedBitmap)_1:" + BitmapCompat.getAllocationByteCount(resizedBitmap));

            if (BitmapCompat.getAllocationByteCount(resizedBitmap) < MAX_IMAGE_SIZE_2MB) {

                for (int i = 0; i < 2000; i = i + 100) {
                    newHeight = newHeight + i;
                    newWidth = newWidth + i;

                    System.out.println("BitmapCompat-->newWidth :" + newWidth);
                    System.out.println("BitmapCompat-->newHeight:" + newHeight);

                    scaleWidth = ((float) newWidth) / width;
                    scaleHeight = ((float) newHeight) / height;

                    Matrix tempMatrix = new Matrix();
                    tempMatrix.postScale(scaleWidth, scaleHeight);

                    resizedBitmap = Bitmap.createBitmap(mBitmap, 0, 0, width, height, tempMatrix, false);

                    System.out.println("BitmapCompat.getAllocationByteCount(resizedBitmap)_2:" + BitmapCompat.getAllocationByteCount(resizedBitmap));

                    if (BitmapCompat.getAllocationByteCount(resizedBitmap) > MAX_IMAGE_SIZE_2MB)
                        break;
                }
            }
            return resizedBitmap;

            //bm.recycle();
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(null, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
            return mBitmap;
        }
    }*/

    public Bitmap getResizedBitmap() {
        Bitmap mBitmap = null;
        int maxByteValue;

        try {
            if (getScreeanShotBitmap() != null)
                mBitmap = getScreeanShotBitmap();
            else if (getBitmap() != null)
                mBitmap = getBitmap();

            if (mBitmap == null) return null;

            System.out.println("BitmapCompat.getAllocationByteCount(mBitmap):" + BitmapCompat.getAllocationByteCount(mBitmap));

            if (BitmapCompat.getAllocationByteCount(mBitmap) > MAX_IMAGE_SIZE_5MB)
                maxByteValue = MAX_IMAGE_SIZE_2ANDHALFMB;
            else
                maxByteValue = MAX_IMAGE_SIZE_1ANDHALFMB;

            if (BitmapCompat.getAllocationByteCount(mBitmap) > maxByteValue) {

                for (float i = 0.9f; i > 0; i = i - 0.05f) {
                    System.out.println("i_1:" + i);
                    resizedBitmap = Bitmap.createScaledBitmap(mBitmap,
                            (int) (mBitmap.getWidth() * i),
                            (int) (mBitmap.getHeight() * i), true);

                    System.out.println("BitmapCompat.getAllocationByteCount(resizedBitmap):" + BitmapCompat.getAllocationByteCount(resizedBitmap));

                    if (BitmapCompat.getAllocationByteCount(resizedBitmap) < maxByteValue) {
                        break;
                    } else {
                        if (resizedBitmap != null && !resizedBitmap.isRecycled()) {
                            resizedBitmap.recycle();
                            resizedBitmap = null;
                        }
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

                        if (BitmapCompat.getAllocationByteCount(resizedBitmap) > maxByteValue)
                            break;
                        else {
                            if (resizedBitmap != null && !resizedBitmap.isRecycled()) {
                                resizedBitmap.recycle();
                                resizedBitmap = null;
                            }
                        }
                    }
                } else
                    resizedBitmap = mBitmap;
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
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

            imageRealPath = UriAdapter.getRealPathFromURI(mediaUri, context);

            if (imageRealPath != null && !imageRealPath.isEmpty())
                bitmap = ExifUtil.rotateImageIfRequired(imageRealPath, bitmap);
            else {
                imageRealPath = UriAdapter.getFilePathFromURI(context, mediaUri, MEDIA_TYPE_IMAGE);
                bitmap = ExifUtil.rotateImageIfRequired(imageRealPath, bitmap);
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
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
                } catch (Exception e) {
                    ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                            new Object() {
                            }.getClass().getEnclosingMethod().getName(), e.toString());
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
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
            ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
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
            ErrorSaveHelper.writeErrorToDB(context, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setResizedBitmap(Bitmap resizedBitmap) {
        this.resizedBitmap = resizedBitmap;
    }
}
