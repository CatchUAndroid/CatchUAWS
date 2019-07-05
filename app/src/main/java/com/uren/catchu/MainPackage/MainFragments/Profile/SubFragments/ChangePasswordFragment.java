package com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments;

import android.content.Intent;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.uren.catchu.GeneralUtils.ClickableImage.ClickableImageView;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.Interfaces.InfoDialogBoxCallback;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.MainActivity;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.Operations.SettingOperation;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.uren.catchu.Constants.StringConstants.ANIMATE_RIGHT_TO_LEFT;

public class ChangePasswordFragment extends BaseFragment {

    View mView;

    @BindView(R.id.commonToolbarbackImgv)
    ClickableImageView commonToolbarbackImgv;
    @BindView(R.id.toolbarTitleTv)
    TextView toolbarTitleTv;
    @BindView(R.id.commonToolbarTickImgv)
    ImageView commonToolbarTickImgv;
    @BindView(R.id.currPasswordEdittext)
    EditText currPasswordEdittext;
    @BindView(R.id.newPasswordEdittext)
    EditText newPasswordEdittext;
    @BindView(R.id.validatePassEdittext)
    EditText validatePassEdittext;
    @BindView(R.id.container)
    RelativeLayout container;

    FirebaseUser user;
    String newPassword;

    public ChangePasswordFragment() {

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
            mView = inflater.inflate(R.layout.fragment_change_password, container, false);
            ButterKnife.bind(this, mView);
            init();
            addListeners();
        }
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((NextActivity) getActivity()).ANIMATION_TAG = ANIMATE_RIGHT_TO_LEFT;
    }

    private void init() {
        setToolbarTitle();
    }

    public void setToolbarTitle() {
        toolbarTitleTv.setText(getResources().getString(R.string.CHANGE_PASSWORD));
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
                validatePasswords();
            }
        });

        validatePassEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 0) {
                    commonToolbarTickImgv.setVisibility(View.GONE);
                } else {
                    if (currPasswordEdittext.getText().length() > 0 && newPasswordEdittext.getText().length() > 0)
                        commonToolbarTickImgv.setVisibility(View.VISIBLE);
                    else
                        commonToolbarTickImgv.setVisibility(View.GONE);
                }
            }
        });
    }

    public void validatePasswords() {

        if (newPasswordEdittext.getText().toString().trim().length() < 6) {
            Snackbar.make(container, getContext().getResources().getString(R.string.PASSWORD_ERR_LENGTH), Snackbar.LENGTH_LONG)
                    .show();
            return;
        }

        if (!newPasswordEdittext.getText().toString().equals(validatePassEdittext.getText().toString())) {
            Snackbar.make(container, getContext().getResources().getString(R.string.CHECK_PASSWORD_VALIDATION_VALUE), Snackbar.LENGTH_LONG)
                    .show();
            return;
        }

        changePassword();
    }

    private void changePassword() {

        user = FirebaseAuth.getInstance().getCurrentUser();
        newPassword = newPasswordEdittext.getText().toString();

        AuthCredential credential = EmailAuthProvider.
                getCredential(AccountHolderInfo.getInstance().getUser().getUserInfo().getEmail(),
                        currPasswordEdittext.getText().toString());

        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        CommonUtils.showToastShort(getContext(), getContext().getResources().getString(R.string.PASSWORD_IS_CHANGED));
                                        thread.start();
                                    } else {
                                        DialogBoxUtil.showErrorDialog(getContext(), getContext().getResources().getString(R.string.error) + task.getException().getMessage(), new InfoDialogBoxCallback() {
                                            @Override
                                            public void okClick() {
                                            }
                                        });
                                    }
                                }
                            });
                        } else {
                            Snackbar.make(container, getContext().getResources().getString(R.string.CURRENT_PASSWORD_INCORRECT), Snackbar.LENGTH_LONG)
                                    .show();
                        }
                    }
                });
    }

    Thread thread = new Thread() {
        @Override
        public void run() {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            SettingOperation.userSignOut(new CompleteCallback() {
                @Override
                public void onComplete(Object object) {
                    getActivity().finish();
                    startActivity(new Intent(getActivity(), MainActivity.class));
                }

                @Override
                public void onFailed(Exception e) {

                }
            });
        }
    };
}
