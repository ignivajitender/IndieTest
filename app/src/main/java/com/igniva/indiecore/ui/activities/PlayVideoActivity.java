package com.igniva.indiecore.ui.activities;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import com.igniva.indiecore.R;
import com.igniva.indiecore.utils.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by igniva-andriod-05 on 7/11/16.
 */
    public class PlayVideoActivity extends BaseActivity {


        private Button btnPlay;
        private MediaPlayer player;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.play_video_activity);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            MediaController mediaController = new MediaController(this);
            outFilePath = getExternalFilesDir("/") + "/video.mp4";
            String vidAddress = "http://indiecorelive.ignivastaging.com/data/files/uploads/2f3kxpfR4gDqLqk4R/1478502731438-vid_1478502669.mp4";
            Uri vidUri = Uri.parse(vidAddress);
            videoView = (VideoView) findViewById(R.id.VideoView);
            mediaController.setAnchorView(videoView);
            videoView.setMediaController(mediaController);
            videoView.setVideoURI(vidUri);
            videoView.start();

//            prepareVideoView();

        }

        private VideoView videoView;
        String videoPath = "http://indiecorelive.ignivastaging.com/data/files/uploads/2f3kxpfR4gDqLqk4R/1478502731438-vid_1478502669.mp4";
        String outFilePath = "";//

        private void prepareVideoView() {
//            MediaController mediaController = new MediaController(this);
//            videoView = (VideoView) findViewById(R.id.VideoView);
//            mediaController.setAnchorView(videoView);
//            videoView.setVideoURI();
//            videoView.setMediaController(mediaController);

//            new VideoDownloader().execute(videoPath);

//            btnPlay = (Button) findViewById(R.id.btnPlayVideo);
//            btnPlay.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                }
//            });

//            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                @Override
//                public void onPrepared(MediaPlayer mp) {
//                    player = mp;
//
//                    player.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
//                        @Override
//                        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
//                            Log.w("download","size changed");
//                        }
//                    });
//                }
//            });
        }
        File outFile;

    @Override
    protected void setUpLayout() {

    }

    @Override
    protected void setDataInViewObjects() {

    }

    class VideoDownloader extends AsyncTask<String, Integer, Void> {

            @Override
            protected Void doInBackground(String... params) {


                outFile = new File(outFilePath);
                FileOutputStream out = null;
                BufferedInputStream input = null;
                try {
                    try {
                        out = new FileOutputStream(outFile,true);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    try {
                        URL url = null;
                        try {
                            url = new URL(videoPath);
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
                            out.write(data,0,len);
                            readBytes += len;
                            // Following commented code is to play video along with downloading but not working.
/*                      readb += len;
                    if(readb > 1000000)
                    {
                        out.flush();
                        playVideo();
                        readb = 0;
                    }
*/
                            Log.w("download",(readBytes/1024)+"kb of "+(fileLength/1024)+"kb");
                        }



                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (out != null)
                            out.flush();
                        out.close();
                        if(input != null)
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
                Log.w("download", "Done");
                playVideo();

            }
        }

        private void playVideo() {

            videoView.setVideoPath(outFile.getAbsolutePath());
            videoView.start();
        }
    }

