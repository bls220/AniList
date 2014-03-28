package com.bls220.anilist;

import java.util.ArrayList;
import java.util.Arrays;

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
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnCloseListener;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bls22.anilist.parser.AniListParser;
import com.bls220.anilist.InfoFragment.EINFO_TYPE;
import com.bls220.anilist.LoginDialogFragment.LoginDialogListener;
import com.bls220.anilist.ResultFragment.OnResultClickListener;
import com.bls220.anilist.anilist.AniList;
import com.bls220.anilist.anime.AnimeListFragment;
import com.bls220.anilist.anime.UpdateAnimeDialogFragment;
import com.bls220.anilist.anime.UpdateAnimeDialogFragment.UpdateAnimeDialogListener;
import com.bls220.anilist.manga.MangaListFragment;
import com.bls220.anilist.manga.UpdateMangaDialogFragment;
import com.bls220.anilist.manga.UpdateMangaDialogFragment.UpdateMangaDialogListener;
import com.bls220.anilist.utils.ExecuteHtmlTaskQueue;
import com.bls220.anilist.utils.ExecuteHtmlTaskQueue.Task;
import com.bls220.anilist.utils.FetchBitmap;
import com.bls220.anilist.utils.FetchBitmap.OnBitmapResultListener;
import com.bls220.anilist.utils.HtmlHelperTask.OnTaskCompleteListener;
import com.bls220.anilist.utils.HtmlHelperTask.RequestParams;
import com.bls220.anilist.utils.HtmlHelperTask.TaskResults;
import com.bls220.anilist.utils.Utils;

