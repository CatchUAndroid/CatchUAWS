package com.uren.catchu.GeneralUtils;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.uren.catchu.GeneralUtils.PhotoUtil.PhotoSelectUtil;
import com.uren.catchu.MainPackage.NextActivity;
import com.uren.catchu.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static com.uren.catchu.Constants.StringConstants.APP_GOOGLE_PLAY_DEFAULT_LINK;

public class CommonUtils {

    public static String serverPrefix = "**";
    public static String infoPrefix = "++";
    public static String neredeyiz = "NEREDEYIZ";


    public static final void showToast(Context context, String message) {

        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static final void showToastLong(Context context, String message) {

        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static final String getDeviceID(Context context) {
        return Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    public static int getPaddingInPixels(Context context, float dpSize) {
        final float scale = context.getResources().getDisplayMetrics().density;
        int paddingInPx = (int) (dpSize * scale + 0.5f);
        return paddingInPx;
    }


    public static final String getVersionName(Context context) {

        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pInfo.versionName;

    }

    public static void setEnableOrDisableAllItemsOfLinearLayout(LinearLayout layout, boolean enableType) {
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            child.setEnabled(enableType);
        }
    }

    public static void setButtonBackgroundColor(Context context, Button button, int color) {

        if (Build.VERSION.SDK_INT >= 23) {
            button.setBackgroundColor(context.getResources().getColor(color, null));
        } else {
            button.setBackgroundColor(context.getResources().getColor(color));
        }
    }


    public static void setButtonBackgroundColor(Context context, TextView textView, int color) {

        if (Build.VERSION.SDK_INT >= 23) {
            textView.setBackgroundColor(context.getResources().getColor(color, null));
        } else {
            textView.setBackgroundColor(context.getResources().getColor(color));
        }
    }

    public static Drawable setDrawableSelector(Context context, int normal, int selected) {


        Drawable state_normal = ContextCompat.getDrawable(context, normal);

        Drawable state_pressed = ContextCompat.getDrawable(context, selected);


        Bitmap state_normal_bitmap = ((BitmapDrawable) state_normal).getBitmap();

        // Setting alpha directly just didn't work, so we draw a new bitmap!
        Bitmap disabledBitmap = Bitmap.createBitmap(
                state_normal.getIntrinsicWidth(),
                state_normal.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(disabledBitmap);

        Paint paint = new Paint();
        paint.setAlpha(126);
        canvas.drawBitmap(state_normal_bitmap, 0, 0, paint);

        BitmapDrawable state_normal_drawable = new BitmapDrawable(context.getResources(), disabledBitmap);


        StateListDrawable drawable = new StateListDrawable();

        drawable.addState(new int[]{android.R.attr.state_selected},
                state_pressed);
        drawable.addState(new int[]{android.R.attr.state_enabled},
                state_normal_drawable);

        return drawable;
    }


    public static StateListDrawable selectorRadioImage(Context context, Drawable normal, Drawable pressed) {
        StateListDrawable states = new StateListDrawable();
        states.addState(new int[]{android.R.attr.state_checked}, pressed);
        states.addState(new int[]{}, normal);
        //                imageView.setImageDrawable(states);
        return states;
    }

    public static StateListDrawable selectorRadioButton(Context context, int normal, int pressed) {
        StateListDrawable states = new StateListDrawable();
        states.addState(new int[]{android.R.attr.state_checked}, new ColorDrawable(pressed));
        states.addState(new int[]{}, new ColorDrawable(normal));
        return states;
    }

    public static ColorStateList selectorRadioText(Context context, int normal, int pressed) {
        ColorStateList colorStates = new ColorStateList(new int[][]{new int[]{android.R.attr.state_checked}, new int[]{}}, new int[]{pressed, normal});
        return colorStates;
    }


    public static StateListDrawable selectorRadioDrawable(Drawable normal, Drawable pressed) {
        StateListDrawable states = new StateListDrawable();
        states.addState(new int[]{android.R.attr.state_checked}, pressed);
        states.addState(new int[]{}, normal);
        return states;
    }

    public static StateListDrawable selectorBackgroundColor(Context context, int normal, int pressed) {
        StateListDrawable states = new StateListDrawable();
        states.addState(new int[]{android.R.attr.state_pressed}, new ColorDrawable(pressed));
        states.addState(new int[]{}, new ColorDrawable(normal));
        return states;
    }

    public static StateListDrawable selectorBackgroundDrawable(Drawable normal, Drawable pressed) {
        StateListDrawable states = new StateListDrawable();
        states.addState(new int[]{android.R.attr.state_pressed}, pressed);
        states.addState(new int[]{}, normal);
        return states;
    }

    public static ColorStateList selectorText(Context context, int normal, int pressed) {
        ColorStateList colorStates = new ColorStateList(new int[][]{new int[]{android.R.attr.state_pressed}, new int[]{}}, new int[]{pressed, normal});
        return colorStates;
    }

    /**
     * Creates an animator that smoothly animates the passed view height from startHeight to
     * endHeight.
     *
     * @param view        The view that needs to be animated.
     * @param startHeight Starting height of the view.
     * @param endHeight   Final height of the view.
     * @return
     */
    public static ValueAnimator getAnimation(final android.view.View view, final ViewGroup.LayoutParams params, int startHeight, int endHeight) {

        //We create the animator and setup the starting height and the final height. The animator
        //Will create smooth itnermediate values (based on duration) to go across these two values.
        ValueAnimator animator = ValueAnimator.ofInt(startHeight, endHeight);
        //Overriding updateListener so that we can tell the animator what to do at each update.
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //We get the value of the animatedValue, this will be between [startHeight,endHeight]
                int val = (Integer) animation.getAnimatedValue();
                //We retrieve the layout parameters and pick up the height of the View.
                params.height = val;
                //Once we have updated the height all we need to do is to call the set method.
                view.setLayoutParams(params);
            }
        });
        //A duration for the whole animation, this can easily become a function parameter if needed.
        animator.setDuration(1500);
        return animator;
    }

