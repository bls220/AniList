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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.Spinner;

/**
 * @author bsmith
 * 
 */
public class UpdateDialogFragment extends DialogFragment {

    //Arguments
    static final String ARG_UPDATE_TYPE   = "updateType";
    static final String ARG_STATUS_RES    = "statusRes";
    static final String ARG_STATUS        = "status";
    static final String ARG_SCORE         = "score";
    static final String ARG_NUM_LEFT      = "numLeft";
    static final String ARG_NUM_LEFT_MAX  = "numLeftMax";
    static final String ARG_NUM_RIGHT     = "numRight";
    static final String ARG_NUM_RIGHT_MAX = "numRightMax";
    static final String ARG_ID            = "ID";

    public enum EUpdateType { ANIME, MANGA };

	/*
	 * The activity that creates an instance of this dialog fragment must implement this interface in order to receive
	 * event callbacks. Each method passes the DialogFragment in case the host needs to query it.
	 */
	public interface UpdateDialogListener {
		public void onUpdateDialogPositiveClick(UpdateDialogFragment dialog, EUpdateType type, NumberPicker numLeft, NumberPicker numRight, Spinner spinnerStatus, RatingBar barScore);
	}

	// Use this instance of the interface to deliver action events
	UpdateDialogListener mListener;

	private Integer ID;
    private EUpdateType TYPE;

    NumberPicker numLeft;
    NumberPicker numRight;
    Spinner spinnerStatus;
    RatingBar barScore;

    //Change Flags
	private boolean numLeftNeedsUpdate;
	private boolean numRightNeedsUpdate;
	private boolean scoreNeedsUpdate;
	private boolean statusNeedsUpdate;


    /**
     * Create a new instance of UpdateDialogFragment, providing arguments.
     * Setting numLeftMax or numRightMax to negative value will disable and find the corresponds number picker
     */
    public static UpdateDialogFragment newInstance(EUpdateType type, @android.support.annotation.ArrayRes int statusEntriesRes, CharSequence status, float score, int numLeft, int numLeftMax, int numRight, int numRightMax, int ID) {
        UpdateDialogFragment f = new UpdateDialogFragment();

        Bundle args = new Bundle();
        args.putString(ARG_UPDATE_TYPE,type.toString());
        args.putInt(ARG_STATUS_RES, statusEntriesRes);
        args.putCharSequence(ARG_STATUS,status);
        args.putFloat(ARG_SCORE,score);
        args.putInt(ARG_NUM_LEFT,numLeft);
        args.putInt(ARG_NUM_LEFT_MAX,numLeftMax);
        args.putInt(ARG_NUM_RIGHT,numRight);
        args.putInt(ARG_NUM_RIGHT_MAX,numRightMax);
        args.putInt(ARG_ID, ID);
        f.setArguments(args);

        return f;
    }

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.fragment_dialog_update, null);

        //Get Arguments
        int statusRes = getArguments().getInt(ARG_STATUS_RES); //Must be valid or crash
        CharSequence status = getArguments().getCharSequence(ARG_STATUS); //Must be valid
        float score = getArguments().getFloat(ARG_SCORE,-1.0f);
        int numLeftVal = getArguments().getInt(ARG_NUM_LEFT,-1);
        int numLeftMax = getArguments().getInt(ARG_NUM_LEFT_MAX,-1);
        int numRightVal = getArguments().getInt(ARG_NUM_RIGHT,-1);
        int numRightMax = getArguments().getInt(ARG_NUM_RIGHT_MAX,-1);
        setID(getArguments().getInt(ARG_ID));

        TYPE = EUpdateType.valueOf(getArguments().getString(ARG_UPDATE_TYPE));

		// Setup UI
		numLeft = (NumberPicker) view.findViewById(R.id.NumberPickerLeft);
        if( numLeftMax >= 0) {
            numLeft.setMinValue(0);
            numLeft.setMaxValue(numLeftMax);
            final Integer numLeftStartVal = numLeftVal;
            numLeft.setValue(numLeftStartVal);
            numLeft.setOnValueChangedListener(new OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    numLeftNeedsUpdate = (newVal != numLeftStartVal);
                }
            });
        }
        else{
            numLeft.setMinValue(0);
            numLeft.setMaxValue(0);
            numLeft.setEnabled(false);
            numLeft.setVisibility(View.GONE);
            view.findViewById(R.id.tvNumLeft).setVisibility(View.GONE);
        }

		numRight = (NumberPicker) view.findViewById(R.id.NumberPickerRight);
        if( numRightMax >=0 ) {
            numRight.setMinValue(0);
            numRight.setMaxValue(numRightMax);
            final Integer numRightStartVal = numRightVal;
            numRight.setValue(numRightStartVal);
            numRight.setOnValueChangedListener(new OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    numRightNeedsUpdate = (newVal != numRightStartVal);
                }
            });
        }
        else{
            numRight.setMinValue(0);
            numRight.setMaxValue(0);
            numRight.setEnabled(false);
            numRight.setVisibility(View.GONE);
            view.findViewById(R.id.tvNumRight).setVisibility(View.GONE);
        }

		barScore = (RatingBar) view.findViewById(R.id.scoreRatingBar);
		final Float startScoreVal = score;
		barScore.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
				scoreNeedsUpdate = (rating != startScoreVal);
			}
		});

		spinnerStatus = (Spinner) view.findViewById(R.id.spinnerStatus);
		ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(getActivity(),statusRes, android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);
		spinnerStatus.setSelection(statusAdapter.getPosition(status));
		final Integer startStatusPos = statusAdapter.getPosition(status);
		spinnerStatus.setOnItemSelectedListener(new OnItemSelectedListener() {
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
						mListener.onUpdateDialogPositiveClick(UpdateDialogFragment.this, getType(), getNumLeft(),getNumRight(),getStatus(),getScore());
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

    private NumberPicker getNumLeft(){
        return numLeft;
    }

    private NumberPicker getNumRight(){
        return numRight;
    }

    private Spinner getStatus(){
        return spinnerStatus;
    }

    private RatingBar getScore(){
        return barScore;
    }

	/**
	 * @return the ID
	 */
	public Integer getID() {
		return ID;
	}

	/**
	 * @param ID - the ID to set
	 */
	public void setID(Integer ID) {
		this.ID = ID;
	}

    public EUpdateType getType() {
        return TYPE;
    }

	public boolean statusNeedsUpdate() {
		return statusNeedsUpdate;
	}

	public boolean scoreNeedsUpdate() {
		return scoreNeedsUpdate;
	}

	public boolean numLeftNeedsUpdate() {
		return numLeftNeedsUpdate;
	}

	public boolean numRightNeedsUpdate() {
		return numRightNeedsUpdate;
	}

}
