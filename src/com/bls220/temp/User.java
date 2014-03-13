package com.bls220.temp;

/**
 * Class that holds information about a given user such as id, anime lists, manga lists, etc.
 * 
 * @author bsmith
 * 
 */
public class User {

	// Basic Info
	String mUsername;
	Integer mID;

	// Lists
	private final AniList<AniEntry> mAnimeLists;
	private final AniList<AniEntry> mMangaLists;

	public User() {
		mAnimeLists = new AniList<AniEntry>();
		mMangaLists = new AniList<AniEntry>();
		mID = 0;
		mUsername = "";
	}

	public boolean isLoggedIn() {
		return mID != null && mID > 0;
	}

	public void setID(Integer id) {
		mID = id;
	}

	public Integer getID() {
		return mID;
	}

	public void setUsername(String username) {
		mUsername = username;
	}

	public String getUsername() {
		return mUsername;
	}

	public AniList<AniEntry> getAnimeLists() {
		return mAnimeLists;
	}

	public AniList<AniEntry> getMangaLists() {
		return mMangaLists;
	}

}
