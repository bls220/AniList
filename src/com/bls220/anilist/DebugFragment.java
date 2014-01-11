/**
 * 
 */
package com.bls220.anilist;

import org.apache.http.HttpStatus;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bls220.anilist.AnimeListAdapter.ExpandListChild;
import com.bls220.anilist.AnimeListAdapter.ExpandListGroup;
import com.bls220.anilist.HtmlHelperTask.OnTaskCompleteListener;
import com.bls220.anilist.HtmlHelperTask.TaskResults;

/**
 * @author bsmith
 * 
 */
public class DebugFragment extends Fragment {

	TextView editOutput;
	AnimeListAdapter animeListAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main_debug, container, false);

		editOutput = (TextView) rootView.findViewById(R.id.editOutput);
		editOutput.setMovementMethod(new ScrollingMovementMethod());

		((Button) (rootView.findViewById(R.id.btnBasicTest))).setOnClickListener(new BasicTestClickListener());

		((Button) (rootView.findViewById(R.id.btnLoginTest))).setOnClickListener(new LoginTestClickListener());

		((Button) (rootView.findViewById(R.id.btnAnimeList))).setOnClickListener(new AnimeListTestClickListener());

		((Button) (rootView.findViewById(R.id.btnClear))).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				editOutput.setText("");
			}
		});

		ExpandableListView animeList = (ExpandableListView) rootView.findViewById(R.id.animeListView);
		animeListAdapter = new AnimeListAdapter(getActivity());
		animeListAdapter.addItem(new ExpandListChild("Dummy Child"), new ExpandListGroup("Dummy Group"));
		animeList.setAdapter(animeListAdapter);

		return rootView;
	}

	private class BasicTestClickListener implements OnClickListener, OnTaskCompleteListener {
		@Override
		public void onClick(View v) {
			Toast.makeText(getActivity(), "Basic Test", Toast.LENGTH_SHORT).show();
			((MainActivity) getActivity()).requestPage("", false, null, this);
		}

		@Override
		public void onTaskComplete(TaskResults results) {
			editOutput.append(results.output);
			if (results.status.getStatusCode() != HttpStatus.SC_ACCEPTED
					&& results.status.getStatusCode() != HttpStatus.SC_OK) {
				Toast.makeText(
						getActivity(),
						String.format("Error: [%d] %s", results.status.getStatusCode(),
								results.status.getReasonPhrase()), Toast.LENGTH_LONG).show();
			}
			Toast.makeText(getActivity(), "Basic Test Complete", Toast.LENGTH_SHORT).show();
		}
	}

	private class LoginTestClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			Toast.makeText(getActivity(), "Login Test", Toast.LENGTH_SHORT).show();
			// Show login Dialog
			new LoginDialogFragment().show(getFragmentManager(), "login");
		}
	}

	private class AnimeListTestClickListener implements OnClickListener, OnTaskCompleteListener {
		@Override
		public void onClick(View v) {
			Toast.makeText(getActivity(), "Anime List Test", Toast.LENGTH_SHORT).show();
			// Fetch anime page
			// TODO: acquire User_ID
			((MainActivity) getActivity()).requestPage("/animelist/11631/SubHobo", false, null, this);
		}

		@Override
		public void onTaskComplete(TaskResults results) {
			if (results.status.getStatusCode() != HttpStatus.SC_ACCEPTED
					&& results.status.getStatusCode() != HttpStatus.SC_OK) {
				Toast.makeText(
						getActivity(),
						String.format("Error: [%d] %s", results.status.getStatusCode(),
								results.status.getReasonPhrase()), Toast.LENGTH_LONG).show();
				return;
			}
			animeListAdapter.clear();
			Document doc = Jsoup.parse(results.output);
			// Get All lists
			Element allLists = doc.getElementById("lists");
			// Get List names
			Elements listHeaders = allLists.getElementsByTag("h3");
			// Get Lists
			Elements lists = allLists.getElementsByClass("list");

			for (int i = 0; i < listHeaders.size(); i++) {
				ExpandListGroup group = new ExpandListGroup(listHeaders.get(i).text());
				// Create children entries
				Elements list = lists.get(i).getElementsByClass("rtitle");
				for (Element entry : list) {
					// Extract anime ID
					String name = entry.select("a").text();
					String id = entry.select("a").attr("href");
					id = id.substring(7, id.indexOf("/", 7));
					// Get Score
					String score = entry.select("[class~=^cscr]").text();
					// Get Progress
					String progress = "";
					progress = entry.select(".plus").get(0).parent().text();

					animeListAdapter
							.addItem(
									new ExpandListChild(String.format("%s (%s)    %s    %s", name, id, score, progress)),
									group);
				}
			}

			animeListAdapter.notifyDataSetChanged();
			Toast.makeText(getActivity(), "Anime List Test Complete", Toast.LENGTH_SHORT).show();
		}
	}
}
