package com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments;

import android.annotation.SuppressLint;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
/*import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;*/
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.uren.catchu.GeneralUtils.ClickableImage.ClickableImageView;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.Interfaces.InfoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.Interfaces.ServiceCompleteCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.Interfaces.PhoneVerifyCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.JavaClasses.PhoneVerification;
import com.uren.catchu.MainPackage.MainFragments.Profile.Utils.UpdateUserProfileProcess;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.Phone;
import catchu.model.UserProfileProperties;

import static com.uren.catchu.Constants.NumericConstants.VERIFY_PHONE_NUM_DURATION;

@SuppressLint("ValidFragment")
public class VerifyPhoneNumberFragment extends Fragment {

    View mView;

    @BindView(R.id.phoneNumberTv)
    TextView phoneNumberTv;
    @BindView(R.id.sendCodeAgainBtn)
    Button sendCodeAgainBtn;
    @BindView(R.id.changePhoneBtn)
    Button changePhoneBtn;
    @BindView(R.id.verifyCodeEt)
    EditText verifyCodeEt;
    @BindView(R.id.commonToolbarbackImgv)
    ClickableImageView commonToolbarbackImgv;
    @BindView(R.id.commonToolbarTickImgv)
    ImageView commonToolbarTickImgv;
    @BindView(R.id.toolbarTitleTv)
    TextView toolbarTitleTv;
    @BindView(R.id.warningMessageTv)
    TextView warningMessageTv;
    @BindView(R.id.remainingTimeTv)
    TextView remainingTimeTv;

    GradientDrawable buttonShape;
    PhoneVerification phoneVerification;
    CompleteCallback completeCallback;
    Phone phone;

    public VerifyPhoneNumberFragment(Phone phone, PhoneVerification phoneVerification, CompleteCallback completeCallback) {
        this.phone = phone;
        this.phoneVerification = phoneVerification;
        this.completeCallback = completeCallback;
    }

    @Override
    public void onStart() {
        getActivity().findViewById(R.id.tabMainLayout).setVisibility(View.GONE);
        super.onStart();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_verify_phone_num, container, false);
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
        setPhoneNum();
        setToolbarTitle();
        setTimer();
    }

    private void setPhoneNum() {
        if (phone != null && phone.getDialCode() != null && phone.getPhoneNumber() != null) {
            phoneNumberTv.setText(phone.getDialCode().trim() + phone.getPhoneNumber().toString().trim());
        }
    }

    public void setToolbarTitle() {
        toolbarTitleTv.setText(getResources().getString(R.string.VERIFY));
    }

    private void setButtonShapes() {
        buttonShape = ShapeUtil.getShape(getResources().getColor(R.color.White, null),
                getResources().getColor(R.color.Gray, null), GradientDrawable.RECTANGLE, 15, 2);
        sendCodeAgainBtn.setBackground(buttonShape);
        changePhoneBtn.setBackground(buttonShape);
    }

    public void addListeners() {
        commonToolbarbackImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        commonToolbarTickImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                phoneVerification.verifyPhoneNumberWithCode(phoneVerification.getmVerificationId(),
                        verifyCodeEt.getText().toString().trim(), getContext(), new PhoneVerifyCallback() {
                            @Override
                            public void onReturn(boolean isVerified) {
                                if (isVerified)
                                    saveUserPhoneAndCountry();
                                else
                                    DialogBoxUtil.showErrorDialog(getActivity(), getResources().getString(R.string.INVALID_VERIFICATION_CODE_ENTERED), new InfoDialogBoxCallback() {
                                        @Override
                                        public void okClick() {

                                        }
                                    });
                            }
                        });
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
                    commonToolbarTickImgv.setVisibility(View.VISIBLE);
                } else
                    commonToolbarTickImgv.setVisibility(View.GONE);
            }
        });

        sendCodeAgainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                warningMessageTv.setVisibility(View.GONE);
                phoneVerification.resendVerificationCode(phone.getDialCode().trim() + phone.getPhoneNumber().toString().trim(), phoneVerification.getmResendToken());
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
        userProfileProperties.setPhone(phone);

        new UpdateUserProfileProcess(getActivity(), new ServiceCompleteCallback() {
            @Override
            public void onSuccess() {
                DialogBoxUtil.showInfoDialogWithLimitedTime(getActivity(), null, getActivity().getResources().getString(R.string.UPDATE_IS_SUCCESSFUL), 1500, new InfoDialogBoxCallback() {
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
