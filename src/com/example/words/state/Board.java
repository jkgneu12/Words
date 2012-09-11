package com.example.words.state;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.words.AppController;
import com.example.words.Utils;
import com.example.words.view.GameBoard;

public class Board implements Parcelable{
	
	public String[] tiles;

	public int[] indices;
	
	public Board(Game game) {
		clearTiles();  
	}
	
	public void update(GameBoard gb) {
		tiles = gb.getLetters();
		indices = gb.getIndices();
	}

	public String getTilesString() {
		return Utils.arrayToString(tiles);
	}

	public ArrayList<String> getTilesList() {
		return Utils.arrayToListStrip(tiles);
	}
	
	public void clearTiles() {
		this.tiles = new String[AppController.NUM_GAMEBOARD_TILES];  
		this.indices = new int[AppController.NUM_GAMEBOARD_TILES];  
	}

	
	
	
	
	
	
	public Board(Parcel in) {
		in.readStringArray(tiles);
		in.readIntArray(indices);
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeArray(tiles);
		dest.writeIntArray(indices);
	}
	
	public static final Parcelable.Creator<Board> CREATOR
	= new Parcelable.Creator<Board>() {
		public Board createFromParcel(Parcel in) {
			return new Board(in);
		}

		public Board[] newArray(int size) {
			return new Board[size];
		}
	};

	
}
