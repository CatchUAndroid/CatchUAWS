package com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.uren.catchu.ApiGatewayFunctions.CountryListProcess;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.GeneralUtils.ClickableImage.ClickableImageView;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.Interfaces.InfoDialogBoxCallback;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.Interfaces.ItemClickListener;
import com.uren.catchu.Interfaces.ServiceCompleteCallback;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.JavaClasses.PhoneVerification;
import com.uren.catchu.MainPackage.MainFragments.Profile.Utils.UpdateUserProfileProcess;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.Country;
import catchu.model.CountryListResponse;
import catchu.model.Phone;
import catchu.model.UserProfileProperties;

import static com.uren.catchu.Constants.StringConstants.ANIMATE_DOWN_TO_UP;
import static com.uren.catchu.Constants.StringConstants.ANIMATE_LEFT_TO_RIGHT;

@SuppressLint("ValidFragment")
public class PhoneNumEditFragment extends BaseFragment {

    View mView;

    @BindView(R.id.commonToolbarbackImgv)
    ClickableImageView commonToolbarbackImgv;
    @BindView(R.id.commonToolbarNextImgv)
    ImageView commonToolbarNextImgv;
    @BindView(R.id.commonToolbarTickImgv)
    ImageView commonToolbarTickImgv;
    @BindView(R.id.toolbarTitleTv)
    TextView toolbarTitleTv;
    @BindView(R.id.countryCodeTv)
    TextView countryCodeTv;
    @BindView(R.id.countryDialCodeTv)
    TextView countryDialCodeTv;
    @BindView(R.id.phoneNumEt)
    EditText phoneNumEt;
    @BindView(R.id.editPhoneMainLayout)
    RelativeLayout editPhoneMainLayout;

    CountryListResponse countryListResponse;
    PhoneVerification phoneVerification;
    CompleteCallback completeCallback;
    ProgressDialog mProgressDialog;
    String completePhoneNum;
    Phone selectedPhone;
    Phone phone;

    public PhoneNumEditFragment(Phone phone, CompleteCallback completeCallback) {
        this.phone = phone;
        this.completeCallback = completeCallback;
    }

    @Override
    public void onStart() {
        Objects.requireNonNull(getActivity()).findViewById(R.id.tabMainLayout).setVisibility(View.GONE);
        super.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((NextActivity) Objects.requireNonNull(getActivity())).ANIMATION_TAG = ANIMATE_LEFT_TO_RIGHT;
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_phone_num_edit, container, false);
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
        toolbarTitleTv.setText(getResources().getString(R.string.PHONE_NUM));
        mProgressDialog = new ProgressDialog(getActivity());
        countryListResponse = new CountryListResponse();
        countryListResponse.setItems(new ArrayList<>());
        selectedPhone = new Phone();
    }

    public void addListeners() {
        commonToolbarbackImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });

        commonToolbarNextImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVerificationCode();
            }
        });

        commonToolbarTickImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.hideKeyBoard(Objects.requireNonNull(getContext()));
                clearUserPhoneNum();
            }
        });

        countryCodeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                if (s != null && !s.toString().isEmpty()) {
                    commonToolbarTickImgv.setVisibility(View.GONE);

                    if (phone != null && phone.getPhoneNumber() != null) {
                        if (s.toString().trim().equals(phone.getPhoneNumber().toString()))
                            commonToolbarNextImgv.setVisibility(View.GONE);
                        else
                            commonToolbarNextImgv.setVisibility(View.VISIBLE);
                    } else
                        commonToolbarNextImgv.setVisibility(View.VISIBLE);
                } else if (s != null && s.toString().isEmpty()) {
                    commonToolbarTickImgv.setVisibility(View.VISIBLE);
                    commonToolbarNextImgv.setVisibility(View.GONE);
                }
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

                            String locale = Objects.requireNonNull(getActivity()).getResources().getConfiguration().locale.getCountry();

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

            @Override
            public void onTokenFail(String message) {
            }
        });
    }

    public void setSelectedPhone(Country country) {
        countryDialCodeTv.setText(country.getDialCode());
        countryCodeTv.setText(country.getCode());
        selectedPhone.setCountryCode(country.getCode());
        selectedPhone.setDialCode(country.getDialCode());
    }

    public void clearUserPhoneNum() {
        UserProfileProperties userProfileProperties = AccountHolderInfo.getInstance().getUser().getUserInfo();
        userProfileProperties.setPhone(null);

        new UpdateUserProfileProcess(getActivity(), new ServiceCompleteCallback() {
            @Override
            public void onSuccess() {
                completeCallback.onComplete(" ");
                Objects.requireNonNull(getActivity()).onBackPressed();
            }

            @Override
            public void onFailed(Exception e) {
                DialogBoxUtil.showErrorDialog(getActivity(), e.getMessage(), new InfoDialogBoxCallback() {
                    @Override
                    public void okClick() {

                    }
                });
                e.printStackTrace();
            }
        }, false, userProfileProperties, null);
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
                DialogBoxUtil.showErrorDialog(getActivity(), Objects.requireNonNull(getActivity()).getResources().getString(R.string.error) + e.getMessage(), new InfoDialogBoxCallback() {
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
                    Objects.requireNonNull(getActivity()).onBackPressed();
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
