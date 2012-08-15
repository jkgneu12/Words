package com.example.words.state;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.words.Constants;
import com.example.words.view.GameBoard;
import com.example.words.view.LastWord;
import com.parse.ParseObject;

public class LastTurn implements Parcelable {
	
	private Game game;
	
	public String[] currentLastWord;
	public String[] completeLastWord;
	public boolean[] partOfLastWord;
	
	public boolean lastPlayerPassed;
	
	
	public LastTurn(Game game) {
		this.game = game;
		
		this.currentLastWord = new String[Constants.NUM_TILE_HOLDERS];  
		this.completeLastWord = new String[Constants.NUM_TILE_HOLDERS];  
		
		this.lastPlayerPassed = false;
	}
	
	public void update(GameBoard gb, LastWord lw) {
		currentLastWord = lw.getLetters();
		partOfLastWord = gb.partOfLastWordArray();
	}
	
	public void refresh(ParseObject obj) {
		currentLastWord = Constants.listToArrayStrip(obj.getList("lastWord"));
		completeLastWord = Constants.listToArrayStrip(obj.getList("lastWord"));
		lastPlayerPassed = obj.getBoolean("passed");
	}
	
	
	
	
	
	
	
	public LastTurn(Parcel in) {
		in.readStringArray(currentLastWord);
		in.readStringArray(completeLastWord);
		in.readBooleanArray(partOfLastWord);
		lastPlayerPassed = in.readByte() == 1;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeArray(currentLastWord);
		dest.writeArray(completeLastWord);
		dest.writeBooleanArray(partOfLastWord);
		dest.writeByte((byte)(lastPlayerPassed ? 1 : 0));
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	public static final Parcelable.Creator<LastTurn> CREATOR
	= new Parcelable.Creator<LastTurn>() {
		public LastTurn createFromParcel(Parcel in) {
			return new LastTurn(in);
		}

		public LastTurn[] newArray(int size) {
			return new LastTurn[size];
		}
	};


	

}