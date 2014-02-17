package com.bls220.anilist;

/**
 * 
 */

import java.util.ArrayList;

import org.apache.http.HttpStatus;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Toast;

import com.bls220.anilist.utils.HtmlHelperTask.OnTaskCompleteListener;
import com.bls220.anilist.utils.HtmlHelperTask.TaskResults;
import com.bls220.anilist.utils.Utils;
import com.bls220.expandablelist.ExpandableListAdapter;
import com.bls220.expandablelist.ExpandableListAdapter.ExpandGroup;
import com.bls220.expandablelist.ExpandableListAdapter.ExpandListGroup;

/**
 * @author bsmith
 * 
 */
public abstract class AniListFragment extends Fragment implements OnChildClickListener, OnTaskCompleteListener {

	protected static final String TAG = AniListFragment.class.getSimpleName();

	protected ExpandableListAdapter listAdapter;

	private final class Worker extends Thread {
		private final String mangaPage;

		public Worker(String mangaPage) {
			this.mangaPage = mangaPage;
		}

		@Override
		public void run() {
			ArrayList<ExpandListGroup> items = processHTML(mangaPage);
			for (ExpandListGroup item : items) {
				listAdapter.addItem(null, item);
			}
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (listAdapter != null)
						listAdapter.notifyDataSetChanged();
				}
			});
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MainActivity activity = (MainActivity) getActivity();
		listAdapter = new ExpandableListAdapter();
		if (activity.getUserID() > 0)
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

		// get Anime List
		fetchList();

		return rootView;
	}

	@Override
	public void onTaskComplete(final TaskResults results) {
		listAdapter.clear();

		if (results.status.getStatusCode() != HttpStatus.SC_ACCEPTED
				&& results.status.getStatusCode() != HttpStatus.SC_OK) {
			Toast.makeText(getActivity(),
					String.format("Error: [%d] %s", results.status.getStatusCode(), results.status.getReasonPhrase()),
					Toast.LENGTH_LONG).show();
			listAdapter.addItem(null, new ExpandGroup("An Error Occured. Please Try Agian."));
			listAdapter.notifyDataSetChanged();
			return;
		}

		new Worker(results.output).start();

	}

	public void fetchList() {
		MainActivity activity = (MainActivity) getActivity();
		if (activity.getUserID() > 0) {
			String url = getURLPath() + activity.getUserID();
			Utils.requestPage(activity, url, false, null, this);
		}
	}

	protected abstract ArrayList<ExpandListGroup> processHTML(String html);

	protected abstract String getURLPath();
}