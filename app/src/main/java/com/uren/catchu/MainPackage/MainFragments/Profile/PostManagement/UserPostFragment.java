package com.uren.catchu.MainPackage.MainFragments.Profile.PostManagement;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.PostManagement.Adapters.UserPostPagerAdapter;
import com.uren.catchu.MainPackage.MainFragments.Profile.PostManagement.JavaClasses.SingletonPostList;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.uren.catchu.Constants.StringConstants.ANIMATE_LEFT_TO_RIGHT;
import static com.uren.catchu.Constants.StringConstants.PROFILE_POST_TYPE_CAUGHT;
import static com.uren.catchu.Constants.StringConstants.PROFILE_POST_TYPE_MY_POSTS;


public class UserPostFragment extends BaseFragment
        implements View.OnClickListener {

    View mView;
    String catchType, toolbarTitle;
    UserPostPagerAdapter userPostPagerAdapter;
    ImageView imgViewGrid, imgViewList;
    TextView txtViewGrid, txtViewList;
    TabItem tabGridView, tabListView;


    @BindView(R.id.commonToolbarbackImgv)
    ImageView commonToolbarbackImgv;
    @BindView(R.id.toolbarTitleTv)
    TextView toolbarTitleTv;
    @BindView(R.id.tablayout)
    TabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewPager;

    public static UserPostFragment newInstance(String catchType) {
        Bundle args = new Bundle();
        args.putString("catchType", catchType);
        UserPostFragment fragment = new UserPostFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        CommonUtils.LOG_NEREDEYIZ("FeedFragment");

        if (mView == null) {
            mView = inflater.inflate(R.layout.profile_post_fragment, container, false);
            ButterKnife.bind(this, mView);
            getItemsFromBundle();

            if(catchType != null){
                initItems();
                initListeners();
                setUpPager();
            }

        }


        return mView;
    }

    private void getItemsFromBundle() {
        Bundle args = getArguments();
        if (args != null) {
            catchType = (String) args.getString("catchType");

            if(catchType.equals(PROFILE_POST_TYPE_MY_POSTS)){
                toolbarTitle = getContext().getResources().getString(R.string.myPosts);
            }else if(catchType.equals(PROFILE_POST_TYPE_CAUGHT)){
                toolbarTitle = getContext().getResources().getString(R.string.catchedPosts);
            }else{
                toolbarTitle = "";
            }

        }
    }

    private void initItems() {

        toolbarTitleTv.setText(toolbarTitle);
        tabGridView = (TabItem) mView.findViewById(R.id.tabGridView);
        tabListView = (TabItem) mView.findViewById(R.id.tabListView);

        SingletonPostList.reset();
    }

    private void initListeners() {
        commonToolbarbackImgv.setOnClickListener(this);
    }

    private void setUpPager() {

        userPostPagerAdapter = new UserPostPagerAdapter(getFragmentManager(), tabLayout.getTabCount(), catchType);
        viewPager.setAdapter(userPostPagerAdapter);

        setCustomTab();
        setTabListener();

    }

    private void setCustomTab() {

        View headerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.profile_post_custom_tab, null, false);

        LinearLayout linearLayout1 = (LinearLayout) headerView.findViewById(R.id.ll);
        LinearLayout linearLayout2 = (LinearLayout) headerView.findViewById(R.id.ll2);
        imgViewGrid = (ImageView) headerView.findViewById(R.id.imgViewGrid);
        imgViewList = (ImageView) headerView.findViewById(R.id.imgViewList);
        txtViewGrid = (TextView) headerView.findViewById(R.id.txtViewGrid);
        txtViewList = (TextView) headerView.findViewById(R.id.txtViewList);

        //intial values
        imgViewGrid.setColorFilter(ContextCompat.getColor(getContext(), R.color.Red), android.graphics.PorterDuff.Mode.SRC_IN);
        imgViewList.setColorFilter(ContextCompat.getColor(getContext(), R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);

        txtViewGrid.setTextColor(ContextCompat.getColor(getContext(), R.color.Red));
        txtViewList.setTextColor(ContextCompat.getColor(getContext(), R.color.black));

        tabLayout.getTabAt(0).setCustomView(linearLayout1);
        tabLayout.getTabAt(1).setCustomView(linearLayout2);

    }

    private void setTabListener() {

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 0) {
                    imgViewGrid.setColorFilter(ContextCompat.getColor(getContext(), R.color.Red), android.graphics.PorterDuff.Mode.SRC_IN);
                    imgViewList.setColorFilter(ContextCompat.getColor(getContext(), R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);

                    txtViewGrid.setTextColor(ContextCompat.getColor(getContext(), R.color.Red));
                    txtViewList.setTextColor(ContextCompat.getColor(getContext(), R.color.black));

                } else if (tab.getPosition() == 1) {
                    imgViewGrid.setColorFilter(ContextCompat.getColor(getContext(), R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
                    imgViewList.setColorFilter(ContextCompat.getColor(getContext(), R.color.Red), android.graphics.PorterDuff.Mode.SRC_IN);

                    txtViewGrid.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                    txtViewList.setTextColor(ContextCompat.getColor(getContext(), R.color.Red));

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


    @Override
    public void onClick(View view) {

        if (view == commonToolbarbackImgv) {
            ((NextActivity) getActivity()).ANIMATION_TAG = ANIMATE_LEFT_TO_RIGHT;
            getActivity().onBackPressed();
        }

    }
}