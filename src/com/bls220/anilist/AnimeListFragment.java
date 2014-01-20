/**
 * 
 */
package com.bls220.anilist;

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
import android.widget.TextView;
import android.widget.Toast;

import com.bls220.anilist.AnimeListAdapter.ExpandListChild;
import com.bls220.anilist.AnimeListAdapter.ExpandListGroup;
import com.bls220.anilist.HtmlHelperTask.OnTaskCompleteListener;
import com.bls220.anilist.HtmlHelperTask.RequestParams;
import com.bls220.anilist.HtmlHelperTask.TaskResults;

/**
 * @author bsmith
 * 
 */
public class AnimeListFragment extends Fragment implements OnChildClickListener, OnTaskCompleteListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		animeListAdapter = new AnimeListAdapter(getActivity());
		animeListAdapter.addItem(null, new ExpandListGroup("Please Login to view your list"));
	}

	private AnimeListAdapter animeListAdapter;

	TextView editOutput;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main_anime, container, false);

		setRetainInstance(true);

		ExpandableListView animeList = (ExpandableListView) rootView.findViewById(R.id.animeListView);
		animeList.setAdapter(animeListAdapter);
		animeList.setOnChildClickListener(this);

		// get Anime List
		fetchAnimeList();

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

	@Override
	public void onTaskComplete(TaskResults results) {
		if (results.status.getStatusCode() != HttpStatus.SC_ACCEPTED
				&& results.status.getStatusCode() != HttpStatus.SC_OK) {
			Toast.makeText(getActivity(),
					String.format("Error: [%d] %s", results.status.getStatusCode(), results.status.getReasonPhrase()),
					Toast.LENGTH_LONG).show();
			return;
		}
		animeListAdapter.clear();
		Document doc = Jsoup.parse(results.output);
		// Get All lists
		Element allLists = doc.getElementById("lists");
		// Get List names
		Elements listHeaders = allLists.getElementsByTag("h3");
		// Get Lists
		Elements lists = allLists.getElementsByClass("list");

		for (int i = 0; i < listHeaders.size(); i++) {
			ExpandListGroup group = new ExpandListGroup(listHeaders.get(i).text());
			// Create children entries
			Elements list = lists.get(i).getElementsByClass("rtitle");
			for (Element entry : list) {
				// Extract anime ID
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

				animeListAdapter.addItem(new ExpandListChild(String.format("%s", name, id), Integer.parseInt(id),
						score, curEp, totEp, group.getName()), group);
			}
		}

		animeListAdapter.notifyDataSetChanged();
	}

	public void fetchAnimeList() {
		MainActivity activity = (MainActivity) getActivity();
		if (activity.userID > 0)
			requestPage("/animelist/" + activity.userID, false, null, this);
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
