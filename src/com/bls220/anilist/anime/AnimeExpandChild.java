package com.bls220.anilist.anime;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.bls220.anilist.R;
import com.bls220.expandablelist.ExpandableListAdapter.ExpandListChild;

public class AnimeExpandChild implements ExpandListChild {

	private String mName;
	private Float mScore;
	private Integer mEpisode;
	private Integer mMaxEpisodes;
	private Integer mAnimeID;
	private CharSequence mStatus;

	public AnimeExpandChild(String name, Integer animeID, String score, Integer episode, Integer maxEpisodes,
			CharSequence status) {
		setName(name);
		setScore(score);
		setEpisode(episode);
		setMaxEpisode(maxEpisodes);
		setAnimeID(animeID);
		setStatus(status);
	}

	public AnimeExpandChild(String name, Integer animeID) {
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

		tv = (TextView) v.findViewById(R.id.tvChapters);
		tv.setText(getEpisodeProgress());
	}

	@Override
	public View inflate(LayoutInflater inflater) {
		return inflater.inflate(R.layout.expandlist_child_item_anime, null);
	}
}