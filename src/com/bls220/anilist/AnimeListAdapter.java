package com.bls220.anilist;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

/**
 * @author bsmith
 * 
 */

public class AnimeListAdapter extends BaseExpandableListAdapter {

	private final Context context;
	private final ArrayList<ExpandListGroup> groups;

	public AnimeListAdapter(Context context) {
		this(context, null);
	}

	public AnimeListAdapter(Context context, ArrayList<ExpandListGroup> groups) {
		this.context = context;
		if (groups == null) {
			this.groups = new ArrayList<ExpandListGroup>();
		} else {
			this.groups = groups;
		}
	}

	public void clear() {
		this.groups.clear();
	}

	public void addItem(ExpandListChild item, ExpandListGroup group) {
		if (!groups.contains(group)) {
			if (group.getItems() == null) {
				group.setItems(new ArrayList<AnimeListAdapter.ExpandListChild>());
			}
			groups.add(group);
		}
		int index = groups.indexOf(group);
		if (item == null)
			return;
		ArrayList<ExpandListChild> ch = groups.get(index).getItems();
		ch.add(item);
		groups.get(index).setItems(ch);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		ArrayList<ExpandListChild> chList = groups.get(groupPosition).getItems();
		return chList.get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup parent) {
		final ExpandListChild child = (ExpandListChild) getChild(groupPosition, childPosition);
		if (view == null) {
			LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = infalInflater.inflate(R.layout.expandlist_child_item, null);
		}
		TextView tv = (TextView) view.findViewById(R.id.tvName);
		tv.setText(child.getName());

		tv = (TextView) view.findViewById(R.id.tvScore);
		tv.setText(child.getScore());

		tv = (TextView) view.findViewById(R.id.tvEpisodes);
		tv.setText(child.getEpisodeProgress());

		view.setTag(child);
		return view;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		ArrayList<ExpandListChild> chList = groups.get(groupPosition).getItems();

		return chList.size();

	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return groups.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return groups.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isLastChild, View view, ViewGroup parent) {
		ExpandListGroup group = (ExpandListGroup) getGroup(groupPosition);
		if (view == null) {
			LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inf.inflate(R.layout.expandlist_group_item, null);
		}
		TextView tv = (TextView) view.findViewById(R.id.tvGroup);
		tv.setText(group.getName());
		// TODO Auto-generated method stub
		return view;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isChildSelectable(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return true;
	}

	public static class ExpandListChild {

		private String mName;
		private String mScore;
		private Integer mEpisode;
		private Integer mMaxEpisodes;
		private Integer mAnimeID;

		public ExpandListChild() {
		}

		public ExpandListChild(String name, Integer animeID, String score, Integer episode, Integer maxEpisodes) {
			setName(name);
			setScore(score);
			setEpisode(episode);
			setMaxEpisode(maxEpisodes);
			setAnimeID(animeID);
		}

		public ExpandListChild(String name, Integer animeID) {
			this(name, animeID, "-", 0, 0);
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return mName;
		}

		/**
		 * @param name
		 *            - The name to be set
		 */
		public void setName(String name) {
			mName = name;
		}

		/**
		 * @return the score
		 */
		public String getScore() {
			return mScore;
		}

		/**
		 * @param score
		 *            - the score to set
		 */
		public void setScore(String score) {
			mScore = score;
		}

		/**
		 * @return the progress by episode
		 */
		public String getEpisodeProgress() {
			String str = "";
			if (mEpisode < 0) {
				if (mMaxEpisodes >= 0) {
					str = String.valueOf(mMaxEpisodes);
				}
			} else {
				str = String.format("%d/%d", mEpisode, mMaxEpisodes);
			}
			return str;
		}

		/**
		 * @param episodes
		 *            - the episodes to set
		 */
		public void setEpisode(Integer episode) {
			mEpisode = episode;
		}

		public Integer getEpisode() {
			return mEpisode;
		}

		public void setMaxEpisode(Integer maxEpisodes) {
			mMaxEpisodes = maxEpisodes;
		}

		public Integer getMaxEpisode() {
			return mMaxEpisodes;
		}

		/**
		 * @return the AnimeID
		 */
		public Integer getAnimeID() {
			return mAnimeID;
		}

		/**
		 * @param AnimeID
		 *            the AnimeID to set
		 */
		public void setAnimeID(Integer animeID) {
			mAnimeID = animeID;
		}
	}

	public static class ExpandListGroup {

		private String Name;
		private ArrayList<ExpandListChild> Items;

		public ExpandListGroup() {
		}

		public ExpandListGroup(String name) {
			this.Name = name;
		}

		public String getName() {
			return Name;
		}

		public void setName(String name) {
			this.Name = name;
		}

		public ArrayList<ExpandListChild> getItems() {
			return Items;
		}

		public void setItems(ArrayList<ExpandListChild> Items) {
			this.Items = Items;
		}

	}

}
