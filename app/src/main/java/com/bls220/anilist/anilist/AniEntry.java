package com.bls220.anilist.anilist;

public class AniEntry {
	private final Integer mID;
	private final String mTitle;

	// Common
	private Float mScore;
	private CharSequence mStatus;
	private Integer mPriority; // TODO: add support for this
	private Integer mCurrent; // current Episode/Volume
	private Integer mMax; // max Episode/Volume

	// Manga Specific
	private Integer mChapter; // Current chapter of manga -- should be 0 for anime

	// Manga Constructor - For Convenience
	public AniEntry(String title, Integer id, String score, Integer current, Integer max,
			CharSequence status, Integer chapter) {
		mID = id;
		mTitle = title;
		setScore(score);
		setStatus(status);
		setCurrent(current);
		setMax(max);
		setChapter(chapter);
	}

	// Common Constructor - For Convenience
	public AniEntry(String title, Integer id, String score, Integer current, Integer max,
			CharSequence status) {
		this(title, id, score, current, max, status, 0);
	}

	// Minimal Common Constructor
	public AniEntry(String title, Integer id) {
		this(title, id, "-", 0, 0, "Watching");
	}

	public Integer getID() {
		return mID;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return mTitle;
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
		if (mScore < 0) { return "-"; }
		return mScore.toString();
	}

	/**
	 * @return the current progress as a string
	 */
	public String getProgressString() {
		String str = "";
		if (mCurrent >= 0 && mMax >= 0) {
			str = String.format("%d/%d", mCurrent, mMax);
		}
		else {
			if (mCurrent >= 0)
				str = mCurrent.toString();
			if (mMax >= 0)
				str = mMax.toString();
		}
		return str;
	}

	/**
	 * @param curProgress
	 *            - the current episode/volume to be set
	 */
	public void setCurrent(Integer curProgress) {
		mCurrent = curProgress;
	}

	public Integer getCurrent() {
		return mCurrent;
	}

	public void setMax(Integer maxProgress) {
		mMax = maxProgress;
	}

	public Integer getMax() {
		return mMax;
	}

	/**
	 * @return the Status
	 */
	public String getStatus() {
		return mStatus.toString();
	}

	/**
	 * @param status
	 *            the Status to set
	 */
	public void setStatus(CharSequence status) {
		this.mStatus = status;
	}

	/**
	 * @param chapter
	 *            the chapter to set
	 */
	private void setChapter(Integer chapter) {
		this.mChapter = chapter;
	}

	/**
	 * @return the Chapter
	 */
	public Integer getChapter() {
		return mChapter;
	}
}