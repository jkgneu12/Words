package com.example.words.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.example.words.adapter.GameRowData;
import com.example.words.state.Game;

public class Score extends TextView {

	public Score(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void refreshUI(Game game, GameRowData gameData) {
		String scoreText;
		if(gameData.isCurrentPlayer)
			scoreText = getScorePrefix(game,gameData) + game.currentPlayer.score + " : " + game.waitingPlayer.score;
		else
			scoreText = getScorePrefix(game,gameData) + game.waitingPlayer.score + " : " + game.currentPlayer.score;
		setText(scoreText);
	}

	public String getScorePrefix(Game game, GameRowData gameData) {
		if(gameData.isGameOver)
			return getEndScorePrefix(game, gameData);
		else
			return getLiveScorePrefix(game,gameData);
	}

	public String getLiveScorePrefix(Game game, GameRowData gameData){
		if(gameData.isCurrentPlayer){
			if(game.currentPlayer.score > game.waitingPlayer.score)
				return "Winning ";
			else if(game.currentPlayer.score < game.waitingPlayer.score)
				return "Losing ";
		} else {
			if(game.currentPlayer.score > game.waitingPlayer.score)
				return "Losing ";
			else if(game.currentPlayer.score < game.waitingPlayer.score)
				return "Winning ";
		}
		return "Tied ";
	}

	public String getEndScorePrefix(Game game, GameRowData gameData){
		boolean forMe = gameData.isCurrentPlayer;
		if(forMe){
			if(game.currentPlayer.score > game.waitingPlayer.score)
				return "Won ";
			else if(game.currentPlayer.score < game.waitingPlayer.score)
				return "Lost ";
		} else {
			if(game.currentPlayer.score > game.waitingPlayer.score)
				return "Lost ";
			else if(game.currentPlayer.score < game.waitingPlayer.score)
				return "Won ";
		}
		return "Tied ";
	}

}
