package com.uren.catchu.MainPackage.MainFragments.SearchTab;

import android.os.Bundle;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;

public class SearchFragment extends BaseFragment {

    public SearchFragment(){

    }

    public static SearchFragment newInstance() {
        Bundle args = new Bundle();
        SearchFragment fragment = new SearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


}
