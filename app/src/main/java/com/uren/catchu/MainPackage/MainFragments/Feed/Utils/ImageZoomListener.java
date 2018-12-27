package com.uren.catchu.MainPackage.MainFragments.Feed.Utils;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.uren.catchu.GeneralUtils.FirebaseHelperModel.ErrorSaveHelper;

public class ImageZoomListener implements View.OnTouchListener {

    private static final String TAG = "Touch";
    @SuppressWarnings("unused")
    private static final float MIN_ZOOM = 1f, MAX_ZOOM = 1f;

    // These matrices will be used to scale points of the image
    Matrix initMatrix;
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();

    // The 3 states (events) which the user is trying to perform
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    // these PointF objects are used to record the point(s) the user is touching
    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;

    public ImageZoomListener(Matrix initMatrix) {
        this.initMatrix = initMatrix;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {


        try {
            ImageView view = (ImageView) v;
            view.setScaleType(ImageView.ScaleType.MATRIX);
            float scale;

            dumpEvent(event);
            // Handle touch events here...

            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:   // first finger down only
                    savedMatrix.set(matrix);
                    start.set(event.getX(), event.getY());
                    mode = DRAG;
                    break;

                case MotionEvent.ACTION_UP: // first finger lifted

                case MotionEvent.ACTION_POINTER_UP: // second finger lifted

                    mode = NONE;
                    break;

                case MotionEvent.ACTION_POINTER_DOWN: // first and second finger down

                    oldDist = spacing(event);
                    if (oldDist > 5f) {
                        savedMatrix.set(matrix);
                        midPoint(mid, event);
                        mode = ZOOM;
                        Log.d(TAG, "mode=ZOOM");
                    }
                    break;

                case MotionEvent.ACTION_MOVE:

                    if (mode == DRAG) {
                        matrix.set(savedMatrix);
                        matrix.postTranslate(event.getX() - start.x, event.getY() - start.y); // create the transformation in the matrix  of points
                    } else if (mode == ZOOM) {
                        // pinch zooming
                        float newDist = spacing(event);
                        if (newDist > 5f) {
                            matrix.set(savedMatrix);
                            scale = newDist / oldDist; // setting the scaling of the

                            // matrix...if scale > 1 means
                            // zoom in...if scale < 1 means
                            // zoom out
                            matrix.postScale(scale, scale, mid.x, mid.y);
                        }
                    }
                    break;
            }

            view.setImageMatrix(matrix); // display the transformation on screen
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(null, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }

        return true; // indicate event was handled
    }

    private float spacing(MotionEvent event) {
        float spacingVal = 0;
        try {
            float x = event.getX(0) - event.getX(1);
            float y = event.getY(0) - event.getY(1);
            spacingVal =  (float) Math.sqrt(x * x + y * y);
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(null, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }

        return spacingVal;
    }

    /*
     * --------------------------------------------------------------------------
     * Method: midPoint Parameters: PointF object, MotionEvent Returns: void
     * Description: calculates the midpoint between the two fingers
     * ------------------------------------------------------------
     */

    private void midPoint(PointF point, MotionEvent event) {
        try {
            float x = event.getX(0) + event.getX(1);
            float y = event.getY(0) + event.getY(1);
            point.set(x / 2, y / 2);
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(null, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }

    /**
     * Show an event in the LogCat view, for debugging
     */
    private void dumpEvent(MotionEvent event) {
        try {
            String names[] = {"DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE", "POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?"};
            StringBuilder sb = new StringBuilder();
            int action = event.getAction();
            int actionCode = action & MotionEvent.ACTION_MASK;
            sb.append("event ACTION_").append(names[actionCode]);

            if (actionCode == MotionEvent.ACTION_POINTER_DOWN || actionCode == MotionEvent.ACTION_POINTER_UP) {
                sb.append("(pid ").append(action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
                sb.append(")");
            }

            sb.append("[");
            for (int i = 0; i < event.getPointerCount(); i++) {
                sb.append("#").append(i);
                sb.append("(pid ").append(event.getPointerId(i));
                sb.append(")=").append((int) event.getX(i));
                sb.append(",").append((int) event.getY(i));
                if (i + 1 < event.getPointerCount())
                    sb.append(";");
            }

            sb.append("]");
            Log.d("Touch Events ---------", sb.toString());
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(null, this.getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.toString());
            e.printStackTrace();
        }
    }
}
