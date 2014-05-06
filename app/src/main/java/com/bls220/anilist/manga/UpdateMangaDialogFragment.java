/**
 * 
 */
package com.bls220.anilist.manga;

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
public class UpdateMangaDialogFragment extends DialogFragment {

	/*
	 * The activity that creates an instance of this dialog fragment must implement this interface in order to receive
	 * event callbacks. Each method passes the DialogFragment in case the host needs to query it.
	 */
	public interface UpdateMangaDialogListener {
		public void onUpdateMangaDialogPositiveClick(UpdateMangaDialogFragment dialog);
	}

	// Use this instance of the interface to deliver action events
	UpdateMangaDialogListener mListener;
	private Integer chapter;
	private Integer volume;
	private Integer maxVolume;
	private Integer mangaID;
	private Float score;
	private String status;

	private NumberPicker chapterNumberPicker;
	private NumberPicker volumeNumberPicker;
	private RatingBar scoreBar;
	private Spinner statusSpinner;
	private ArrayAdapter<String> statusAdapter;

	private boolean chapterNeedsUpdate;
	private boolean volumeNeedsUpdate;
	private boolean scoreNeedsUpdate;
	private boolean statusNeedsUpdate;

	@SuppressWarnings("unchecked")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.dialog_manga_update, null);

		// Setup UI
		chapterNumberPicker = (NumberPicker) view.findViewById(R.id.chapterNumberPicker);
		chapterNumberPicker.setMinValue(1);
		chapterNumberPicker.setMaxValue(Integer.MAX_VALUE);
		chapterNumberPicker.setValue(chapter);

		if (chapter > 0) {
			final Integer startChapterVal = chapter;
			chapterNumberPicker.setOnValueChangedListener(new OnValueChangeListener() {
				@Override
				public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
					chapterNeedsUpdate = (newVal != startChapterVal);
				}
			});
		} else {
			chapterNumberPicker.setEnabled(false);
			chapterNumberPicker.setMaxValue(0);
			chapterNumberPicker.setMinValue(0);
		}

		volumeNumberPicker = (NumberPicker) view.findViewById(R.id.volumeNumberPicker);

		if (volume >= 0 && maxVolume > 0) {
			final Integer startVolumeVal = volume;

			volumeNumberPicker.setMinValue(0);
			volumeNumberPicker.setMaxValue(maxVolume);
			volumeNumberPicker.setValue(volume);

			volumeNumberPicker.setOnValueChangedListener(new OnValueChangeListener() {
				@Override
				public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
					volumeNeedsUpdate = (newVal != startVolumeVal);
				}
			});
		} else {
			volumeNumberPicker.setEnabled(false);
			volumeNumberPicker.setMaxValue(0);
			volumeNumberPicker.setMinValue(0);
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
						mListener.onUpdateMangaDialogPositiveClick(UpdateMangaDialogFragment.this);
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
			mListener = (UpdateMangaDialogListener) activity;
		} catch (ClassCastException e) {
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(activity.toString() + " must implement UpdateDialogListener");
		}
	}

	/**
	 * @return the score
	 */
	public Float getScore() {
		if (scoreBar != null) { return scoreBar.getRating() * 2; }
		return score * 2;
	}

	/**
	 * @param score
	 *            the score to set
	 */
	public void setScore(Float score) {
		this.score = score / 2;
	}

	public void setVolume(Integer vol) {
		volume = vol;
	}

	public Integer getVolume() {
		if (volumeNumberPicker != null) { return volumeNumberPicker.getValue(); }
		return volume;
	}

	public void setMaxVolumes(Integer max) {
		maxVolume = max;
	}

	public void setChapter(Integer chap) {
		chapter = chap;
	}

	public Integer getChapter() {
		if (chapterNumberPicker != null) { return chapterNumberPicker.getValue(); }
		return chapter;
	}

	/**
	 * @return the mangaID
	 */
	public Integer getMangaID() {
		return mangaID;
	}

	/**
	 * @param mangaID
	 *            the animeID to set
	 */
	public void setMangaID(Integer mangaID) {
		this.mangaID = mangaID;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		if (statusSpinner != null) { return (String) statusSpinner.getSelectedItem(); }
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

	public boolean chapterNeedsUpdate() {
		return chapterNeedsUpdate;
	}

	public boolean volumeNeedsUpdate() {
		return volumeNeedsUpdate;
	}

}
