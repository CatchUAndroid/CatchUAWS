package com.uren.catchu.LoginPackage;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.TwitterAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.uren.catchu.ApiGatewayFunctions.EndPointProcess;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.ApiGatewayFunctions.UserDetail;
import com.uren.catchu.GeneralUtils.BitmapConversion;
import com.uren.catchu.GeneralUtils.BlurBuilder;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.LoginPackage.Models.LoginUser;
import com.uren.catchu.LoginPackage.Utils.ClickableImageView;
import com.uren.catchu.LoginPackage.Utils.Validation;
import com.uren.catchu.MainActivity;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.JavaClasses.MessageUpdateProcess;
import com.uren.catchu.MainPackage.MainFragments.Profile.MessageManagement.JavaClasses.MyFirebaseMessagingService;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.Arrays;

import catchu.model.BaseResponse;
import catchu.model.Endpoint;
import catchu.model.UserProfile;
import io.fabric.sdk.android.Fabric;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import static com.uren.catchu.Constants.StringConstants.CHAR_E;
import static com.uren.catchu.Constants.StringConstants.ENDPOINT_LOGGED_IN;
import static com.uren.catchu.Constants.StringConstants.ENDPOINT_PLATFORM_ANDROID;
import static com.uren.catchu.Constants.StringConstants.PROVIDER_TYPE_FACEBOOK;
import static com.uren.catchu.Constants.StringConstants.PROVIDER_TYPE_TWITTER;

