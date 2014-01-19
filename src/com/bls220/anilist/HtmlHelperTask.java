package com.bls220.anilist;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

public class HtmlHelperTask extends AsyncTask<HtmlHelperTask.RequestParams, Void, HtmlHelperTask.TaskResults> {

	private static final String TAG = HtmlHelperTask.class.getSimpleName();

	private final Activity mActivity;
	private final OnTaskCompleteListener mListener;
	private ProgressDialog mProgress;

	private static BasicHttpContext httpContext;

	public static class TaskResults {
		public StatusLine status;
		public String errorMsg = "";
		public String output = "";
	}

	public static interface OnTaskCompleteListener {
		public void onTaskComplete(TaskResults results);
	}

	public static class RequestParams {
		String mUrl = "localhost";
		boolean mPost = false;
		List<NameValuePair> mParams;

		public RequestParams(String url, Boolean doPost, List<NameValuePair> params) {
			mPost = doPost;
			mUrl = url;
			mParams = params;
		}

		public RequestParams(String url) {
			this(url, false, null);
		}

	}

	public HtmlHelperTask(Activity activity, OnTaskCompleteListener listener) {
		super();
		mActivity = activity;
		mListener = listener;
		if (httpContext == null) {
			httpContext = new BasicHttpContext();
			BasicCookieStore cookieStore = new BasicCookieStore();
			httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
		}
	}

	@Override
	protected void onPreExecute() {
		mActivity.setProgressBarIndeterminateVisibility(true);
		// mProgress = new ProgressDialog(mActivity);
		// mProgress.setTitle("Fetching...");
		// mProgress.setMessage("Please wait.");
		// mProgress.setCancelable(false);
		// mProgress.setIndeterminate(true);
		// mProgress.show();
	}

	@Override
	protected TaskResults doInBackground(RequestParams... params) {
		if (params == null)
			return null; // Nothing to do
		TaskResults results = new TaskResults();

		RequestParams inputs = params[0];

		HttpParams httpParameters = new BasicHttpParams();
		// Set the timeout for waiting for data.
		int timeoutSocket = 5000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

		// Get client
		HttpClient client = new DefaultHttpClient(httpParameters);
		HttpResponse httpResponse = null;

		try {
			if (inputs.mPost) {
				// Do Post
				HttpPost httpPost = new HttpPost(inputs.mUrl);
				// Add param data
				if (inputs.mParams != null) {
					httpPost.setEntity(new UrlEncodedFormEntity(inputs.mParams));
				}
				// Execute HTTP Post Request
				Log.d(TAG, "HTTP POST " + httpPost.getURI());
				httpResponse = client.execute(httpPost, httpContext);
			} else {
				// Do Get
				Uri.Builder uriBuilder = Uri.parse(inputs.mUrl).buildUpon().scheme("http");
				// Add param data
				if (inputs.mParams != null) {
					for (NameValuePair pair : inputs.mParams) {
						uriBuilder.appendQueryParameter(pair.getName(), pair.getValue());
					}
				}

				// Execute HTTP Get Request
				HttpGet httpGet = new HttpGet(uriBuilder.build().toString());
				Log.d(TAG, "HTTP GET " + httpGet.getURI());
				httpResponse = client.execute(httpGet, httpContext);

			}
			HttpEntity httpEntity = httpResponse.getEntity();

			results.output = EntityUtils.toString(httpEntity);
			results.status = httpResponse.getStatusLine();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			results.errorMsg = "Host Not Found.";
		} catch (SocketTimeoutException e) {
			results.errorMsg = "Server did not respond.";
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (results.status == null) {
			results.status = new BasicStatusLine(HttpVersion.HTTP_1_1, 0, results.errorMsg);
		}
		Log.d(TAG, String.format("Status: [%d] %s", results.status.getStatusCode(), results.status.getReasonPhrase()));
		return results;
	}

	@Override
	protected void onPostExecute(TaskResults results) {

		// Cleanup
		mActivity.setProgressBarIndeterminateVisibility(false);
		// mProgress.dismiss();

		// Return
		if (mListener != null)
			mListener.onTaskComplete(results);
		return;
	}

}
