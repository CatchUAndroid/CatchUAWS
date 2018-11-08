package com.uren.catchu.MainPackage.MainFragments.SearchTab;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uren.catchu.Adapters.SpecialSelectTabAdapter;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.ApiGatewayFunctions.UserDetail;
import com.uren.catchu.GeneralUtils.ApiModelsProcess.AccountHolderFollowProcess;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.InfoDialogBoxCallback;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.SearchTab.SubFragments.GroupFragment;

import com.uren.catchu.Singleton.AccountHolderInfo;

import butterknife.ButterKnife;
import catchu.model.FriendList;
import catchu.model.UserProfile;

import static com.uren.catchu.Constants.StringConstants.propGroups;
import static com.uren.catchu.Constants.StringConstants.propPersons;

public class SearchFragment extends BaseFragment {

    private Context context;

    View view;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    public String selectedProperty;
    static SpecialSelectTabAdapter adapter;

    private final int personFragmentTab = 0;
    private final int groupFragmentTab = 1;

    String userid;
    Toolbar mToolBar;



    private EditText editTextSearch;
    private ImageView imgCancelSearch;
    private String searchText = "";
    private String tempSearchText = "";
    private RelativeLayout rl;
    private RelativeLayout r2;
    private TextView txtAddGroup;
    private Boolean refreshSearch = true;

    public SearchFragment() {

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