public class MainActivity extends ActionBarActivity implements LoginDialogListener, UpdateAnimeDialogListener,
		OnResultClickListener, UpdateMangaDialogListener {

	private static final String TAG = MainActivity.class.getSimpleName();

	protected static final String SEARCH_TYPE = "type";

	private User mUser;

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

		mUser = new User();

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
			selectItem(-1);
		}

	}

	@TargetApi(14)
	private void enableHomeButton() {
		getActionBar().setHomeButtonEnabled(true);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("userID", getUser().getID());
		outState.putString("userName", mUser.getUsername());
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (savedInstanceState != null) {
			mUser.setID(savedInstanceState.getInt("userID"));
			mUser.setUsername(savedInstanceState.getString("userName"));
			if (!mUser.getUsername().isEmpty()) {
				((TextView) mDrawerHeader.findViewById(R.id.txtDescription)).setText(mUser.getUsername());
			}
			fetchAvatar();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);

		final Spinner testSpinner = new Spinner(this);
		final float spinnerWeight = 0.2f;
		testSpinner.setAdapter(new ArrayAdapter<String>(this, R.layout.search_type_list_item, getResources()
				.getStringArray(R.array.search_types_array)));
		testSpinner.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, spinnerWeight));

		final SearchView search = (SearchView) menu.findItem(R.id.menu_search).getActionView();
		search.setOnSearchClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				search.setWeightSum(1f);
				Integer count = search.getChildCount();
				Log.d(TAG, count.toString());
				for (int i = 0; i < count; i++) {
					LayoutParams params = (LayoutParams) search.getChildAt(i).getLayoutParams();
					params.width = 0;
					params.weight = (1f - spinnerWeight) / count;
				}
				search.addView(testSpinner);
				search.refreshDrawableState();
			}
		});

		search.setOnCloseListener(new OnCloseListener() {
			@Override
			public boolean onClose() {
				search.removeView(testSpinner);
				return false;
			}
		});

		search.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				ResultFragment fragment = ResultFragment.newInstance(query, (String) testSpinner.getSelectedItem());
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,
						fragment);
				ft.addToBackStack(null);
				ft.commit();
				return false;
			}

			@Override
			public boolean onQueryTextChange(String text) {
				return false;
			}
		});
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
		if (mDrawerToggle.onOptionsItemSelected(item)) { return true; }
		// Handle action buttons
		switch (item.getItemId()) {
		case R.id.menu_search:
			Toast.makeText(MainActivity.this, "Search selected", Toast.LENGTH_SHORT).show();
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
		boolean updateTitle = true;

		switch (position) {
		case 0:
			if (getUser().isLoggedIn()) {
				// idk yet
			} else {
				// Show login Dialog
				new LoginDialogFragment().show(getSupportFragmentManager(), "login");
				mDrawerLayout.closeDrawer(mDrawerList);
			}
			updateTitle = false;
			break;
		default:
			position = 1;
		case 1:
			// Show anime list
			fragment = new AnimeListFragment();
			break;
		case 2:
			// Show manga list
			fragment = new MangaListFragment();
			break;
		}

		replaceContent(fragment, args);

		// update selected item and title, then close the drawer
		mDrawerList.setItemChecked(position, true);
		if (updateTitle)
			setTitle(mDrawerTitles[position - 1]);
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	private void replaceContent(Fragment fragment, Bundle args) {
		if (fragment != null) {
			FragmentManager fragmentManager = getSupportFragmentManager();
			fragment.setArguments(args);
			fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
		}
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
						mUser.setID(Integer.parseInt(userURL.substring(6)));
					} else {
						mUser.setID(0);
					}

					// Done
					if (getUser().isLoggedIn()) {
						Toast.makeText(getBaseContext(),
								String.format("Login Successful. User ID: %d", getUser().getID()),
								Toast.LENGTH_SHORT).show();

						Runnable showDefaultFragment = new Runnable() {
							@Override
							public void run() {
								selectItem(-1);
							}
						};

						// Get anime list
						fetchAnimeList(showDefaultFragment);
						// Get manga list
						fetchMangaList(null);
						// Get avatar and info
						mUser.setUsername(doc.getElementsByTag("header").select("h1").text());
						((TextView) mDrawerHeader.findViewById(R.id.txtDescription)).setText(mUser.getUsername());
						// Use cached and then update file
						fetchAvatar();
						fetchAvatar(false);
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
		Utils.requestPage(this, "/login.php", true, params, loginListener);
	}

	@Override
	public void onUpdateMangaDialogPositiveClick(UpdateMangaDialogFragment dialog) {
		Log.i(TAG,
				String.format("Updating - ID: %d  Stat: %s  Scr: %1.2f  Ch: %d  Vol: %d", dialog.getMangaID(),
						dialog.getStatus(), dialog.getScore(), dialog.getChapter(), dialog.getVolume()));

		final String updatePath = "/update_manga.php";
		ExecuteHtmlTaskQueue execQueue = new ExecuteHtmlTaskQueue(this, new Runnable() {

			@Override
			public void run() {
				// Refresh MangaList
				fetchMangaList(null);
			}
		});
		RequestParams params;

		NameValuePair[] pairs = new BasicNameValuePair[4];
		pairs[0] = new BasicNameValuePair("manga_id", dialog.getMangaID().toString());
		pairs[1] = new BasicNameValuePair("dur", "");

		if (dialog.chapterNeedsUpdate()) {
			// Update Chapter
			pairs[2] = new BasicNameValuePair("updateVar", dialog.getChapter().toString());
			pairs[3] = new BasicNameValuePair("utype", "chap_read");
			params = new RequestParams(getString(R.string.baseURL).concat(updatePath), true, Arrays.asList(pairs
					.clone()));
			execQueue.add(new Task(params, null));
		}

		if (dialog.volumeNeedsUpdate()) {
			// Update Chapter
			pairs[2] = new BasicNameValuePair("updateVar", dialog.getVolume().toString());
			pairs[3] = new BasicNameValuePair("utype", "vol_read");
			params = new RequestParams(getString(R.string.baseURL).concat(updatePath), true, Arrays.asList(pairs
					.clone()));
			execQueue.add(new Task(params, null));
		}

		if (dialog.scoreNeedsUpdate()) {
			// Update Score
			pairs[2] = new BasicNameValuePair("updateVar", dialog.getScore().toString());
			pairs[3] = new BasicNameValuePair("utype", "score");
			params = new RequestParams(getString(R.string.baseURL).concat(updatePath), true, Arrays.asList(pairs
					.clone()));
			execQueue.add(new Task(params, null));
		}

		if (dialog.statusNeedsUpdate()) {
			// Update Status
			pairs[2] = new BasicNameValuePair("updateVar", dialog.getStatus().toLowerCase());
			pairs[3] = new BasicNameValuePair("utype", "status");
			params = new RequestParams(getString(R.string.baseURL).concat(updatePath), true, Arrays.asList(pairs
					.clone()));
			execQueue.add(new Task(params, null));
		}

		// Run all
		execQueue.execute();
	}

	@Override
	public void onUpdateAnimeDialogPositiveClick(UpdateAnimeDialogFragment dialog) {
		Log.i(TAG,
				String.format("Updating - ID: %d  Stat: %s  Scr: %1.2f  Ep: %d", dialog.getAnimeID(),
						dialog.getStatus(), dialog.getScore(), dialog.getEpisode()));

		final String updatePath = "/update_anime.php";
		ExecuteHtmlTaskQueue execQueue = new ExecuteHtmlTaskQueue(this, new Runnable() {

			@Override
			public void run() {
				// Refresh AnimeList
				fetchAnimeList(null);
			}
		});
		RequestParams params;

		NameValuePair[] pairs = new BasicNameValuePair[4];
		pairs[0] = new BasicNameValuePair("anime_id", dialog.getAnimeID().toString());
		pairs[1] = new BasicNameValuePair("dur", "24");

		if (dialog.progressNeedsUpdate()) {
			// Update Episode
			pairs[2] = new BasicNameValuePair("updateVar", dialog.getEpisode().toString());
			pairs[3] = new BasicNameValuePair("utype", "ep_watched");
			params = new RequestParams(getString(R.string.baseURL).concat(updatePath), true, Arrays.asList(pairs
					.clone()));
			execQueue.add(new Task(params, null));
		}

		if (dialog.scoreNeedsUpdate()) {
			// Update Score
			pairs[2] = new BasicNameValuePair("updateVar", dialog.getScore().toString());
			pairs[3] = new BasicNameValuePair("utype", "score");
			params = new RequestParams(getString(R.string.baseURL).concat(updatePath), true, Arrays.asList(pairs
					.clone()));
			execQueue.add(new Task(params, null));
		}

		if (dialog.statusNeedsUpdate()) {
			// Update Status
			pairs[2] = new BasicNameValuePair("updateVar", dialog.getStatus().toLowerCase());
			pairs[3] = new BasicNameValuePair("utype", "status");
			params = new RequestParams(getString(R.string.baseURL).concat(updatePath), true, Arrays.asList(pairs
					.clone()));
			execQueue.add(new Task(params, null));
		}

		// Run all
		execQueue.execute();
	}

	private void fetchAvatar() {
		fetchAvatar(true);
	}

	private void fetchAvatar(boolean useCached) {
		new FetchBitmap(MainActivity.this, "http://img.anilist.co/user/sml/" + getUser().getID() + ".jpg",
				new OnBitmapResultListener() {
					@Override
					public void onBitmapResult(Bitmap bm) {
						((ImageView) mDrawerHeader.findViewById(R.id.imgPoster)).setImageBitmap(bm);
					}
				}, useCached).execute();
	}

	public void fetchAnimeList(Runnable callback) {
		if (getUser().isLoggedIn()) {
			String url = "/animelist/" + getUser().getID();
			Utils.requestPage(this, url, false, null, new FetchListCompleteHandler(AniListParser.ETYPE.ANIME, callback));
		}
	}

	public void fetchMangaList(Runnable callback) {
		if (getUser().isLoggedIn()) {
			String url = "/mangalist/" + getUser().getID();
			Utils.requestPage(this, url, false, null, new FetchListCompleteHandler(AniListParser.ETYPE.MANGA, callback));
		}
	}

	public User getUser() {
		return mUser;
	}

	@Override
	public void onRequestInfoPage(int id, String type) {
		Toast.makeText(this, "Requesting info on result ID: " + id, Toast.LENGTH_SHORT).show();
		InfoFragment fragment = InfoFragment.newInstance(Enum.valueOf(EINFO_TYPE.class, type.toUpperCase()), id);
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment);
		ft.addToBackStack(null);
		ft.commit();
	}

	private class FetchListCompleteHandler implements OnTaskCompleteListener {
		private final AniListParser.ETYPE mAniType;
		private final Runnable mCallback;

		public FetchListCompleteHandler(AniListParser.ETYPE type, Runnable callback) {
			mAniType = type;
			mCallback = callback;
		}

		@Override
		public void onTaskComplete(TaskResults results) {
			if (results.status.getStatusCode() != HttpStatus.SC_ACCEPTED
					&& results.status.getStatusCode() != HttpStatus.SC_OK) {
				Toast.makeText(
						MainActivity.this,
						String.format("Error: [%d] %s", results.status.getStatusCode(),
								results.status.getReasonPhrase()),
						Toast.LENGTH_LONG).show();
				return;
			}

			final String html = results.output;

			new Thread(new Runnable() {

				@Override
				public void run() {
					AniList list = null;
					switch (mAniType) {
					case ANIME:
						list = getUser().getAnimeLists();
						break;
					case MANGA:
						list = getUser().getMangaLists();
						break;
					default:
						return;
					}
					AniListParser.parse(mAniType, html, list);
					if (mCallback != null) {
						MainActivity.this.runOnUiThread(mCallback);
					}
				}
			}).start();
		}

	}
}
