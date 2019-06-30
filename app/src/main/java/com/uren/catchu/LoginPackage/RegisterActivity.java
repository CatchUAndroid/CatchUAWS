package com.uren.catchu.LoginPackage;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.uren.catchu.ApiGatewayFunctions.EndPointProcess;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.GeneralUtils.BitmapConversion;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.Interfaces.InfoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.LoginPackage.Models.LoginUser;
import com.uren.catchu.LoginPackage.Utils.Validation;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.JavaClasses.MessageUpdateProcess;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.JavaClasses.MyFirebaseMessagingService;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import catchu.model.BaseResponse;
import catchu.model.Endpoint;
import io.fabric.sdk.android.Fabric;

import static com.uren.catchu.Constants.StringConstants.CHAR_E;
import static com.uren.catchu.Constants.StringConstants.ENDPOINT_LOGGED_IN;
import static com.uren.catchu.Constants.StringConstants.ENDPOINT_PLATFORM_ANDROID;


public class RegisterActivity extends AppCompatActivity
        implements View.OnClickListener {

    RelativeLayout registerLayout;
    EditText usernameET;
    EditText emailET;
    EditText passwordET;
    Button btnRegister;

    //Local
    LoginUser newLoginUser;
    String userName;
    String userEmail;
    String userPassword;
    ProgressDialog progressDialog;

    //Firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Fabric.with(this, new Crashlytics());
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

        init();
        setShapes();
        BitmapConversion.setBlurBitmap(RegisterActivity.this, registerLayout,
                R.drawable.register_bg, 0.2f, 20.5f, null);
    }

    public void setShapes(){
        usernameET.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.transparent, null),
                getResources().getColor(R.color.White, null), GradientDrawable.RECTANGLE, 20, 4));
        emailET.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.transparent, null),
                getResources().getColor(R.color.White, null), GradientDrawable.RECTANGLE, 20, 4));
        passwordET.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.transparent, null),
                getResources().getColor(R.color.White, null), GradientDrawable.RECTANGLE, 20, 4));
        btnRegister.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.colorPrimary, null),
                getResources().getColor(R.color.White, null), GradientDrawable.RECTANGLE, 20, 4));
    }

    private void init() {
        registerLayout = (RelativeLayout) findViewById(R.id.registerLayout);
        usernameET = (EditText) findViewById(R.id.input_username);
        emailET = (EditText) findViewById(R.id.input_email);
        passwordET = (EditText) findViewById(R.id.input_password);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        registerLayout.setOnClickListener(this);
        usernameET.setOnClickListener(this);
        emailET.setOnClickListener(this);
        passwordET.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {

        if (v == btnRegister) {
            if (checkNetworkConnection())
                btnRegisterClicked();
        }
    }

    public boolean checkNetworkConnection() {
        if (!CommonUtils.isNetworkConnected(RegisterActivity.this)) {
            CommonUtils.connectionErrSnackbarShow(registerLayout, RegisterActivity.this);
            return false;
        } else
            return true;
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

    /*****************************CLICK EVENTS******************************/

    private void btnRegisterClicked() {

        progressDialog.setMessage(this.getString(R.string.REGISTERING_USER));
        progressDialog.show();

        userName = usernameET.getText().toString();
        userEmail = emailET.getText().toString();
        userPassword = passwordET.getText().toString();

        //validation controls
        if (!checkValidation(userName, userEmail, userPassword)) {
            return;
        }

        createUser(userName, userEmail, userPassword);

    }

    private boolean checkValidation(String name, String email, String password) {

        //username validation
        if (!Validation.getInstance().isValidUserName(this, name)) {
            progressDialog.dismiss();
            DialogBoxUtil.showInfoDialogBox(RegisterActivity.this,
                    Validation.getInstance().getErrorMessage(), null, new InfoDialogBoxCallback() {
                        @Override
                        public void okClick() {

                        }
                    });

            return false;
        }

        //email validation
        if (!Validation.getInstance().isValidEmail(this, email)) {
            progressDialog.dismiss();
            DialogBoxUtil.showInfoDialogBox(RegisterActivity.this,
                    Validation.getInstance().getErrorMessage(), null, new InfoDialogBoxCallback() {
                        @Override
                        public void okClick() {

                        }
                    });
            return false;
        }

        //password validation
        if (!Validation.getInstance().isValidPassword(this, password)) {
            //Toast.makeText(this, Validation.getInstance().getErrorMessage() , Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();

            DialogBoxUtil.showInfoDialogBox(RegisterActivity.this,
                    Validation.getInstance().getErrorMessage(), null, new InfoDialogBoxCallback() {
                        @Override
                        public void okClick() {

                        }
                    });

            return false;
        }

        return true;
    }

    private void createUser(final String userName, final String userEmail, final String userPassword) {

        final Context context = this;

        mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {


                        if (task.isSuccessful()) {
                            Log.i("Info", "CreateUser : Success");
                            progressDialog.dismiss();
                            setUserInfo(userName, userEmail);
                            //updateDeviceTokenForFCM();
                            startAppIntroPage();
                            //startMainPage();
                        } else {
                            progressDialog.dismiss();
                            Log.i("Info", "CreateUser : Fail");
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthUserCollisionException e) {
                                DialogBoxUtil.showInfoDialogBox(RegisterActivity.this,
                                        context.getString(R.string.COLLISION_EXCEPTION), null, new InfoDialogBoxCallback() {
                                            @Override
                                            public void okClick() {

                                            }
                                        });

                            } catch (Exception e) {
                                DialogBoxUtil.showInfoDialogBox(RegisterActivity.this,
                                        context.getString(R.string.UNKNOWN_ERROR) + "(" + e.toString() + ")", null, new InfoDialogBoxCallback() {
                                            @Override
                                            public void okClick() {

                                            }
                                        });
                            }
                        }
                    }
                });
    }

    private void setUserInfo(String userName, String userEmail) {

        newLoginUser = new LoginUser();
        newLoginUser.setUsername(userName);
        newLoginUser.setEmail(userEmail);
        newLoginUser.setUserId(mAuth.getCurrentUser().getUid());
    }

    public void startAppIntroPage() {
        Intent intent = new Intent(RegisterActivity.this, AppIntroductionActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("LoginUser", newLoginUser);
        startActivity(intent);
    }

    /*public void updateDeviceTokenForFCM(){
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String deviceToken = instanceIdResult.getToken();
                MyFirebaseMessagingService.sendRegistrationToServer(deviceToken, mAuth.getCurrentUser().getUid());
                startEndPointProcess(deviceToken);
            }
        });

        MessageUpdateProcess.updateTokenSigninValue(mAuth.getCurrentUser().getUid(), CHAR_E);
    }

    private void startEndPointProcess(final String deviceToken){

        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {

                final Endpoint endpoint = new Endpoint();
                endpoint.setDeviceToken(deviceToken);
                endpoint.setUserid(mAuth.getCurrentUser().getUid());
                endpoint.setPlatformType(ENDPOINT_PLATFORM_ANDROID);
                endpoint.setRequestType(ENDPOINT_LOGGED_IN);

                EndPointProcess endPointProcess = new EndPointProcess(new OnEventListener<BaseResponse>() {

                    @Override
                    public void onSuccess(BaseResponse baseResponse) {

                        Log.i("", "");
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.i("", "");
                    }

                    @Override
                    public void onTaskContinue() {

                    }
                }, token, endpoint);

                endPointProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }

            @Override
            public void onTokenFail(String message) {

            }
        });
    }*/
}