public class LoginActivity extends AppCompatActivity
        implements View.OnClickListener {

    RelativeLayout backgroundLayout;
    EditText emailET;
    EditText passwordET;
    TextView registerText;
    TextView forgetPasText;
    ClickableImageView imgFacebook;
    ClickableImageView imgTwitter;
    Button btnLogin;
    Button forgetPasswordBtn;
    Button createAccBtn;
    private TwitterLoginButton mLoginButton;
    private CallbackManager mCallbackManager;
    private CheckBox rememberMeCheckBox;
    private Boolean saveLogin;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;

    private boolean fbLoginClicked = false;
    private boolean twLoginClicked = false;

    //Local
    String userEmail;
    String userPassword;
    ProgressDialog progressDialog;
    public LoginUser loginUser;
    private InputStream profileImageStream;
    private Bitmap photo = null;

    //Firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        //These has to be done before setContentView
        initFacebookLogin();
        initTwitterLogin();

        setContentView(R.layout.activity_login);
        Fabric.with(this, new Crashlytics());

        initVariables();
        setShapes();
        BitmapConversion.setBlurBitmap(LoginActivity.this, backgroundLayout,
                R.drawable.login_background, 0.3f, 15f, null);
    }

    public void setShapes() {
        emailET.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.transparent, null),
                getResources().getColor(R.color.White, null), GradientDrawable.RECTANGLE, 20, 4));
        passwordET.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.transparent, null),
                getResources().getColor(R.color.White, null), GradientDrawable.RECTANGLE, 20, 4));
        imgFacebook.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.transparent, null),
                getResources().getColor(R.color.White, null), GradientDrawable.OVAL, 50, 3));
        imgTwitter.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.transparent, null),
                getResources().getColor(R.color.White, null), GradientDrawable.OVAL, 50, 3));
        btnLogin.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.colorPrimary, null),
                getResources().getColor(R.color.White, null), GradientDrawable.RECTANGLE, 20, 4));
        forgetPasswordBtn.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.transparent, null),
                getResources().getColor(R.color.White, null), GradientDrawable.RECTANGLE, 20, 4));
        createAccBtn.setBackground(ShapeUtil.getShape(getResources().getColor(R.color.transparent, null),
                getResources().getColor(R.color.White, null), GradientDrawable.RECTANGLE, 20, 4));
    }

    private void initFacebookLogin() {
        FacebookSdk.sdkInitialize(getApplicationContext());
    }

    private void initTwitterLogin() {

        TwitterAuthConfig authConfig = new TwitterAuthConfig(
                getString(R.string.twitter_consumer_key),
                getString(R.string.twitter_consumer_secret));

        TwitterConfig twitterConfig = new TwitterConfig.Builder(this)
                .twitterAuthConfig(authConfig)
                .build();

        Twitter.initialize(twitterConfig);
    }

    private void initVariables() {
        initUIValues();
        setClickableTexts(this);
        initUIListeners();
        progressDialog = new ProgressDialog(this);

        loginUser = new LoginUser();
        mAuth = FirebaseAuth.getInstance();

        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        saveLogin = loginPreferences.getBoolean("saveLogin", false);
        if (saveLogin == true) {
            emailET.setText(loginPreferences.getString("email", emailET.getText().toString()));
            passwordET.setText(loginPreferences.getString("password", passwordET.getText().toString()));
            rememberMeCheckBox.setChecked(true);
        }
    }

    private void initUIValues(){
        backgroundLayout = (RelativeLayout) findViewById(R.id.loginLayout);
        emailET = (EditText) findViewById(R.id.input_email);
        passwordET = (EditText) findViewById(R.id.input_password);
        registerText = (TextView) findViewById(R.id.btnRegister);
        forgetPasText = (TextView) findViewById(R.id.btnForgetPassword);
        imgFacebook = (ClickableImageView) findViewById(R.id.clickImageFB);
        imgTwitter = (ClickableImageView) findViewById(R.id.clickImageTwitter);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        rememberMeCheckBox = findViewById(R.id.rememberMeCb);
        forgetPasswordBtn = findViewById(R.id.forgetPasswordBtn);
        createAccBtn = findViewById(R.id.createAccBtn);
    }

    private void initUIListeners(){
        backgroundLayout.setOnClickListener(this);
        emailET.setOnClickListener(this);
        passwordET.setOnClickListener(this);
        imgFacebook.setOnClickListener(this);
        imgTwitter.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
    }

    private void setClickableTexts(Activity act) {
        String textRegister = getResources().getString(R.string.createAccount);
        String textForgetPssword = getResources().getString(R.string.forgetPassword);
        final SpannableString spanStringRegister = new SpannableString(textRegister);
        final SpannableString spanStringForgetPas = new SpannableString(textForgetPssword);
        spanStringRegister.setSpan(new UnderlineSpan(), 0, spanStringRegister.length(), 0);
        spanStringForgetPas.setSpan(new UnderlineSpan(), 0, spanStringForgetPas.length(), 0);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {

                if (textView.equals(registerText)) {
                    //Toast.makeText(LoginActivity.this, "RegisterActivity click!", Toast.LENGTH_SHORT).show();
                    registerTextClicked();
                } else if (textView.equals(forgetPasText)) {
                    //Toast.makeText(LoginActivity.this, "Forgetpas click!", Toast.LENGTH_SHORT).show();
                    forgetPasTextClicked();
                }
            }
        };
        spanStringRegister.setSpan(clickableSpan, 0, spanStringRegister.length(), 0);
        spanStringForgetPas.setSpan(clickableSpan, 0, spanStringForgetPas.length(), 0);

        registerText.setText(spanStringRegister);
        forgetPasText.setText(spanStringForgetPas);
        registerText.setMovementMethod(LinkMovementMethod.getInstance());
        forgetPasText.setMovementMethod(LinkMovementMethod.getInstance());
        registerText.setHighlightColor(Color.TRANSPARENT);
        forgetPasText.setHighlightColor(Color.TRANSPARENT);
        registerText.setLinkTextColor(getResources().getColor(R.color.White, null));
        forgetPasText.setLinkTextColor(getResources().getColor(R.color.White, null));
    }

    @Override
    public void onClick(View view) {

        if (view == backgroundLayout) {
            saveLoginInformation();
            CommonUtils.hideKeyBoard(LoginActivity.this);
        } else if (view == emailET) {

        } else if (view == passwordET) {

        } else if (view == imgFacebook) {
            if (checkNetworkConnection())
                imgFacebookClicked();
        } else if (view == imgTwitter) {
            if (checkNetworkConnection())
                imgTwitterClicked();
        } else if (view == btnLogin) {
            if (checkNetworkConnection())
                loginBtnClicked();
        } else if (view == rememberMeCheckBox) {
            saveLoginInformation();
        } else {

        }
    }

    public boolean checkNetworkConnection() {
        if (!CommonUtils.isNetworkConnected(LoginActivity.this)) {
            CommonUtils.connectionErrSnackbarShow(backgroundLayout, LoginActivity.this);
            return false;
        } else
            return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            saveLoginInformation();

        } else if (keyCode == KeyEvent.KEYCODE_HOME) {
            saveLoginInformation();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void saveLoginInformation() {

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(emailET.getWindowToken(), 0);

        String username = emailET.getText().toString();
        String password = passwordET.getText().toString();

        if (rememberMeCheckBox.isChecked()) {
            loginPrefsEditor.putBoolean("saveLogin", true);
            loginPrefsEditor.putString("email", username);
            loginPrefsEditor.putString("password", password);
            loginPrefsEditor.commit();
        } else {
            loginPrefsEditor.clear();
            loginPrefsEditor.commit();
        }
    }


    /*****************************CLICK EVENTS******************************/

    private void registerTextClicked() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        //finish();
    }

    private void forgetPasTextClicked() {
        Intent intent = new Intent(this, ForgetPasswordActivity.class);
        startActivity(intent);
    }

    private void imgFacebookClicked() {

        fbLoginClicked = true;
        twLoginClicked = false;

        // Initialize Facebook LoginActivity button
        mCallbackManager = CallbackManager.Factory.create();

        LoginButton loginButton = (LoginButton) findViewById(R.id.facebookLoginButton);

        loginButton.setReadPermissions(Arrays.asList(
                "public_profile",
                "email",
                "user_birthday",
                "user_friends"));

        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                CommonUtils.LOG_NEREDEYIZ("loginButton.registerCallback");
                getFacebookuserInfo(loginResult);
            }

            @Override
            public void onCancel() {
                CommonUtils.LOG_NEREDEYIZ("facebook:onCancel");
                Log.i("Info", "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                CommonUtils.LOG_FAIL("facebook:onError:", error.toString());
            }
        });

        loginButton.performClick();
    }


    private void imgTwitterClicked() {

        twLoginClicked = true;
        fbLoginClicked = false;

        mLoginButton = findViewById(R.id.twitterLoginButton);

        mLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {

                handleTwitterSession(result.data);
            }

            @Override
            public void failure(TwitterException exception) {

                Log.i("Info", "twitterLogin:failure:" + exception);
            }
        });

        mLoginButton.performClick();
    }

    private void loginBtnClicked() {

        saveLoginInformation();
        progressDialog.setMessage(this.getString(R.string.LOGGING_USER));
        progressDialog.show();

        userEmail = emailET.getText().toString();
        userPassword = passwordET.getText().toString();

        //validation controls
        if (!checkValidation(userEmail, userPassword)) {
            return;
        }

        loginUser(userEmail, userPassword);
    }

    private boolean checkValidation(String email, String password) {

        //email validation
        if (!Validation.getInstance().isValidEmail(this, email)) {
            //Toast.makeText(this, Validation.getInstance().getErrorMessage() , Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            openDialog(Validation.getInstance().getErrorMessage());
            return false;
        }

        //password validation
        if (!Validation.getInstance().isValidPassword(this, password)) {
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

    private void loginUser(final String userEmail, String userPassword) {
        final Context context = this;

        mAuth.signInWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();

                        if (task.isSuccessful()) {
                            Log.i("info:", "signIn successfull..");

                            setUserInfo("", userEmail);
                            startMainPage();
                        } else {

                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                Log.i("error register", e.toString());
                                openDialog(context.getString(R.string.INVALID_CREDENTIALS));
                            } catch (FirebaseAuthInvalidUserException e) {
                                Log.i("error register", e.toString());
                                openDialog(context.getString(R.string.INVALID_USER));
                            } catch (Exception e) {
                                Log.i("error signIn ", e.toString());
                                openDialog(context.getString(R.string.UNKNOWN_ERROR) + "(" + e.toString() + ")");

                            }
                        }
                    }
                });
    }

    private void setUserInfo(String userName, String userEmail) {

        if (!userName.isEmpty() && !userName.equals("")) {
            loginUser.setUsername(userName);
        } else {
            loginUser.setUsername("default");
        }

        loginUser.setEmail(userEmail);
        loginUser.setUserId(mAuth.getCurrentUser().getUid());
        //updateDeviceTokenForFCM();
    }

    /*public void updateDeviceTokenForFCM(){
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( new OnSuccessListener<InstanceIdResult>() {
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

                    }

                    @Override
                    public void onFailure(Exception e) {

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

    private void startMainPage() {
        loginUser.setUserId(mAuth.getCurrentUser().getUid());
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("LoginUser", loginUser);
        startActivity(intent);
        finish();
    }

    private void startAppIntroPage() {
        loginUser.setUserId(mAuth.getCurrentUser().getUid());
        Intent intent = new Intent(this, AppIntroductionActivity.class);
        intent.putExtra("LoginUser", loginUser);
        startActivity(intent);
        finish();
    }

    public void getFacebookuserInfo(final LoginResult loginResult) {

        CommonUtils.LOG_NEREDEYIZ("getFacebookuserInfo");

        GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        Log.i("Info", "Facebook response:" + response.toString());

                        try {

                            loginUser.setName(object.getString("name"));
                            loginUser.setEmail(object.getString("email"));

                            //fb profile pic
                            String facebookUserId = object.getString("id");
                            String url = "https://graph.facebook.com/" + facebookUserId + "/picture?type=large";
                            loginUser.setProfilePhotoUrl(url);
                            loginUser.setProviderId(facebookUserId);
                            loginUser.setProviderType(PROVIDER_TYPE_FACEBOOK);

                            handleFacebookAccessToken(loginResult.getAccessToken());

                        } catch (JSONException e) {
                            Log.i("Info", "  >>JSONException error:" + e.toString());
                        } catch (Exception e) {
                            Log.i("Info", "  >>Profile error:" + e.toString());
                        }
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender,birthday");
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();
    }

    private void handleFacebookAccessToken(AccessToken token) {

        Log.i("Info", "handleFacebookAccessToken starts:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

        try {
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in loginUser's information
                                Log.i("Info", "  >>signInWithCredential:success");
                                //updateDeviceTokenForFCM();
                                checkUserInSystem();

                            } else {
                                // If sign in fails, display a message to the loginUser.
                                Log.i("Info", "  >>signInWithCredential:failure:" + task.getException());
                            }
                        }
                    });
        } catch (Exception e) {
            Log.i("Info", "  >>handleFacebookAccessToken error:" + e.toString());
        }
    }


    private void handleTwitterSession(final TwitterSession session) {

        Log.i("Info", "handleTwitterSession:" + session);

        AuthCredential credential = TwitterAuthProvider.getCredential(
                session.getAuthToken().token,
                session.getAuthToken().secret);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Log.i("Info", "signInWithCredential Twitter:success");
                            getTwitterUserInfo(session);
                            checkUserInSystem();

                        } else {
                            Log.i("Info", "  >>signInWithCredential:failure:" + task.getException());
                        }
                    }
                }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("Info", "  >>signInWithCredential:onFailure:" + e.toString());
            }
        });
    }

    private void getTwitterUserInfo(TwitterSession session) {

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setOAuthConsumerKey(getResources().getString(R.string.twitter_consumer_key));
        cb.setOAuthConsumerSecret(getResources().getString(R.string.twitter_consumer_secret));
        cb.setOAuthAccessToken(getResources().getString(R.string.twitter_token));
        cb.setOAuthAccessTokenSecret(getResources().getString(R.string.twitter_token_secret));
        twitter4j.Twitter twitter = new TwitterFactory(cb.build()).getInstance();

        //Email
        TwitterAuthClient client = new TwitterAuthClient();
        client.requestEmail(session, new Callback<String>() {
            @Override
            public void success(Result<String> result) {
                loginUser.setEmail(result.data);
                Log.i("twitterEmail :", result.data);
            }

            @Override
            public void failure(TwitterException exception) {

            }
        });

        //name
        loginUser.setName(mAuth.getCurrentUser().getProviderData().get(0).getDisplayName());
        //profile picture
        String profilePicture = mAuth.getCurrentUser().getProviderData().get(0).getPhotoUrl().toString();
        profilePicture = profilePicture.replaceFirst("_normal", "");
        Log.i("pr", profilePicture);
        loginUser.setProfilePhotoUrl(profilePicture);
        //username
        loginUser.setUsername(session.getUserName());
        //providerId
        loginUser.setProviderId(String.valueOf(session.getUserId()));
        //providerType
        loginUser.setProviderType(PROVIDER_TYPE_TWITTER);
        //updateDeviceTokenForFCM();
    }

    public void checkUserInSystem() {

        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {

                UserDetail loadUserDetail = new UserDetail(new OnEventListener<UserProfile>() {

                    @Override
                    public void onSuccess(UserProfile up) {
                        Log.i("userDetail", "successful");
                        if (up != null && up.getUserInfo() != null && up.getUserInfo().getUserid() != null) {
                            startMainPage();
                        } else
                            startAppIntroPage();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        startAppIntroPage();
                        CommonUtils.showToastShort(LoginActivity.this, e.toString());
                    }

                    @Override
                    public void onTaskContinue() {

                    }
                }, mAuth.getCurrentUser().getUid(), mAuth.getCurrentUser().getUid(), "true", token);

                loadUserDetail.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }

            @Override
            public void onTokenFail(String message) {
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i("*-*-*-* NEREDEYIZ", "onActivityResult");

        try {

            if (fbLoginClicked) {
                // Pass the activity result back to the Facebook SDK
                mCallbackManager.onActivityResult(requestCode, resultCode, data);
            }

            if (twLoginClicked) {
                // Pass the activity result to the Twitter login button.
                mLoginButton.onActivityResult(requestCode, resultCode, data);
            }

        } catch (Exception e) {
            Log.i("Info", "onActivityResult error:" + e.toString());
        }
    }


}
