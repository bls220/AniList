package com.bls220.anilist.anilist;

/**
 * 
 */

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

import com.bls220.anilist.MainActivity;
import com.bls220.anilist.R;
import com.bls220.expandablelist.ExpandableListAdapter;
import com.bls220.expandablelist.ExpandableListAdapter.ExpandGroup;
import com.bls220.expandablelist.ExpandableListAdapter.ExpandListChild;
import com.bls220.expandablelist.ExpandableListAdapter.ExpandListGroup;

/**
 * @author bsmith
 * 
 */
public abstract class AniListFragment extends Fragment implements OnChildClickListener {

	protected static final String TAG = AniListFragment.class.getSimpleName();

	protected ExpandableListAdapter listAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MainActivity activity = (MainActivity) getActivity();
		listAdapter = new ExpandableListAdapter();
		if (activity.getUser().isLoggedIn())
			listAdapter.addItem(null, new ExpandGroup("Loading..."));
		else
			listAdapter.addItem(null, new ExpandGroup("Please Login to view your list"));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main_list, container, false);

		setRetainInstance(true);

		ExpandableListView animeList = (ExpandableListView) rootView.findViewById(R.id.expandableListView);
		animeList.setAdapter(listAdapter);
		animeList.setOnChildClickListener(this);

		// get list
		updateList();

		return rootView;
	}

	protected void updateList(AniList aniLists) {
		if (aniLists == null)
			return;

		final ArrayList<ExpandListGroup> groups = new ArrayList<ExpandListGroup>(3);
		List<String> animeGroups = aniLists.getGroups();
		for (String title : animeGroups) {
			ExpandGroup group = new ExpandGroup(title);
			ArrayList<ExpandListChild> children = new ArrayList<ExpandListChild>();
			for (AniEntry entry : aniLists.getGroupAsList(title)) {
				children.add(setupAniExpandChild(entry));
			}
			group.setItems(children);
			groups.add(group);
		}

		if (listAdapter != null) {
			listAdapter.clear();
			for (ExpandListGroup item : groups) {
				listAdapter.addItem(null, item);
			}
			listAdapter.notifyDataSetChanged();
		}

	}

	protected abstract void updateList();

	protected abstract void fetchList();

	protected abstract AniExpandChild setupAniExpandChild(AniEntry entry);
}
