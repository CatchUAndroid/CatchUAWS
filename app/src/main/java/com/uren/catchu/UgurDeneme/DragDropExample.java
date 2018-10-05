package com.uren.catchu.UgurDeneme;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.ClipData;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.uren.catchu.R;

public class DragDropExample extends AppCompatActivity {

    private ImageView paper;
    private ImageView trash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen();
        setContentView(R.layout.activity_drag_drop_example);
        paper = (ImageView) findViewById(R.id.paper);
        paper.setTag("paper");
        trash = (ImageView) findViewById(R.id.trash);
        trash.setTag("trash");

        trash.setOnDragListener(new TrashDragListener(
                R.drawable.approve_icon_96,
                R.drawable.cancel_gray));

        trash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paper.setVisibility(View.VISIBLE);
            }
        });

        paper.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                view.startDrag(data, shadowBuilder, view, 0);
                view.setVisibility(View.INVISIBLE);
                return true;
            }
        });
    }

    private static class TrashDragListener implements View.OnDragListener {

        private static final String TAG = "TrashDragListener";

        private int enterShape;
        private int normalShape;
        private boolean hit;

        public TrashDragListener(int enterShape, int normalShape) {
            this.enterShape = enterShape;
            this.normalShape = normalShape;
        }

        @Override
        public boolean onDrag(View v, DragEvent event) {
            final ImageView containerView = (ImageView) v;
            final ImageView draggedView = (ImageView) event.getLocalState();
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    Log.d(TAG, "onDrag: ACTION_DRAG_STARTED");
                    hit = false;
                    return true;
                case DragEvent.ACTION_DRAG_ENTERED:
                    Log.d(TAG, "onDrag: ACTION_DRAG_ENTERED");
                    containerView.setImageResource(enterShape);
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    Log.d(TAG, "onDrag: ACTION_DRAG_EXITED");
                    containerView.setImageResource(normalShape);
                    return true;
                case DragEvent.ACTION_DROP:
                    Log.d(TAG, "onDrag: ACTION_DROP");
                    hit = true;
                    draggedView.post(new Runnable() {
                        @Override
                        public void run() {
                            draggedView.setVisibility(View.GONE);
                        }
                    });
                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    Log.d(TAG, "onDrag: ACTION_DRAG_ENDED");
                    containerView.setImageResource(normalShape);
                    v.setVisibility(View.VISIBLE);
                    if (!hit) {
                        draggedView.post(new Runnable() {
                            @Override
                            public void run() {
                                draggedView.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                    return true;
                default:
                    return true;
            }
        }
    }

    private void setFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}