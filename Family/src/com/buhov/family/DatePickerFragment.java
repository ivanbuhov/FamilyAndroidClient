package com.buhov.family;

import java.util.Calendar;

import com.buhov.family.FamilyHttpClient.Entities.Date;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

public class DatePickerFragment extends DialogFragment {

	private OnDateSetListener onDateSetListener;
	
	private Date date;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current date as the default date in the picker
		final Calendar c = Calendar.getInstance();
		int year = (this.date == null) ? c.get(Calendar.YEAR) : this.date.getYear();
		int month = (this.date == null) ? c.get(Calendar.MONTH) : this.date.getMonth();
		int day = (this.date == null) ? c.get(Calendar.DAY_OF_MONTH) : this.date.getDay();
	
		// Create a new instance of DatePickerDialog and return it
		return new DatePickerDialog(getActivity(), onDateSetListener, year, month, day);
	}
	
	public void setOnDateSetListener(OnDateSetListener listener) {
		this.onDateSetListener = listener;
	}
	
	public void setDate(Date date) {
		if(date != null) {
			this.date = new Date(date.getYear(), date.getMonth() - 1, date.getDay());
		}
	}
}