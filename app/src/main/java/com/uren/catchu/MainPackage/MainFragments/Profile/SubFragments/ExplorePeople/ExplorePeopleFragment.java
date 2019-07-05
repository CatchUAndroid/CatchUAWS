package com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.ExplorePeople;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.uren.catchu.Adapters.SpecialSelectTabAdapter;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.uren.catchu.Constants.StringConstants.ANIMATE_RIGHT_TO_LEFT;

public class ExplorePeopleFragment extends BaseFragment {

    View view;

    @BindView(R.id.editTextSearch)
    EditText editTextSearch;
    @BindView(R.id.imgCancelSearch)
    ImageView imgCancelSearch;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.searchToolbarBackImgv)
    ImageView searchToolbarBackImgv;
    @BindView(R.id.searchToolbarAddItemImgv)
    ImageView searchToolbarAddItemImgv;

    SpecialSelectTabAdapter adapter;
    FacebookFriendsFragment facebookFriendsFragment;
    ContactsFragment contactsFragment;
    int selectedProperty;

    private static final int TAB_FACEBOOK = 0;
    private static final int TAB_CONTACTS = 1;

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

        ((NextActivity) getActivity()).ANIMATION_TAG = ANIMATE_RIGHT_TO_LEFT;

        if (view == null) {

            view = inflater.inflate(R.layout.fragment_explore_people, container, false);
            ButterKnife.bind(this, view);
            initializeItems();
            addListeners();
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void initializeItems() {
        searchToolbarAddItemImgv.setVisibility(View.GONE);
        setupViewPager();
        tabLayout.setupWithViewPager(viewPager);
        selectedProperty = TAB_FACEBOOK;
    }

    private void setupViewPager() {
        facebookFriendsFragment = new FacebookFriendsFragment(false);
        contactsFragment = new ContactsFragment( false);

        adapter = new SpecialSelectTabAdapter(getChildFragmentManager());
        adapter.addFragment(facebookFriendsFragment, getResources().getString(R.string.FACEBOOK));
        adapter.addFragment(contactsFragment, getResources().getString(R.string.CONTACTS));
        viewPager.setAdapter(adapter);
    }

    private void addListeners() {

        searchToolbarBackImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

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
                            ((ContactsFragment) adapter.getItem(TAB_CONTACTS)).updateAdapter("");
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

        imgCancelSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchTextClear();
                CommonUtils.hideKeyBoard(getContext());
                searchToolbarBackImgv.setVisibility(View.VISIBLE);
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
                if (s != null && s.toString() != null) {
                    if (!s.toString().trim().isEmpty()) {
                        imgCancelSearch.setVisibility(View.VISIBLE);
                        searchToolbarBackImgv.setVisibility(View.GONE);
                    } else {
                        imgCancelSearch.setVisibility(View.GONE);
                        searchToolbarBackImgv.setVisibility(View.VISIBLE);
                    }

                    if (selectedProperty == TAB_FACEBOOK) {
                        ((FacebookFriendsFragment) adapter.getItem(TAB_FACEBOOK)).updateAdapter(s.toString());
                    } else if (selectedProperty == TAB_CONTACTS) {
                        ((ContactsFragment) adapter.getItem(TAB_CONTACTS)).updateAdapter(s.toString());
                    }
                }
            }
        });
    }

    public void searchTextClear() {
        if (editTextSearch != null && editTextSearch.getText() != null)
            editTextSearch.setText("");
    }
}
