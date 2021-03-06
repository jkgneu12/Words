package com.example.words;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.example.words.activity.BaseActivity;
import com.example.words.activity.GameActivity;
import com.example.words.activity.GameFragment;
import com.example.words.network.ValidateTask;
import com.example.words.state.Game;
import com.example.words.view.LastWord;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseTwitterUtils;

public class Utils {

	public static int getActivityWidth(Activity activity){
		DisplayMetrics pSize = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(pSize);
		return pSize.widthPixels;
	}

	public static float getDIPixels(Activity activity, int p) {
		DisplayMetrics pSize = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(pSize);
		return pSize.density * p;
	}

	public static int getTileDimensions(Activity activity){
		return (getActivityWidth(activity) / AppController.NUM_GAMEBOARD_TILES) - 5;
	}

	public static int getTileMargin(Activity activity, int numTiles) {
		int spaceForEachTile = getActivityWidth(activity) / numTiles;
		return (int)((spaceForEachTile - getTileDimensions(activity)) / 2);
	}

	public static String startValidateGameBoard(GameActivity activity, GameFragment fragment, Game game, LastWord lastWord)  {
		if(!lastWord.usedAtLeastOneTile())
			return "You must use at least 1 tile from the last word";

		String[] wordArray = game.board.tiles;
		String word = arrayToString(wordArray).trim().toUpperCase();
		if(game.prevWords.usedWords.contains(word)) 
			return "Already Used";

		String lastWordString = lastWord.getLastWord();
		if(!isNullOrEmpty(lastWordString) && word.contains(lastWordString.toUpperCase()))
			return "Can't reuse the last word with rearranging the letters";

		word = word.toLowerCase();
		
		final ProgressDialog waiting = new ProgressDialog(activity, ProgressDialog.STYLE_SPINNER);
		waiting.setTitle("Please Wait");
		waiting.setMessage("Validating \"" + word.substring(0,1).toUpperCase() + word.substring(1) + "\"");
		waiting.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				//finish();
			}
		});
		waiting.show();

		String url = "http://api.wordnik.com//v4/word.json/" + word + "/scrabbleScore?api_key=be7067c9f3d5828a9e0e618f32f08a06c3d0e3e3a6abad472";
		ValidateTask task = new ValidateTask(fragment, waiting); 
		task.execute(url);
		return "1";
	}

	public static String arrayToString(String[] wordArray) {
		StringBuilder s = new StringBuilder(wordArray.length);
		for(int z = 0; z < wordArray.length; z++){
			if(!isNull(wordArray[z]))
				s.append(wordArray[z]);
		}
		return s.toString();
	}

	public static ArrayList<String> arrayToList(String[] wordArray) {
		ArrayList<String> list = new ArrayList<String>(wordArray.length);
		for(int z = 0; z < wordArray.length; z++){
			if(isNull(wordArray[z]))
				list.add("null");
			else
				list.add(wordArray[z]);
		}
		return list;
	}
	
	public static ArrayList<String> arrayToListStrip(String[] wordArray) {
		ArrayList<String> list = new ArrayList<String>(wordArray.length);
		for(int z = 0; z < wordArray.length; z++){
			if(!isNull(wordArray[z]))
				list.add(wordArray[z]);
		}
		return list;
	}

	public static ArrayList<Integer> arrayToList(int[] intArray) {
		ArrayList<Integer> list = new ArrayList<Integer>(intArray.length);
		for(int z = 0; z < intArray.length; z++){
			list.add(intArray[z]);
		}
		return list;
	}
	
	

	public static String[] listToArray(List<Object> list) {
		String[] array = new String[list.size()];
		list.toArray(array);
		return array;
	}
	
	public static String[] listToArrayStrip(List<Object> list) {
		String[] array = new String[list.size()];
		int count = 0;
		for(int z = 0; z < list.size(); z++){
			String val = (String)list.get(z);
			if(!isNullOrEmpty(val)){
				array[count++] = val;
			}
		}
		return array;
	}


	public static boolean isNull(String string) {
		return string == null || string.equals("null");
	}

	public static boolean isNullOrEmpty(String string) {
		return isNull(string) || string.length() <= 0;
	}

	public static void initParse(Activity a){
		Parse.initialize(a, "VhnMRCE8J0r9fJLuXvGWMQvdNEw6GSxoAQCApqf2", "r4BwcVVLoX7wo92garHMfPa10O6xdmlVIS57ymt8");
		ParseFacebookUtils.initialize("490350197661440");
		ParseTwitterUtils.initialize("gruvvPslY6g4e8uEGgLqtA", "wcpS5Bt5NEfoooibKRg650Beww7EtQEPFGJTQvJs");
	}
	
	
	public static String sanitizeUserName(String userName){
		return userName.replaceAll("\\s", "");
	}

	public static float getPreviousWordSize(Activity activity) {
		return getDIPixels(activity, 16);
	}

	public static int getPreviousWordMargin(Activity activity) {
		return (int)getDIPixels(activity, 1);
	}

	public static List<List<Integer>> handleJSONArray(List<Object> list) {
		if(list == null) return null;
		List<List<Integer>> ret = new ArrayList<List<Integer>>();
		for(Object o : list){
			List<Integer> innerList = new ArrayList<Integer>();
			JSONArray json = (JSONArray)o;
			for(int z = 0; z < json.length(); z++){
				try {
					innerList.add(json.getInt(z));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			ret.add(innerList);
		}
		return ret;
	}
	
	public static boolean getSharedPrefBool(Activity activity, String pref, boolean defaultValue){
		return activity.getSharedPreferences("SETTINGS", Activity.MODE_PRIVATE).getBoolean(pref, defaultValue);
	}

	public static int countNonNulls(String[] array) {
		int sum = 0;
		for(int z = 0; z < array.length; z++){
			if(!isNullOrEmpty(array[z]))
				sum++;
		}
		return sum;
	}

	public static void handleParseErrors(ParseException e, final BaseActivity activity) {
		Log.e("Parse", e.getMessage());
		if(e.getMessage().equals("A new version is available. You must update to continue playing.")){
			AlertDialog.Builder builder = new AlertDialog.Builder(activity);
			builder.setMessage(e.getMessage())
			.setCancelable(false)
			.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse(AppController.UPDATE_SITE));
					activity.startActivity(intent);
				}
			})
			.setNegativeButton("No", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
					Toast.makeText(activity, "You must update in order to play.", Toast.LENGTH_LONG).show();
					activity.finish();
				}
			});
			builder.show();
		}
	}
}
