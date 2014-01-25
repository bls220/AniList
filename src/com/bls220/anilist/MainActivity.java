package com.bls220.anilist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.annotation.TargetApi;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bls220.anilist.ExecuteHtmlTaskQueue.Task;
import com.bls220.anilist.FetchBitmap.OnBitmapResultListner;
import com.bls220.anilist.HtmlHelperTask.OnTaskCompleteListener;
import com.bls220.anilist.HtmlHelperTask.RequestParams;
import com.bls220.anilist.HtmlHelperTask.TaskResults;
import com.bls220.anilist.LoginDialogFragment.LoginDialogListener;
import com.bls220.anilist.UpdateDialogFragment.UpdateDialogListener;

public class MainActivity extends ActionBarActivity implements LoginDialogListener, UpdateDialogListener {

	private static final String TAG = MainActivity.class.getSimpleName();

	Integer userID = 0;
	String userName = "";

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private View mDrawerHeader;

	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private String[] mDrawerTitles;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		setContentView(R.layout.activity_main);

		mTitle = mDrawerTitle = getTitle();
		mDrawerTitles = getResources().getStringArray(R.array.drawer_items_array);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		// set a custom shadow that overlays the main content when the drawer opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		// set up the drawer's list view with items and click listener
		mDrawerHeader = getLayoutInflater().inflate(R.layout.list_item_avatar, null);
		mDrawerList.addHeaderView(mDrawerHeader);
		mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mDrawerTitles));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		// enable ActionBar app icon to behave as action to toggle nav drawer
		getActionBar().setDisplayHomeAsUpEnabled(true);
		enableHomeButton();

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description for accessibility */
		R.string.drawer_close /* "close drawer" description for accessibility */
		) {
			@Override
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			selectItem(1);
		}
	}

	@TargetApi(14)
	private void enableHomeButton() {
		getActionBar().setHomeButtonEnabled(true);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("userID", userID);
		outState.putString("userName", userName);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (savedInstanceState != null) {
			userID = savedInstanceState.getInt("userID");
			userName = savedInstanceState.getString("userName");
			if (!userName.isEmpty()) {
				((TextView) mDrawerHeader.findViewById(R.id.textView1)).setText(userName);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content view
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action buttons
		switch (item.getItemId()) {
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/* The click listner for ListView in the navigation drawer */
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			selectItem(position);
		}
	}

	private void selectItem(int position) {
		// update the main content by replacing fragments
		Fragment fragment = null;
		Bundle args = new Bundle();
		String tag;
		boolean updateTitle = true;

		switch (position) {
		case 0:
			if (userID > 0) {
				// idk yet
			} else {
				// Show login Dialog
				new LoginDialogFragment().show(getSupportFragmentManager(), "login");
				mDrawerLayout.closeDrawer(mDrawerList);
			}
			updateTitle = false;
			tag = "user";
			break;
		case 1:
			// Show anime list
			fragment = new AnimeListFragment();
			tag = "anime";
			break;
		case 2:
			// Show debug screen
			fragment = new DebugFragment();
			tag = "debug";
			break;
		default:
			fragment = new DummySectionFragment();
			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position);
			fragment.setArguments(args);
			tag = "dummy";
			updateTitle = false;
			break;
		}

		if (fragment != null) {
			FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, tag).commit();
		}

		// update selected item and title, then close the drawer
		mDrawerList.setItemChecked(position, true);
		if (updateTitle)
			setTitle(mDrawerTitles[position - 1]);
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main_dummy, container, false);
			TextView dummyTextView = (TextView) rootView.findViewById(R.id.section_label);
			dummyTextView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
			return rootView;
		}
	}

	public void requestPage(String path, Boolean doPost, List<NameValuePair> paramPairs, OnTaskCompleteListener listener) {
		if (paramPairs == null) {
			paramPairs = new ArrayList<NameValuePair>();
		}
		HtmlHelperTask task = new HtmlHelperTask(this, listener);
		RequestParams params = new RequestParams(getString(R.string.baseURL).concat(path), doPost, paramPairs);
		task.execute(params);
	}

	@Override
	public void onLoginDialogPositiveClick(LoginDialogFragment dialog) {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>(3);
		params.add(new BasicNameValuePair("username", dialog.getUsername()));
		params.add(new BasicNameValuePair("password", dialog.getPassword()));
		params.add(new BasicNameValuePair("remember_me", "1"));
		OnTaskCompleteListener loginListener = new OnTaskCompleteListener() {
			@Override
			public void onTaskComplete(TaskResults results) {
				if (results.status.getStatusCode() == HttpStatus.SC_ACCEPTED
						|| results.status.getStatusCode() == HttpStatus.SC_OK) {
					// Get UserID
					Document doc = Jsoup.parse(results.output);
					String userURL = doc.select("a[href~=^/user/").attr("href");
					if (!userURL.isEmpty()) {
						userID = Integer.parseInt(userURL.substring(6));
					} else {
						userID = 0;
					}

					// Debug stuff
					TextView editOutput = ((TextView) findViewById(R.id.editOutput));
					if (editOutput != null) {
						editOutput.append(userID.toString());
						Toast.makeText(getBaseContext(), "Login Test Complete", Toast.LENGTH_SHORT).show();
					}

					// Done
					if (userID > 0) {
						Toast.makeText(getBaseContext(), String.format("Login Successful. User ID: %d", userID),
								Toast.LENGTH_SHORT).show();
						// Get anime list
						fetchAnimeList();
						// Get avatar and info
						userName = doc.getElementsByTag("header").select("h1").text();
						((TextView) mDrawerHeader.findViewById(R.id.textView1)).setText(userName);
						FetchBitmap task = new FetchBitmap("http://img.anilist.co/user/sml/" + userID + ".jpg",
								new OnBitmapResultListner() {
									@Override
									public void onBitmapResult(Bitmap bm) {
										((ImageView) mDrawerHeader.findViewById(R.id.imageView1)).setImageBitmap(bm);
									}
								});
						task.execute();
						return;
					}
				} else {
					Log.d(TAG,
							String.format("Login Error: [%d] %s", results.status.getStatusCode(),
									results.status.getReasonPhrase()));
				}

				// Error
				Toast.makeText(getBaseContext(), "Login Failed", Toast.LENGTH_LONG).show();
			}
		};
		requestPage("/login.php", true, params, loginListener);
	}

	@Override
	public void onUpdateDialogPositiveClick(UpdateDialogFragment dialog) {
		Log.i(TAG,
				String.format("Updating - ID: %d  Stat: %s  Scr: %1.2f  Ep: %d", dialog.getAnimeID(),
						dialog.getStatus(), dialog.getScore(), dialog.getEpisode()));

		final String updatePath = "/update_anime.php";
		ExecuteHtmlTaskQueue execQueue = new ExecuteHtmlTaskQueue(this, new Runnable() {

			@Override
			public void run() {
				// Refresh AnimeList
				fetchAnimeList();
			}
		});
		RequestParams params;

		NameValuePair[] pairs = new BasicNameValuePair[4];
		pairs[0] = new BasicNameValuePair("anime_id", dialog.getAnimeID().toString());
		pairs[1] = new BasicNameValuePair("dur", "24");

		if (dialog.ProgressNeedsUpdate()) {
			// Update Episode
			pairs[2] = new BasicNameValuePair("updateVar", dialog.getEpisode().toString());
			pairs[3] = new BasicNameValuePair("utype", "ep_watched");
			params = new RequestParams(getString(R.string.baseURL).concat(updatePath), true, Arrays.asList(pairs
					.clone()));
			execQueue.add(new Task(params, null));
		}

		if (dialog.ScoreNeedsUpdate()) {
			// Update Score
			pairs[2] = new BasicNameValuePair("updateVar", dialog.getScore().toString());
			pairs[3] = new BasicNameValuePair("utype", "score");
			params = new RequestParams(getString(R.string.baseURL).concat(updatePath), true, Arrays.asList(pairs
					.clone()));
			execQueue.add(new Task(params, null));
		}

		if (dialog.StatusNeedsUpdate()) {
			// Update Status
			pairs[2] = new BasicNameValuePair("updateVar", dialog.getStatus());
			pairs[3] = new BasicNameValuePair("utype", "status");
			params = new RequestParams(getString(R.string.baseURL).concat(updatePath), true, Arrays.asList(pairs
					.clone()));
			execQueue.add(new Task(params, null));
		}

		// Run all
		execQueue.execute();
	}

	public void fetchAnimeList() {
		AnimeListFragment animeFrag = (AnimeListFragment) getSupportFragmentManager().findFragmentByTag("anime");
		if (animeFrag != null && animeFrag.isVisible()) {
			animeFrag.fetchAnimeList();
		}
	}
}
