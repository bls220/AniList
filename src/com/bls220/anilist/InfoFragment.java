package com.bls220.anilist;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bls220.anilist.utils.HtmlHelperTask.OnTaskCompleteListener;
import com.bls220.anilist.utils.HtmlHelperTask.TaskResults;
import com.bls220.anilist.utils.Utils;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * 
 */
public class InfoFragment extends Fragment implements OnTaskCompleteListener {
	public static final String ARG_ID = "id";
	public static final String ARG_TYPE = "type";
	private static final String TAG = InfoFragment.class.getSimpleName();

	public static enum EINFO_TYPE {
		ANIME, MANGA
	};

	private Integer mID;
	private EINFO_TYPE mType;

	public InfoFragment() {
		// Required empty public constructor
	}

	public static ResultFragment newInstance(EINFO_TYPE type, int id) {
		ResultFragment fragment = new ResultFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_ID, id);
		args.putInt(ARG_TYPE, type.ordinal());
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments() != null) {
			mID = getArguments().getInt(ARG_ID);
			mType = EINFO_TYPE.values()[getArguments().getInt(ARG_TYPE)];

			// Start Fetch

			String url;
			switch (mType) {
			case ANIME:
				url = "/anime/";
				break;
			case MANGA:
				url = "/manga/";
				break;
			default:
				url = null;
				break;
			}
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			Utils.requestPage(getActivity(), url + mID.toString(), false, params, this);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_info, container, false);
		return view;
	}

	@Override
	public void onTaskComplete(TaskResults results) {
		// TODO Auto-generated method stub

	}

}
