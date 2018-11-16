package com.uren.catchu.MainPackage.MainFragments.Profile.PostManagement;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.ApiGatewayFunctions.PostLikeListProcess;
import com.uren.catchu.GeneralUtils.ClickableImage.ClickableImageView;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Feed.Adapters.PersonListAdapter;
import com.uren.catchu.MainPackage.MainFragments.Feed.Interfaces.PersonListItemClickListener;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.PostHelper;
import com.uren.catchu.MainPackage.MainFragments.Feed.SubFragments.FilterFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.JavaClasses.UserInfoListItem;
import com.uren.catchu.MainPackage.MainFragments.Profile.PostManagement.Adapters.ProfilePostPagerAdapter;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.User;
import catchu.model.UserListResponse;

import static com.uren.catchu.Constants.StringConstants.ANIMATE_LEFT_TO_RIGHT;
import static com.uren.catchu.Constants.StringConstants.AWS_EMPTY;
import static com.uren.catchu.Constants.StringConstants.COMING_FOR_LIKE_LIST;


public class ProfilePostFragment extends BaseFragment
        implements View.OnClickListener {

    View mView;
    ProfilePostPagerAdapter profilePostPagerAdapter;
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

    public static ProfilePostFragment newInstance() {
        Bundle args = new Bundle();
        ProfilePostFragment fragment = new ProfilePostFragment();
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

            initItems();
            setUpPager();
            initListeners();
        }


        return mView;
    }

    private void initItems() {

        toolbarTitleTv.setText("My posts");
        tabGridView = (TabItem) mView.findViewById(R.id.tabGridView);
        tabListView = (TabItem) mView.findViewById(R.id.tabListView);
    }

    private void initListeners() {
        commonToolbarbackImgv.setOnClickListener(this);
    }

    private void setUpPager() {

        profilePostPagerAdapter = new ProfilePostPagerAdapter(getFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(profilePostPagerAdapter);

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