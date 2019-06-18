package com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.uren.catchu.ApiGatewayFunctions.FollowInfoProcess;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;

import com.uren.catchu.GeneralUtils.ClickableImage.ClickableImageView;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.Interfaces.ReturnCallback;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.Interfaces.ListItemClickListener;
import com.uren.catchu.MainPackage.MainFragments.Profile.JavaClasses.UserInfoListItem;
import com.uren.catchu.MainPackage.MainFragments.Profile.OtherProfile.OtherProfileFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.ProfileFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.Adapters.FollowingAdapter;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.FollowInfoListResponse;
import catchu.model.User;

import static com.uren.catchu.Constants.NumericConstants.DEFAULT_GET_FOLLOWER_PAGE_COUNT;
import static com.uren.catchu.Constants.NumericConstants.DEFAULT_GET_FOLLOWER_PERPAGE_COUNT;
import static com.uren.catchu.Constants.StringConstants.ANIMATE_LEFT_TO_RIGHT;
import static com.uren.catchu.Constants.StringConstants.ANIMATE_RIGHT_TO_LEFT;
import static com.uren.catchu.Constants.StringConstants.FOLLOW_STATUS_FOLLOWING;
import static com.uren.catchu.Constants.StringConstants.GET_USER_FOLLOWINGS;


@SuppressLint("ValidFragment")
public class FollowingFragment extends BaseFragment {

    View mView;

    @BindView(R.id.following_recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.commonToolbarbackImgv)
    ClickableImageView commonToolbarbackImgv;
    @BindView(R.id.toolbarTitleTv)
    TextView toolbarTitleTv;

    //Search layout variables
    @BindView(R.id.searchEdittext)
    EditText searchEdittext;
    @BindView(R.id.searchCancelImgv)
    ImageView searchCancelImgv;
    @BindView(R.id.searchResultTv)
    TextView searchResultTv;

    private LinearLayoutManager mLayoutManager;
    private FollowingAdapter followingAdapter;
    private String requestedUserId;
    int perPage, page;
    private int pastVisibleItems, visibleItemCount, totalItemCount;
    private boolean loading = true;
    private String toolbarTitle;

    private static final int CODE_FIRST_LOAD = 0;
    private static final int CODE_MORE_LOAD = 1;
    private int loadCode = CODE_FIRST_LOAD;

    public FollowingFragment(String requestedUserId, String toolbarTitle) {
        this.requestedUserId = requestedUserId;
        this.toolbarTitle = toolbarTitle;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        if (mView == null) {
            mView = inflater.inflate(R.layout.profile_subfragment_following, container, false);
            ButterKnife.bind(this, mView);
            init();
            setPaginationValues();
            setListeners();
            initRecyclerView();
            getFollowingList();
        }
        return mView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((NextActivity) getActivity()).ANIMATION_TAG = ANIMATE_LEFT_TO_RIGHT;
    }

    private void init() {
        toolbarTitleTv.setText(toolbarTitle + " " + getContext().getResources().getString(R.string.followings));
        searchEdittext.setHint(getContext().getResources().getString(R.string.SEARCH_FOLLOWINGS));
        searchResultTv.setText(getContext().getResources().getString(R.string.USER_NOT_FOUND));
    }

    private void setListeners() {
        commonToolbarbackImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        searchCancelImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEdittext.setText("");
                searchCancelImgv.setVisibility(View.GONE);
            }
        });

        searchEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && s.toString() != null) {
                    if (!s.toString().trim().isEmpty()) {
                        searchCancelImgv.setVisibility(View.VISIBLE);
                    } else {
                        searchCancelImgv.setVisibility(View.GONE);
                    }

                    if (followingAdapter != null)
                        followingAdapter.updateAdapter(s.toString(), new ReturnCallback() {
                            @Override
                            public void onReturn(Object object) {
                                int itemSize = (int) object;

                                if (itemSize == 0)
                                    searchResultTv.setVisibility(View.VISIBLE);
                                else
                                    searchResultTv.setVisibility(View.GONE);
                            }
                        });
                } else
                    searchCancelImgv.setVisibility(View.GONE);
            }
        });
    }

    private void setPaginationValues() {
        perPage = DEFAULT_GET_FOLLOWER_PERPAGE_COUNT;
        page = DEFAULT_GET_FOLLOWER_PAGE_COUNT;
    }

    private void initRecyclerView() {
        setLayoutManager();
        setAdapter();
        setRecyclerViewScroll();
    }

    private void setLayoutManager() {
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
    }

    private void setRecyclerViewScroll() {

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            loading = false;
                            page++;
                            followingAdapter.addProgressLoading();
                            loadCode = CODE_MORE_LOAD;
                            getFollowingList();
                        }
                    }
                }
            }
        });
    }

    private void setUpRecyclerView(FollowInfoListResponse followInfoListResponse) {
        loading = true;

        if (page != 1)
            followingAdapter.removeProgressLoading();

        followingAdapter.addAll(followInfoListResponse.getItems());
    }

    private void setAdapter() {
        followingAdapter = new FollowingAdapter(getContext());
        recyclerView.setAdapter(followingAdapter);

        followingAdapter.setListItemClickListener(new ListItemClickListener() {
            @Override
            public void onClick(View view, User user, int clickedPosition) {
                startFollowingInfoProcess(user, clickedPosition);
            }
        });
    }

    private void getFollowingList() {

        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {
                startGetFollowingList(token);
            }

            @Override
            public void onTokenFail(String message) {
            }
        });
    }

    private void startGetFollowingList(String token) {
        String userId = AccountHolderInfo.getUserID();
        String requestType = GET_USER_FOLLOWINGS;
        String requestedUserId = this.requestedUserId;

        FollowInfoProcess followInfoProcess = new FollowInfoProcess(new OnEventListener<FollowInfoListResponse>() {
            @Override
            public void onSuccess(FollowInfoListResponse followInfoListResponse) {

                if (followInfoListResponse != null)
                    setUpRecyclerView(followInfoListResponse);

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Exception e) {
                CommonUtils.LOG_FAIL("FollowInfoProcess", e.toString());
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onTaskContinue() {
                if (loadCode == CODE_FIRST_LOAD)
                    progressBar.setVisibility(View.VISIBLE);
            }
        }, userId, requestedUserId, requestType, perPage, page, token);

        followInfoProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private void startFollowingInfoProcess(User user, int clickedPosition) {

        if (mFragmentNavigation != null && user != null) {
            if (user.getUserid() != null && !user.getUserid().trim().isEmpty()) {
                if (user.getUserid().equals(AccountHolderInfo.getUserID()))
                    mFragmentNavigation.pushFragment(new ProfileFragment(false), ANIMATE_RIGHT_TO_LEFT);
                else if (followingAdapter != null) {
                    UserInfoListItem userInfoListItem = new UserInfoListItem(user);
                    userInfoListItem.setAdapter(followingAdapter);
                    userInfoListItem.setClickedPosition(clickedPosition);
                    mFragmentNavigation.pushFragment(new OtherProfileFragment(userInfoListItem), ANIMATE_RIGHT_TO_LEFT);
                }
            }
        }
    }

}
