package com.uren.catchu.MainPackage.MainFragments.SearchTab.SubFragments;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.uren.catchu.MainPackage.MainFragments.SearchTab.Adapters.UserDetailAdapter;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.ApiGatewayFunctions.SearchResultProcess;
import com.uren.catchu.FragmentControllers.FragNavController;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.Interfaces.ListItemClickListener;
import com.uren.catchu.MainPackage.MainFragments.Profile.JavaClasses.FollowInfoListItem;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.OtherProfileFragment;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.FollowInfoResultArrayItem;
import catchu.model.SearchResult;

import static com.uren.catchu.Constants.StringConstants.ANIMATE_RIGHT_TO_LEFT;

@SuppressLint("ValidFragment")
public class PersonFragment extends BaseFragment {

    RecyclerView personRecyclerView;
    View mView;
    SearchResult searchResult;
    UserDetailAdapter userDetailAdapter;

    @BindView(R.id.progressBar)
    public ProgressBar progressBar;

    LinearLayoutManager linearLayoutManager;
    private static final String SEARCH_TEXT = "SEARCH_TEXT";

    public PersonFragment() {
    }

    public static PersonFragment newInstance(String searchText) {
        Bundle args = new Bundle();
        args.putSerializable(SEARCH_TEXT, searchText);
        PersonFragment fragment = new PersonFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_special_select, container, false);
        ButterKnife.bind(this, mView);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        personRecyclerView = mView.findViewById(R.id.specialRecyclerView);
        getSearchResult(getArguments().getString(SEARCH_TEXT));
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void getSearchResult(final String searchText) {
        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {
                startGetProfileDetail(searchText, token);
            }
        });
    }

    private void startGetProfileDetail(final String searchText, String token) {

        SearchResultProcess searchResultProcess = new SearchResultProcess(getActivity(), new OnEventListener<SearchResult>() {

            @Override
            public void onSuccess(SearchResult object) {
                if (progressBar != null)
                    progressBar.setVisibility(View.GONE);
                searchResult = object;

                userDetailAdapter = new UserDetailAdapter(getActivity(), searchText, searchResult, new ListItemClickListener() {
                    @Override
                    public void onClick(View view, FollowInfoResultArrayItem rowItem, int clickedPosition) {
                        startFollowingInfoProcess(rowItem, clickedPosition);
                    }
                });
                personRecyclerView.setAdapter(userDetailAdapter);
                linearLayoutManager = new LinearLayoutManager(getActivity());
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                personRecyclerView.setLayoutManager(linearLayoutManager);
            }

            @Override
            public void onFailure(Exception e) {
                if (progressBar != null)
                    progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onTaskContinue() {
                if (progressBar != null)
                    progressBar.setVisibility(View.VISIBLE);
            }
        }, AccountHolderInfo.getInstance().getUser().getUserInfo().getUserid(), searchText, token);

        searchResultProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void startFollowingInfoProcess(FollowInfoResultArrayItem rowItem, int clickedPosition) {

        if (!rowItem.getUserid().equals(AccountHolderInfo.getInstance().getUser().getUserInfo().getUserid())) {
            if (mFragmentNavigation != null) {
                FollowInfoListItem followInfoListItem = new FollowInfoListItem(rowItem);
                followInfoListItem.setAdapter(userDetailAdapter);
                followInfoListItem.setClickedPosition(clickedPosition);
                mFragmentNavigation.pushFragment(OtherProfileFragment.newInstance(followInfoListItem), ANIMATE_RIGHT_TO_LEFT);
            }
        } else {
            NextActivity.switchAndUpdateTabSelection(FragNavController.TAB5);
        }
    }
}
