package com.bls22.anilist.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.bls220.anilist.anilist.AniEntry;
import com.bls220.anilist.anilist.AniList;

public class AniListParser {

	public static enum ETYPE {
		ANIME, MANGA
	};

	public static AniList parse(ETYPE type, String html, AniList anilist) {
		switch (type) {
		case ANIME:
			return parseAnime(html, anilist);
		case MANGA:
			return parseManga(html, anilist);
		}
		return null;
	};

	public static AniList parseAnime(String html, AniList anilist) {
		Document doc = Jsoup.parse(html);
		// Get All lists
		Element allLists = doc.getElementById("lists");
		// Get List names
		Elements listHeaders = allLists.getElementsByTag("h3");
		// Get Lists
		Elements lists = allLists.getElementsByClass("list");

		// Clear all groups in anime lists
		anilist.removeAllGroups();
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
				anilist.addToGroup(title, new AniEntry(String.format("%s", name, id),
						animeID,
						score,
						curEp,
						totEp,
						title // TODO: fix for custom lists
						));
			}
		}
		return anilist;
	};

	public static AniList parseAnime(String html) {
		return parseAnime(html, new AniList());
	};

	public static AniList parseManga(String html, AniList anilist) {
		Document doc = Jsoup.parse(html);
		// Get All lists
		Element allLists = doc.getElementById("lists");
		// Get List names
		Elements listHeaders = allLists.getElementsByTag("h3");
		// Get Lists
		Elements lists = allLists.getElementsByClass("list");

		// Clear all groups in manga lists
		anilist.removeAllGroups();
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
				Integer curChap = Integer.parseInt(chapter);

				// Get Progress
				String[] progress = cols.get(2).text().replace("+", "").trim().split("/");

				if (progress[0].isEmpty()) {
					progress[0] = "-1";
				}
				Integer curVol = Integer.parseInt(progress[0]);
				Integer totVol = progress.length > 1 ? Integer.parseInt(progress[1]) : -1;

				Integer mangaID = Integer.parseInt(id);
				anilist.addToGroup(title, new AniEntry(String.format("%s", name, id),
						mangaID,
						score,
						curVol,
						totVol,
						title, // TODO: fix for custom lists
						curChap
						));
			}
		}
		return anilist;
	};

	public static AniList parseManga(String html) {
		return parseManga(html, new AniList());
	}
}
