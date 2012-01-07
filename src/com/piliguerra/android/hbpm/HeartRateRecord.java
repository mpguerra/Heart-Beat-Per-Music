package com.piliguerra.android.hbpm;

import java.util.Date;
import java.util.HashMap;

public class HeartRateRecord {

	public static final String FIELD_NAME_DATE_TAKEN = "date_taken";
	public static final String FIELD_NAME_HEART_RATE = "heartrate";
	
	/**
	 * this number denotes the version of this class. so that we can decide if
	 * the structure of this class is the same as the one being deserialized
	 * into it
	 */
	private static final long serialVersionUID = 1L;
	public Date dateTaken;
	public int heartRate;

	public HeartRateRecord() {
	}

	public HeartRateRecord(final HashMap<String, String> aRow) {
		dateTaken = new Date(aRow.get(FIELD_NAME_DATE_TAKEN));
		heartRate = Integer.parseInt(aRow.get(FIELD_NAME_HEART_RATE));
		}

}
