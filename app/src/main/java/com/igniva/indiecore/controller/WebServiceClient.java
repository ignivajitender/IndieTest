package com.igniva.indiecore.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import com.igniva.indiecore.R;
import com.igniva.indiecore.utils.Utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;

import org.json.JSONObject;

import com.igniva.indiecore.utils.Log;
import com.igniva.indiecore.model.ResponsePojo;

public class WebServiceClient {

    // Global variables
    Context mContext;
    private static final String LOG_TAG = "WebServiceClient";
    private static String url;
    private static String payload;
    private static HttpMethod method;
    private static ProgressDialog progressDialog;
    private static ResponseHandlerListener mResponseHandlerListener;

    private enum HttpMethod {
        HTTP_GET, HTTP_POST, HTTP_PUT
    }


    public enum WebError {
        INVALID_CREDENTIALS, UNAUTHORIZED, PASSWORD_FORMAT_ERROR,
        CURRENT_PASSWORD_INVALID, INVALID_JSON, INCOMPLETE_JSON,
        NO_CREDENTIALS_STORED, CONNECTION_ERROR, EMAIL_NOT_FOUND_FOR_RESET_PASSWORD,
        SAVE_FILE_ERROR, UNKNOWN, USER_ALREADY_EXISTS, NOT_FOUND
    }

    private final static String CONTENT_TYPE = "application/json";

    /**
     * Webservice Urls
     */
    public static final String HTTP_PROTOCOL = "http://";
    public static final String HTTP_HOST_IP = "scanapp.ignivastaging.com";
    public static final String HTTP_LOGIN = HTTP_PROTOCOL + HTTP_HOST_IP + "/users/login?";
    public static final String HTTP_REGISTR = HTTP_PROTOCOL + HTTP_HOST_IP + "/users/register";


    public WebServiceClient(Context context) {
        mContext = context;
    }


    public static void getLogin(final Context context, String payload, ResponseHandlerListener responseHandlerListener) {
        url = HTTP_PROTOCOL + HTTP_HOST_IP + HTTP_LOGIN;
        method = HttpMethod.HTTP_POST;
        mResponseHandlerListener = responseHandlerListener;
        checkNetworkState(url, payload, method, context);
    }


    public static void signUpUser(final Context context, String payload, ResponseHandlerListener responseHandlerListener) {
        url = HTTP_LOGIN;
        url = "http://spplitt.ignivastaging.com/users/login";
        method = HttpMethod.HTTP_POST;
        mResponseHandlerListener = responseHandlerListener;
        checkNetworkState(url, payload, method, context);
    }

    /**
     * Check Available Network connection and make http call only if network is
     * available else show no network available
     *
     * @param url      , the url to be called
     * @param _payload ,the data to be send while making http call
     * @param method   , the requested method
     * @param context  , the context of calling class
     */
    private static void checkNetworkState(String url, String _payload,
                                          HttpMethod method, Context context) {
        if (Utility.isInternetConnection(context)) {
            new CallWebserviceTask(url, _payload, method, context).execute();
        } else {
            // open dialog here
            new Utility().showNoInternetDialog((Activity) context);

        }
    }

    /**
     * General Async task to call Webservices
     */
    private static class CallWebserviceTask extends
            AsyncTask<Void, Void, Object[]> {

        private final String mUrl;
        private final String mPayload;
        private final HttpMethod mMethod;
        private  boolean isDisplayDialog=false;
        private Context mContext;

        public CallWebserviceTask(String url, String _payload,
                                  HttpMethod method, final Context context) {
            mContext = context;
            mUrl = url;
            mPayload = _payload;
            mMethod = method;
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (isDisplayDialog) {
                progressDialog = ProgressDialog.show(mContext, "", mContext
                                .getResources().getString(R.string.please_wait), true,
                        false);
                // progressDialog.setCancelable(true);
            }
        }

        @Override
        protected Object[] doInBackground(Void... vParams) {

            ResponsePojo responsePojo = null;
            WebError error = null;
            String method = "";

            URL url = null;
            try {
                url = new URL(mUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Content-Type", "text/plain");
                connection.setRequestProperty("USER-AGENT", "Mozilla/5.0");
                connection.setRequestProperty("ACCEPT-LANGUAGE", "en-US,en;0.5");
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(30000);
                if (mMethod.equals(HttpMethod.HTTP_GET)) {
                    connection.setRequestMethod("GET");
                } else if (mMethod.equals(HttpMethod.HTTP_POST)) {
                    connection.setRequestMethod("POST");
                    connection.setDoInput(true);
                    connection.setDoOutput(true);

                }
                //
                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(mPayload);

                writer.flush();
                writer.close();
                os.close();


//
//                DataOutputStream dStream = new DataOutputStream(connection.getOutputStream());
//                dStream.writeBytes(mPayload); //Writes out the string to the underlying output stream as a sequence of bytes
//                dStream.flush(); // Flushes the data output stream.
//                dStream.close(); // Closing the output stream.
                connection.connect();

                int successCode = connection.getResponseCode();

                if (successCode == 200) {
//                    final StringBuilder output = new StringBuilder("Request URL " + url);
//                    output.append(System.getProperty("line.separator") + "Request Parameters " + mPayload);
//                    output.append(System.getProperty("line.separator")  + "Response Code " + successCode);
                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                    String line = "";
                    StringBuilder responseOutput = new StringBuilder();
                    System.out.println("output===============" + br);
                    while ((line = br.readLine()) != null) {
                        responseOutput.append(line);
                    }
                    br.close();
                    Log.d(LOG_TAG, responseOutput.toString());
                    Gson gson = new Gson();
                    responsePojo = gson.fromJson(responseOutput.toString(), ResponsePojo.class);
                    // output.append(System.getProperty("line.separator") + "Response " + System.getProperty("line.separator") + System.getProperty("line.separator") + responseOutput.toString());
                } else {
                    Log.d(LOG_TAG, "Success code is " + successCode);
                    InputStream inputStreamReader = connection.getErrorStream();
                    connection.getResponseMessage();
                    error = WebError.UNKNOWN;
                }
            } catch (Exception e) {
                error = WebError.UNKNOWN;
                e.printStackTrace();
            }


            return new Object[]{responsePojo, error};
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onPostExecute(Object[] result) {

            try {
                // TODO handle Authtoken Expiration
                if (((ResponsePojo) result[0]).getStatus() == 1000) {
                    WebNotificationManager.unRegisterResponseListener(mResponseHandlerListener);
                    progressDialog.dismiss();
                    // Show Logout Dialog , on click of OK redirect to Login screen
                } else {
                    //
                    WebNotificationManager.onResponseCallReturned(
                            (ResponsePojo) result[0], (WebError) result[1],
                            progressDialog);
                }
            } catch (Exception e) {
                WebNotificationManager.onResponseCallReturned(
                        (ResponsePojo) result[0], (WebError) result[1],
                        progressDialog);
            }
        }
    }


}
