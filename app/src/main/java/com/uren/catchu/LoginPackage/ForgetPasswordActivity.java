package com.uren.catchu.LoginPackage;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.InfoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.LoginPackage.Utils.Validation;
import com.uren.catchu.R;

public class ForgetPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    RelativeLayout forgetPasswordLayout;
    EditText emailET;
    Button btnSendLink;
    ImageView lockImgv;

    String userEmail;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
        init();
        setShapes();
    }

    private void setShapes() {
        lockImgv.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.transparentBlack, null),
                0, GradientDrawable.OVAL, 50, 0));
        emailET.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.White, null),
                0, GradientDrawable.RECTANGLE, 20, 0));
        btnSendLink.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.colorPrimary, null),
                getResources().getColor(R.color.White, null), GradientDrawable.RECTANGLE, 20, 3));
    }

    private void init() {
        emailET = (EditText) findViewById(R.id.input_email);
        btnSendLink = (Button) findViewById(R.id.btnSendLink);
        forgetPasswordLayout = findViewById(R.id.forgetPasswordLayout);
        lockImgv = findViewById(R.id.lockImgv);
        forgetPasswordLayout.setOnClickListener(this);
        emailET.setOnClickListener(this);
        btnSendLink.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public void onClick(View v) {

        if (v == btnSendLink) {
            if(checkNetworkConnection())
                btnSendLinkClicked();
        }
    }

    public boolean checkNetworkConnection() {
        if (!CommonUtils.isNetworkConnected(ForgetPasswordActivity.this)) {
            CommonUtils.connectionErrSnackbarShow(forgetPasswordLayout, ForgetPasswordActivity.this);
            return false;
        } else
            return true;
    }

    private void btnSendLinkClicked() {

        progressDialog.setMessage(this.getString(R.string.PLEASE_WAIT));
        progressDialog.show();

        userEmail = emailET.getText().toString();

        //validation controls
        if (!checkValidation(userEmail)) {
            return;
        }

        sendLinkToMail(userEmail);
    }

    private boolean checkValidation(String userEmail) {

        if (!Validation.getInstance().isValidEmail(this, userEmail)) {
            progressDialog.dismiss();
            DialogBoxUtil.showInfoDialogBox(ForgetPasswordActivity.this,
                    Validation.getInstance().getErrorMessage(), null, new InfoDialogBoxCallback() {
                        @Override
                        public void okClick() {

                        }
                    });
            return false;
        }
        return true;
    }

    private void sendLinkToMail(String userEmail) {

        final Context context = this;

        FirebaseAuth auth = FirebaseAuth.getInstance();
        ActionCodeSettings actionCodeSettings = ActionCodeSettings.newBuilder()
                .setAndroidPackageName("uren.com.catchu", true, null)
                .setHandleCodeInApp(false)
                .setIOSBundleId(null)
                .setUrl("https://catchu-594ca.firebaseapp.com/")
                .build();


        auth.sendPasswordResetEmail(userEmail, actionCodeSettings)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Log.i("reset Email status :", "Email sent success.");

                            DialogBoxUtil.showInfoDialogBox(ForgetPasswordActivity.this,
                                    context.getString(R.string.PASSWORD_LINK_SEND_SUCCESS), null, new InfoDialogBoxCallback() {
                                        @Override
                                        public void okClick() {

                                        }
                                    });
                        } else {
                            Log.i("reset Email status :", "Email sent fail.");

                            DialogBoxUtil.showInfoDialogBox(ForgetPasswordActivity.this,
                                    context.getString(R.string.PASSWORD_LINK_SEND_FAIL), null, new InfoDialogBoxCallback() {
                                        @Override
                                        public void okClick() {

                                        }
                                    });
                        }

                    }
                });

    }

}
