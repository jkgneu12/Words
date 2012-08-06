package com.example.words.adapter;

public class GameRowData {
	
	public String id;
	public String opponent;
	public String opponentId;
	public int opponentScore;
	public int yourScore;
	
	
	public GameRowData(String id, String opponent, String opponentId, int opponentScore, int yourScore) {
		this.id = id;
		this.opponent = opponent;
		this.opponentId = opponentId;
		this.opponentScore = opponentScore;
		this.yourScore = yourScore;
	}

}
