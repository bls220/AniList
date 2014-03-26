package com.bls220.anilist.anilist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import android.util.Log;

/**
 * Represents a collection of anime/manga lists
 * 
 * @author bsmith
 * 
 */
public class AniList {

	private final String TAG = AniList.class.getSimpleName();

	private class Group extends HashSet<Integer> {
		/**
		 * 
		 */
		private static final long serialVersionUID = -2294302172275817856L;
		private final String mTitle;

		private Group(String title) {
			super();
			mTitle = title;
		}

		public String getTitle() {
			return mTitle;
		}
	}

	private ArrayList<Group> mGroups;
	static private HashMap<Integer, AniEntry> mEntries; // Anime | Manga Entries, key = anime_id

	/**
	 * Creates a new group with given the <var>title</var> if it doesn't exist.
	 * 
	 * @param title
	 *            - The title to give the new group
	 * @return - The newly created group, for convenience
	 */
	private Group createGroup(String title) {
		if (title == null || title.isEmpty()) {
			Log.e(TAG, "Group title can't be null or blank");
			return null;
		}

		if (mGroups == null) {
			mGroups = new ArrayList<Group>(1);
		}

		Group group = getGroup(title);
		if (group == null) {
			group = new Group(title);
			mGroups.add(group);
		}

		return group;
	}

	private void addToGroup(Group group, AniEntry entry) {
		addEntry(entry);
		group.add(entry.getID());
	}

	/**
	 * Finds the group with the given title
	 * 
	 * @param title
	 *            - The title of the group to find
	 * @return - the first group that matches or null if no group was found.
	 */
	private Group getGroup(String title) {
		for (Group group : mGroups) {
			if (group.getTitle().equals(title)) { return group; }
		}
		return null;
	}

	/**
	 * Gets all the entries in a group.
	 * 
	 * @param title
	 *            - the title of the group to get
	 * @return - A list of entries in the group if it exists, else null.
	 */
	public ArrayList<AniEntry> getGroupAsList(String title) {
		Group group = getGroup(title);
		if (group == null) { return null; }
		ArrayList<AniEntry> list = new ArrayList<AniEntry>(group.size());
		for (Integer id : group) {
			list.add(getEntry(id));
		}
		return list;
	}

	/**
	 * Adds an entry to the entry collection. Will update an entry if it already exists.
	 * 
	 * @param entry
	 *            - the entry to be added
	 */
	public void addEntry(AniEntry entry) {
		Integer id = entry.getID();
		if (updateEntry(entry) != null) {
			Log.w(TAG, "Overwrote entry with ID: " + id.toString());
		}
	}

	/**
	 * Updates an entry to the entry collection. Will add an entry if it doesn't exist.
	 * 
	 * @param entry
	 *            - the entry to be updated
	 * 
	 * @return - the old entry, null if the entry did not exist.
	 */
	public AniEntry updateEntry(AniEntry entry) {
		if (mEntries == null) {
			mEntries = new HashMap<Integer, AniEntry>();
		}

		// Add to entry list
		Integer id = entry.getID();
		return mEntries.put(id, entry);
	}

	/**
	 * Gets the entry with the associated ID.
	 * 
	 * @param id
	 *            - the id of the entry to get
	 * @return - the entry with the associated ID or null if no entry was found.
	 */
	public AniEntry getEntry(Integer id) {
		if (mEntries == null)
			return null;
		return mEntries.get(id);
	}

	public void addGroup(String title, List<AniEntry> entries) {
		Group group = createGroup(title);
		for (AniEntry entry : entries) {
			addToGroup(group, entry);
		}
	}

	public void addToGroup(String title, AniEntry entry) {
		Group group = createGroup(title);
		addToGroup(group, entry);
	}

	public List<String> getGroups() {
		if (mGroups == null) {
			mGroups = new ArrayList<Group>();
		}

		ArrayList<String> groups = new ArrayList<String>(mGroups.size());
		for (Group group : mGroups) {
			groups.add(group.getTitle());
		}
		return groups;
	}

	public void removeGroup(String title) {
		Group group = getGroup(title);
		if (group != null) {
			mGroups.remove(group);
		}
	}

	public void removeAllGroups() {
		mGroups = null;
	}
}
