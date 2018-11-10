package com.uren.catchu.MainPackage.MainFragments.Feed;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dinuscxj.refresh.RecyclerRefreshLayout;
import com.dinuscxj.refresh.RecyclerRefreshLayout.OnRefreshListener;
import com.uren.catchu.Adapters.LocationTrackerAdapter;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.ApiGatewayFunctions.PostListResponseProcess;
import com.uren.catchu.GeneralUtils.ClickableImage.ClickableImageView;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DataModelUtil.UserDataUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.InfoDialogBoxCallback;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Feed.Adapters.FeedAdapter;
import com.uren.catchu.MainPackage.MainFragments.Feed.Adapters.FeedPagerAdapter;
import com.uren.catchu.MainPackage.MainFragments.Feed.Interfaces.FeedRefreshCallback;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.FeedContextMenuManager;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.FeedItemAnimator;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.PostHelper;
import com.uren.catchu.MainPackage.MainFragments.Feed.SubFragments.FilterFragment;
import com.uren.catchu.MainPackage.MainFragments.Feed.SubFragments.SearchFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.Adapters.NewsPagerAdapter;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.NewsList;
import com.uren.catchu.Permissions.PermissionModule;

import com.uren.catchu.R;
import com.uren.catchu.SharePackage.GalleryPicker.Interfaces.LocationCallback;
import com.uren.catchu.Singleton.AccountHolderInfo;
import com.uren.catchu.Singleton.Interfaces.AccountHolderInfoCallback;
import com.uren.catchu._Libraries.PulseView.PulsatorLayout;
import com.uren.catchu._Libraries.VideoPlay.CustomRecyclerView;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.Media;
import catchu.model.Post;
import catchu.model.PostListResponse;
import catchu.model.User;
import catchu.model.UserProfile;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.uren.catchu.Constants.NumericConstants.DEFAULT_FEED_PAGE_COUNT;
import static com.uren.catchu.Constants.NumericConstants.DEFAULT_FEED_PERPAGE_COUNT;
import static com.uren.catchu.Constants.NumericConstants.FILTERED_FEED_RADIUS;
import static com.uren.catchu.Constants.StringConstants.ANIMATE_RIGHT_TO_LEFT;
import static com.uren.catchu.Constants.StringConstants.AWS_EMPTY;
import static com.uren.catchu.Constants.StringConstants.FEED_TYPE_PUBLIC;
import static com.uren.catchu.Constants.StringConstants.IMAGE_TYPE;
import static com.uren.catchu.Constants.StringConstants.VIDEO_TYPE;


public class FeedFragment extends BaseFragment implements View.OnClickListener {

    View mView;
    FeedAdapter feedAdapter;
    LinearLayoutManager mLayoutManager;

    @BindView(R.id.imgFilter)
    ClickableImageView imgFilter;
    @BindView(R.id.llFilter)
    LinearLayout llFilter;
    @BindView(R.id.llSearch)
    LinearLayout llSearch;

    @BindView(R.id.htab_viewpager)
    ViewPager vpNews;
    @BindView(R.id.htab_tabs)
    TabLayout tabs;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_feed, container, false);
            ButterKnife.bind(this, mView);

            CommonUtils.LOG_NEREDEYIZ("FeedFragment");
            setUpPager();
        }

        return mView;
    }

    private void setUpPager() {

        FeedPagerAdapter adp = new FeedPagerAdapter(getFragmentManager());
        FeedPublicFragment f1 = new FeedPublicFragment();
        FeedCatchedFragment f2 = new FeedCatchedFragment();

        adp.addFrag(f1, "Public");
        adp.addFrag(f2, "Catched");

        tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        vpNews.setAdapter(adp);
        vpNews.setOffscreenPageLimit(12);
        tabs.setupWithViewPager(vpNews);

    }


    @Override
    public void onClick(View v) {

    }
}