package com.uren.catchu.GeneralUtils;

import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class GridViewUtil {

    public static RecyclerView.ItemDecoration addItemDecoration(final int SPAN_COUNT, final int MARGING_GRID) {

        RecyclerView.ItemDecoration itemDecoration = new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

                int position = parent.getChildLayoutPosition(view);

                if (position % SPAN_COUNT == 0) {
                    //outRect.left = MARGING_GRID;
                    outRect.right = MARGING_GRID;
                    outRect.bottom = MARGING_GRID;
                    outRect.top = MARGING_GRID;
                }
                if (position % SPAN_COUNT == 1) {
                    outRect.left = MARGING_GRID / 2;
                    outRect.right = MARGING_GRID / 2;
                    outRect.bottom = MARGING_GRID / 2;
                    outRect.top = MARGING_GRID / 2;
                }
                if (position % SPAN_COUNT == 2) {
                    outRect.left = MARGING_GRID;
                    //outRect.right = MARGING_GRID;
                    outRect.bottom = MARGING_GRID;
                    outRect.top = MARGING_GRID;
                }

               /*
                outRect.left = MARGING_GRID;
                outRect.right = MARGING_GRID;
                outRect.bottom = MARGING_GRID;
                if (parent.getChildLayoutPosition(view) >= 0 && parent.getChildLayoutPosition(view) <= SPAN_COUNT) {
                    outRect.top = MARGING_GRID;
                }
                */
            }
        };

        return itemDecoration;
    }

}
