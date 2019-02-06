package com.uren.catchu;

import android.content.Intent;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.uren.catchu.GeneralUtils.CommonUtils;

public class SplashScreenActivity extends AppCompatActivity {

    /*
    * Su anda kullanilmiyor. Android Manifest dosyasından launcher olarak duzenlendigi zaman kullanima hazirdir.
    * */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        startMainActivity();
    }

    private void startMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }


}
