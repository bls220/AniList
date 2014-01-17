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
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.RatingBar;
import android.widget.Spinner;

/**
 * @author bsmith
 * 
 */
public class UpdateDialogFragment extends DialogFragment {

	/*
	 * The activity that creates an instance of this dialog fragment must implement this interface in order to receive
	 * event callbacks. Each method passes the DialogFragment in case the host needs to query it.
	 */
	public interface UpdateDialogListener {
		public void onUpdateDialogPositiveClick(UpdateDialogFragment dialog);
	}

	// Use this instance of the interface to deliver action events
	UpdateDialogListener mListener;
	private Integer curEp;
	private Integer maxEp;
	private Integer animeID;
	private Float score;
	private String status;

	private NumberPicker epNumberPicker;
	private RatingBar scoreBar;
	private Spinner statusSpinner;
	private ArrayAdapter<String> statusAdapter;

	@SuppressWarnings("unchecked")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.dialog_update, null);

		// Setup UI
		epNumberPicker = (NumberPicker) view.findViewById(R.id.episodeNumberPicker);
		epNumberPicker.setMaxValue(maxEp >= 0 ? maxEp : 0);
		epNumberPicker.setValue(curEp);

		scoreBar = (RatingBar) view.findViewById(R.id.scoreRatingBar);
		scoreBar.setRating(score);

		statusSpinner = (Spinner) view.findViewById(R.id.spinner1);
		statusAdapter = (ArrayAdapter<String>) statusSpinner.getAdapter();
		statusSpinner.setSelection(statusAdapter.getPosition(status));

		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		builder.setView(view)
		// Add action buttons
				.setPositiveButton("Update", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						// Send the positive button event back to the host activity
						mListener.onUpdateDialogPositiveClick(UpdateDialogFragment.this);
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

	// Override the Fragment.onAttach() method to instantiate the UpdateDialogListener
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// Verify that the host activity implements the callback interface
		try {
			// Instantiate the UpdateDialogListener so we can send events to the host
			mListener = (UpdateDialogListener) activity;
		} catch (ClassCastException e) {
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(activity.toString() + " must implement UpdateDialogListener");
		}
	}

	/**
	 * @return the score
	 */
	public Float getScore() {
		if (scoreBar != null) {
			return scoreBar.getRating() * 2;
		}
		return score * 2;
	}

	/**
	 * @param score
	 *            the score to set
	 */
	public void setScore(Float score) {
		this.score = score / 2;
	}

	public void setMaxEpisodes(Integer max) {
		maxEp = max;
	}

	public void setEpisode(Integer ep) {
		curEp = ep;
	}

	public Integer getEpisode() {
		if (epNumberPicker != null) {
			return epNumberPicker.getValue();
		}
		return curEp;
	}

	/**
	 * @return the animeID
	 */
	public Integer getAnimeID() {
		return animeID;
	}

	/**
	 * @param animeID
	 *            the animeID to set
	 */
	public void setAnimeID(Integer animeID) {
		this.animeID = animeID;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		if (statusSpinner != null) {
			return (String) statusSpinner.getSelectedItem();
		}
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

}
