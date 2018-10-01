package com.uren.catchu.LoginPackage.Utils;

import android.content.Context;
import android.text.TextUtils;

import com.uren.catchu.R;


/**
 * Created by ASUS on 30.5.2018.
 */

public class Validation {

    private static Validation instance = null;
    private String errorMessage;

    //Constants
    private final static String EMAIL_ERR_REQUIRED = "Email boş olamaz!";
    private final static String EMAIL_ERR_INVALID = "Geçerli bir email adresi giriniz!";
    private final static String PASSWORD_ERR_REQUIRED = "Şifre boş olamaz!";
    private final static String PASSWORD_ERR_LENGTH = "Şifre en az 6 karakter olmalıdır!";
    private final static String USERNAME_ERR_REQUIRED = "Kullanıcı adı boş olamaz!";

    private final static int PASSWORD_MAX_LENGTH = 6;

    public static Validation getInstance() {
        if (instance == null) {
            instance = new Validation();
        }
        return(instance);
    }

    private Validation() {
    }

    public boolean isValidUserName(Context context,String userName){

        if (TextUtils.isEmpty(userName)) {
            errorMessage = context.getString(R.string.USERNAME_ERR_REQUIRED);
            return false;
        }

        return true;
    }

    public boolean isValidEmail(Context context, String email){

        if (TextUtils.isEmpty(email)) {
            errorMessage =context.getString(R.string.EMAIL_ERR_REQUIRED);
            return false;
        }

        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            errorMessage = context.getString(R.string.EMAIL_ERR_INVALID);
            return false;
        }

        return true;
    }

    public boolean isValidPassword(Context context, String password){

        if (TextUtils.isEmpty(password)) {
            errorMessage = context.getString(R.string.PASSWORD_ERR_REQUIRED);
            return false;
        }

        if (password.length() < PASSWORD_MAX_LENGTH) {
            errorMessage = context.getString(R.string.PASSWORD_ERR_LENGTH);
            return false;
        }

        return true;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
