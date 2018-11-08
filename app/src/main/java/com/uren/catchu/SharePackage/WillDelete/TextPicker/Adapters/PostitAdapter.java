package com.uren.catchu.SharePackage.WillDelete.TextPicker.Adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.uren.catchu.R;
import com.uren.catchu.SharePackage.WillDelete.TextPicker.Interfaces.PostitSelectCallback;
import com.uren.catchu.SharePackage.WillDelete.TextPicker.Models.PostitModel;

public class PostitAdapter extends PagerAdapter{
    private Context context;
    PostitSelectCallback postitSelectCallback;
    private LayoutInflater layoutInflater;
    LinearLayout postitLayout;

    public PostitAdapter(Context context, PostitSelectCallback postitSelectCallback) {
        this.context = context;
        this.postitSelectCallback = postitSelectCallback;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return PostitModel.getPostitCount();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = layoutInflater.inflate(R.layout.postit_layout, container, false);
        postitLayout = view.findViewById(R.id.postitLayout);
        setSelectedPostit();
        container.addView(view);
        return view;
    }

    public void setSelectedPostit() {
        for (int index = 0; index < getCount(); index++) {
            LinearLayout layout = new LinearLayout(context);
            layout.setLayoutParams(new LinearLayout.LayoutParams(0 , 90, 1f));
            layout.setGravity(Gravity.CENTER);

            ImageView imageView = new ImageView(context);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(90, 90));
            imageView.setImageDrawable(context.getResources().getDrawable(PostitModel.getSelectedPostit(index), null));
            layout.addView(imageView);
            postitLayout.addView(layout);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    postitSelectCallback.onSelect();
                }
            });
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
