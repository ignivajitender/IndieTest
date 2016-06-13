package com.igniva.indiecore.utils;

import android.widget.AutoCompleteTextView;
import android.widget.EditText;

public class FieldValidators {
	/**
	 * @param editText
	 *            - EditText field which need to be validated
	 * @return - Returns true if editText is Null or empty
	 * 
	 */
	public static boolean isNullOrEmpty(EditText editText) {
		return editText.getText() == null
				|| editText.getText().toString().trim().length() == 0;
	}

	/**
	 * @param autoCompleteTextView
	 *            - AutoCompleteTextView field which need to be validated
	 * @return Returns true if AutoCompleteTextView is Null or empty
	 */
	public static boolean isNullOrEmpty(
			AutoCompleteTextView autoCompleteTextView) {
		return autoCompleteTextView.getText() == null
				|| autoCompleteTextView.getText().toString().length() == 0;
	}

}
