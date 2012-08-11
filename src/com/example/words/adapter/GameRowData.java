package com.example.words.adapter;

import android.os.Parcel;
import android.os.Parcelable;

public class GameRowData implements Parcelable{

	public String id;
	public String opponent;
	public String opponentId;
	public int opponentScore;
	public int yourScore;
	public boolean currentPlayer;


	public GameRowData(String id, String opponent, String opponentId, int opponentScore, int yourScore, boolean currentPlayer) {
		this.id = id;
		this.opponent = opponent;
		this.opponentId = opponentId;
		this.opponentScore = opponentScore;
		this.yourScore = yourScore;
		this.currentPlayer = currentPlayer;
	}


	public GameRowData(Parcel in) {
		this.id = in.readString();
		this.opponent = in.readString();
		this.opponentId = in.readString();
		this.opponentScore = in.readInt();
		this.yourScore = in.readInt();
		this.currentPlayer = in.readByte() == 1;
	}


	@Override
	public int describeContents() {
		return 0;
	}


	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(opponent);
		dest.writeString(opponentId);
		dest.writeInt(opponentScore);
		dest.writeInt(yourScore);
		dest.writeByte((byte) (currentPlayer ? 1 : 0));
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
