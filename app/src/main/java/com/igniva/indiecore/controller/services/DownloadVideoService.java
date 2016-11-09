package com.igniva.indiecore.controller.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

import com.igniva.indiecore.db.BadgesDb;
import com.igniva.indiecore.model.ChatPojo;
import com.igniva.indiecore.utils.Constants;
import com.igniva.indiecore.utils.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class DownloadVideoService extends IntentService {
    public static final int UPDATE_PROGRESS = 8344;

    public DownloadVideoService() {
        super("DownloadVideoService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
      File newFile = null;
        Log.e("Download Service Started");
        String urlToDownload = intent.getStringExtra(Constants.MEDIA_PATH);
        ResultReceiver receiver = intent.getParcelableExtra(Constants.RESULT_RECEIVER);
        String messageId = intent.getStringExtra(Constants.MESSAGE_ID);

        try {
            //Create Directory if not present
            File direct = new File(Constants.direct);
            if (!direct.exists()) {
                File wallpaperDirectory = new File(String.valueOf(direct));
                wallpaperDirectory.mkdirs();
            }
            //Create File if not present
            newFile = new File(direct, "VID_"+System.currentTimeMillis() + ".mp4");
            if (!newFile.exists()) {
                try {
                    newFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            URL url = new URL(urlToDownload);
            URLConnection connection = url.openConnection();
            connection.connect();
            // this will be useful so that you can show a typical 0-100% progress bar
            int fileLength = connection.getContentLength();

            // download the file
            InputStream input = new BufferedInputStream(connection.getInputStream());
            OutputStream output = new FileOutputStream(newFile.getPath());

            byte data[] = new byte[1024];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
               /* Bundle resultData = new Bundle();
                resultData.putInt("progress", (int) (total * 100 / fileLength));
                receiver.send(UPDATE_PROGRESS, resultData);*/
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Download "+e.getMessage());
        }

        //Update Local db

        Log.e("Download CompletedInService1");
        BadgesDb badgesDb = new BadgesDb(this);
        ChatPojo mChatPojo = new ChatPojo();
        mChatPojo.setImagePath(newFile.getPath());
        mChatPojo.setMessageId(messageId);
        badgesDb.updateMediaPath(mChatPojo);

        Log.e("Download CompletedInService2");

        Bundle resultData = new Bundle();
        resultData.putInt("progress", 100);
        resultData.putParcelable("ChatPojo",mChatPojo);
        resultData.putString(Constants.MESSAGE_ID,messageId);
        receiver.send(UPDATE_PROGRESS, resultData);
        Log.e("Download CompletedInService3");
    }
}
