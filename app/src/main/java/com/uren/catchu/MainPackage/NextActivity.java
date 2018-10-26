package com.uren.catchu.MainPackage;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;
import com.uren.catchu.FragmentControllers.FragNavController;
import com.uren.catchu.FragmentControllers.FragNavTransactionOptions;
import com.uren.catchu.FragmentControllers.FragmentHistory;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.MainPackage.MainFragments.AddPinFragment;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Feed.FeedFragment;
import com.uren.catchu.MainPackage.MainFragments.NewsFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.ProfileFragment;
import com.uren.catchu.MainPackage.MainFragments.SearchTab.SearchFragment;
import com.uren.catchu.R;
import com.uren.catchu.SharePackage.ShareFragment;
import com.uren.catchu.Singleton.AccountHolderInfo;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.uren.catchu.Constants.StringConstants.ANIMATE_DOWN_TO_UP;
import static com.uren.catchu.Constants.StringConstants.ANIMATE_LEFT_TO_RIGHT;
import static com.uren.catchu.Constants.StringConstants.ANIMATE_RIGHT_TO_LEFT;
import static com.uren.catchu.Constants.StringConstants.ANIMATE_UP_TO_DOWN;

public class NextActivity extends AppCompatActivity implements
        BaseFragment.FragmentNavigation,
        FragNavController.TransactionListener,
        FragNavController.RootFragmentListener {

    private Context context;

    private int onPauseCount = 0;
    private boolean onPausedInd = false;
    public static Activity thisActivity;
    public static int prevPosition = 0;

    @BindView(R.id.content_frame)
    FrameLayout contentFrame;

    public String ANIMATION_TAG;
    public FragNavTransactionOptions transactionOptions;

    private int[] mTabIconsSelected = {
            R.drawable.tab_home,
            R.drawable.tab_search,
            R.drawable.tab_share,
            R.drawable.tab_news,
            R.drawable.tab_profile};

    public static String[] TABS;
    public static TabLayout bottomTabLayout;

    private static FragNavController mNavController;

    private FragmentHistory fragmentHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);
        thisActivity = this;

        initValues();

        fragmentHistory = new FragmentHistory();

        mNavController = FragNavController.newBuilder(savedInstanceState, getSupportFragmentManager(), R.id.content_frame)
                .transactionListener(this)
                .rootFragmentListener(this, TABS.length)
                .build();


        switchTab(0);

        bottomTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                fragmentHistory.push(tab.getPosition());
                switchTab(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                mNavController.clearStack();
                switchTab(tab.getPosition());
            }
        });

        fillAccountHolder();
    }

    public void fillAccountHolder() {
        AccountHolderInfo.setInstance(null, getApplicationContext());
        AccountHolderInfo.getInstance();
    }

    private void initValues() {
        onPausedInd = true;
        context = this;
        ButterKnife.bind(this);
        bottomTabLayout = findViewById(R.id.bottom_tab_layout);
        TABS = getResources().getStringArray(R.array.tab_name);

        //setStatusBarTransparent();
        initTab();
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

    private void initTab() {
        if (bottomTabLayout != null) {
            for (int i = 0; i < TABS.length; i++) {
                bottomTabLayout.addTab(bottomTabLayout.newTab());
                TabLayout.Tab tab = bottomTabLayout.getTabAt(i);
                if (tab != null)
                    tab.setCustomView(getTabView(i));
            }
        }
    }

    private View getTabView(int position) {
        View view = LayoutInflater.from(NextActivity.this).inflate(R.layout.tab_item_bottom, null);
        ImageView icon = (ImageView) view.findViewById(R.id.tab_icon);
        icon.setImageDrawable(CommonUtils.setDrawableSelector(NextActivity.this, mTabIconsSelected[position], mTabIconsSelected[position]));
        return view;
    }

    public void onStart() {
        super.onStart();
        onPausedInd = false;
        Log.i("onPausedInd", "  >>onStart onPausedInd:" + onPausedInd);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public static void switchTab(int position) {
        mNavController.switchTab(position);

        if(position != mNavController.TAB3){
            prevPosition = position;
        }
    }

    @Override
    protected void onResume() {

        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();

        onPauseCount = onPauseCount + 1;

        if (onPauseCount > 1) {
            onPausedInd = true;
            Log.i("onPausedInd", "  >>onPause  onPausedInd:" + onPausedInd);
        }
    }

    @Override
    public void onBackPressed() {
        if (!mNavController.isRootFragment()) {
            setTransactionOption();
            mNavController.popFragment(transactionOptions);
        } else {

            if (fragmentHistory.isEmpty()) {
                super.onBackPressed();
            } else {

                if (fragmentHistory.getStackSize() > 1) {

                    int position = fragmentHistory.popPrevious();
                    switchAndUpdateTabSelection(position);
                } else {
                    switchAndUpdateTabSelection(0);
                    fragmentHistory.emptyStack();
                }
            }
        }
    }

    public static void switchAndUpdateTabSelection(int position){
        switchTab(position);
        updateTabSelection(position);
    }

    private void setTransactionOption() {
        if (transactionOptions == null) {
            transactionOptions = FragNavTransactionOptions.newBuilder().build();
        }

        switch (ANIMATION_TAG) {
            case ANIMATE_RIGHT_TO_LEFT:
                transactionOptions.enterAnimation = R.anim.slide_from_right;
                transactionOptions.exitAnimation = R.anim.slide_to_left;
                transactionOptions.popEnterAnimation = R.anim.slide_from_left;
                transactionOptions.popExitAnimation = R.anim.slide_to_right;
                break;
            case ANIMATE_LEFT_TO_RIGHT:
                transactionOptions.enterAnimation = R.anim.slide_from_left;
                transactionOptions.exitAnimation = R.anim.slide_to_right;
                transactionOptions.popEnterAnimation = R.anim.slide_from_right;
                transactionOptions.popExitAnimation = R.anim.slide_to_left;
                break;
            case ANIMATE_DOWN_TO_UP:
                transactionOptions.enterAnimation = R.anim.slide_from_down;
                transactionOptions.exitAnimation = R.anim.slide_to_up;
                transactionOptions.popEnterAnimation = R.anim.slide_from_up;
                transactionOptions.popExitAnimation = R.anim.slide_to_down;
                break;
            case ANIMATE_UP_TO_DOWN:
                transactionOptions.enterAnimation = R.anim.slide_from_up;
                transactionOptions.exitAnimation = R.anim.slide_to_down;
                transactionOptions.popEnterAnimation = R.anim.slide_from_down;
                transactionOptions.popExitAnimation = R.anim.slide_to_up;
                break;
            default:
                transactionOptions = null;
        }
    }

    public static void updateTabSelection(int currentTab) {

        for (int i = 0; i < TABS.length; i++) {
            TabLayout.Tab selectedTab = bottomTabLayout.getTabAt(i);
            if (currentTab != i) {
                selectedTab.getCustomView().setSelected(false);
            } else {
                selectedTab.getCustomView().setSelected(true);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mNavController != null) {
            mNavController.onSaveInstanceState(outState);
        }
    }

    @Override
    public void pushFragment(Fragment fragment) {
        if (mNavController != null) {
            mNavController.pushFragment(fragment);
        }
    }

    @Override
    public void pushFragment(Fragment fragment, String animationTag) {

        ANIMATION_TAG = animationTag;
        setTransactionOption();

        if (mNavController != null) {
            mNavController.pushFragment(fragment, transactionOptions);
        }

    }

    @Override
    public void onTabTransaction(Fragment fragment, int index) {
        // If we have a backstack, show the back button
        if (getSupportActionBar() != null && mNavController != null) {

            //updateToolbar();
        }
    }

    private void updateToolbar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(!mNavController.isRootFragment());
        getSupportActionBar().setDisplayShowHomeEnabled(!mNavController.isRootFragment());
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_18dp);
    }

    @Override
    public Fragment getRootFragment(int index) {
        switch (index) {

            case FragNavController.TAB1:
                return new FeedFragment();
            case FragNavController.TAB2:
                return new SearchFragment();
            case FragNavController.TAB3:
                return new AddPinFragment();
            case FragNavController.TAB4:
                return new NewsFragment();
            case FragNavController.TAB5:
                return new ProfileFragment();
        }
        throw new IllegalStateException("Need to send an index that we know");
    }

/*


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.logOut) {

            //AWS sig-out
            IdentityManager.getDefaultIdentityManager().signOut();

            finish();
            startActivity(new Intent(this, MainActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }
*/

    @Override
    public void onFragmentTransaction(Fragment fragment, FragNavController.TransactionType transactionType) {
        //do fragmentty stuff. Maybe change title, I'm not going to tell you how to live your life
        // If we have a backstack, show the back button
        if (getSupportActionBar() != null && mNavController != null) {

            //updateToolbar();
        }
    }

    public void updateToolbarTitle(String title) {

        getSupportActionBar().setTitle(title);
    }


}