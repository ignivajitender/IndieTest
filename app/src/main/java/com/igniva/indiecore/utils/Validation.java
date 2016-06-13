package com.igniva.indiecore.utils;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Common Utility to Validate any type of user input
 */
public class Validation {

    public static boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean isValidPassword(String passsword) {
        boolean isValid=false;
        //TODO to check the validation of a password
        return isValid;
    }


//        }else{
//            Utility.showAlertDialog("Please Enter all the fields Code!",context);
//            countryCode.setFocusableInTouchMode(true);
//            countryCode.requestFocus();
//            return false;
//        }
//        if (!isNullorEmpty(mobileNumber)) {
//            return true;
//        }else{
//            Utility.showAlertDialog("Please Enter Mobile Number!",  context);
//            mobileNumber.setFocusableInTouchMode(true);
//            mobileNumber.requestFocus();
//            return false;
//        }
//    }


    public static boolean isValidMobile(Context context,EditText countryCode, EditText mobileNumber) {
        boolean isValid = false;
        if (!isNullorEmpty(countryCode)&&!isNullorEmpty(mobileNumber)) {
            String mCountryCode = countryCode.getText().toString();
            String mMobileNumber = mobileNumber.getText().toString();
            return true;
        }else{
            return isValid;
        }
//        }else{
//            Utility.showAlertDialog("Please Enter all the fields Code!",context);
//            countryCode.setFocusableInTouchMode(true);
//            countryCode.requestFocus();
//            return false;
//        }
//        if (!isNullorEmpty(mobileNumber)) {
//            return true;
//        }else{
//            Utility.showAlertDialog("Please Enter Mobile Number!",  context);
//            mobileNumber.setFocusableInTouchMode(true);
//            mobileNumber.requestFocus();
//            return false;
//        }
    }



    /**
     * check for email and password
     */

    public static String validateFields(Activity activity, EditText musername,
                                        EditText mpassword) {
        String password_str = null;
        String email_str = null;
        password_str = mpassword.getText().toString();
        email_str = musername.getText().toString();

        ArrayList<EditText> textFields = new ArrayList<EditText>();
        textFields.add(mpassword);
        textFields.add(musername);
        if (FieldValidators.isNullOrEmpty(musername)
                && FieldValidators.isNullOrEmpty(mpassword)) {
            Utility.showToastMessageLong(activity, "Please enter email and password");
            musername.setFocusable(true);
            musername.requestFocus();
            return null;

        } else if (FieldValidators.isNullOrEmpty(musername)) {
            // ArrayList<EditText> textFields1 = new ArrayList<EditText>();
            textFields.add(mpassword);
            Utility.showToastMessageLong(activity, "Please enter email");
            musername.setFocusable(true);
            InputMethodManager imm = (InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(musername, InputMethodManager.SHOW_IMPLICIT);
            musername.requestFocus();
        } else if (Validation.isValidEmail(musername.getText().toString()) == false) {
            Utility.showToastMessageLong(activity, "Please enter a valid email");
            musername.setFocusable(true);
            InputMethodManager imm = (InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(musername, InputMethodManager.SHOW_IMPLICIT);
            musername.requestFocus();
        } else if (FieldValidators.isNullOrEmpty(mpassword)) {
            Utility.showToastMessageLong(activity, "Please enter password");
            mpassword.setFocusable(true);
            InputMethodManager imm = (InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(mpassword, InputMethodManager.SHOW_IMPLICIT);
            mpassword.requestFocus();
        } else if (mpassword.length() < 6) {
            Utility.showToastMessageLong(activity,
                    "Password length should be more than 6 characters");
            mpassword.setFocusable(true);
            InputMethodManager imm = (InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(mpassword, InputMethodManager.SHOW_IMPLICIT);
            mpassword.requestFocus();
        } else {
            return "validated";
        }
        return null;

    }

    /**
     * To check whether a given edittext is null or empty
     *
     * @param editText
     * @return status of edittext
     */
    public static boolean isNullorEmpty(EditText editText){
        if (editText==null||editText.length()<1)
            return true;
        else
            return false;

    }


}
