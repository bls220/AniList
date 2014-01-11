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
		ExpandListChild child = (ExpandListChild) getChild(groupPosition, childPosition);
		if (view == null) {
			LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
			view = infalInflater.inflate(R.layout.expandlist_child_item, null);
		}
		TextView tv = (TextView) view.findViewById(R.id.tvChild);
		tv.setText(child.getName().toString());
		tv.setTag(child.getTag());
		// TODO Auto-generated method stub
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
			LayoutInflater inf = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
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

		private String Name;
		private String Tag;

		public ExpandListChild() {
		}

		public ExpandListChild(String name) {
			this.Name = name;
		}

		public String getName() {
			return Name;
		}

		public void setName(String Name) {
			this.Name = Name;
		}

		public String getTag() {
			return Tag;
		}

		public void setTag(String Tag) {
			this.Tag = Tag;
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
