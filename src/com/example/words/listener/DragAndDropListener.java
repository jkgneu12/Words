package com.example.words.listener;

import android.annotation.TargetApi;
import android.view.DragEvent;
import android.view.View;
import android.view.View.OnDragListener;

import com.example.words.view.Tile;

@TargetApi(11)
public class DragAndDropListener implements OnDragListener{

	private IDragAndDrop caller;

	public DragAndDropListener(IDragAndDrop view) {
		this.caller = view;
	}

	@Override
    public boolean onDrag(View view, DragEvent dragEvent) {
		int dragAction = dragEvent.getAction();
        Tile tile = (Tile) dragEvent.getLocalState();
        if (dragAction == DragEvent.ACTION_DRAG_EXITED) {
        	caller.dragExited(tile);
        } else if (dragAction == DragEvent.ACTION_DRAG_ENTERED ) {
        	caller.dragEntered(tile);
        } else if (dragAction == DragEvent.ACTION_DRAG_ENDED) {
        	caller.dragEnded(tile);
        } else if (dragAction == DragEvent.ACTION_DROP && caller.containsDragable()) {
        	caller.dragDropped(tile, dragEvent);
        } else if (dragAction == DragEvent.ACTION_DRAG_LOCATION) {
        	caller.dragMoved(tile, dragEvent);
        }
        return true;
    }
}
