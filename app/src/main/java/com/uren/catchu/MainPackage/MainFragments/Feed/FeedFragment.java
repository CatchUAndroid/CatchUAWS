package com.uren.catchu.MainPackage.MainFragments.Feed;


import android.content.Context;
import android.content.Intent;
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
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.ToxicBakery.viewpager.transforms.RotateUpTransformer;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.uren.catchu.GeneralUtils.ClickableImage.ClickableImageView;
import com.uren.catchu.GeneralUtils.CommonUtils;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

import static com.uren.catchu.Constants.NumericConstants.REQUEST_CODE_START_MESSAGE_LIST_ACTIVITY;
import static com.uren.catchu.Constants.StringConstants.ANIMATE_RIGHT_TO_LEFT;


public class FeedFragment extends BaseFragment implements View.OnClickListener {

    View mView;
    FeedPagerAdapter feedPagerAdapter;
    ImageView imgFeedPublic, imgFeedCatched;
    TextView txtFeedTypePublic, txtFeedTypeCatched;
    TabItem tabChats, tabCalls;

    @BindView(R.id.imgFilter)
    ClickableImageView imgFilter;
    @BindView(R.id.llFilter)
    RelativeLayout llFilter;
    @BindView(R.id.llSearch)
    LinearLayout llSearch;
    @BindView(R.id.toolbarLayout)
    Toolbar toolbar;
    @BindView(R.id.tablayout)
    TabLayout tabLayout;
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

    private static final int TAB_PUBLIC = 0;
    private static final int TAB_CATCHED = 1;

    int selectedTabPosition = TAB_PUBLIC;

    int unreadMessageCount = 0;
    private int progressStatus = 0;
    private Handler handler = new Handler();

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

            initItems();
            setUpPager();
            initListeners();
            getUserUnreadMsgCount();
        }
        return mView;
    }


    @Override
    public void onStart() {
        getActivity().findViewById(R.id.tabMainLayout).setVisibility(View.VISIBLE);
        super.onStart();
    }

    private void initItems() {
        tabChats = mView.findViewById(R.id.tabChats);
        tabCalls = mView.findViewById(R.id.tabCalls);
    }

    private void initListeners() {
        imgFilter.setOnClickListener(this);
        llSearch.setOnClickListener(this);
        myMessagesImgv.setOnClickListener(this);
    }

    private void setUpPager() {

        feedPagerAdapter = new FeedPagerAdapter(getFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(feedPagerAdapter);
        viewPager.setPageTransformer(true, new RotateUpTransformer());

        setCustomTab();
        setTabListener();

    }

    private void setCustomTab() {

        View headerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.feed_custom_tab, null, false);

        LinearLayout linearLayout1 = headerView.findViewById(R.id.ll);
        LinearLayout linearLayout2 = headerView.findViewById(R.id.ll2);
        imgFeedPublic = headerView.findViewById(R.id.imgFeedPublic);
        imgFeedCatched = headerView.findViewById(R.id.imgFeedCatched);
        txtFeedTypePublic = headerView.findViewById(R.id.txtFeedTypePublic);
        txtFeedTypeCatched = headerView.findViewById(R.id.txtFeedTypeCatched);

        //intial values
        imgFeedPublic.setColorFilter(ContextCompat.getColor(getContext(), R.color.oceanBlue), android.graphics.PorterDuff.Mode.SRC_IN);
        imgFeedCatched.setColorFilter(ContextCompat.getColor(getContext(), R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);

        txtFeedTypePublic.setTextColor(ContextCompat.getColor(getContext(), R.color.oceanBlue));
        txtFeedTypeCatched.setTextColor(ContextCompat.getColor(getContext(), R.color.black));

        tabLayout.getTabAt(0).setCustomView(linearLayout1);
        tabLayout.getTabAt(1).setCustomView(linearLayout2);

    }

    private void setTabListener() {

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                selectedTabPosition = tab.getPosition();

                if (tab.getPosition() == TAB_PUBLIC) {
                    imgFeedPublic.setColorFilter(ContextCompat.getColor(getContext(), R.color.oceanBlue), android.graphics.PorterDuff.Mode.SRC_IN);
                    imgFeedCatched.setColorFilter(ContextCompat.getColor(getContext(), R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);

                    txtFeedTypePublic.setTextColor(ContextCompat.getColor(getContext(), R.color.oceanBlue));
                    txtFeedTypeCatched.setTextColor(ContextCompat.getColor(getContext(), R.color.black));

                    /*
                    toolbar.setBackgroundColor(ContextCompat.getColor(getContext(),
                            R.color.colorAccent));
                    tabLayout.setBackgroundColor(ContextCompat.getColor(getContext(),
                            R.color.colorAccent));

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getContext(),
                                R.color.colorAccent));
                    }
                    */
                } else if (tab.getPosition() == TAB_CATCHED) {
                    imgFeedPublic.setColorFilter(ContextCompat.getColor(getContext(), R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
                    //imgFeedCatched.setColorFilter(ContextCompat.getColor(getContext(), R.color.DarkOrange), android.graphics.PorterDuff.Mode.SRC_IN);
                    imgFeedCatched.clearColorFilter();

                    txtFeedTypePublic.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                    txtFeedTypeCatched.setTextColor(ContextCompat.getColor(getContext(), R.color.oceanBlue));

                } else {

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
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