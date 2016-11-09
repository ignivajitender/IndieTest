package com.igniva.indiecore.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.igniva.indiecore.R;
import com.igniva.indiecore.controller.WebServiceClient;

import org.apache.http.entity.mime.MultipartEntity;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;

/**
 * Created by igniva-php-08 on 25/5/16.
 */
public class WebServiceClientUploadImage extends
        AsyncTask<String, Integer, String> {
    String mUrl;
    String mTaskName;
    MultipartEntity mReqEntity;
    Context mContext;
    ProgressDialog progressDialog;
    ProgressBar mProgressBar;
    StringBuilder builder;
    AsyncResult mCallBack;
    AsyncResultDownload mCallBackDownlaod;
    String mediaId;
    String path;
    String mMessageId;
    int urlNo;

    public WebServiceClientUploadImage(Context mContext, AsyncResult callBackUpload, String urlString, MultipartEntity reqEntity, int urlNo, String taskName) {
        this.mUrl = urlString;
        this.mReqEntity = reqEntity;
        this.mContext = mContext;
        this.mCallBack = callBackUpload;
        this.urlNo = urlNo;
        this.mTaskName = taskName;
    }

    public WebServiceClientUploadImage(ProgressBar progressBar, Context mContext, AsyncResultDownload callBackDownload, String urlString, String taskName, int urlNo, String messageId) {
        this.mUrl = WebServiceClient.HTTP_DOWNLOAD_IMAGE + urlString;
        this.mContext = mContext;
        this.mCallBackDownlaod = callBackDownload;
        this.mTaskName = taskName;
        this.mProgressBar = progressBar;
        this.urlNo = urlNo;
        this.mediaId = urlString;
        this.mMessageId = messageId;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        try {
            if (mTaskName.equalsIgnoreCase(Constants.DOWNLOAD)) {
                mProgressBar.setVisibility(View.VISIBLE);
            } else {
                progressDialog = ProgressDialog.show(mContext, "", mContext
                                .getResources().getString(R.string.please_wait), true,
                        false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected String doInBackground(String... voids) {
        try {
            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }

            URL url = new URL(mUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.setRequestProperty("Connection", "Keep-Alive");

            if (mTaskName.equalsIgnoreCase(Constants.DOWNLOAD)) {
                // getting file length
                int lenghtOfFile = conn.getContentLength();

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);
                Bitmap image = BitmapFactory.decodeStream(input);
                path = createDirectoryAndSaveFile(image, Utility.randomString());
                conn.connect();
            } else {
                conn.addRequestProperty("Content-length", mReqEntity.getContentLength() + "");
                conn.addRequestProperty(mReqEntity.getContentType().getName(), mReqEntity.getContentType().getValue());
                OutputStream os = conn.getOutputStream();
                mReqEntity.writeTo(conn.getOutputStream());
                os.close();
                conn.connect();

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = null;
                    builder = new StringBuilder();
                    try {
                        reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String line = "";
                        while ((line = reader.readLine()) != null) {
                            builder.append(line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (reader != null) {
                            try {
                                reader.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        if (builder != null)
            return builder.toString();
        else
            return path;
    }

    private String createDirectoryAndSaveFile(Bitmap imageToSave, String fileName) {

        File direct = new File(Constants.direct);
        if (!direct.exists()) {
            File wallpaperDirectory = new File(String.valueOf(direct));
            wallpaperDirectory.mkdirs();
        }

        File file = new File(direct, fileName + ".jpg");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file.getPath();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        try {
            if (urlNo == 77) {
                mCallBackDownlaod.onDownloadTaskResponse(result, urlNo, mMessageId, mediaId);
            } else {
                mCallBack.onTaskResponse(result, urlNo);
            }
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            if (mProgressBar != null) {
                mProgressBar.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
