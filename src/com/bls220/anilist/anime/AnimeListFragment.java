/**
 * 
 */
package com.bls220.anilist.anime;

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

/**
 * @author bsmith
 * 
 */
public class AnimeListFragment extends Fragment implements OnChildClickListener, OnTaskCompleteListener {
	private ExpandableListAdapter animeListAdapter;

	private final class AnimeWorker extends Thread {
		private final String animePage;

		public AnimeWorker(String animePage) {
			this.animePage = animePage;
		}

		@Override
		public void run() {
			Document doc = Jsoup.parse(animePage);
			// Get All lists
			Element allLists = doc.getElementById("lists");
			// Get List names
			Elements listHeaders = allLists.getElementsByTag("h3");
			// Get Lists
			Elements lists = allLists.getElementsByClass("list");

			for (int i = 0; i < listHeaders.size(); i++) {
				ExpandGroup group = new ExpandGroup(listHeaders.get(i).text());
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
					// Get Progress
					String[] progress = cols.get(1).text().replace("+", "").trim().split("/");

					if (progress[0].isEmpty()) {
						progress[0] = "-1";
					}
					Integer curEp = progress.length > 1 ? Integer.parseInt(progress[0]) : -1;
					Integer totEp = progress.length > 1 ? Integer.parseInt(progress[1]) : Integer.parseInt(progress[0]);

					animeListAdapter.addItem(new AnimeExpandChild(String.format("%s", name, id), Integer.parseInt(id),
							score, curEp, totEp, group.getName()), group);
				}
			}
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (animeListAdapter != null)
						animeListAdapter.notifyDataSetChanged();
				}
			});
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MainActivity activity = (MainActivity) getActivity();
		animeListAdapter = new ExpandableListAdapter(activity);
		if (activity.getUserID() > 0)
			animeListAdapter.addItem(null, new ExpandGroup("Loading..."));
		else
			animeListAdapter.addItem(null, new ExpandGroup("Please Login to view your list"));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main_list, container, false);

		setRetainInstance(true);

		ExpandableListView animeList = (ExpandableListView) rootView.findViewById(R.id.expandableListView);
		animeList.setAdapter(animeListAdapter);
		animeList.setOnChildClickListener(this);

		// get Anime List
		fetchList();

		return rootView;
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
		AnimeExpandChild child = (AnimeExpandChild) v.getTag();
		Toast.makeText(
				v.getContext(),
				String.format("%s %1.2f %s %s", child.getName(), child.getScore(), child.getEpisodeProgress(),
						child.getStatus()), Toast.LENGTH_SHORT).show();
		// Show update Dialog
		UpdateAnimeDialogFragment dialog = new UpdateAnimeDialogFragment();
		// get episode info from child
		dialog.setMaxEpisodes(child.getMaxEpisode());
		dialog.setEpisode(child.getEpisode());
		dialog.setAnimeID(child.getAnimeID());
		dialog.setScore(child.getScore());
		dialog.setStatus(child.getStatus());
		dialog.show(getFragmentManager(), "update");
		return true;
	}

	@Override
	public void onTaskComplete(final TaskResults results) {
		animeListAdapter.clear();

		if (results.status.getStatusCode() != HttpStatus.SC_ACCEPTED
				&& results.status.getStatusCode() != HttpStatus.SC_OK) {
			Toast.makeText(getActivity(),
					String.format("Error: [%d] %s", results.status.getStatusCode(), results.status.getReasonPhrase()),
					Toast.LENGTH_LONG).show();
			animeListAdapter.addItem(null, new ExpandGroup("An Error Occured. Please Try Agian."));
			animeListAdapter.notifyDataSetChanged();
			return;
		}

		new AnimeWorker(results.output).start();

	}

	public void fetchList() {
		MainActivity activity = (MainActivity) getActivity();
		if (activity.getUserID() > 0) {
			String url = "/animelist/" + activity.getUserID();
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
