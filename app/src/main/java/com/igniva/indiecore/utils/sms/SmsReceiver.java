package com.igniva.indiecore.utils.sms;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.igniva.indiecore.ui.activities.OtpVerificationActivity;

/**
 * Created by Ravi on 09/07/15.
 */
public class SmsReceiver extends BroadcastReceiver {
    private static final String TAG = SmsReceiver.class.getSimpleName();
    public static final String OTP_DELIMITER = "code:";
    @Override
    public void onReceive(Context context, Intent intent) {

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
                    if (!(senderAddress.contains("WAYSMS")||senderAddress.equalsIgnoreCase("VM-VERIFY")||senderAddress.contains("bytwoo"))) {
                        return;
                    }

//                    Indicore code: 8678. Valid for 5 minutes.

                    if(message.contains("Indicore")){

                        OtpVerificationActivity.receivedOtp = getVerificationCode(message);
                        Log.e(TAG, "OTP received: " + OtpVerificationActivity.receivedOtp);

                        try {
                            OtpVerificationActivity.mOtpField.setText( OtpVerificationActivity.receivedOtp);
                         //   new OtpVerificationActivity().updateOtp();


                        }catch (Exception e){

                            e.printStackTrace();
                        }

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
        int index = message.indexOf(OTP_DELIMITER);

        if (index != -1) {
            int start = index + 6;
            int length = 4;
            code = message.substring(start, start + length);
            return code;
        }

        return code;
    }
}
