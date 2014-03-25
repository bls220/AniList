/**
 * 
 */
package com.bls220.anilist.anime;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;

import com.bls220.anilist.MainActivity;
import com.bls220.anilist.anilist.AniEntry;
import com.bls220.anilist.anilist.AniExpandChild;
import com.bls220.anilist.anilist.AniList;
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
		UpdateAnimeDialogFragment dialog = new UpdateAnimeDialogFragment();
		// get episode info from child
		dialog.setMaxEpisodes(entry.getMax());
		dialog.setEpisode(entry.getCurrent());
		dialog.setAnimeID(entry.getID());
		dialog.setScore(entry.getScore());
		dialog.setStatus(entry.getStatus());
		dialog.show(getFragmentManager(), "update");
		return true;
	}

	@Override
	protected AniList<AniEntry> processHTML(String html) {
		AniList<AniEntry> animeLists = ((MainActivity) getActivity()).getUser().getAnimeLists();

		Document doc = Jsoup.parse(html);
		// Get All lists
		Element allLists = doc.getElementById("lists");
		// Get List names
		Elements listHeaders = allLists.getElementsByTag("h3");
		// Get Lists
		Elements lists = allLists.getElementsByClass("list");

		// Clear all groups in anime lists
		animeLists.removeAllGroups();
		for (int i = 0; i < listHeaders.size(); i++) {
			String title = listHeaders.get(i).text();
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

				Integer animeID = Integer.parseInt(id);
				animeLists.addToGroup(title, new AniEntry(String.format("%s", name, id),
						animeID,
						score,
						curEp,
						totEp,
						title // TODO: fix for custom lists
						));
			}
		}
		return animeLists;
	}

	@Override
	protected AniExpandChild setupAniExpandChild(AniEntry entry) {
		return new AniExpandChild(entry, true);
	}

}