    public static boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    public static void LOG_OK(String proccessName) {
        if (proccessName.length() > 21) {
            proccessName = proccessName.substring(0, 21);
        }
        Log.i(serverPrefix + proccessName, "OK");
    }

    public static void LOG_OK_BUT_NULL(String proccessName) {
        if (proccessName.length() > 21) {
            proccessName = proccessName.substring(0, 21);
        }
        Log.i(serverPrefix + proccessName, "SERVER:OK BUT DATA:NULL");
    }

    public static void LOG_FAIL(String proccessName, String failDetail) {
        if (proccessName.length() > 21) {
            proccessName = proccessName.substring(0, 21);
        }
        Log.i(serverPrefix + proccessName, "FAIL - " + failDetail);
    }

    public static void LOG_NEREDEYIZ(String konum) {
        if (konum.length() > 21) {
            konum = konum.substring(0, 21);
        }
        Log.i(infoPrefix + neredeyiz, konum);
    }

    public static void hideKeyBoard(Context context) {
        Activity activity = (Activity) context;
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (activity.getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static String getGooglePlayAppLink(Context context) {
        return APP_GOOGLE_PLAY_DEFAULT_LINK + context.getPackageName();
    }

    public static String timeAgo(Context context, String createAt) {

        String convTime = "";
        Resources resources = context.getResources();
        //String suffix = resources.getString(R.string.ago);
        String suffix = "";


        try {
            Date nowTime = new Date();
            Date date = CommonUtils.fromISO8601UTC(createAt);

            long dateDiff = nowTime.getTime() - date.getTime();

            long second = TimeUnit.MILLISECONDS.toSeconds(dateDiff);
            long minute = TimeUnit.MILLISECONDS.toMinutes(dateDiff);
            long hour = TimeUnit.MILLISECONDS.toHours(dateDiff);
            long day = TimeUnit.MILLISECONDS.toDays(dateDiff);

            if (second < 60) {
                convTime = second + " " + resources.getString(R.string.seconds) + " " + suffix;
            } else if (minute < 60) {
                convTime = minute + " " + resources.getString(R.string.minutes) + " " + suffix;
            } else if (hour < 24) {
                convTime = hour + " " + resources.getString(R.string.hours) + " " + suffix;
            } else if (day >= 7) {
                if (day > 30) {
                    convTime = (day / 30) + " " + resources.getString(R.string.months) + " " + suffix;
                } else if (day > 360) {
                    convTime = (day / 360) + " " + resources.getString(R.string.years) + " " + suffix;
                } else {
                    convTime = (day / 7) + " " + resources.getString(R.string.weeks) + " " + suffix;
                }
            } else if (day < 7) {
                convTime = day + " " + resources.getString(R.string.days) + " " + suffix;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convTime;
    }

    public static Date fromISO8601UTC(String dateStr) {

        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df.setTimeZone(tz);

        try {
            return df.parse(dateStr);
        } catch (ParseException e) {
            Log.e("dateError", "Date Parse error");
            e.printStackTrace();
        }

        return null;
    }

    public static void setImageScaleType(PhotoSelectUtil photoSelectUtil, ImageView imageView) {
        if (photoSelectUtil.isPortraitMode())
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        else
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        }

        return false;
    }

    public static void connectionErrSnackbarShow(View view, Context context){
        Snackbar snackbar = Snackbar.make(view,
                context.getResources().getString(R.string.CHECK_YOUR_INTERNET_CONNECTION),
                Snackbar.LENGTH_SHORT);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(context.getResources().getColor(R.color.Red, null));
        TextView tv = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(context.getResources().getColor(R.color.White, null));
        snackbar.show();
    }
}
