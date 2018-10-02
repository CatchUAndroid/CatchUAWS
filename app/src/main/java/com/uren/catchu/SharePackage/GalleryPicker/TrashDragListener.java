package com.uren.catchu.SharePackage.GalleryPicker;

import android.util.Log;
import android.view.DragEvent;
import android.view.View;

import com.uren.catchu.SharePackage.GalleryPicker.Interfaces.TrashDragDropCallback;

public class TrashDragListener implements View.OnDragListener {

    private static final String TAG = "TrashDragListener";
    private TrashDragDropCallback trashDragDropCallback;

    public TrashDragListener(TrashDragDropCallback trashDragDropCallback) {
        this.trashDragDropCallback = trashDragDropCallback;
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                Log.i("Info","TrashDragListener: ACTION_DRAG_STARTED");
                return true;
            case DragEvent.ACTION_DRAG_ENTERED:
                Log.i("Info","TrashDragListener: ACTION_DRAG_ENTERED");
                return true;
            case DragEvent.ACTION_DRAG_EXITED:
                Log.i("Info", "TrashDragListener: ACTION_DRAG_EXITED");
                return true;
            case DragEvent.ACTION_DRAG_LOCATION:
                return true;
            case DragEvent.ACTION_DROP:
                Log.i("Info","TrashDragListener: ACTION_DROP");
                trashDragDropCallback.onDropped();
                return true;
            case DragEvent.ACTION_DRAG_ENDED:
                Log.i("Info","TrashDragListener: ACTION_DRAG_ENDED");
                return true;
            default:
                return true;
        }
    }
}