package com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uren.catchu.ApiGatewayFunctions.CountryListProcess;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.InfoDialogBoxCallback;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import catchu.model.Country;
import catchu.model.CountryListResponse;

import static com.uren.catchu.Constants.StringConstants.PUTEXTRA_PHONE_NUM;

public class SelectCountryFragment extends Fragment {

    View mView;
    EditText selectCountryEt;
    ListView countryListView;

    public SelectCountryFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (mView == null) {
            mView = inflater.inflate(R.layout.country_select_layout, container, false);
            ButterKnife.bind(this, mView);
        }

        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        addListeners();
        getCountryList();
    }

    private void init() {
        selectCountryEt = mView.findViewById(R.id.selectCountryEt);
        countryListView = mView.findViewById(R.id.countryListView);
    }

    public void addListeners() {
        selectCountryEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    public void getCountryList() {
        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {

                CountryListProcess countryListProcess = new CountryListProcess(new OnEventListener() {
                    @Override
                    public void onSuccess(Object object) {
                        if (object != null)
                            fillCountryList(object);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        DialogBoxUtil.showErrorDialog(getActivity(), getResources().getString(R.string.error) + e.getMessage(), new InfoDialogBoxCallback() {
                            @Override
                            public void okClick() {
                            }
                        });
                    }

                    @Override
                    public void onTaskContinue() {

                    }
                }, AccountHolderInfo.getInstance().getUser().getUserInfo().getUserid(),
                        token);
                countryListProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });
    }

    public void fillCountryList(Object object) {
        CountryListResponse countryListResponse = (CountryListResponse) object;
        List<String> countryList = new ArrayList<String>();

        for (Country country : countryListResponse.getItems()) {
            String countryItem = "";

            if (country.getName() != null && !country.getName().trim().isEmpty() &&
                    country.getDialCode() != null && !country.getDialCode().trim().isEmpty()) {
                countryItem = country.getName() + "(" + country.getDialCode() + ")";
                countryList.add(countryItem);
            }
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                countryList);

        countryListView.setAdapter(arrayAdapter);
    }
}
