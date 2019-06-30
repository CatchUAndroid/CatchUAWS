package com.uren.catchu;

import android.content.Intent;
import android.os.Handler;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;
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
