package com.bls220.anilist.manga;

/**
 * 
 */

import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;

import com.bls220.anilist.MainActivity;
import com.bls220.anilist.anilist.AniEntry;
import com.bls220.anilist.anilist.AniExpandChild;
import com.bls220.anilist.anilist.AniListFragment;

/**
 * @author bsmith
 * 
 */
public class MangaListFragment extends AniListFragment {

	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
		AniExpandChild child = (AniExpandChild) v.getTag();
		AniEntry entry = child.getEntry();

		Log.v(TAG, String.format("Preparing to update %s (%d)\n" +
				"\t Episodes: %s\n" +
				"\t Score:    %f\n" +
				"\t Status:   %s",
				entry.getTitle(), entry.getID(), entry.getProgressString(), entry.getScore(), entry.getStatus()));

		// Show update Dialog
		UpdateMangaDialogFragment dialog = new UpdateMangaDialogFragment();
		// get info from child
		dialog.setMangaID(entry.getID());
		dialog.setScore(entry.getScore());
		dialog.setStatus(entry.getStatus());
		dialog.setChapter(entry.getChapter());
		dialog.setVolume(entry.getCurrent());
		dialog.show(getFragmentManager(), "update");
		return true;
	}

	@Override
	protected AniExpandChild setupAniExpandChild(AniEntry entry) {
		return new AniExpandChild(entry, false);
	}

	@Override
	protected void fetchList() {
		Runnable callback = new Runnable() {
			@Override
			public void run() {
				updateList();
			}
		};
		((MainActivity) getActivity()).fetchMangaList(callback);
	}

	@Override
	protected void updateList() {
		updateList(((MainActivity) getActivity()).getUser().getMangaLists());
	}
}
