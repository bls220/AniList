package com.bls220.anilist.manga;

/**
 * 
 */

import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.bls220.anilist.AniListFragment;
import com.bls220.expandablelist.ExpandableListAdapter;
import com.bls220.expandablelist.ExpandableListAdapter.ExpandGroup;
import com.bls220.expandablelist.ExpandableListAdapter.ExpandListGroup;

/**
 * @author bsmith
 * 
 */
public class MangaListFragment extends AniListFragment {

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
	protected ArrayList<ExpandListGroup> processHTML(String html) {
		ArrayList<ExpandListGroup> groups = new ArrayList<ExpandableListAdapter.ExpandListGroup>(3);

		Document doc = Jsoup.parse(html);
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

				// add child
				group.getItems().add(
						new MangaExpandChild(String.format("%s", name, id), Integer.parseInt(id), score, curChap,
								curVol, group.getName()));
			}
			groups.add(group);
		}
		return groups;
	}

	@Override
	protected String getURLPath() {
		return "/mangalist/";
	}

}
