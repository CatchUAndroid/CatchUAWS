package com.uren.catchu.MainPackage.MainFragments.Profile.PostManagement;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.ToxicBakery.viewpager.transforms.RotateUpTransformer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.PostManagement.Adapters.UserPostPagerAdapter;
import com.uren.catchu.MainPackage.MainFragments.Profile.PostManagement.JavaClasses.SingletonPostList;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import devlight.io.library.ntb.NavigationTabBar;

import static com.uren.catchu.Constants.NumericConstants.USER_POST_VIEW_TYPE_GRID;
import static com.uren.catchu.Constants.NumericConstants.USER_POST_VIEW_TYPE_LIST;
import static com.uren.catchu.Constants.StringConstants.ANIMATE_LEFT_TO_RIGHT;


public class UserPostFragment extends BaseFragment
        implements View.OnClickListener {

    View mView;
    String catchType, targetUid, toolbarTitle;
    UserPostPagerAdapter userPostPagerAdapter;
    int selectedTabPosition = 0;

    @BindView(R.id.commonToolbarbackImgv)
    ImageView commonToolbarbackImgv;
    @BindView(R.id.toolbarTitleTv)
    TextView toolbarTitleTv;
    @BindView(R.id.viewpager)
    ViewPager viewPager;

    FloatingActionButton fabScrollUp;

    public UserPostFragment(String catchType, String targetUid, String toolbarTitle){
        this.catchType = catchType;
        this.targetUid = targetUid;
        this.toolbarTitle = toolbarTitle;
    }

    @Override
    public void onStart() {
        getActivity().findViewById(R.id.tabMainLayout).setVisibility(View.GONE);
        super.onStart();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.profile_post_fragment, container, false);
            ButterKnife.bind(this, mView);

            if (catchType != null) {
                initItems();
                initListeners();
            }
        }
        return mView;
    }

    private void initItems() {
        final NavigationTabBar navigationTabBar = mView.findViewById(R.id.ntb_horizontal);
        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_apps_white, null),
                        Color.parseColor("#d1395c"))
                        .title(getContext().getResources().getString(R.string.feedViewTypeGrid))
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_list_white, null),
                        Color.parseColor("#FF861F"))
                        .title(getContext().getResources().getString(R.string.feedViewTypeList))
                        .build()
        );

        navigationTabBar.setModels(models);

        setUpPager();

        navigationTabBar.setViewPager(viewPager, 0);
        navigationTabBar.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {
                viewPager.setCurrentItem(position);
                if (position == 0) {
                    selectedTabPosition = USER_POST_VIEW_TYPE_GRID;
                } else if (position == 1) {
                    selectedTabPosition = USER_POST_VIEW_TYPE_LIST;
                } else {

                }
            }

            @Override
            public void onPageScrollStateChanged(final int state) {

            }
        });

        toolbarTitleTv.setText(toolbarTitle);
        fabScrollUp = mView.findViewById(R.id.fabScrollUp);
        SingletonPostList.reset();
    }

    private void initListeners() {
        commonToolbarbackImgv.setOnClickListener(this);
        fabScrollUp.setOnClickListener(this);
    }

    private void setUpPager() {

        SingletonPostList.getInstance().clearPostList(); // clear singleton post list

        userPostPagerAdapter = new UserPostPagerAdapter(getFragmentManager(), 2, catchType, targetUid);
        viewPager.setAdapter(userPostPagerAdapter);
        viewPager.setPageTransformer(true, new RotateUpTransformer());
    }

    @Override
    public void onClick(View view) {

        if (view == commonToolbarbackImgv) {
            ((NextActivity) getActivity()).ANIMATION_TAG = ANIMATE_LEFT_TO_RIGHT;
            getActivity().onBackPressed();
        }

        if(view == fabScrollUp){

            if(selectedTabPosition == USER_POST_VIEW_TYPE_GRID){
                //UserPostGridViewFragment.customGridLayoutManager.smoothScrollToPosition(UserPostGridViewFragment.gridRecyclerView, null, 0);
            }

            if(selectedTabPosition == USER_POST_VIEW_TYPE_LIST){
                //UserPostListViewFragment.customLinearLayoutManager.scrollToPositionWithOffset(0,);
                //UserPostListViewFragment.customLinearLayoutManager.smoothScrollToPosition(UserPostListViewFragment.listRecyclerView, null, 0);
            }
        }
    }

}