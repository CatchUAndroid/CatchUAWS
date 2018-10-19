package com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.uren.catchu.ApiGatewayFunctions.CountryListProcess;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DataModelUtil.UserDataUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.InfoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.PhotoChosenCallback;
import com.uren.catchu.GeneralUtils.FileAdapter;
import com.uren.catchu.GeneralUtils.PhotoSelectUtils;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.Interfaces.ItemClickListener;
import com.uren.catchu.Interfaces.ServiceCompleteCallback;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.JavaClasses.FollowInfoListItem;
import com.uren.catchu.MainPackage.MainFragments.Profile.Utils.UpdateUserProfileProcess;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;
import com.uren.catchu.SharePackage.GalleryPicker.TextEditFragment;
import com.uren.catchu.Singleton.AccountHolderInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.Country;
import catchu.model.CountryListResponse;
import catchu.model.UserProfile;
import catchu.model.UserProfileProperties;

import static android.app.Activity.RESULT_OK;
import static com.uren.catchu.Constants.StringConstants.ANIMATE_DOWN_TO_UP;
import static com.uren.catchu.Constants.StringConstants.ANIMATE_LEFT_TO_RIGHT;
import static com.uren.catchu.Constants.StringConstants.ANIMATE_RIGHT_TO_LEFT;
import static com.uren.catchu.Constants.StringConstants.PUTEXTRA_PHONE_NUM;

@SuppressLint("ValidFragment")
public class PhoneNumEditFragment extends BaseFragment {

    View mView;
    ImageView backImgv;
    ImageView nextImgv;
    TextView toolbarTitleTv;
    TextView countryNameTv;
    TextView countryCodeTv;
    EditText phoneNumEt;
    View counSelectMainLayout;
    RelativeLayout editPhoneMainLayout;
    String selectedCountry = "";
    CountryListResponse countryListResponse;

    EditText selectCountryEt;
    ListView countryListView;

    String phoneNum = "";

    public PhoneNumEditFragment(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        if (mView == null) {
            mView = inflater.inflate(R.layout.edit_phonenum_layout, container, false);
            ButterKnife.bind(this, mView);
            init();
            addListeners();
            checkPhoneNumExistance();
        }

        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void init() {
        backImgv = mView.findViewById(R.id.backImgv);
        toolbarTitleTv = mView.findViewById(R.id.toolbarTitleTv);
        nextImgv = mView.findViewById(R.id.nextImgv);
        countryNameTv = mView.findViewById(R.id.countryNameTv);
        countryCodeTv = mView.findViewById(R.id.countryCodeTv);
        phoneNumEt = mView.findViewById(R.id.phoneNumEt);
        editPhoneMainLayout = mView.findViewById(R.id.editPhoneMainLayout);
        toolbarTitleTv.setText(getResources().getString(R.string.PHONE_NUM));
        countryListResponse = new CountryListResponse();
        countryListResponse.setItems(new ArrayList<Country>());
    }

    public void addListeners() {
        backImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        nextImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        countryNameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCountryFragment();
            }
        });

        countryCodeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCountryFragment();
            }
        });

        phoneNumEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (phoneNum != null) {
                    if (s.toString().trim().equals(phoneNum))
                        nextImgv.setVisibility(View.GONE);
                    else
                        nextImgv.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void checkPhoneNumExistance() {

        if (phoneNum != null && !phoneNum.trim().isEmpty()) {
            phoneNumEt.setText(phoneNum);
        }

        if (AccountHolderInfo.getInstance().getUser() != null &&
                AccountHolderInfo.getInstance().getUser().getUserInfo() != null &&
                AccountHolderInfo.getInstance().getUser().getUserInfo().getPhoneCountry() != null) {

            if (AccountHolderInfo.getInstance().getUser().getUserInfo().getPhoneCountry().getCode() != null &&
                    !AccountHolderInfo.getInstance().getUser().getUserInfo().getPhoneCountry().getCode().trim().isEmpty() &&
                    AccountHolderInfo.getInstance().getUser().getUserInfo().getPhoneCountry().getName() != null &&
                    !AccountHolderInfo.getInstance().getUser().getUserInfo().getPhoneCountry().getName().trim().isEmpty()) {
                countryCodeTv.setText(AccountHolderInfo.getInstance().getUser().getUserInfo().getPhoneCountry().getCode());
                countryNameTv.setText(AccountHolderInfo.getInstance().getUser().getUserInfo().getPhoneCountry().getName());
            } else
                getCountryList();
        } else
            getCountryList();
    }

    public void startCountryFragment() {
        if (mFragmentNavigation != null) {
            mFragmentNavigation.pushFragment(new SelectCountryFragment(new ItemClickListener() {
                @Override
                public void onClick(Object object, int clickedItem) {
                    selectedCountry = (String) object;
                    parseSelectedCountry(selectedCountry);
                }
            }), ANIMATE_DOWN_TO_UP);
        }
    }

    public void parseSelectedCountry(String selectedCountry) {
        String[] parts = selectedCountry.split("\\(");
        countryNameTv.setText(parts[0]);

        String[] parts2 = parts[1].split("\\)");
        countryCodeTv.setText(parts2[0]);
    }

    public void getCountryList() {
        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {

                CountryListProcess countryListProcess = new CountryListProcess(new OnEventListener() {
                    @Override
                    public void onSuccess(Object object) {
                        if (object != null) {
                            countryListResponse = (CountryListResponse) object;

                            String locale = getActivity().getResources().getConfiguration().locale.getCountry();

                            for (Country country : countryListResponse.getItems()) {
                                if (country != null && country.getCode() != null && !country.getCode().trim().isEmpty()) {
                                    if (country.getCode().trim().equals(locale)) {
                                        countryCodeTv.setText(country.getDialCode());
                                        countryNameTv.setText(country.getName());
                                        break;
                                    }
                                }
                            }
                        }
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
}
