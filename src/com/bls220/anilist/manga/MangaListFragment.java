package com.bls220.anilist.manga;

/**
 * 
 */

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
		dialog.setChapter(-1); // TODO: fix
		dialog.setVolume(entry.getCurrent());
		dialog.show(getFragmentManager(), "update");
		return true;
	}

	@Override
	protected AniList<AniEntry> processHTML(String html) {
		AniList<AniEntry> mangaLists = ((MainActivity) getActivity()).getUser().getMangaLists();

		Document doc = Jsoup.parse(html);
		// Get All lists
		Element allLists = doc.getElementById("lists");
		// Get List names
		Elements listHeaders = allLists.getElementsByTag("h3");
		// Get Lists
		Elements lists = allLists.getElementsByClass("list");

		// Clear all groups in manga lists
		mangaLists.removeAllGroups();
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
				// Get Chapters
				String chapter = cols.get(1).text().replace("+", "").trim();
				if (chapter.isEmpty()) {
					chapter = "-1";
				}
				Integer curChap = Integer.parseInt(chapter); // TODO: Do something with this THING

				// Get Progress
				String[] progress = cols.get(2).text().replace("+", "").trim().split("/");

				if (progress[0].isEmpty()) {
					progress[0] = "-1";
				}
				Integer curVol = Integer.parseInt(progress[0]);
				Integer totVol = progress.length > 1 ? Integer.parseInt(progress[1]) : -1;

				Integer mangaID = Integer.parseInt(id);
				mangaLists.addToGroup(title, new AniEntry(String.format("%s", name, id),
						mangaID,
						score,
						curVol,
						totVol,
						title // TODO: fix for custom lists
						));
			}
		}
		return mangaLists;
	}

	@Override
	protected String getURLPath() {
		return "/mangalist/";
	}

	@Override
	protected AniExpandChild setupAniExpandChild(AniEntry entry) {
		return new AniExpandChild(entry, false);
	}

}
