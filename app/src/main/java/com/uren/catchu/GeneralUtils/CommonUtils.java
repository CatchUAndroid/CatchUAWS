package com.uren.catchu.GeneralUtils;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.uren.catchu.GeneralUtils.PhotoUtil.PhotoSelectUtil;
import com.uren.catchu.R;

import org.joda.time.Days;
import org.joda.time.ReadableInstant;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static com.uren.catchu.Constants.StringConstants.APP_GOOGLE_PLAY_DEFAULT_LINK;

public class CommonUtils {

    public static String serverPrefix = "**";
    public static String infoPrefix = "++";
    public static String neredeyiz = "NEREDEYIZ";
    public static String exceptionErrPrefix = "--------->";


    public static final void showToastShort(Context context, String message) {

        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static final void showToastLong(Context context, String message) {

        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }


    /*public static final void showCustomToast(Context context, String message) {

            if (context == null) return;
            if (message == null || message.isEmpty()) return;
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.layout_custom_toast, null);
            View layout = (LinearLayout) view.findViewById(R.id.custom_toast_container);

            layout.setBackground(ShapeUtil.getShape(context.getResources().getColor(R.color.CornflowerBlue, null),
                    context.getResources().getColor(R.color.White, null), GradientDrawable.RECTANGLE, 15, 3));

            TextView text = (TextView) layout.findViewById(R.id.text);
            text.setText(message);
            text.setTextColor(context.getResources().getColor(R.color.White, null));

            Toast toast = new Toast(context);
            toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM, 0, 200);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();

    }*/

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

    public static String getVersion(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            String version = packInfo.versionName;
            return version;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "0";
        }
    }

    public static void commentApp(Context context) {
        try {
            String mAddress = "market://details?id=" + context.getPackageName();
            Intent marketIntent = new Intent("android.intent.action.VIEW");
            marketIntent.setData(Uri.parse(mAddress));
            context.startActivity(marketIntent);
        } catch (Exception e) {
            Toast.makeText(context, context.getString(R.string.commentFailed), Toast.LENGTH_SHORT).show();
        }
    }

    public static Drawable setDrawableSelector(Context context, int normal, int selected) {

        StateListDrawable drawable = null;

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

        drawable = new StateListDrawable();

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
        }
        return false;
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

    public static void LOG_EXCEPTION_ERR(String proccessName, String failDetail) {
        Log.i(exceptionErrPrefix + proccessName, "FAIL - " + failDetail);
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

        return convTime;
    }

    public static String getMessageTime(Context context, long time) {
        String dateValueStr = null;
        String hour = null;

        Date date = new Date(time);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        format.setTimeZone(TimeZone.getDefault());
        String formatted = format.format(date);
        hour = formatted.substring(11, 16);

        Date todayDate = new Date(System.currentTimeMillis());
        String formattedTodayDate = format.format(todayDate);

        if (formatted.substring(0, 10).equals(formattedTodayDate.substring(0, 10)))
            dateValueStr = context.getResources().getString(R.string.TODAY);
        else if (isYesterday(date))
            dateValueStr = context.getResources().getString(R.string.YESTERDAY);
        else {
            String[] monthArray = context.getResources().getStringArray(R.array.months);
            String monthValue = monthArray[Integer.parseInt(formatted.substring(5, 7)) - 1];

            dateValueStr = formatted.substring(8, 10) + " "
                    + monthValue.substring(0, 3) +
                    " " + formatted.substring(0, 4);
        }

        return dateValueStr + "  " + hour;
    }


    public static boolean isYesterday(Date d) {
        return DateUtils.isToday(d.getTime() + DateUtils.DAY_IN_MILLIS);
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

    public static void connectionErrSnackbarShow(View view, Context context) {
        Snackbar snackbar = Snackbar.make(view,
                context.getResources().getString(R.string.CHECK_YOUR_INTERNET_CONNECTION),
                Snackbar.LENGTH_SHORT);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(context.getResources().getColor(R.color.Red, null));
        TextView tv = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(context.getResources().getColor(R.color.White, null));
        snackbar.show();
    }

    public static void snackbarShow(View view, Context context, String message, int colorId) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(context.getResources().getColor(colorId, null));
        TextView tv = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(context.getResources().getColor(R.color.White, null));
        snackbar.show();
    }

    public static int getRandomColor(Context context) {

        Resources resources = context.getResources();

        int colorList[] = {
                R.color.green,
                R.color.green,
                R.color.PeachPuff,
                R.color.Gold,
                R.color.Pink,
                R.color.LightPink,
                R.color.Orange,
                R.color.LightSalmon,
                R.color.DarkOrange,
                R.color.Coral,
                R.color.HotPink,
                R.color.Tomato,
                R.color.OrangeRed,
                R.color.DeepPink,
                R.color.Fuchsia,
                R.color.Magenta,
                R.color.LightCoral,
                R.color.PaleGoldenrod,
                R.color.Violet,
                R.color.DarkSalmon,
                R.color.Lavender,
                R.color.Yellow,
                R.color.LightBlue,
                R.color.DarkGray,
                R.color.Brown,
                R.color.Sienna,
                R.color.Yellow,
                R.color.DarkOrchid,
                R.color.PaleGreen,
                R.color.DarkViolet
        };

        Random rand = new Random();
        return colorList[rand.nextInt(colorList.length)];
    }
}
