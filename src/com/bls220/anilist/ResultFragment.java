package com.bls220.anilist;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bls220.anilist.utils.HtmlHelperTask.OnTaskCompleteListener;
import com.bls220.anilist.utils.HtmlHelperTask.TaskResults;
import com.bls220.anilist.utils.Utils;

/**
 * A fragment representing a list of Items.
 * <p />
 * Large screen devices (such as tablets) are supported by replacing the ListView with a GridView.
 * <p />
 * Activities containing this fragment MUST implement the {@link Callbacks} interface.
 */
public class ResultFragment extends Fragment implements AbsListView.OnItemClickListener, OnTaskCompleteListener {

	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	public static final String ARG_QUERY = "query";
	public static final String ARG_QUERY_TYPE = "query_type";
	private static final String TAG = ResultFragment.class.getSimpleName();

	// TODO: Rename and change types of parameters
	private String mQuery;
	private String mType;

	private OnResultClickListener mListener;

	/**
	 * The fragment's ListView/GridView.
	 */
	private AbsListView mListView;

	/**
	 * The Adapter which will be used to populate the ListView/GridView with Views.
	 */
	private ListAdapter mAdapter;

	// TODO: Rename and change types of parameters
	public static ResultFragment newInstance(String query, String type) {
		ResultFragment fragment = new ResultFragment();
		Bundle args = new Bundle();
		args.putString(ARG_QUERY, query);
		args.putString(ARG_QUERY_TYPE, type);
		fragment.setArguments(args);
		return fragment;
	}

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation
	 * changes).
	 */
	public ResultFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments() != null) {
			mQuery = getArguments().getString(ARG_QUERY);
			mType = getArguments().getString(ARG_QUERY_TYPE).toLowerCase();
			Toast.makeText(getActivity(), String.format("%s \t %s", mType, mQuery), Toast.LENGTH_SHORT).show();

			// Start Search

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("q", mQuery));
			params.add(new BasicNameValuePair("type", mType));
			Utils.requestPage(getActivity(), "/getSearch.php", false, params, this);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_result, container, false);

		// Set the adapter
		mListView = (AbsListView) view.findViewById(android.R.id.list);
		((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);
		// Set OnItemClickListener so we can be notified on item clicks
		mListView.setOnItemClickListener(this);

		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnResultClickListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnResultClickListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (null != mListener) {
			// Notify the active callbacks interface (the activity, if the
			// fragment is attached to one) that an item has been selected.
			mListener.onRequestInfoPage(((ResultHolder) parent.getAdapter().getItem(position)).id);
		}
	}

	/**
	 * The default content for this Fragment has a TextView that is shown when the list is empty. If you would like to
	 * change the text, call this method to supply the text it should use.
	 */
	public void setEmptyText(CharSequence emptyText) {
		View emptyView = mListView.getEmptyView();

		if (emptyText instanceof TextView) {
			((TextView) emptyView).setText(emptyText);
		}
	}

	@Override
	public void onTaskComplete(TaskResults results) {
		// Get results
		Document doc = Jsoup.parse(results.output);
		Elements links = doc.select("a[href]");
		ArrayList<ResultHolder> vals = new ArrayList<ResultHolder>();
		ListIterator<Element> itr = links.listIterator();
		while (itr.hasNext()) {
			Element result = itr.next();
			String name = result.text();
			String id = result.attr("href");
			id = id.substring(7);
			vals.add(new ResultHolder(name, Integer.parseInt(id)));
			Log.d(TAG, "Found: " + name);
		}
		mAdapter = new ArrayAdapter<ResultHolder>(getActivity(), android.R.layout.simple_list_item_1,
				android.R.id.text1, vals);
		mListView.setAdapter(mAdapter);
	}

	/**
	 * This interface must be implemented by activities that contain this fragment to allow an interaction in this
	 * fragment to be communicated to the activity and potentially other fragments contained in that activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html" >Communicating with Other
	 * Fragments</a> for more information.
	 */
	public interface OnResultClickListener {
		// TODO: Update argument type and name
		public void onRequestInfoPage(int id);
	}

	private static class ResultHolder {
		String title;
		Integer id;

		ResultHolder(String _title, Integer _id) {
			title = _title;
			id = _id;
		}

		@Override
		public String toString() {
			return title;
		}
	}
}
