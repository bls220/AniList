package com.bls220.anilist.manga;

/**
 * 
 */

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Toast;

import com.bls220.anilist.MainActivity;
import com.bls220.anilist.R;
import com.bls220.anilist.utils.HtmlHelperTask;
import com.bls220.anilist.utils.HtmlHelperTask.OnTaskCompleteListener;
import com.bls220.anilist.utils.HtmlHelperTask.RequestParams;
import com.bls220.anilist.utils.HtmlHelperTask.TaskResults;
import com.bls220.expandablelist.ExpandableListAdapter;
import com.bls220.expandablelist.ExpandableListAdapter.ExpandGroup;
import com.bls220.expandablelist.ExpandableListAdapter.ExpandListGroup;

/**
 * @author bsmith
 * 
 */
public class MangaListFragment extends Fragment implements OnChildClickListener, OnTaskCompleteListener {

	private static final String TAG = MangaListFragment.class.getSimpleName();

	private ExpandableListAdapter mangaListAdapter;

	private final class MangaWorker extends Thread {
		private final String mangaPage;

		public MangaWorker(String mangaPage) {
			this.mangaPage = mangaPage;
		}

		@Override
		public void run() {
			Document doc = Jsoup.parse(mangaPage);
			// Get All lists
			Element allLists = doc.getElementById("lists");
			// Get List names
			Elements listHeaders = allLists.getElementsByTag("h3");
			// Get Lists
			Elements lists = allLists.getElementsByClass("list");

			for (int i = 0; i < listHeaders.size(); i++) {
				ExpandListGroup group = new ExpandGroup(listHeaders.get(i).text());
				// Create children entries
				Elements list = lists.get(i).getElementsByClass("rtitle");
				for (Element entry : list) {
					// Extract item ID
					String name = entry.select("a").text();
					String id = entry.select("a").attr("href");
					id = id.substring(7, id.indexOf("/", 7));
					// Get Columns
					Elements cols = entry.select("td.sml_col");
					// Get Score
					String score = cols.get(0).text();
					// Get Chapters
					String chapter = cols.get(1).text().replace("+", "").trim();
					if (chapter.isEmpty()) {
						chapter = "-1";
					}
					Integer curChap = Integer.parseInt(chapter);
					// Get Volumes
					String volume = cols.get(2).text().replace("+", "").trim();
					if (volume.isEmpty()) {
						volume = "-1";
					}
					Integer curVol = Integer.parseInt(volume);

					// TODO: make different child
					mangaListAdapter.addItem(new MangaExpandChild(String.format("%s", name, id), Integer.parseInt(id),
							score, curChap, curVol, group.getName()), group);
				}
			}
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (mangaListAdapter != null)
						mangaListAdapter.notifyDataSetChanged();
				}
			});
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MainActivity activity = (MainActivity) getActivity();
		mangaListAdapter = new ExpandableListAdapter(activity);
		if (activity.getUserID() > 0)
			mangaListAdapter.addItem(null, new ExpandGroup("Loading..."));
		else
			mangaListAdapter.addItem(null, new ExpandGroup("Please Login to view your list"));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main_list, container, false);

		setRetainInstance(true);

		ExpandableListView animeList = (ExpandableListView) rootView.findViewById(R.id.expandableListView);
		animeList.setAdapter(mangaListAdapter);
		animeList.setOnChildClickListener(this);

		// get Anime List
		fetchList();

		return rootView;
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
		MangaExpandChild child = (MangaExpandChild) v.getTag();
		Toast.makeText(
				v.getContext(),
				String.format("%s %1.2f %d %d", child.getName(), child.getScore(), child.getChapter(),
						child.getVolume()), Toast.LENGTH_SHORT).show();
		// Show update Dialog
		UpdateMangaDialogFragment dialog = new UpdateMangaDialogFragment();
		// get info from child
		dialog.setMangaID(child.getMangaID());
		dialog.setScore(child.getScore());
		dialog.setStatus(child.getStatus());
		dialog.setChapter(child.getChapter());
		dialog.setVolume(child.getVolume());
		dialog.show(getFragmentManager(), "update");
		return true;
	}

	@Override
	public void onTaskComplete(final TaskResults results) {
		mangaListAdapter.clear();

		if (results.status.getStatusCode() != HttpStatus.SC_ACCEPTED
				&& results.status.getStatusCode() != HttpStatus.SC_OK) {
			Toast.makeText(getActivity(),
					String.format("Error: [%d] %s", results.status.getStatusCode(), results.status.getReasonPhrase()),
					Toast.LENGTH_LONG).show();
			mangaListAdapter.addItem(null, new ExpandGroup("An Error Occured. Please Try Agian."));
			mangaListAdapter.notifyDataSetChanged();
			return;
		}

		new MangaWorker(results.output).start();

	}

	public void fetchList() {
		MainActivity activity = (MainActivity) getActivity();
		if (activity.getUserID() > 0) {
			String url = "/mangalist/" + activity.getUserID();
			requestPage(url, false, null, this);
		}
	}

	public void requestPage(String path, Boolean doPost, List<NameValuePair> paramPairs, OnTaskCompleteListener listener) {
		if (paramPairs == null) {
			paramPairs = new ArrayList<NameValuePair>();
		}
		HtmlHelperTask task = new HtmlHelperTask(getActivity(), listener);
		RequestParams params = new RequestParams(getString(R.string.baseURL).concat(path), doPost, paramPairs);
		task.execute(params);
	}
}
