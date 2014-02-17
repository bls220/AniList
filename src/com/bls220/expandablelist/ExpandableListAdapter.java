package com.bls220.expandablelist;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.bls220.anilist.R;

/**
 * @author bsmith
 * 
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter {

	public static interface ExpandListGroup {

		ArrayList<ExpandListChild> getItems();

		void setItems(ArrayList<ExpandListChild> arrayList);

		CharSequence getName();

		void setupView(View v);

		View inflate(LayoutInflater inflater);

	}

	public static interface ExpandListChild {
		void setupView(View v);

		View inflate(LayoutInflater inflater);
	}

	private final ArrayList<ExpandListGroup> groups;

	public ExpandableListAdapter() {
		this(null);
	}

	public ExpandableListAdapter(ArrayList<ExpandListGroup> groups) {
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
				group.setItems(new ArrayList<ExpandableListAdapter.ExpandListChild>());
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
		ArrayList<ExpandListChild> chList = groups.get(groupPosition).getItems();
		return chList.get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup parent) {
		final ExpandListChild child = (ExpandListChild) getChild(groupPosition, childPosition);
		if (view == null) {
			LayoutInflater inf = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = child.inflate(inf);
		}
		child.setupView(view);

		view.setTag(child);
		return view;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		ArrayList<ExpandListChild> chList = groups.get(groupPosition).getItems();

		return chList.size();

	}

	@Override
	public Object getGroup(int groupPosition) {
		return groups.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return groups.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isLastChild, View view, ViewGroup parent) {
		ExpandListGroup group = (ExpandListGroup) getGroup(groupPosition);
		if (view == null) {
			LayoutInflater inf = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = group.inflate(inf);
		}
		group.setupView(view);
		return view;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int arg0, int arg1) {
		return true;
	}

	public static class ExpandGroup implements ExpandListGroup {

		private final CharSequence Name;
		private ArrayList<ExpandListChild> Items;

		public ExpandGroup(String name) {
			this.Name = name;
			Items = new ArrayList<ExpandableListAdapter.ExpandListChild>();
		}

		@Override
		public CharSequence getName() {
			return Name;
		}

		@Override
		public ArrayList<ExpandListChild> getItems() {
			return Items;
		}

		@Override
		public void setItems(ArrayList<ExpandListChild> Items) {
			this.Items = Items;
		}

		@Override
		public void setupView(View v) {
			TextView tv = (TextView) v.findViewById(R.id.tvGroup);
			if (tv != null) {
				tv.setText(getName());
			}
		}

		@Override
		public View inflate(LayoutInflater inflater) {
			return inflater.inflate(R.layout.expandlist_group_item, null);
		}

	}

	public static class ExpandChid implements ExpandListChild {

		private final String mText;

		public ExpandChid(String text) {
			mText = text;
		}

		@Override
		public void setupView(View v) {
			((TextView) v.findViewById(android.R.id.text1)).setText(mText);
		}

		@Override
		public View inflate(LayoutInflater inflater) {
			return inflater.inflate(android.R.layout.simple_list_item_1, null);
		}

	}
}
