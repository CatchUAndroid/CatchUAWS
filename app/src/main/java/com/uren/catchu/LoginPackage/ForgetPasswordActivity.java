package com.uren.catchu.LoginPackage;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.uren.catchu.LoginPackage.Utils.Validation;
import com.uren.catchu.R;

public class ForgetPasswordActivity extends AppCompatActivity
        implements View.OnClickListener {

    //XML
    RelativeLayout backgroundLayout;
    EditText emailET;
    Button btnSendLink;

    //Local
    String userEmail;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);

        init();

    }

    private void init() {
        backgroundLayout = (RelativeLayout) findViewById(R.id.forgetPasswordLayout);
        emailET = (EditText) findViewById(R.id.input_email);
        btnSendLink = (Button) findViewById(R.id.btnSendLink);

        backgroundLayout.setOnClickListener(this);
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

        if (v == backgroundLayout) {


        }

        if (v == emailET) {

        }

        if (v == btnSendLink) {

        }


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

        //email validation
        if (!Validation.getInstance().isValidEmail(this, userEmail)) {
            //Toast.makeText(this, Validation.getInstance().getErrorMessage() , Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            openDialog(Validation.getInstance().getErrorMessage());
            return false;
        }
        return true;

    }

    public void openDialog(String message) {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("OOPS!!");
        alert.setMessage(message);
        alert.setPositiveButton("OK", null);
        alert.show();

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
                            openDialog(context.getString(R.string.PASSWORD_LINK_SEND_SUCCESS));
                        } else {
                            Log.i("reset Email status :", "Email sent fail.");
                            openDialog(context.getString(R.string.PASSWORD_LINK_SEND_FAIL));
                        }

                    }
                });

    }

}
