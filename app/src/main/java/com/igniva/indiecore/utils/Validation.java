package com.igniva.indiecore.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.EditText;

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
