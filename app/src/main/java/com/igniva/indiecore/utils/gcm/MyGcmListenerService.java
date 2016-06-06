package com.igniva.indiecore.utils.gcm;

/**
 * Created by igniva-andriod-05 on 19/5/16.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GcmListenerService;
import com.igniva.indiecore.R;


public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";
     private int userid=0;
    private String userId="";
    private int notificationId=0;
    private String notificationType="";
    private String messsgeTitle="";
    private  String messageDescription="";
    private int REQUEST=100;
    private int MESSAGE=150;
    private  int UPCOMMINGSESSION=160;
    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]




//    "user_id" : "1234",
//            "notification_id" : "102",
//            "notification_type" : "screen name",
//            "message_title" : "Title",
//            "message_description" : "Description"
//}




    @Override
    public void onMessageReceived(String from, Bundle data) {

        userId = data.getString("user_id");
        notificationId = data.getInt("notification_id");
        notificationType = data.getString("notification_type");
        messsgeTitle = data.getString("message_title");
        messageDescription = data.getString("message_description");
        // Show notification
        //sendNotificationMessage();
    }

            // [START_EXCLUDE]
            /**
             * Production applications would usually process the message here.
             * Eg: - Syncing with server.
             *     - Store message in local database.
             *     - Update UI.
             */

            /**
             * In some cases it may be useful to show a notification indicating to the user
             * that a message was received.
            // [END_EXCLUDE]
        }
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param notificationType GCM message received.
     */
    private void sendNotificationRequest(String notificationType,Class className) {

        Intent intent = new Intent(this,className);
        Bundle databundle = new Bundle();
        databundle.putInt("index",4);
        databundle.putInt("userId",userid);
        

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Coach Cycle")
                .setContentText(messsgeTitle)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationCompat.BigTextStyle style =
                new NotificationCompat.BigTextStyle(notificationBuilder);


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }



    private void sendNotificationMessage(String notificationType, Class nameOfClass) {
        Intent intent = new Intent(this, nameOfClass);
        intent.putExtra("index",4);
        intent.putExtra("userId",userid);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Coach Cycle")
                .setContentText(messsgeTitle)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationCompat.BigTextStyle style =
                new NotificationCompat.BigTextStyle(notificationBuilder);


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
