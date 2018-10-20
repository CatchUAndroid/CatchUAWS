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
import com.uren.catchu.MainPackage.MainFragments.Profile.JavaClasses.PhoneVerification;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import catchu.model.Country;
import catchu.model.CountryListResponse;

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
    LinearLayout remTimeLayout;
    GradientDrawable buttonShape;
    PhoneVerification phoneVerification;
    CompleteCallback completeCallback;

    public VerifyPhoneNumberFragment(String phoneNum, PhoneVerification phoneVerification, CompleteCallback completeCallback) {
        this.phoneNum = phoneNum;
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
        remTimeLayout = mView.findViewById(R.id.remTimeLayout);
        warningMessageTv = mView.findViewById(R.id.warningMessageTv);
        setPhoneNum();
        setToolbarTitle();
    }

    private void setPhoneNum() {
        if (phoneNum != null && !phoneNum.trim().isEmpty())
            phoneNumberTv.setText(phoneNum);
    }

    public void setToolbarTitle(){
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

                            DialogBoxUtil.showInfoDialogWithLimitedTime(getActivity(), null,  getResources().getString(R.string.PHONE_VERIFICATION_IS_SUCCESSFUL), 1000, new InfoDialogBoxCallback() {
                                @Override
                                public void okClick() {
                                    completeCallback.onComplete(null);
                                    getActivity().onBackPressed();
                                }
                            });
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
                phoneVerification.resendVerificationCode(phoneNum, phoneVerification.getmResendToken());
            }
        });

        changePhoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    public void setTimer(){
        remTimeLayout.setVisibility(View.VISIBLE);

        new CountDownTimer(VERIFY_PHONE_NUM_DURATION * 1000, 1000) {

            int duration = VERIFY_PHONE_NUM_DURATION;

            public void onTick(long millisUntilFinished) {
                remainingTimeTv.setText(checkDigit(duration));
                duration--;
            }

            public void onFinish() {
                warningMessageTv.setVisibility(View.VISIBLE);
            }

        }.start();
    }

    public String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }

}
