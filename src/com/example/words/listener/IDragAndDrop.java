package com.example.words.listener;

import android.view.DragEvent;

import com.example.words.view.Tile;

public interface IDragAndDrop {
	
	public void dragExited(Tile tile);
	public void dragEntered(Tile tile);
	public void dragEnded(Tile tile);
	public void dragDropped(Tile tile, DragEvent dragEvent);
	public boolean containsDragable();
	public void dragMoved(Tile tile, DragEvent dragEvent);

}
