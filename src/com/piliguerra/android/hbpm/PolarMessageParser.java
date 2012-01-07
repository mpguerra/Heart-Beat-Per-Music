package com.piliguerra.android.hbpm;

import java.text.DecimalFormat;

import android.util.Log;

public class PolarMessageParser {

	private static final String LOG_TAG = "PolarMessageParser";
	public final static DecimalFormat nbrFmt = new DecimalFormat("#000");

	public static HeartRateRecord parseBytes(final int[] bytes) {

		final HeartRateRecord hrr = new HeartRateRecord();
		hrr.heartRate = bytes[5];

		String byteStr = "bytes = [";
		for (final int b : bytes) {
			byteStr += nbrFmt.format(b) + ", ";
		}
		Log.i(LOG_TAG, byteStr + "]");

		return hrr;
	}

}
