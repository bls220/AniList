/**
 * 
 */
package com.bls220.anilist;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * @author bsmith
 * 
 */
public class LoginDialogFragment extends DialogFragment {

	/*
	 * The activity that creates an instance of this dialog fragment must implement this interface in order to receive
	 * event callbacks. Each method passes the DialogFragment in case the host needs to query it.
	 */
	public interface LoginDialogListener {
		public void onLoginDialogPositiveClick(LoginDialogFragment dialog);
	}

	// Use this instance of the interface to deliver action events
	LoginDialogListener mListener;
	EditText mEditUser;
	EditText mEditPass;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.dialog_login, null);

		mEditUser = (EditText) view.findViewById(R.id.editUsername);
		mEditPass = (EditText) view.findViewById(R.id.editPassword);

		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		builder.setView(view)
		// Add action buttons
				.setPositiveButton("Login", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						// Send the positive button event back to the host activity
						mListener.onLoginDialogPositiveClick(LoginDialogFragment.this);
					}
				}).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						// Send the negative button event back to the host activity
						dialog.cancel();
					}
				});
		return builder.create();
	}

	// Override the Fragment.onAttach() method to instantiate the LoginDialogListener
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// Verify that the host activity implements the callback interface
		try {
			// Instantiate the LoginDialogListener so we can send events to the host
			mListener = (LoginDialogListener) activity;
		} catch (ClassCastException e) {
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(activity.toString() + " must implement LoginDialogListener");
		}
	}

	public String getUsername() {
		if (mEditUser != null) {
			return mEditUser.getText().toString();
		}
		return "";
	}

	public String getPassword() {
		if (mEditPass != null) {
			return mEditPass.getText().toString();
		}
		return "";
	}

}
