package com.igniva.indiecore.utils.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.igniva.indiecore.ui.activities.OtpVerificationActivity;
import com.igniva.indiecore.utils.Constants;

/**
 * Created by siddharth on 09/06/16.
 */
public class SmsReceiver extends BroadcastReceiver {
    private static final String TAG = SmsReceiver.class.getSimpleName();
    public static final String OTP_DELIMITER = "code:";
    Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        final Bundle bundle = intent.getExtras();
        try {
            if (bundle != null) {
                Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (Object aPdusObj : pdusObj) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) aPdusObj);
                    String senderAddress = currentMessage.getDisplayOriginatingAddress();
                    String message = currentMessage.getDisplayMessageBody();

                    Log.e(TAG, "Received SMS: " + message + ", Sender: " + senderAddress);

                    // if the SMS is not from our gateway, ignore the message
                    if ((senderAddress.contains("VERIFY")) ||
                            (senderAddress.equalsIgnoreCase("IM-VERIFY")) ||
                            (senderAddress.contains("bytwoo")) ||
                            (senderAddress.contains("WAYSMS"))) {

//                          Indicore code: 8678. Valid for 5 minutes.

                           if (message.contains("Indiecore")) {

                            try {
                                OtpVerificationActivity.receivedOtp = getVerificationCode(message);
                                Log.e(TAG, "OTP received: " + OtpVerificationActivity.receivedOtp);

                                //context.sendBroadcast(new Intent(OtpVerificationActivity.receivedOtp).putExtra(Constants.OTP_CODE,OtpVerificationActivity.receivedOtp));
                                OtpVerificationActivity.mOtpField.setText(OtpVerificationActivity.receivedOtp);
                                OtpVerificationActivity.mOtpField.setSelection(OtpVerificationActivity.receivedOtp.length());
                                //   new OtpVerificationActivity().updateOtp();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        return;
                    }

                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * Getting the OTP from sms message body
     * ':' is the separator of OTP from the message
     *
     * @param message
     * @return
     */
    private String getVerificationCode(String message) {
        String code = null;
        try {
            int index = message.indexOf(OTP_DELIMITER);

            if (index != -1) {
                int start = index + 6;
                int length = 4;
                code = message.substring(start, start + length);
                return code;
            }
        }catch (Exception e){
            e.printStackTrace();
            code="";
        }

        return code;
    }
}
