package com.bls220.anilist.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;

import android.app.Activity;

import com.bls220.anilist.R;
import com.bls220.anilist.utils.HtmlHelperTask.OnTaskCompleteListener;
import com.bls220.anilist.utils.HtmlHelperTask.RequestParams;

public final class Utils {
	static public HtmlHelperTask requestPage(Activity activity, String path, Boolean doPost,
			List<NameValuePair> paramPairs, OnTaskCompleteListener listener) {
		if (paramPairs == null) {
			paramPairs = new ArrayList<NameValuePair>();
		}
		HtmlHelperTask task = new HtmlHelperTask(activity, listener);
		RequestParams params = new RequestParams(activity.getString(R.string.baseURL).concat(path), doPost, paramPairs);
		task.execute(params);
		return task;
	}

}
