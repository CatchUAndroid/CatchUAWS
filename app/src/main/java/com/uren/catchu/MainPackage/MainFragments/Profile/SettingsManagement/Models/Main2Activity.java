package com.uren.catchu.MainPackage.MainFragments.Profile.SettingsManagement.Models;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.uren.catchu.ModelViews.PaintView;
import com.uren.catchu.R;

public class Main2Activity extends AppCompatActivity {

    PaintView paintView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        paintView = findViewById(R.id.paintView);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        //paintView.init(metrics, null);
        paintView.normal();
    }
}
