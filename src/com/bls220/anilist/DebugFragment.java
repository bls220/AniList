/**
 * 
 */
package com.bls220.anilist;

import org.apache.http.HttpStatus;

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
import com.bls220.anilist.HtmlHelperTask.TaskResults;

/**
 * @author bsmith
 * 
 */
public class DebugFragment extends Fragment {

	TextView editOutput;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main_debug, container, false);

		editOutput = (TextView) rootView.findViewById(R.id.editOutput);
		editOutput.setMovementMethod(new ScrollingMovementMethod());

		((Button) (rootView.findViewById(R.id.btnBasicTest))).setOnClickListener(new BasicTestClickListener());

		((Button) (rootView.findViewById(R.id.btnLoginTest))).setOnClickListener(new LoginTestClickListener());

		((Button) (rootView.findViewById(R.id.btnClear))).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				editOutput.setText("");
			}
		});

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
}
