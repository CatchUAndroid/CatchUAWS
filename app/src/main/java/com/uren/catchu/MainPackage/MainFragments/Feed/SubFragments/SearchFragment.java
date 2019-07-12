package com.uren.catchu.MainPackage.MainFragments.Feed.SubFragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.ApiGatewayFunctions.SearchResultProcess;
import com.uren.catchu.GeneralUtils.ClickableImage.ClickableImageView;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Feed.Adapters.SearchResultAdapter;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.FeedItemAnimator;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.PostHelper;
import com.uren.catchu.MainPackage.MainFragments.Profile.Interfaces.ListItemClickListener;
import com.uren.catchu.MainPackage.MainFragments.Profile.JavaClasses.UserInfoListItem;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.User;
import catchu.model.UserListResponse;

import static com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread;

public class SearchFragment extends BaseFragment
        implements View.OnClickListener, ListItemClickListener {

    View mView;
    SearchResultAdapter searchResultAdapter;
    LinearLayoutManager mLayoutManager;

    String searchText = "";
    String tempSearchText = "";

    @BindView(R.id.search_recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.imgCancel)
    ClickableImageView imgCancel;

    @BindView(R.id.edtSearch)
    EditText edtSearch;
    @BindView(R.id.txtResult)
    TextView txtResult;

    private Timer timer;

    public SearchFragment() {
    }

    public static SearchFragment newInstance() {
        Bundle args = new Bundle();
        SearchFragment fragment = new SearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        Objects.requireNonNull(getActivity()).findViewById(R.id.tabMainLayout).setVisibility(View.GONE);
        super.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (mView == null) {
            mView = inflater.inflate(R.layout.search_person_fragment, container, false);
            ButterKnife.bind(this, mView);

            showResultView(true, getString(R.string.searchUser));
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
        ((NextActivity) Objects.requireNonNull(getActivity())).ANIMATION_TAG = null;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initListeners() {

        imgCancel.setOnClickListener(this);
        //recyclerView.setOnClickListener(this);
        edtSearch.setOnClickListener(this);

        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                edtSearch.clearFocus();
                showKeyboard(false);
                return false;
            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // user is typing: reset already started timer (if existing)
                if (timer != null) {
                    timer.cancel();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

                // user typed: start the timer
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {

                        // do your actual work here
                        tempSearchText = edtSearch.getText().toString();

                        if (tempSearchText.matches("")) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    searchText = tempSearchText;
                                    searchResultAdapter.addProgressLoading();
                                    searchResultAdapter.clearList();
                                    searchResultAdapter.removeProgressLoading();
                                    showResultView(true, getString(R.string.searchUser));
                                }
                            });

                            return;
                        }

                        if (!tempSearchText.matches(searchText)) {
                            searchText = tempSearchText;
                            getSearchResult();
                        }
                    }
                }, 800);

            }
        });

    }

    private void initRecyclerView() {
        setLayoutManager();
        setAdapter();
    }

    private void setLayoutManager() {
        mLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new FeedItemAnimator());
    }

    private void setAdapter() {
        searchResultAdapter = new SearchResultAdapter(Objects.requireNonNull(getContext()), mFragmentNavigation);
        recyclerView.setAdapter(searchResultAdapter);
        searchResultAdapter.setListItemClickListener(this);
    }

    private void showKeyboard(boolean showKeyboard) {

        if (showKeyboard) {
            InputMethodManager imm = (InputMethodManager) Objects.requireNonNull(getContext()).getSystemService(Context.INPUT_METHOD_SERVICE);
            Objects.requireNonNull(imm).toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        } else {
            InputMethodManager imm = (InputMethodManager) Objects.requireNonNull(getContext()).getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            Objects.requireNonNull(imm).hideSoftInputFromWindow(edtSearch.getWindowToken(), 0);
            edtSearch.setFocusable(false);
            edtSearch.setFocusableInTouchMode(true);
        }
    }

    private void showResultView(boolean isShowResult, @Nullable String msg) {
        if (isShowResult) {
            if (msg != null) {
                txtResult.setText(msg);
                txtResult.setVisibility(View.VISIBLE);
            }
        } else {
            txtResult.setText("");
            txtResult.setVisibility(View.GONE);
        }
    }


    @Override
    public void onClick(View v) {

        if (v == imgCancel) {
            showKeyboard(false);
            Objects.requireNonNull(getActivity()).onBackPressed();
        }

        if (v == recyclerView) {
            showKeyboard(false);
        }

        if (v == edtSearch) {
            edtSearch.requestFocus();
            showKeyboard(true);
        }

    }

    public void getSearchResult() {
        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {
                startGetProfileDetail(token);
            }

            @Override
            public void onTokenFail(String message) {
            }
        });
    }

    private void startGetProfileDetail(String token) {

        String userId = AccountHolderInfo.getUserID();
        String page = "1";
        String perPage = "5000";

        SearchResultProcess searchResultProcess = new SearchResultProcess(getActivity(), new OnEventListener<UserListResponse>() {

            @Override
            public void onSuccess(UserListResponse userListResponse) {
                if (userListResponse != null) {
                    setUpRecyclerView(userListResponse);
                }
            }

            @Override
            public void onFailure(Exception e) {
                searchResultAdapter.removeProgressLoading();
            }

            @Override
            public void onTaskContinue() {
                searchResultAdapter.addProgressLoading();
            }
        }, userId, searchText, perPage, page, token);

        searchResultProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void setUpRecyclerView(UserListResponse userListResponse) {

        searchResultAdapter.removeProgressLoading();
        searchResultAdapter.updateListItems(userListResponse.getItems());

        if (userListResponse.getItems().size() > 0) {
            showResultView(false, null);
        } else {
            showResultView(true, getString(R.string.THERE_IS_NO_SEARCH_RESULT));
        }
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
