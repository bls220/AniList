/**
 * 
 */
package com.bls220.anilist;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.bls220.anilist.AnimeListAdapter.ExpandListChild;

/**
 * @author bsmith
 * 
 */
public class AnimeListFragment extends Fragment implements OnChildClickListener {

	TextView editOutput;
	AnimeListController animeController;

	public interface AnimeListController {
		public AnimeListAdapter getAnimeListAdapter();

		public void fetchAnimeList();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main_anime, container, false);

		try {
			animeController = (AnimeListController) getActivity();
		} catch (ClassCastException e) {
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(getActivity().toString() + " must implement AnimeListController");
		}

		ExpandableListView animeList = (ExpandableListView) rootView.findViewById(R.id.animeListView);
		animeList.setAdapter(animeController.getAnimeListAdapter());
		animeList.setOnChildClickListener(this);

		// Get anime list
		animeController.fetchAnimeList();
		return rootView;
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
		ExpandListChild child = (ExpandListChild) v.getTag();
		Toast.makeText(
				v.getContext(),
				String.format("%s %1.2f %s %s", child.getName(), child.getScore(), child.getEpisodeProgress(),
						child.getStatus()), Toast.LENGTH_SHORT).show();
		// Show update Dialog
		UpdateDialogFragment dialog = new UpdateDialogFragment();
		// get episode info from child
		dialog.setMaxEpisodes(child.getMaxEpisode());
		dialog.setEpisode(child.getEpisode());
		dialog.setAnimeID(child.getAnimeID());
		dialog.setScore(child.getScore());
		dialog.setStatus(child.getStatus());
		dialog.show(getFragmentManager(), "update");
		return true;
	}
}
