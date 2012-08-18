package com.example.words.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;

import com.example.words.activity.GameActivity;
import com.example.words.activity.GameFragment;

public class GameAdpater extends FragmentStatePagerAdapter
{

	private SparseArray<GameFragment> frags;
	private GameActivity activity;
	
	public GameAdpater(GameActivity activity, FragmentManager fragmentManager) {
		super(fragmentManager);
		this.activity = activity;
		frags = new SparseArray<GameFragment>(activity.games.size());
	}

	@Override
	public Fragment getItem(int position) {
		if(frags.size() <= position || frags.get(position) == null){
			GameFragment frag = new GameFragment(activity, activity.games.get(position), false);
			frags.put(position, frag);
		} 
		return frags.get(position);
	}

	@Override
	public int getCount() {
		return activity.games.size();
	}
	
	@Override
	public CharSequence getPageTitle(int position) {
		return activity.games.get(position).opponent;
	}
	
	public String[] getFragmentTags(){
		String[] tags = new String[activity.games.size()];
		for(int z = 0; z < frags.size(); z++){
			int key = frags.keyAt(z);
			GameFragment frag = frags.get(key);
			if(frag != null)
				tags[key] = frag.getTag();
		}
		return tags;
	}
	
	public void setFragmentTags(String[] tags){
		for(int z = 0; z < tags.length; z++){
			GameFragment frag = (GameFragment) activity.getSupportFragmentManager().findFragmentByTag(tags[z]);
			frags.put(z, frag);
		}
	}
}
