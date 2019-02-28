package com.uren.catchu.ModelViews;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.uren.catchu.GeneralUtils.BitmapConversion;
import com.uren.catchu.MainPackage.MainFragments.Profile.SettingsManagement.Models.FingerPath;

import java.util.ArrayList;

public class PaintView extends View {

    private int BRUSH_SIZE = 10;
    public int DEFAULT_COLOR = Color.RED;
    //public static final int DEFAULT_BG_COLOR = Color.WHITE;
    public static final float TOUCH_TOLERANCE = 4;
    private float mX, mY;
    private Path mPath;
    private Paint mPaint;
    private ArrayList<FingerPath> paths = new ArrayList<>();
    private int currentColor;
    //private int backgroundColor = DEFAULT_BG_COLOR;
    private int strokeWidth;
    private boolean emboss;
    private boolean blur;
    private MaskFilter mEmboss;
    private MaskFilter mBlur;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mBitmapPaint = new Paint(Paint.DITHER_FLAG);

    public PaintView(Context context) {
        this(context, null);
    }

    public PaintView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(DEFAULT_COLOR);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setXfermode(null);
        mPaint.setAlpha(0xff);
        mEmboss = new EmbossMaskFilter(new float[]{1, 1, 1}, 0.4f, 6, 3.5f);
        mBlur = new BlurMaskFilter(5, BlurMaskFilter.Blur.NORMAL);
    }

    public void init(int width,int height , Bitmap bitmap) {
        Bitmap mutableBitmap = null;
        try {
            Bitmap tempBitmap = BitmapConversion.getResizedBitmap(bitmap, width, height);

            mutableBitmap = tempBitmap.copy(Bitmap.Config.ARGB_8888, true);
        } catch (Exception e){
            e.printStackTrace();
            return;
        }
        mBitmap = mutableBitmap;
        mCanvas = new Canvas(mBitmap);

        mCanvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);

        currentColor = DEFAULT_COLOR;
        strokeWidth = BRUSH_SIZE;
    }

    public void normal() {
        emboss = false;
        blur = false;
    }

    public void emboss() {
        emboss = true;
        blur = false;
    }

    public void blur() {
        emboss = false;
        blur = true;
    }

    public void setmBitmap(int width,int height , Bitmap bitmap){
        Bitmap tempBitmap = BitmapConversion.getResizedBitmap(bitmap, width, height);

        Bitmap mutableBitmap = tempBitmap.copy(Bitmap.Config.ARGB_8888, true);
        mBitmap = mutableBitmap;
        mCanvas = new Canvas(mBitmap);

        mCanvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
    }

    public void setBrushSize(int brushSize){
        strokeWidth = brushSize;
    }

    public void setCurrentColor(int currentColor){
        this.currentColor = currentColor;
    }

    public int getCurrentColor(){
        return currentColor;
    }

    public void clear() {
        //backgroundColor = DEFAULT_BG_COLOR;
        paths.clear();
        //normal();
        invalidate();
    }

    public Bitmap getmBitmap() {
        return mBitmap;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        //mCanvas.drawColor(backgroundColor);

        for (FingerPath fp : paths) {
            mPaint.setColor(fp.color);
            mPaint.setStrokeWidth(fp.strokeWidth);
            mPaint.setMaskFilter(null);

            if (fp.emboss)
                mPaint.setMaskFilter(mEmboss);
            else if (fp.blur)
                mPaint.setMaskFilter(mBlur);

            mCanvas.drawPath(fp.path, mPaint);
        }

        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.restore();
    }

    private void touchStart(float x, float y) {
        mPath = new Path();
        FingerPath fp = new FingerPath(currentColor, emboss, blur, strokeWidth, mPath);
        paths.add(fp);

        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touchMove(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);

        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    private void touchUp() {
        mPath.lineTo(mX, mY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchStart(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touchUp();
                invalidate();
                break;
        }

        return true;
    }
}
