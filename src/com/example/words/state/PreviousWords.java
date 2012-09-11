package com.example.words.state;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.words.Utils;
import com.parse.ParseObject;

public class PreviousWords implements Parcelable {
	
	private Game game;
	
	public List<String> usedWords; 
	public List<Integer> scores;
	public List<String> turns; 
	public List<List<Integer>> reused;
	
	
	public PreviousWords(Game game) {
		this.game = game;
		
		usedWords = new ArrayList<String>();
		scores = new ArrayList<Integer>();
		turns = new ArrayList<String>();
		reused = new ArrayList<List<Integer>>();
	}
	
	public void addGameBoardToUsedWord(int points, List<Integer> reusedIndices) {
		usedWords.add(game.board.getTilesString());
		if(scores == null)
			scores = new ArrayList<Integer>();
		scores.add(points);
		if(turns == null)
			turns = new ArrayList<String>();
		turns.add(game.currentPlayer.id);
		if(reused == null)
			reused = new ArrayList<List<Integer>>();
		reused.add(reusedIndices);
		
	}
	
	public void updateParseObject(ParseObject parseObject) {
		parseObject.put("usedWords", usedWords);
		parseObject.put("scores", scores);
		parseObject.put("turns", turns);
		parseObject.put("reused", reused);
	}
	
	
	public void refresh(ParseObject obj) {
		usedWords = obj.getList("usedWords");
		scores = obj.getList("scores");
		turns = obj.getList("turns");
		reused = Utils.handleJSONArray(obj.getList("reused"));
	}
	
	
	
	public PreviousWords(Parcel in) {
		usedWords = in.readArrayList(null);
		scores = in.readArrayList(null);
		turns = in.readArrayList(null);
		reused = in.readArrayList(null);
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeList(usedWords);
		dest.writeList(scores);
		dest.writeList(turns);
		dest.writeList(reused);
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	public static final Parcelable.Creator<PreviousWords> CREATOR
	= new Parcelable.Creator<PreviousWords>() {
		public PreviousWords createFromParcel(Parcel in) {
			return new PreviousWords(in);
		}

		public PreviousWords[] newArray(int size) {
			return new PreviousWords[size];
		}
	};


	

}
