package com.uren.catchu.MainPackage.MainFragments.Feed.SubActivities;

import android.media.Image;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.PostItem;
import com.uren.catchu.MainPackage.MainFragments.Feed.SubFragments.ImageFragment;
import com.uren.catchu.MainPackage.MainFragments.Feed.SubFragments.VideoFragment;
import com.uren.catchu.R;

import butterknife.BindView;
import catchu.model.Media;

import static com.uren.catchu.Constants.StringConstants.IMAGE_TYPE;
import static com.uren.catchu.Constants.StringConstants.VIDEO_TYPE;

public class PostItemDetailActivity extends AppCompatActivity {

    @BindView(R.id.mainContainer)
    FrameLayout mainContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_item_detail);

        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
        decideNextFragment();

    }

    private void decideNextFragment() {

        Media media = PostItem.getInstance().getMedia();

        if (media.getType().equals(IMAGE_TYPE)) {
            showFragment(new ImageFragment());
        } else if (media.getType().equals(VIDEO_TYPE)) {
            showFragment(new VideoFragment());
        } else {
            Log.e("info", "unknown media type detected");
        }
    }

    public void showFragment(Fragment fragment) {

        String TAG = fragment.getClass().getSimpleName();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.mainContainer, fragment, TAG);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commitAllowingStateLoss();
    }

    public void backstackFragment() {
        Log.d("Stack count", getSupportFragmentManager().getBackStackEntryCount() + "");

        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            finish();
        }
        getSupportFragmentManager().popBackStack();
        removeCurrentFragment();
    }

    public void removeCurrentFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        Fragment currentFrag = getSupportFragmentManager()
                .findFragmentById(R.id.mainContainer);

        if (currentFrag != null) {
            transaction.remove(currentFrag);
        }
        transaction.commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            finish();
        }
    }


}
