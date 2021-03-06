package com.example.words.adapter;

import android.os.Parcel;
import android.os.Parcelable;

public class GameRowData extends Data implements Parcelable{

	public String id;
	public String opponent;
	public String opponentId;
	public int opponentScore;
	public int yourScore;
	public boolean isCurrentPlayer;
	public boolean isGameOver;
	public String opponentUserName;


	public GameRowData(String id, String opponent, String opponentUserName, String opponentId, int opponentScore, int yourScore, boolean currentPlayer, boolean gameOver) {
		this.id = id;
		this.opponent = opponent;
		this.opponentUserName = opponentUserName;
		this.opponentId = opponentId;
		this.opponentScore = opponentScore;
		this.yourScore = yourScore;
		this.isCurrentPlayer = currentPlayer;
		this.isGameOver = gameOver;
	}
	
	@Override
	public String getFilterableName() {
		return opponentUserName.toLowerCase();
	}


	public GameRowData(Parcel in) {
		this.id = in.readString();
		this.opponent = in.readString();
		this.opponentUserName = in.readString();
		this.opponentId = in.readString();
		this.opponentScore = in.readInt();
		this.yourScore = in.readInt();
		this.isCurrentPlayer = in.readByte() == 1;
		this.isGameOver = in.readByte() == 1;
	}


	@Override
	public int describeContents() {
		return 0;
	}


	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(opponent);
		dest.writeString(opponentUserName);
		dest.writeString(opponentId);
		dest.writeInt(opponentScore);
		dest.writeInt(yourScore);
		dest.writeByte((byte) (isCurrentPlayer ? 1 : 0));
		dest.writeByte((byte) (isGameOver ? 1 : 0));
	}

	public static final Parcelable.Creator<GameRowData> CREATOR
	= new Parcelable.Creator<GameRowData>() {
		public GameRowData createFromParcel(Parcel in) {
			return new GameRowData(in);
		}

		public GameRowData[] newArray(int size) {
			return new GameRowData[size];
		}
	};

}
