package com.igniva.indiecore.ui.activities;

import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.igniva.indiecore.R;
import com.igniva.indiecore.controller.WebServiceClient;
import com.igniva.indiecore.utils.Constants;
import com.igniva.indiecore.utils.Utility;

public class ViewPlayerActivity extends BaseActivity {

    VideoView vvPlayer;
    //String mediaId;
    String mediaPath;
    String fromClass;
    ProgressBar mProgressbar;
    private int stopPosition;
    //private MediaPlayer mMediaPlayer;
    /* int position;
     String messageId;
     boolean localPath;*/
    public static final String LOG_TAG = "ViewPlayerActivity";
    // ChatPojo mChatPojo;

    /*String outFilePath = "";
    File outFile;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_player);
        setUpLayout();
        setDataInViewObjects();
    }

    @Override
    protected void setUpLayout() {
        vvPlayer = (VideoView) findViewById(R.id.vvPlayer);
        mProgressbar = (ProgressBar) findViewById(R.id.progressbar);
    }

    @Override
    protected void setDataInViewObjects() {
        try {
            mediaPath = getIntent().getStringExtra(Constants.MEDIA_PATH);
            fromClass = getIntent().getStringExtra(Constants.FROM_CLASS);


      /*  position = getIntent().getIntExtra(Constants.POSITION, 0);
        messageId = getIntent().getStringExtra(Constants.MESSAGE_ID);
        localPath = getIntent().getBooleanExtra(Constants.LOCALE, true);*/

       /* if (!localPath) {
            mediaPath = WebServiceClient.HTTP_DOWNLOAD_IMAGE + mediaId;
        } else {
            mediaPath = mediaId;
        }*/

            if (fromClass != null && fromClass.equalsIgnoreCase("WallPostAdapter")) {
                mediaPath = WebServiceClient.HTTP_STAGING + mediaPath;
            }


