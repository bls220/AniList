/**
 * 
 */
package com.bls220.anilist.anime;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.Spinner;

import com.bls220.anilist.R;

/**
 * @author bsmith
 * 
 */
public class UpdateAnimeDialogFragment extends DialogFragment {

	/*
	 * The activity that creates an instance of this dialog fragment must implement this interface in order to receive
	 * event callbacks. Each method passes the DialogFragment in case the host needs to query it.
	 */
	public interface UpdateAnimeDialogListener {
		public void onUpdateAnimeDialogPositiveClick(UpdateAnimeDialogFragment dialog);
	}

	// Use this instance of the interface to deliver action events
	UpdateAnimeDialogListener mListener;
	private Integer curEp;
	private Integer maxEp;
	private Integer animeID;
	private Float score;
	private String status;

	private NumberPicker epNumberPicker;
	private RatingBar scoreBar;
	private Spinner statusSpinner;
	private ArrayAdapter<String> statusAdapter;

	private boolean epNeedsUpdate;
	private boolean scoreNeedsUpdate;
	private boolean statusNeedsUpdate;

	@SuppressWarnings("unchecked")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.dialog_anime_update, null);

		// Setup UI
		epNumberPicker = (NumberPicker) view.findViewById(R.id.chapterNumberPicker);

		if (maxEp > 0 && curEp >= 0) {
			final Integer startEpVal = curEp;

			epNumberPicker.setMaxValue(maxEp);
			epNumberPicker.setMinValue(0);
			epNumberPicker.setValue(curEp);

			epNumberPicker.setOnValueChangedListener(new OnValueChangeListener() {
				@Override
				public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
					epNeedsUpdate = (newVal != startEpVal);
				}
			});
		} else {
			epNumberPicker.setEnabled(false);
			epNumberPicker.setMinValue(0);
			epNumberPicker.setMaxValue(0);
		}

		scoreBar = (RatingBar) view.findViewById(R.id.scoreRatingBar);
		scoreBar.setRating(score);

		final Float startScoreVal = score;
		scoreBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
				scoreNeedsUpdate = (rating != startScoreVal);
			}
		});

		statusSpinner = (Spinner) view.findViewById(R.id.spinner1);
		statusAdapter = (ArrayAdapter<String>) statusSpinner.getAdapter();
		statusSpinner.setSelection(statusAdapter.getPosition(status));

		final Integer startStatusPos = statusAdapter.getPosition(status);
		statusSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {
				statusNeedsUpdate = (pos != startStatusPos);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		builder.setView(view)
		// Add action buttons
				.setPositiveButton("Update", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						// Send the positive button event back to the host activity
						mListener.onUpdateAnimeDialogPositiveClick(UpdateAnimeDialogFragment.this);
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
			mListener = (UpdateAnimeDialogListener) activity;
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

	public boolean statusNeedsUpdate() {
		return statusNeedsUpdate;
	}

	public boolean scoreNeedsUpdate() {
		return scoreNeedsUpdate;
	}

	public boolean progressNeedsUpdate() {
		return epNeedsUpdate;
	}

}
