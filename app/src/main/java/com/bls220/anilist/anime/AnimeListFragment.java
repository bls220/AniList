/**
 * 
 */
package com.bls220.anilist.anime;

import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;

import com.bls220.anilist.MainActivity;
import com.bls220.anilist.R;
import com.bls220.anilist.UpdateDialogFragment;
import com.bls220.anilist.anilist.AniEntry;
import com.bls220.anilist.anilist.AniExpandChild;
import com.bls220.anilist.anilist.AniListFragment;

/**
 * @author bsmith
 * 
 */
public class AnimeListFragment extends AniListFragment {

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
		UpdateDialogFragment dialog = UpdateDialogFragment.newInstance(UpdateDialogFragment.EUpdateType.ANIME,R.array.status_array_anime,entry.getStatus(),entry.getScore(),entry.getCurrent(),entry.getMax(),-1,-1,entry.getID());
		dialog.show(getFragmentManager(), "update");
		return true;
	}

	@Override
	protected AniExpandChild setupAniExpandChild(AniEntry entry) {
		return new AniExpandChild(entry, true);
	}

	@Override
	protected void fetchList() {
		Runnable callback = new Runnable() {
			@Override
			public void run() {
				updateList();
			}
		};
		((MainActivity) getActivity()).fetchAnimeList(callback);
	}

	@Override
	protected void updateList() {
		updateList(((MainActivity) getActivity()).getUser().getAnimeLists());
	}

}
