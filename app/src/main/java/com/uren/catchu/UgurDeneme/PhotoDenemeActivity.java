package com.uren.catchu.UgurDeneme;

import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.R;

public class PhotoDenemeActivity extends AppCompatActivity {

    String url = "https://s3.amazonaws.com/catchumobileappbucket/originals/1bc4d075-fb59-4b91-af58-ed50d483fdb7.jpg";
    ImageView imageView;
    Button button;
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_deneme);
        imageView = findViewById(R.id.imageView);
        button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (i == 0) {
                    imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    CommonUtils.showToast(PhotoDenemeActivity.this, "ImageView.ScaleType.FIT_CENTER");
                }

                if (i == 1) {
                    imageView.setScaleType(ImageView.ScaleType.CENTER);
                    CommonUtils.showToast(PhotoDenemeActivity.this, "ImageView.ScaleType.CENTER");
                }

                if (i == 2) {
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    CommonUtils.showToast(PhotoDenemeActivity.this, "ImageView.ScaleType.FIT_XY");
                }

                if (i == 3) {
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    CommonUtils.showToast(PhotoDenemeActivity.this, "ImageView.ScaleType.CENTER_CROP");
                }

                if (i == 4) {
                    imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    CommonUtils.showToast(PhotoDenemeActivity.this, "ImageView.ScaleType.CENTER_INSIDE");
                }

                if (i == 5) {
                    imageView.setScaleType(ImageView.ScaleType.FIT_END);
                    CommonUtils.showToast(PhotoDenemeActivity.this, "ImageView.ScaleType.FIT_END");
                }

                if (i == 6) {
                    imageView.setScaleType(ImageView.ScaleType.FIT_START);
                    CommonUtils.showToast(PhotoDenemeActivity.this, "ImageView.ScaleType.FIT_START");
                }

                if (i == 7) {
                    imageView.setScaleType(ImageView.ScaleType.MATRIX);
                    CommonUtils.showToast(PhotoDenemeActivity.this, "ImageView.ScaleType.MATRIX");
                }

                Glide.with(PhotoDenemeActivity.this)
                        .load(url)
                        .apply(RequestOptions.centerInsideTransform())
                        .into(imageView);

                i++;
            }
        });
    }
}
