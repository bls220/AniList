package com.bls220.anilist;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bls220.anilist.utils.FetchBitmap;
import com.bls220.anilist.utils.FetchBitmap.OnBitmapResultListener;
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

	private TextView txtTitle;
	private TextView txtDescription;
	private ImageView imgPoster;

	public InfoFragment() {
		// Required empty public constructor
	}

	public static InfoFragment newInstance(EINFO_TYPE type, int id) {
		InfoFragment fragment = new InfoFragment();
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
		txtTitle = (TextView) view.findViewById(R.id.txtTitle);
		txtDescription = (TextView) view.findViewById(R.id.txtDescription);
		txtDescription.setMovementMethod(new ScrollingMovementMethod());
		// Hack to allow scrolling inside of scrollview
		txtDescription.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent ev) {
				if (ev.getAction() == MotionEvent.ACTION_DOWN) {
					v.getParent().requestDisallowInterceptTouchEvent(true);
				}
				if (ev.getAction() == MotionEvent.ACTION_UP) {
					v.getParent().requestDisallowInterceptTouchEvent(false);
				}

				return v.onTouchEvent(ev);
			}
		});
		imgPoster = (ImageView) view.findViewById(R.id.imgPoster);
		return view;
	}

	@Override
	public void onTaskComplete(TaskResults results) {
		if (results.status.getStatusCode() != HttpStatus.SC_ACCEPTED
				&& results.status.getStatusCode() != HttpStatus.SC_OK) {
			Toast.makeText(getActivity(),
					String.format("Error: [%d] %s", results.status.getStatusCode(), results.status.getReasonPhrase()),
					Toast.LENGTH_LONG).show();
			return;
		}
		// Process page
		Document doc = Jsoup.parse(results.output);
		// Get header
		Element header = doc.getElementsByClass("animeHeader").get(0); // Should only be one
		Elements animeDes = header.select("div#animeDes");
		// Extract/Set title
		String title = animeDes.select("h1").text();
		txtTitle.setText(title);
		// Extract/Set Description
		String description = animeDes.text();
		txtDescription.setText(description);
		// Extract Poster url
		String posterSrc = getActivity().getString(R.string.baseURL) + header.select("img.poster").attr("src");
		// Fetch Poster Image
		new FetchBitmap(getActivity(), posterSrc, new OnBitmapResultListener() {
			@Override
			public void onBitmapResult(Bitmap bm) {
				if (bm != null) {
					imgPoster.setImageBitmap(bm);
				}
			}
		}).execute();
		Toast.makeText(getActivity(), posterSrc, Toast.LENGTH_LONG).show();
	}

}
