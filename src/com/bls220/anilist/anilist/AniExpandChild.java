package com.bls220.anilist.anilist;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.bls220.anilist.R;
import com.bls220.anilist.R.id;
import com.bls220.anilist.R.layout;
import com.bls220.expandablelist.ExpandableListAdapter.ExpandListChild;

public class AniExpandChild implements ExpandListChild {

	final AniEntry mEntry;
	final boolean mIsAnime;

	public AniExpandChild(AniEntry entry, boolean isAnime) {
		mEntry = entry;
		mIsAnime = isAnime;
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

		// TODO: add chapter
		tv = (TextView) v.findViewById(R.id.tvChapters);
		tv.setText("TBD");
		if (mIsAnime) {
			tv.setVisibility(View.GONE);
		}

		tv = (TextView) v.findViewById(R.id.tvVolumes);
		tv.setText(mEntry.getProgressString());
	}

	@Override
	public View inflate(LayoutInflater inflater) {
		return inflater.inflate(R.layout.expandlist_child_item_manga, null);
	}
}