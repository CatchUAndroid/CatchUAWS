package com.uren.catchu.MainPackage.MainFragments.Feed;


import android.content.Context;
import android.os.Bundle;

import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;

import android.support.v7.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uren.catchu.GeneralUtils.ClickableImage.ClickableImageView;
import com.uren.catchu.GeneralUtils.CommonUtils;

import com.uren.catchu.MainPackage.MainFragments.BaseFragment;

import com.uren.catchu.MainPackage.MainFragments.Feed.Adapters.FeedPagerAdapter;

import com.uren.catchu.MainPackage.MainFragments.Feed.SubFragments.FilterFragment;
import com.uren.catchu.MainPackage.MainFragments.Feed.SubFragments.SearchFragment;

import com.uren.catchu.R;

import butterknife.BindView;
import butterknife.ButterKnife;

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
    LinearLayout llFilter;
    @BindView(R.id.llSearch)
    LinearLayout llSearch;
    @BindView(R.id.toolbarLayout)
    Toolbar toolbar;
    @BindView(R.id.tablayout)
    TabLayout tabLayout;
    @BindView(R.id.htab_viewpager)
    ViewPager viewPager;

    private static final int TAB_PUBLIC = 0;
    private static final int TAB_CATCHED = 1;

    int selectedTabPosition = TAB_PUBLIC;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        CommonUtils.LOG_NEREDEYIZ("FeedFragment");

        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_feed, container, false);
            ButterKnife.bind(this, mView);

            initItems();
            setUpPager();
            initListeners();
        }


        return mView;
    }

    private void initItems() {
        tabChats = (TabItem) mView.findViewById(R.id.tabChats);
        tabCalls = (TabItem) mView.findViewById(R.id.tabCalls);
    }

    private void initListeners() {
        imgFilter.setOnClickListener(this);
        llSearch.setOnClickListener(this);
    }

    private void setUpPager() {

        feedPagerAdapter = new FeedPagerAdapter(getFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(feedPagerAdapter);

        setCustomTab();
        setTabListener();

    }

    private void setCustomTab() {

        View headerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.feed_custom_tab, null, false);

        LinearLayout linearLayout1 = (LinearLayout) headerView.findViewById(R.id.ll);
        LinearLayout linearLayout2 = (LinearLayout) headerView.findViewById(R.id.ll2);
        imgFeedPublic = (ImageView) headerView.findViewById(R.id.imgFeedPublic);
        imgFeedCatched = (ImageView) headerView.findViewById(R.id.imgFeedCatched);
        txtFeedTypePublic = (TextView) headerView.findViewById(R.id.txtFeedTypePublic);
        txtFeedTypeCatched = (TextView) headerView.findViewById(R.id.txtFeedTypeCatched);

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

    public void setSelectedTabPosition(int selectedTabPosition) {
        this.selectedTabPosition = selectedTabPosition;
    }

    @Override
    public void onClick(View view) {

        if (view == imgFilter) {
            mFragmentNavigation.pushFragment(FilterFragment.newInstance(), ANIMATE_RIGHT_TO_LEFT);
        }

        if (view == llSearch) {
            mFragmentNavigation.pushFragment(SearchFragment.newInstance(), "");
        }

    }
}