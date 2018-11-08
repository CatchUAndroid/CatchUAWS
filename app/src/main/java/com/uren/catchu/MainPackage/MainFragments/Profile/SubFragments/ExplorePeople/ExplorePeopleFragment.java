package com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.ExplorePeople;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.uren.catchu.Adapters.SpecialSelectTabAdapter;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.InfoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.ProgressDialogUtil.ProgressDialogUtil;
import com.uren.catchu.Interfaces.OnLoadedListener;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.R;

import butterknife.ButterKnife;

public class ExplorePeopleFragment extends BaseFragment {

    Context context;
    View view;
    ViewPager viewPager;
    TabLayout tabLayout;
    int selectedProperty;
    SpecialSelectTabAdapter adapter;
    Toolbar mToolBar;
    ProgressDialogUtil progressDialogUtil;

    FacebookFriendsFragment facebookFriendsFragment;
    ContactFriendsFragment contactFriendsFragment;

    private EditText editTextSearch;
    private ImageView imgCancelSearch;

    private static final int TAB_FACEBOOK = 0;
    private static final int TAB_CONTACTS = 1;

    boolean facebookFragLoaded = false;
    boolean contactFragLoaded = false;
    Handler handler = new Handler();

    private static final int FRAGMENTS_LOADED_DELAY = 500;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        if (view == null) {

            view = inflater.inflate(R.layout.fragment_search, container, false);
            ButterKnife.bind(this, view);

            context = getActivity();
            initializeItems();
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void initializeItems() {
        progressDialogUtil = new ProgressDialogUtil(getActivity(), null, false);
        progressDialogUtil.dialogShow();
        handler.postDelayed(runnable, FRAGMENTS_LOADED_DELAY);
        overwriteToolbar();

        viewPager = view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        selectedProperty = TAB_FACEBOOK;
        addListeners();
    }

    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (facebookFragLoaded && contactFragLoaded) {
                progressDialogUtil.dialogDismiss();
                handler.removeCallbacks(runnable);
            } else
                handler.postDelayed(this, FRAGMENTS_LOADED_DELAY);
        }
    };

    private void setupViewPager(final ViewPager viewPager) {
        facebookFriendsFragment = new FacebookFriendsFragment(new OnLoadedListener() {
            @Override
            public void onLoaded() {
                facebookFragLoaded = true;
            }

            @Override
            public void onError(String message) {
                progressDialogUtil.dialogDismiss();
                DialogBoxUtil.showErrorDialog(getActivity(), getActivity().getResources().getString(R.string.error) + message, new InfoDialogBoxCallback() {
                    @Override
                    public void okClick() {
                    }
                });
            }
        }, false);

        contactFriendsFragment = new ContactFriendsFragment(new OnLoadedListener() {
            @Override
            public void onLoaded() {
                contactFragLoaded = true;
            }

            @Override
            public void onError(String message) {
                progressDialogUtil.dialogDismiss();
                DialogBoxUtil.showErrorDialog(getActivity(), getActivity().getResources().getString(R.string.error) + message, new InfoDialogBoxCallback() {
                    @Override
                    public void okClick() {
                    }
                });
            }
        }, false);

        adapter = new SpecialSelectTabAdapter(getChildFragmentManager());
        adapter.addFragment(facebookFriendsFragment, getResources().getString(R.string.FACEBOOK));
        adapter.addFragment(contactFriendsFragment, getResources().getString(R.string.CONTACTS));
        viewPager.setAdapter(adapter);
    }

    private void addListeners() {

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                switch (tab.getPosition()) {

                    case TAB_FACEBOOK:
                        selectedProperty = TAB_FACEBOOK;
                        searchTextClear();
                        if (adapter != null && adapter.getItem(TAB_FACEBOOK) != null)
                            ((FacebookFriendsFragment) adapter.getItem(TAB_FACEBOOK)).updateAdapter("");
                        break;

                    case TAB_CONTACTS:
                        selectedProperty = TAB_CONTACTS;
                        searchTextClear();
                        if (adapter != null && adapter.getItem(TAB_CONTACTS) != null)
                            ((ContactFriendsFragment) adapter.getItem(TAB_CONTACTS)).updateAdapter("");
                        break;

                    default:
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public void searchTextClear() {
        if (editTextSearch != null && editTextSearch.getText() != null)
            editTextSearch.getText().clear();
    }

    private void overwriteToolbar() {
        mToolBar = view.findViewById(R.id.toolbar);
        editTextSearch = view.findViewById(R.id.editTextSearch);
        imgCancelSearch = view.findViewById(R.id.imgCancelSearch);

        imgCancelSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchTextClear();
                imgCancelSearch.setVisibility(View.GONE);
            }
        });

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().trim().isEmpty())
                    imgCancelSearch.setVisibility(View.VISIBLE);
                else
                    imgCancelSearch.setVisibility(View.GONE);

                if (selectedProperty == TAB_FACEBOOK) {
                    ((FacebookFriendsFragment) adapter.getItem(TAB_FACEBOOK)).updateAdapter(s.toString());
                } else if (selectedProperty == TAB_CONTACTS) {
                    ((ContactFriendsFragment) adapter.getItem(TAB_CONTACTS)).updateAdapter(s.toString());
                }
            }
        });
    }
}
