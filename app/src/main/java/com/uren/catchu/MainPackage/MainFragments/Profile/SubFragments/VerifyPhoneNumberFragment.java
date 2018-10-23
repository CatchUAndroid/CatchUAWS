package com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments;

import android.annotation.SuppressLint;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.PhoneAuthCredential;
import com.uren.catchu.ApiGatewayFunctions.CountryListProcess;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.InfoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.Interfaces.ItemClickListener;
import com.uren.catchu.Interfaces.ServiceCompleteCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.JavaClasses.PhoneVerification;
import com.uren.catchu.MainPackage.MainFragments.Profile.Utils.UpdateUserProfileProcess;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import catchu.model.Country;
import catchu.model.CountryListResponse;
import catchu.model.UserProfileProperties;

import static com.uren.catchu.Constants.NumericConstants.VERIFY_PHONE_NUM_DURATION;

@SuppressLint("ValidFragment")
public class VerifyPhoneNumberFragment extends Fragment {

    View mView;
    String phoneNum;
    TextView phoneNumberTv;
    Button sendCodeAgainBtn;
    Button changePhoneBtn;
    EditText verifyCodeEt;
    ImageView backImgv;
    ImageView nextImgv;
    TextView toolbarTitleTv;
    TextView warningMessageTv;
    TextView remainingTimeTv;
    GradientDrawable buttonShape;
    PhoneVerification phoneVerification;
    CompleteCallback completeCallback;
    Country myCountry;

    public VerifyPhoneNumberFragment(Country myCountry, String phoneNum, PhoneVerification phoneVerification, CompleteCallback completeCallback) {
        this.phoneNum = phoneNum;
        this.myCountry = myCountry;
        this.phoneVerification = phoneVerification;
        this.completeCallback = completeCallback;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (mView == null) {
            mView = inflater.inflate(R.layout.verify_phone_num_layout, container, false);
            ButterKnife.bind(this, mView);
            init();
            setButtonShapes();
            addListeners();
        }
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void init() {
        phoneNumberTv = mView.findViewById(R.id.phoneNumberTv);
        remainingTimeTv = mView.findViewById(R.id.remainingTimeTv);
        sendCodeAgainBtn = mView.findViewById(R.id.sendCodeAgainBtn);
        changePhoneBtn = mView.findViewById(R.id.changePhoneBtn);
        verifyCodeEt = mView.findViewById(R.id.verifyCodeEt);
        backImgv = mView.findViewById(R.id.backImgv);
        nextImgv = mView.findViewById(R.id.nextImgv);
        toolbarTitleTv = mView.findViewById(R.id.toolbarTitleTv);
        warningMessageTv = mView.findViewById(R.id.warningMessageTv);
        setPhoneNum();
        setToolbarTitle();
        setTimer();
    }

    private void setPhoneNum() {
        if (phoneNum != null && !phoneNum.trim().isEmpty()){
            if(myCountry != null && myCountry.getDialCode() != null && !myCountry.getDialCode().trim().isEmpty())
                phoneNumberTv.setText(myCountry.getDialCode() + phoneNum);
        }
    }

    public void setToolbarTitle() {
        toolbarTitleTv.setText(getResources().getString(R.string.VERIFY_PHONE_NUMBER));
    }

    private void setButtonShapes() {
        buttonShape = ShapeUtil.getShape(getResources().getColor(R.color.White, null),
                getResources().getColor(R.color.Gray, null), GradientDrawable.RECTANGLE, 15, 2);
        sendCodeAgainBtn.setBackground(buttonShape);
        changePhoneBtn.setBackground(buttonShape);
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
                if (phoneVerification.getmCredential() != null) {
                    if (phoneVerification.getmCredential().getSmsCode() != null && !phoneVerification.getmCredential().getSmsCode().trim().isEmpty() &&
                            verifyCodeEt.getText() != null && !verifyCodeEt.getText().toString().trim().isEmpty()) {

                        if (verifyCodeEt.getText().toString().equals(phoneVerification.getmCredential().getSmsCode())) {
                            saveUserPhoneAndCountry();
                        } else
                            DialogBoxUtil.showErrorDialog(getActivity(), getResources().getString(R.string.INVALID_VERIFICATION_CODE_ENTERED), new InfoDialogBoxCallback() {
                                @Override
                                public void okClick() {

                                }
                            });

                    }
                }
            }
        });

        verifyCodeEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 6) {
                    nextImgv.setVisibility(View.VISIBLE);
                } else
                    nextImgv.setVisibility(View.GONE);
            }
        });

        sendCodeAgainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                warningMessageTv.setVisibility(View.GONE);
                phoneVerification.resendVerificationCode(myCountry.getDialCode() + phoneNum, phoneVerification.getmResendToken());
                setTimer();
            }
        });

        changePhoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    public void saveUserPhoneAndCountry() {
        UserProfileProperties userProfileProperties = AccountHolderInfo.getInstance().getUser().getUserInfo();
        userProfileProperties.setPhoneCountry(myCountry);
        userProfileProperties.setPhone(phoneNum);

        new UpdateUserProfileProcess(getActivity(), new ServiceCompleteCallback() {
            @Override
            public void onSuccess() {
                DialogBoxUtil.showInfoDialogWithLimitedTime(getActivity(),null, getActivity().getResources().getString(R.string.UPDATE_IS_SUCCESSFUL), 1500, new InfoDialogBoxCallback() {
                    @Override
                    public void okClick() {
                        completeCallback.onComplete(null);
                        getActivity().onBackPressed();
                    }
                });
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

    public void setTimer() {
        sendCodeAgainBtn.setEnabled(false);

        new CountDownTimer(VERIFY_PHONE_NUM_DURATION * 1000, 1000) {

            int duration = VERIFY_PHONE_NUM_DURATION;

            public void onTick(long millisUntilFinished) {
                remainingTimeTv.setText(checkDigit(duration));
                duration--;
            }

            public void onFinish() {
                remainingTimeTv.setText(checkDigit(0));
                sendCodeAgainBtn.setEnabled(true);
                warningMessageTv.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    public String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }

}
