package com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
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
import com.google.firebase.auth.PhoneAuthCredential;
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
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.Interfaces.ItemClickListener;
import com.uren.catchu.Interfaces.ServiceCompleteCallback;
import com.uren.catchu.LoginPackage.LoginActivity;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.JavaClasses.FollowInfoListItem;
import com.uren.catchu.MainPackage.MainFragments.Profile.JavaClasses.PhoneVerification;
import com.uren.catchu.MainPackage.MainFragments.Profile.Utils.UpdateUserProfileProcess;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;
import com.uren.catchu.SharePackage.GalleryPicker.TextEditFragment;
import com.uren.catchu.Singleton.AccountHolderInfo;
import com.uren.catchu.UgurDeneme.PhoneAuthActivity;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.Country;
import catchu.model.CountryListResponse;
import catchu.model.Phone;
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
    TextView countryCodeTv;
    TextView countryDialCodeTv;
    EditText phoneNumEt;
    View counSelectMainLayout;
    RelativeLayout editPhoneMainLayout;
    //Country selectedCountry;
    CountryListResponse countryListResponse;
    PhoneVerification phoneVerification;
    CompleteCallback completeCallback;
    ProgressDialog mProgressDialog;
    String completePhoneNum;
    Phone selectedPhone;

    EditText selectCountryEt;
    ListView countryListView;

    //String phoneNum = "";
    Phone phone;

    public PhoneNumEditFragment(Phone phone, CompleteCallback completeCallback) {
        this.phone = phone;
        this.completeCallback = completeCallback;
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
        countryCodeTv = mView.findViewById(R.id.countryCodeTv);
        countryDialCodeTv = mView.findViewById(R.id.countryDialCodeTv);
        phoneNumEt = mView.findViewById(R.id.phoneNumEt);
        editPhoneMainLayout = mView.findViewById(R.id.editPhoneMainLayout);
        toolbarTitleTv.setText(getResources().getString(R.string.PHONE_NUM));
        mProgressDialog = new ProgressDialog(getActivity());
        countryListResponse = new CountryListResponse();
        countryListResponse.setItems(new ArrayList<Country>());
        //selectedCountry = new Country();
        selectedPhone = new Phone();
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
                sendVerificationCode();
            }
        });

        countryCodeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(getActivity(), PhoneAuthActivity.class));
                startCountryFragment();
            }
        });

        countryDialCodeTv.setOnClickListener(new View.OnClickListener() {
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

                if(s != null && !s.toString().isEmpty()){
                    if(phone != null && phone.getPhoneNumber() != null){
                        if (s.toString().trim().equals(phone.getPhoneNumber().toString()))
                            nextImgv.setVisibility(View.GONE);
                        else
                            nextImgv.setVisibility(View.VISIBLE);
                    }else
                        nextImgv.setVisibility(View.VISIBLE);
                }else
                    nextImgv.setVisibility(View.GONE);
            }
        });
    }

    public void checkPhoneNumExistance() {

        if (phone != null && phone.getPhoneNumber() != null && !phone.getPhoneNumber().toString().trim().isEmpty()) {
            phoneNumEt.setText(phone.getPhoneNumber().toString().trim());
        }

        if (AccountHolderInfo.getInstance().getUser() != null &&
                AccountHolderInfo.getInstance().getUser().getUserInfo() != null &&
                AccountHolderInfo.getInstance().getUser().getUserInfo().getPhone() != null) {

            if (AccountHolderInfo.getInstance().getUser().getUserInfo().getPhone().getDialCode() != null &&
                    !AccountHolderInfo.getInstance().getUser().getUserInfo().getPhone().getDialCode().trim().isEmpty() &&
                    AccountHolderInfo.getInstance().getUser().getUserInfo().getPhone().getCountryCode() != null &&
                    !AccountHolderInfo.getInstance().getUser().getUserInfo().getPhone().getCountryCode().trim().isEmpty() &&
                    AccountHolderInfo.getInstance().getUser().getUserInfo().getPhone().getPhoneNumber() != null &&
                    !AccountHolderInfo.getInstance().getUser().getUserInfo().getPhone().getPhoneNumber().toString().trim().isEmpty()) {
                countryDialCodeTv.setText(AccountHolderInfo.getInstance().getUser().getUserInfo().getPhone().getDialCode().trim());
                countryCodeTv.setText(AccountHolderInfo.getInstance().getUser().getUserInfo().getPhone().getCountryCode().trim());
                phoneNumEt.setText(AccountHolderInfo.getInstance().getUser().getUserInfo().getPhone().getPhoneNumber().toString().trim());
                selectedPhone.setDialCode(AccountHolderInfo.getInstance().getUser().getUserInfo().getPhone().getDialCode().trim());
                selectedPhone.setCountryCode(AccountHolderInfo.getInstance().getUser().getUserInfo().getPhone().getCountryCode().trim());
                selectedPhone.setPhoneNumber(AccountHolderInfo.getInstance().getUser().getUserInfo().getPhone().getPhoneNumber());
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
                    Country country = (Country) object;
                    setSelectedPhone(country);
                }
            }), ANIMATE_DOWN_TO_UP);
        }
    }

    public void getCountryList() {
        dialogShow();
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
                                        setSelectedPhone(country);
                                        break;
                                    }
                                }
                            }
                        }
                        dialogDismiss();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        dialogDismiss();
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

    public void setSelectedPhone(Country country){
        countryDialCodeTv.setText(country.getDialCode());
        countryCodeTv.setText(country.getCode());
        selectedPhone.setCountryCode(country.getCode());
        selectedPhone.setDialCode(country.getDialCode());
    }

    public void sendVerificationCode() {
        dialogShow();
        completePhoneNum = countryDialCodeTv.getText().toString().trim() + phoneNumEt.getText().toString().trim();

        phoneVerification = new PhoneVerification(getActivity(), completePhoneNum, new CompleteCallback() {
            @Override
            public void onComplete(Object object) {
                dialogDismiss();
                if (object != null) {
                    selectedPhone.setPhoneNumber(new BigDecimal(phoneNumEt.getText().toString().trim()));
                    startVerifyPhoneNumFragment();
                }
            }

            @Override
            public void onFailed(Exception e) {
                dialogDismiss();
                DialogBoxUtil.showErrorDialog(getActivity(), getActivity().getResources().getString(R.string.error) + e.getMessage(), new InfoDialogBoxCallback() {
                    @Override
                    public void okClick() {

                    }
                });
            }
        });
        phoneVerification.startPhoneNumberVerification();
    }

    public void startVerifyPhoneNumFragment() {
        if (mFragmentNavigation != null) {
            mFragmentNavigation.pushFragment(new VerifyPhoneNumberFragment(selectedPhone, phoneVerification, new CompleteCallback() {
                @Override
                public void onComplete(Object object) {
                    completeCallback.onComplete(completePhoneNum);
                    getActivity().onBackPressed();
                }

                @Override
                public void onFailed(Exception e) {

                }
            }), ANIMATE_DOWN_TO_UP);
        }
    }

    public void dialogShow() {
        if (!mProgressDialog.isShowing()) mProgressDialog.show();
    }

    public void dialogDismiss() {
        if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
    }
}
