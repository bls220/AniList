package com.bls220.anilist.manga;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.bls220.anilist.R;
import com.bls220.expandablelist.ExpandableListAdapter.ExpandListChild;

public class MangaExpandChild implements ExpandListChild {

	private String mName;
	private Float mScore;
	private Integer mChapter;
	private Integer mVolume;
	private Integer mMangaID;
	private CharSequence mStatus;

	public MangaExpandChild(String name, Integer mangaID, String score, Integer chapter, Integer volume,
			CharSequence status) {
		setName(name);
		setScore(score);
		setChapter(chapter);
		setVolume(volume);
		setMangaID(mangaID);
		setStatus(status);
	}

	public MangaExpandChild(String name, Integer animeID) {
		// TODO: Change "Watching"
		this(name, animeID, "-", 0, 0, "Watching");
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
	public Float getScore() {
		return mScore;
	}

	/**
	 * @param score
	 *            - the score to set
	 */
	public void setScore(String score) {
		try {
			mScore = Float.parseFloat(score);
		} catch (NumberFormatException ex) {
			mScore = -1F;
		}
	}

	public String getScoreText() {
		if (mScore < 0) {
			return "-";
		}
		return mScore.toString();
	}

	/**
	 * @param chapter
	 *            - the chapter to set
	 */
	public void setChapter(Integer chapter) {
		mChapter = chapter;
	}

	public Integer getChapter() {
		return mChapter;
	}

	public void setVolume(Integer volume) {
		mVolume = volume;
	}

	public Integer getVolume() {
		return mVolume;
	}

	/**
	 * @return the MangaID
	 */
	public Integer getMangaID() {
		return mMangaID;
	}

	/**
	 * @param MangaID
	 *            the MangaID to set
	 */
	public void setMangaID(Integer mangaID) {
		mMangaID = mangaID;
	}

	/**
	 * @return the Status
	 */
	public String getStatus() {
		return mStatus.toString();
	}

	/**
	 * @param Status
	 *            the Status to set
	 */
	public void setStatus(CharSequence status) {
		this.mStatus = status;
	}

	@Override
	public void setupView(View v) {
		TextView tv = (TextView) v.findViewById(R.id.tvName);
		tv.setText(getName());

		tv = (TextView) v.findViewById(R.id.tvScore);
		tv.setText(getScoreText());

		if (getChapter() > 0) {
			tv = (TextView) v.findViewById(R.id.tvChapters);
			tv.setText(getChapter().toString());
		}

		if (getVolume() > 0) {
			tv = (TextView) v.findViewById(R.id.tvVolumes);
			tv.setText(getVolume().toString());
		}
	}

	@Override
	public View inflate(LayoutInflater inflater) {
		return inflater.inflate(R.layout.expandlist_child_item_manga, null);
	}
}