            if (mediaPath != null) {

                if (fromClass != null && fromClass.equalsIgnoreCase("WallPostAdapter")) {
                    mediaPath = WebServiceClient.HTTP_STAGING + mediaPath;
                }
                mProgressbar.setVisibility(View.VISIBLE);

                MediaController mediaController = new MediaController(this);
                mediaController.setMediaPlayer(vvPlayer);
                vvPlayer.setVideoPath(mediaPath);
                vvPlayer.setMediaController(mediaController);
                vvPlayer.requestFocus();
                mediaController.show();

               /* vvPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        finish();
                    }
                });*/

                vvPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    // Close the progress bar and play the video
                    public void onPrepared(MediaPlayer mp) {
                        //mMediaPlayer = mp;
                        mProgressbar.setVisibility(View.GONE);
                        vvPlayer.start();
                    }
                });


            } else {
                new Utility().showOkAndFinish(getResources().getString(R.string.media_not_available), this);
            }

        } catch (Exception e) {
            e.printStackTrace();



        /*if (messageId != null) {
            // DownloadFromUrl(mediaPath, "DownloadedFileVide");
//            new WebServiceClientUploadImage(null, this, asyncResultDownload, mediaId, Constants.DOWNLOAD, 77, messageId).execute();
            // new VideoDownloader().execute(mediaPath);
            new ProgressBack().execute();
        }*/
        }

    }
        @Override
        public void onConfigurationChanged (Configuration newConfig){
            super.onConfigurationChanged(newConfig);
        }

   /* AsyncResultDownload asyncResultDownload = new AsyncResultDownload() {
        @Override
        public void onDownloadTaskResponse(Object result, int urlResponseNo, Object messageId, Object mediaId) {
            try {
                Log.e(LOG_TAG, result.toString());
                mChatPojo = new ChatPojo();
                mChatPojo.setImagePath(result.toString());
                mChatPojo.setMessageId(messageId.toString());
                //updateMediaPath(mChatPojo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        if (mChatPojo != null) {
            intent.putExtra("ChatPojo", mChatPojo);
            intent.putExtra(Constants.MESSAGE_ID, messageId);
            setResult(RESULT_OK, intent);
        } else {
            setResult(RESULT_CANCELED, intent);
        }
        super.onBackPressed();
    }
    class VideoDownloader extends AsyncTask<String, Integer, Void> {
        @Override
        protected Void doInBackground(String... params) {
            outFile = new File(outFilePath);
            FileOutputStream out = null;
            BufferedInputStream input = null;
            try {
                try {
                    out = new FileOutputStream(outFile, true);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    URL url = null;
                    try {
                        url = new URL(mediaPath);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    try {
                        connection.connect();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        throw new RuntimeException("response is not http_ok");
                    }
                    int fileLength = connection.getContentLength();
                    input = new BufferedInputStream(connection.getInputStream());
                    byte data[] = new byte[2048];
                    long readBytes = 0;
                    int len;
                    boolean flag = true;
                    int readb = 0;
                    while ((len = input.read(data)) != -1) {
                        out.write(data, 0, len);
                        readBytes += len;
                        // Following commented code is to play video along with downloading but not working.
*//*                      readb += len;
                    if(readb > 1000000)
                    {
                        out.flush();
                        playVideo();
                        readb = 0;
                    }
*//*
                        Log.w("download", (readBytes / 1024) + "kb of " + (fileLength / 1024) + "kb");
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (out != null)
                        out.flush();
                    out.close();
                    if (input != null)
                        input.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.w("download", "Done" + outFile.getAbsolutePath());
        }
    }
    private class ProgressBack extends AsyncTask<String, String, String> {
        ProgressDialog PD;
        @Override
        protected void onPreExecute() {
            PD = ProgressDialog.show(ViewPlayerActivity.this, null, "Please Wait ...", true);
            PD.setCancelable(true);
        }
        @Override
        protected String doInBackground(String... arg0) {
            DownloadFromUrl(mediaPath, "DownloadedFileVide");
            return null;
        }
        protected void onPostExecute(String result) {
            PD.dismiss();
        }
    }
    public void DownloadFromUrl(String imageURL, String fileName) {  //this is the downloader method
        try {
            URL url = new URL(imageURL);   //you can write here any link
            File direct = new File(Constants.direct);
            if (!direct.exists()) {
                File wallpaperDirectory = new File(String.valueOf(direct));
                wallpaperDirectory.mkdirs();
            }
            File file = new File(direct, fileName + ".mp4");
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //File file = new File(fileName);
            long startTime = System.currentTimeMillis();
            Log.d("ImageManager", "download begining");
            Log.d("ImageManager", "download url:" + url);
            Log.d("ImageManager", "downloaded file name:" + fileName);
                        *//* Open a connection to that URL. *//*
            URLConnection ucon = url.openConnection();
                        *//*
                         * Define InputStreams to read from the URLConnection.
                         *//*
            InputStream is = ucon.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
                        *//*
                         * Read bytes to the Buffer until there is nothing more to read(-1).
                         *//*
            ByteArrayBuffer baf = new ByteArrayBuffer(50);
            int current = 0;
            while ((current = bis.read()) != -1) {
                baf.append((byte) current);
            }
                        *//* Convert the Bytes read to a String. *//*
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baf.toByteArray());
            fos.close();
            Log.d("ImageManager", "download ready in"
                    + ((System.currentTimeMillis() - startTime) / 1000)
                    + " sec");
        } catch (IOException e) {
            Log.d("ImageManager", "Error: " + e);
        }
    }*/

   /* @Override
    public void onPause() {
        super.onPause();
        stopPosition = vvPlayer.getCurrentPosition(); //stopPosition is an int
        if(mMediaPlayer.isPlaying()){
            mMediaPlayer.pause();
        }
        *//*if (vvPlayer.isPlaying())
            vvPlayer.pause();*//*
    }
    @Override
    public void onResume() {
        super.onResume();
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
        }
        // mMediaPlayer.seekTo(stopPosition);
       *//* if (vvPlayer != null) {
            vvPlayer.seekTo(stopPosition);
        }*//*
    }*/


   /* @Override
    public void onPause() {
        super.onPause();
        stopPosition = vvPlayer.getCurrentPosition(); //stopPosition is an int
        if(mMediaPlayer.isPlaying()){
            mMediaPlayer.pause();
        }
        *//*if (vvPlayer.isPlaying())
            vvPlayer.pause();*//*
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
        }
        // mMediaPlayer.seekTo(stopPosition);
       *//* if (vvPlayer != null) {
            vvPlayer.seekTo(stopPosition);
        }*//*
    }*/


    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (vvPlayer != null) {
                vvPlayer.seekTo(stopPosition);
                 vvPlayer.start();
                //vvPlayer.resume();
                if (mProgressbar != null) {
                    mProgressbar.setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (vvPlayer != null) {
                stopPosition = vvPlayer.getCurrentPosition();
                vvPlayer.pause();
            }
        } catch (Exception e) {
        }
    }

}
