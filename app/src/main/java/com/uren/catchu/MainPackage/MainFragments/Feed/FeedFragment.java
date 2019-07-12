package com.uren.catchu.MainPackage.MainFragments.Feed;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.ToxicBakery.viewpager.transforms.RotateUpTransformer;
import com.uren.catchu.GeneralUtils.ClickableImage.ClickableImageView;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Feed.Adapters.FeedPagerAdapter;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.PostHelper;
import com.uren.catchu.MainPackage.MainFragments.Feed.SubFragments.FilterFragment;
import com.uren.catchu.MainPackage.MainFragments.Feed.SubFragments.SearchFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Activities.MessageListActivity;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.Interfaces.UnreadMessageCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.JavaClasses.MessageGetProcess;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import devlight.io.library.ntb.NavigationTabBar;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

import static com.uren.catchu.Constants.NumericConstants.REQUEST_CODE_START_MESSAGE_LIST_ACTIVITY;
import static com.uren.catchu.Constants.StringConstants.ANIMATE_RIGHT_TO_LEFT;


public class FeedFragment extends BaseFragment implements View.OnClickListener {

    View mView;
    FeedPagerAdapter feedPagerAdapter;

    @BindView(R.id.imgFilter)
    ClickableImageView imgFilter;
    @BindView(R.id.llFilter)
    RelativeLayout llFilter;
    @BindView(R.id.llSearch)
    LinearLayout llSearch;
    @BindView(R.id.toolbarLayout)
    Toolbar toolbar;
    @BindView(R.id.htab_viewpager)
    ViewPager viewPager;
    @BindView(R.id.unreadMsgCntTv)
    TextView unreadMsgCntTv;
    @BindView(R.id.myMessagesImgv)
    ClickableImageView myMessagesImgv;

    @BindView(R.id.llSharing)
    LinearLayout llSharing;
    @BindView(R.id.smoothProgressBar)
    SmoothProgressBar smoothProgressBar;
    @BindView(R.id.ntb_horizontal)
    NavigationTabBar navigationTabBar;

    private static final int TAB_PUBLIC = 0;
    private static final int TAB_CATCHED = 1;

    int selectedTabPosition = TAB_PUBLIC;

    int unreadMessageCount = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_feed, container, false);
            PostHelper.InitFeed.setFeedFragment(this);
            ButterKnife.bind(this, mView);
            initNavigationBar();
            setUpPager();
            initListeners();
            getUserUnreadMsgCount();
        }
        return mView;
    }


    @Override
    public void onStart() {
        Objects.requireNonNull(getActivity()).findViewById(R.id.tabMainLayout).setVisibility(View.VISIBLE);
        super.onStart();
    }

    private void initNavigationBar(){
        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.mipmap.icon_world, null),
                        Color.parseColor("#d1395c"))
                        .title(Objects.requireNonNull(getContext()).getResources().getString(R.string.feedTypePublic))
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.mipmap.app_notif_icon, null),
                        Color.parseColor("#FF861F"))
                        .title(getContext().getResources().getString(R.string.feedTypeCatched))
                        .build()
        );

        navigationTabBar.setModels(models);
    }

    private void initListeners() {
        imgFilter.setOnClickListener(this);
        llSearch.setOnClickListener(this);
        myMessagesImgv.setOnClickListener(this);
    }

    private void setUpPager() {

        feedPagerAdapter = new FeedPagerAdapter(getFragmentManager(), 2);
        viewPager.setAdapter(feedPagerAdapter);
        viewPager.setPageTransformer(true, new RotateUpTransformer());
        navigationTabBar.setViewPager(viewPager, 0);
        setTabListener();
    }

    private void setTabListener() {

        navigationTabBar.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {
                //viewPager.setCurrentItem(position);
                selectedTabPosition = position;
            }

            @Override
            public void onPageSelected(final int position) {

            }

            @Override
            public void onPageScrollStateChanged(final int state) {

            }
        });
    }

    public int getSelectedTabPosition() {
        return selectedTabPosition;
    }

    public void getUserUnreadMsgCount() {

        MessageGetProcess.getUnreadMessageCount(new UnreadMessageCallback() {
            @Override
            public void onReturn(int listSize) {
                unreadMessageCount = listSize;

                if (unreadMsgCntTv.getVisibility() == View.GONE && unreadMessageCount > 0)
                    unreadMsgCntTv.setVisibility(View.VISIBLE);

                unreadMsgCntTv.setText(Integer.toString(unreadMessageCount));
            }
        });
    }

    @Override
    public void onClick(View view) {

        if (view == imgFilter) {
            mFragmentNavigation.pushFragment(FilterFragment.newInstance(), ANIMATE_RIGHT_TO_LEFT);
        }

        if (view == llSearch) {
            mFragmentNavigation.pushFragment(SearchFragment.newInstance(), "");
        }

        if (view == myMessagesImgv) {
            startMessageListActivity();
        }
    }

    private void startMessageListActivity() {
        final Intent intent = new Intent(getContext(), MessageListActivity.class);

        if (AccountHolderInfo.getUserID() != null && !AccountHolderInfo.getUserID().isEmpty()) {
            startActivityForResult(intent, REQUEST_CODE_START_MESSAGE_LIST_ACTIVITY);
        } else {
            AccountHolderInfo.getInstance();
            AccountHolderInfo.setAccountHolderInfoCallback(userProfile -> {
                startActivityForResult(intent, REQUEST_CODE_START_MESSAGE_LIST_ACTIVITY);
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_START_MESSAGE_LIST_ACTIVITY) {
            unreadMsgCntTv.setText(Integer.toString(0));
            unreadMsgCntTv.setVisibility(View.GONE);
        }
    }

    public void startProgressBar() {
        llSharing.setVisibility(View.VISIBLE);
        smoothProgressBar.progressiveStart();
    }

    public void stopProgressBar() {
        llSharing.setVisibility(View.GONE);
        smoothProgressBar.progressiveStop();
    }
}