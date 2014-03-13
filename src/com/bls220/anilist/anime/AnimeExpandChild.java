package com.bls220.anilist.anime;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.bls220.anilist.R;
import com.bls220.expandablelist.ExpandableListAdapter.ExpandListChild;
import com.bls220.temp.AniEntry;

public class AnimeExpandChild implements ExpandListChild {

	final AniEntry mEntry;

	public AnimeExpandChild(AniEntry entry) {
		mEntry = entry;
	}

	public AniEntry getEntry() {
		return mEntry;
	}

	@Override
	public void setupView(View v) {
		TextView tv = (TextView) v.findViewById(R.id.tvName);
		tv.setText(mEntry.getTitle());

		tv = (TextView) v.findViewById(R.id.tvScore);
		tv.setText(mEntry.getScoreText());

		tv = (TextView) v.findViewById(R.id.tvChapters);
		tv.setText(mEntry.getProgressString());
	}

	@Override
	public View inflate(LayoutInflater inflater) {
		return inflater.inflate(R.layout.expandlist_child_item_anime, null);
	}
}