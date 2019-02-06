package com.uren.catchu;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.dagang.library.GradientButton;
import com.uren.catchu.Adapters.LocationTrackerAdapter;
import com.uren.catchu.MainPackage.MainFragments.Share.Interfaces.LocationCallback;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.uren.catchu.Constants.NumericConstants.REQUEST_CODE_ENABLE_LOCATION;

public class InfoActivity extends AppCompatActivity
        implements View.OnClickListener {

    @BindView(R.id.btnSettings)
    GradientButton btnSettings;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        ButterKnife.bind(this);

        setStatusBarTransparent();
        setupWindowAnimations();

        initVariables();
    }

    private void initVariables() {

        btnSettings.getButton().setOnClickListener(this);
    }

    private void setupWindowAnimations() {

        //int type = getIntent().getExtras().getInt("EXTRA_TYPE");
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.slide_from_bottom);
        getWindow().setEnterTransition(transition);

    }

    private void setStatusBarTransparent() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Android 5.0
            int visibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Android 6.0
                // visibility |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
            getWindow().getDecorView().setSystemUiVisibility(visibility);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // Android 4.4
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

    }


    @Override
    public void onClick(View view) {

        if (view == btnSettings.getButton()) {
            this.startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQUEST_CODE_ENABLE_LOCATION);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        LocationTrackerAdapter locationTrackObj = new LocationTrackerAdapter(this, new LocationCallback() {
            @Override
            public void onLocationChanged(Location location) {
            }
        });

        if (locationTrackObj.canGetLocation()) {
            progressBar.setVisibility(View.VISIBLE);
            btnSettings.getButton().setClickable(false);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //PostHelper.FeedRefresh.getInstance().feedRefreshStart();
                    finishAfterTransition();
                }
            }, 1000);
        } else {
            progressBar.setVisibility(View.GONE);
        }

    }
}
