package com.uren.catchu.MainPackage.MainFragments.Feed.SubFragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.ApiGatewayFunctions.SearchResultProcess;
import com.uren.catchu.GeneralUtils.ClickableImage.ClickableImageView;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Feed.Adapters.SearchResultAdapter;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.PostHelper;
import com.uren.catchu.MainPackage.MainFragments.Profile.Interfaces.ListItemClickListener;
import com.uren.catchu.MainPackage.MainFragments.Profile.JavaClasses.UserInfoListItem;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;
import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.User;
import catchu.model.UserListResponse;

public class SearchFragment extends BaseFragment
        implements View.OnClickListener, ListItemClickListener {

    View mView;
    SearchResultAdapter searchResultAdapter;
    LinearLayoutManager mLayoutManager;

    String searchText = "";
    String tempSearchText = "";

    @BindView(R.id.search_recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.imgCancel)
    ClickableImageView imgCancel;

    @BindView(R.id.edtSearch)
    EditText edtSearch;

    public static SearchFragment newInstance() {
        Bundle args = new Bundle();
        SearchFragment fragment = new SearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (mView == null) {
            mView = inflater.inflate(R.layout.search_person_fragment, container, false);
            ButterKnife.bind(this, mView);

            initListeners();
            initRecyclerView();
            //getPersonList();
        }

        edtSearch.requestFocus();
        showKeyboard(true);

        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void initListeners() {

        imgCancel.setOnClickListener(this);

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {
                tempSearchText = edtSearch.getText().toString();

                if (tempSearchText.matches("")) {
                    searchText = tempSearchText;
                    return;
                }

                if (!tempSearchText.matches(searchText)) {
                    searchText = tempSearchText;
                    getSearchResult();
                }
            }
        });

    }

    private void initRecyclerView() {
        setLayoutManager();
        setAdapter();
    }

    private void setLayoutManager() {
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
    }

    private void setAdapter() {
        searchResultAdapter = new SearchResultAdapter(getContext(), mFragmentNavigation);
        recyclerView.setAdapter(searchResultAdapter);
        searchResultAdapter.setListItemClickListener(this);
    }

    private void showKeyboard(boolean showKeyboard) {
        if (showKeyboard) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        } else {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(edtSearch.getWindowToken(), 0);
        }
    }


    @Override
    public void onClick(View v) {

        if (v == imgCancel) {
            //((NextActivity) getActivity()).ANIMATION_TAG = ANIMATE_LEFT_TO_RIGHT;
            showKeyboard(false);
            getActivity().onBackPressed();
        }

    }

    public void getSearchResult() {
        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {
                startGetProfileDetail(token);
            }
        });
    }

    private void startGetProfileDetail(String token) {

        String userId = AccountHolderInfo.getUserID();
        String page = "1";
        String perPage = "500";

        SearchResultProcess searchResultProcess = new SearchResultProcess(getActivity(), new OnEventListener<UserListResponse>() {

            @Override
            public void onSuccess(UserListResponse userListResponse) {
                if (userListResponse == null) {
                    CommonUtils.LOG_OK_BUT_NULL("SearchResultProcess");
                } else {
                    CommonUtils.LOG_OK("SearchResultProcess");
                    setUpRecyclerView(userListResponse);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Exception e) {
                CommonUtils.LOG_FAIL("SearchResultProcess", e.toString());
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onTaskContinue() {
                progressBar.setVisibility(View.VISIBLE);
            }
        }, userId, searchText, perPage, page, token);

        searchResultProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private void setUpRecyclerView(UserListResponse userListResponse) {

        for(int i=0; i<userListResponse.getItems().size(); i++){
            Log.i("user ", userListResponse.getItems().get(i).getName());
        }

        searchResultAdapter.updateListItems(userListResponse.getItems());
    }


    @Override
    public void onClick(View view, User user, int clickedPosition) {
        showKeyboard(false);
        UserInfoListItem userInfoListItem = new UserInfoListItem(user);
        userInfoListItem.setAdapter(searchResultAdapter);
        userInfoListItem.setClickedPosition(clickedPosition);
        PostHelper.ProfileClicked.startProcess(getContext(), mFragmentNavigation, userInfoListItem);
    }
}
