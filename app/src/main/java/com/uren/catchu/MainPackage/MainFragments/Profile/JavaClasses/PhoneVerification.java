package com.uren.catchu.MainPackage.MainFragments.Profile.JavaClasses;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.Interfaces.PhoneVerifyCallback;

import java.util.concurrent.TimeUnit;

import static com.uren.catchu.Constants.NumericConstants.VERIFY_PHONE_NUM_DURATION;

public class PhoneVerification {
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private String phoneNum;
    private Context context;
    private CompleteCallback completeCallback;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private Activity activity;
    //private PhoneAuthCredential mCredential;

    public PhoneVerification(Context context, String phoneNum, CompleteCallback completeCallback) {
        this.context = context;
        this.phoneNum = phoneNum;
        this.completeCallback = completeCallback;
        activity = (Activity) context;
        callBackInit();
    }

    public void callBackInit() {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.

                //mCredential = credential;
                mVerificationInProgress = false;
                //completeCallback.onComplete(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.

                mVerificationInProgress = false;
                completeCallback.onFailed(e);
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
                completeCallback.onComplete(verificationId);
            }
        };
    }

    public void verifyPhoneNumberWithCode(String verificationId, String code, Context context,
                                          final PhoneVerifyCallback phoneVerifyCallback) {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            phoneVerifyCallback.onReturn(true);
                        } else {
                            phoneVerifyCallback.onReturn(false);
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {

                            }
                        }
                    }
                });
    }

    public void startPhoneNumberVerification() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNum,                           // Phone number to verify
                VERIFY_PHONE_NUM_DURATION,          // Timeout duration
                TimeUnit.SECONDS,                   // Unit of timeout
                activity,                           // Activity (for callback binding)
                mCallbacks);                        // OnVerificationStateChangedCallbacks

        mVerificationInProgress = true;
    }

    public void resendVerificationCode(String phoneNumber,
                                       PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,                        // Phone number to verify
                VERIFY_PHONE_NUM_DURATION,          // Timeout duration
                TimeUnit.SECONDS,                   // Unit of timeout
                activity,                           // Activity (for callback binding)
                mCallbacks,                         // OnVerificationStateChangedCallbacks
                token);                             // ForceResendingToken from callbacks
    }

    public PhoneAuthProvider.ForceResendingToken getmResendToken() {
        return mResendToken;
    }

    public void setmResendToken(PhoneAuthProvider.ForceResendingToken mResendToken) {
        this.mResendToken = mResendToken;
    }

    public String getmVerificationId() {
        return mVerificationId;
    }

    public void setmVerificationId(String mVerificationId) {
        this.mVerificationId = mVerificationId;
    }
}
