/**
 * 
 */
package com.bls220.anilist;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bls220.anilist.HtmlHelperTask.OnTaskCompleteListener;
import com.bls220.anilist.HtmlHelperTask.RequestParams;
import com.bls220.anilist.HtmlHelperTask.TaskResults;

/**
 * @author bsmith
 * 
 */
public class DebugFragment extends Fragment {

	TextView editOutput;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main_debug,
				container, false);

		editOutput = (TextView) rootView.findViewById(R.id.editOutput);
		editOutput.setMovementMethod(new ScrollingMovementMethod());

		((Button) (rootView.findViewById(R.id.btnBasicTest)))
				.setOnClickListener(new BasicTestClickListener());

		((Button) (rootView.findViewById(R.id.btnLoginTest)))
				.setOnClickListener(new LoginTestClickListener());

		((Button) (rootView.findViewById(R.id.btnClear)))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						editOutput.setText("");
					}
				});

		return rootView;
	}

	private void requestPage(String path, Boolean doPost,
			List<NameValuePair> paramPairs, OnTaskCompleteListener listener) {
		if (paramPairs == null) {
			paramPairs = new ArrayList<NameValuePair>();
		}
		HtmlHelperTask task = new HtmlHelperTask(getActivity(), listener);
		RequestParams params = new RequestParams(getString(R.string.baseURL)
				.concat(path), doPost, paramPairs);
		task.execute(params);
	}

	private class BasicTestClickListener implements OnClickListener,
			OnTaskCompleteListener {
		@Override
		public void onClick(View v) {
			Toast.makeText(getActivity(), "Basic Test", Toast.LENGTH_SHORT)
					.show();
			requestPage("", false, null, this);
		}

		@Override
		public void onTaskComplete(TaskResults results) {
			editOutput.append(results.output);
			if (results.status.getStatusCode() != HttpStatus.SC_ACCEPTED
					&& results.status.getStatusCode() != HttpStatus.SC_OK) {
				Toast.makeText(
						getActivity(),
						String.format("Error: [%d] %s",
								results.status.getStatusCode(),
								results.status.getReasonPhrase()),
						Toast.LENGTH_LONG).show();
			}
			Toast.makeText(getActivity(), "Basic Test Complete",
					Toast.LENGTH_SHORT).show();
		}
	}

	private class LoginTestClickListener implements OnClickListener,
			OnTaskCompleteListener {
		@Override
		public void onClick(View v) {
			Toast.makeText(getActivity(), "Login Test", Toast.LENGTH_SHORT)
					.show();
			// TODO: Show login Dialog
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>(3);
			params.add(new BasicNameValuePair("username", "SubHobo"));
			params.add(new BasicNameValuePair("password", "password"));
			params.add(new BasicNameValuePair("remember_me", "1"));
			requestPage("/login.php", true, params, this);
		}

		@Override
		public void onTaskComplete(TaskResults results) {
			editOutput.append(results.output);
			if (results.status.getStatusCode() != HttpStatus.SC_ACCEPTED
					&& results.status.getStatusCode() != HttpStatus.SC_OK) {
				Toast.makeText(
						getActivity(),
						String.format("Error: [%d] %s",
								results.status.getStatusCode(),
								results.status.getReasonPhrase()),
						Toast.LENGTH_LONG).show();
			}
			Toast.makeText(getActivity(), "Login Test Complete",
					Toast.LENGTH_SHORT).show();
		}
	}
}
