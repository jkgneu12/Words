package com.example.words;

public class Game {
	
	char[] gameBoard;
	char[] myTiles;

	public Game() {
		gameBoard = new char[Constants.NUM_TILE_HOLDERS];
		myTiles = new char[Constants.NUM_MY_TILES];
	}
}
