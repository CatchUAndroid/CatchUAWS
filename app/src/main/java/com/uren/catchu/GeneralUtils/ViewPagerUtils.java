package com.uren.catchu.GeneralUtils;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uren.catchu.R;

public class ViewPagerUtils {

    /**
     * ViewPager layout ismi : viewPAger olmalı.
     * SliderDots panel ismi : SliderDots  olmalı.
    */
    public static void setSliderDotsPanel(int totalDots, View view, final Context context) {

        final int dotscount;
        final ImageView[] dots;
        LinearLayout sliderDotspanel;
        ViewPager viewPager;

        dotscount = totalDots;
        dots = new ImageView[dotscount];
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        sliderDotspanel = (LinearLayout) view.findViewById(R.id.SliderDots);

        sliderDotspanel.removeAllViews();

        if(dotscount<2){
            sliderDotspanel.setVisibility(View.GONE);
            return;
        }else{
            sliderDotspanel.setVisibility(View.VISIBLE);
        }

        for (int i = 0; i < dotscount; i++) {
            dots[i] = new ImageView(context);
            dots[i].setImageDrawable(ContextCompat.getDrawable(context, R.drawable.non_active_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);
            sliderDotspanel.addView(dots[i], params);
        }

        dots[0].setImageDrawable(ContextCompat.getDrawable(context, R.drawable.active_dot));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < dotscount; i++) {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(context, R.drawable.non_active_dot));
                }
                Log.i("nrlh-totalDots", String.valueOf(dotscount));
                Log.i("nrlh-position", String.valueOf(position));
                dots[position].setImageDrawable(ContextCompat.getDrawable(context, R.drawable.active_dot));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public static void setSliderDotsPanelWithTextView(final int totalDots, final int activeColorCode,
                                                      final int inactiveColorCode, final Context context, final ViewPager viewPager,
                                                      final LinearLayout layout) {
        final TextView[] dots;
        dots = new TextView[totalDots];

        final int cActive = context.getResources().getColor(activeColorCode, null);
        final int cInactive = context.getResources().getColor(inactiveColorCode, null);

        layout.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(context);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(cInactive);
            layout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[0].setTextColor(cActive);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < totalDots; i++) {
                    dots[i].setTextColor(cInactive);
                }
                dots[position].setTextColor(cActive);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }
}
