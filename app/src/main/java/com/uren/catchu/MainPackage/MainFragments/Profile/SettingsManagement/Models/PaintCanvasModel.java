package com.uren.catchu.MainPackage.MainFragments.Profile.SettingsManagement.Models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class PaintCanvasModel extends View{
    Context context;
    Bitmap bitmap;

    public PaintCanvasModel(Context context) {
        super(context);
     /*   this.bitmap = bitmap;
        this.context = context;*/
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint pBackground = new Paint();
        pBackground.setColor(Color.WHITE);
        canvas.drawRect(0, 0, 512, 512, pBackground);
        Paint pText = new Paint();
        pText.setColor(Color.BLACK);
        pText.setTextSize(20);
        canvas.drawText("Sample Text", 100, 100, pText);



     /*   Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        canvas.drawCircle(50, 50, 10, paint);
        imageView.setImageBitmap(bitmap);*/
    }
}
