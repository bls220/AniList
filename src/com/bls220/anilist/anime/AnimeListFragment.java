/**
 * 
 */
package com.bls220.anilist.anime;

import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.bls220.anilist.AniListFragment;
import com.bls220.expandablelist.ExpandableListAdapter.ExpandGroup;
import com.bls220.expandablelist.ExpandableListAdapter.ExpandListGroup;

/**
 * @author bsmith
 * 
 */
public class AnimeListFragment extends AniListFragment {

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
	protected ArrayList<ExpandListGroup> processHTML(String html) {
		ArrayList<ExpandListGroup> groups = new ArrayList<ExpandListGroup>(3);

		Document doc = Jsoup.parse(html);
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

				group.getItems().add(
						new AnimeExpandChild(String.format("%s", name, id), Integer.parseInt(id), score, curEp, totEp,
								group.getName()));
			}
			groups.add(group);
		}
		return groups;
	}

	@Override
	protected String getURLPath() {
		return "/animelist/";
	}

}
