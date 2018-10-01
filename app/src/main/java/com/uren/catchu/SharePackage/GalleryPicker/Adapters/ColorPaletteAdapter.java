package com.uren.catchu.SharePackage.GalleryPicker.Adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.uren.catchu.R;
import com.uren.catchu.SharePackage.GalleryPicker.Models.ColorPaletteModel;
import com.uren.catchu.SharePackage.Utils.ColorSelectCallback;

public class ColorPaletteAdapter extends PagerAdapter {
    private Context context;
    ColorSelectCallback colorSelectCallback;
    private LayoutInflater layoutInflater;
    private static final int colorListCount = 3;
    LinearLayout colorPaletteLayout;
    int borderType;

    public ColorPaletteAdapter(Context context, int borderType, ColorSelectCallback colorSelectCallback) {
        this.context = context;
        this.colorSelectCallback = colorSelectCallback;
        this.borderType = borderType;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return colorListCount;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = layoutInflater.inflate(R.layout.color_palette, container, false);
        colorPaletteLayout = view.findViewById(R.id.colorPaletteLayout);
        setPaletteColors(position);
        container.addView(view);
        return view;
    }

    public void setPaletteColors(final int position) {
        int colorCode = 0;
        for (int index = 0; index < 9; index++) {

            if(position == 0)
                colorCode = ColorPaletteModel.getColorList1()[index];
            else if(position == 1)
                colorCode = ColorPaletteModel.getColorList2()[index];
            else if(position == 2)
                colorCode = ColorPaletteModel.getColorList3()[index];

            LinearLayout layout = new LinearLayout(context);
            layout.setLayoutParams(new LinearLayout.LayoutParams(0 , 90, 1f));
            layout.setGravity(Gravity.CENTER);

            ImageView imageView = new ImageView(context);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(90, 90));
            imageView.setBackground(context.getResources().getDrawable(borderType, null));
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.black_circle, null));
            imageView.setColorFilter(ContextCompat.getColor(context, colorCode), android.graphics.PorterDuff.Mode.SRC_IN);
            layout.addView(imageView);

            colorPaletteLayout.addView(layout);

            final int finalColorCode = colorCode;
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    colorSelectCallback.onClick(finalColorCode);
                }
            });
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}