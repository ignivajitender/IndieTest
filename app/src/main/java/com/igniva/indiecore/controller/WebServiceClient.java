package com.igniva.indiecore.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import com.igniva.indiecore.R;
import com.igniva.indiecore.utils.Utility;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.StrictMode;
import com.google.gson.Gson;
import org.apache.http.entity.mime.MultipartEntity;

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
    /**
     * Webservice Urls
     */
    public static final String HTTP_PROTOCOL = "http://";
    public static final String HTTP_HOST_IP = "indiecorelive.ignivastaging.com/api/v1/";
    public static final String HTTP_STAGING=HTTP_PROTOCOL+"indiecorelive.ignivastaging.com";
    public static final String HTTP_LOGIN = HTTP_PROTOCOL + HTTP_HOST_IP +"user/login";
    public static final String HTTP_BADGES = HTTP_PROTOCOL + HTTP_HOST_IP +"badge/market";
    public static final String HTTP_REGISTR = HTTP_PROTOCOL + HTTP_HOST_IP + "/users/register";
    public static  final String HTTP_CREATE_PROFILE=HTTP_PROTOCOL+HTTP_HOST_IP+"user/profile";
    public static final String HTTP_VERIFY_OTP = HTTP_PROTOCOL + HTTP_HOST_IP + "user/verify";
    public static final String HTTP_RESEND_OTP=HTTP_PROTOCOL+HTTP_HOST_IP+"user/control";
    public static  final String HTTP_UPLOAD_IMAGE=HTTP_STAGING+"/files/upload";
    public static final String HTTP_SYNC_CONTACTS=HTTP_PROTOCOL+HTTP_HOST_IP+"user/sync";
    public static final String  HTTP_SELECTED_BADGES=HTTP_PROTOCOL+HTTP_HOST_IP+"badge/get";
    public  static  final String HTTP_ON_OFF_BADGES=HTTP_PROTOCOL+HTTP_HOST_IP+"badge/onoff";
    public static  final String HTTP_GET_BUSINESS_LIST= HTTP_PROTOCOL+HTTP_HOST_IP+"business/list";
    public static  final String HTTP_ON_OFF_BUSINESS_BADGE=HTTP_PROTOCOL+HTTP_HOST_IP+"business/onoff";
    public static  final String HTTP_RECOMMEND_A_BADGE=HTTP_PROTOCOL+HTTP_HOST_IP+"badge/recommend";
    public static  final String HTTP_CREATE_POST=HTTP_PROTOCOL+HTTP_HOST_IP+"post/make";
    public static  final String HTTP_VIEW_ALL_POST=HTTP_PROTOCOL+HTTP_HOST_IP+"post/view";
    public static  final String HTTP_LIKE_UNLIKE_POST=HTTP_PROTOCOL+HTTP_HOST_IP+"post/action";
    public  static  final String HTTP_CHECK_IN_A_BUSINESS=HTTP_PROTOCOL+HTTP_HOST_IP+"business/checkin";
    public  static  final String HTTP_MAKE_A_COMMENT=HTTP_PROTOCOL+HTTP_HOST_IP+"post/comment/make";
    public  static  final String HTTP_VIEW_ALL_COMMENTS=HTTP_PROTOCOL+HTTP_HOST_IP+"post/comment/view";
    public  static  final String HTTP_REMOVE_A_POST=HTTP_PROTOCOL+HTTP_HOST_IP+"post/remove";
    public  static  final String HTTP_FLAG_POST=HTTP_PROTOCOL+HTTP_HOST_IP+"post/flag";
    public static  final String HTTP_LIKE_UNLIKE_COMMENT=HTTP_PROTOCOL+HTTP_HOST_IP+"post/comment/action";
    public  static  final String HTTP_REMOVE_A_COMMENT=HTTP_PROTOCOL+HTTP_HOST_IP+"post/comment/remove";
    public static  final String HTTP_MAKE_A_REPLY=HTTP_PROTOCOL+HTTP_HOST_IP+"post/comment/reply/make";
    public static  final String HTTP_VIEW_ALL_REPLY=HTTP_PROTOCOL+HTTP_HOST_IP+"post/comment/reply/view";
    public static  final String HTTP_LIKE_UNLIKE_REPLY=HTTP_PROTOCOL+HTTP_HOST_IP+"post/comment/reply/action";
    public static  final String HTTP_REMOVE_REPLY =HTTP_PROTOCOL+HTTP_HOST_IP+"post/comment/reply/remove";
    public static  final  String HTTP_VIEW_USER_PROFILE=HTTP_PROTOCOL+HTTP_HOST_IP+"user/profile/";
    public static  final  String HTTP_SET_FAVOURITE=HTTP_PROTOCOL+HTTP_HOST_IP+"favourite/control";
    public static  final  String HTTP_FETCH_FAVOURITE=HTTP_PROTOCOL+HTTP_HOST_IP+"favourite/list";
    public static  final  String HTTP_BLOCK_USER=HTTP_PROTOCOL+HTTP_HOST_IP+"block/control";
    public static  final  String HTTP_BLOCKED_USER_LIST =HTTP_PROTOCOL+HTTP_HOST_IP+"block/list";
    public static  final String HTTP_BUY_A_SLOT=HTTP_PROTOCOL+HTTP_HOST_IP+"badge/buy";
    //
    private final static String CONTENT_TYPE = "application/json";

    private enum HttpMethod {
        HTTP_GET, HTTP_POST, HTTP_PUT
    }

    public enum WebError {
        INVALID_CREDENTIALS, UNAUTHORIZED, PASSWORD_FORMAT_ERROR,
        CURRENT_PASSWORD_INVALID, INVALID_JSON, INCOMPLETE_JSON,
        NO_CREDENTIALS_STORED, CONNECTION_ERROR, EMAIL_NOT_FOUND_FOR_RESET_PASSWORD,
        SAVE_FILE_ERROR, UNKNOWN, USER_ALREADY_EXISTS, NOT_FOUND
    }

    public WebServiceClient(Context context) {
        mContext = context;
    }

    public static void getLogin(final Context context, String payload, ResponseHandlerListener responseHandlerListener) {
        url = HTTP_LOGIN;
        method = HttpMethod.HTTP_POST;
        mResponseHandlerListener = responseHandlerListener;
        checkNetworkState(url, payload, method, context);
    }

    public static void getBadges(final Context context, String payload, ResponseHandlerListener responseHandlerListener) {
        url = HTTP_BADGES;
        method = HttpMethod.HTTP_POST;
        mResponseHandlerListener = responseHandlerListener;
        checkNetworkState(url, payload, method, context);
    }

    public static void verifyOtp(final Context context,String payload,ResponseHandlerListener responseHandlerListener){
        url=HTTP_VERIFY_OTP;
        method=HttpMethod.HTTP_POST;
        mResponseHandlerListener=responseHandlerListener;
        checkNetworkState(url,payload,method,context);
    }

    public static void userselectedBadges(final Context context,String payload,ResponseHandlerListener responselistener){
        url=HTTP_SELECTED_BADGES;
        method=HttpMethod.HTTP_POST;
        mResponseHandlerListener=responselistener;
        checkNetworkState(url,payload,method,context);
    }

    public static void resendOTP(final Context context,String payload,ResponseHandlerListener responseHandlerListener){
        url=HTTP_RESEND_OTP;
        method=HttpMethod.HTTP_POST;
        mResponseHandlerListener=responseHandlerListener;
        checkNetworkState(url,payload,method,context);
    }

    public static void createProfile(final Context context,String payload,ResponseHandlerListener responseHandlerListener){
        url=HTTP_CREATE_PROFILE;
        method=HttpMethod.HTTP_POST;
        mResponseHandlerListener=responseHandlerListener;
        checkNetworkState(url,payload,method,context);
    }

    public static void syncContacts(final Context context,String payload,ResponseHandlerListener responseHandlerListener){
        url=HTTP_SYNC_CONTACTS;
        method=HttpMethod.HTTP_POST;
        mResponseHandlerListener=responseHandlerListener;
        checkNetworkState(url,payload,method,context);
    }

    public static void onOffMyBadges(final Context context,String payload,ResponseHandlerListener responseHandlerListener){
         url=HTTP_ON_OFF_BADGES;
         method=HttpMethod.HTTP_POST;
         mResponseHandlerListener=responseHandlerListener;
         checkNetworkState(url,payload,method,context);
    }

    public static void getBusinessList(final Context context,String payload,ResponseHandlerListener responseHandlerListener){
        url=HTTP_GET_BUSINESS_LIST;
        method=HttpMethod.HTTP_POST;
        mResponseHandlerListener=responseHandlerListener;
        checkNetworkState(url,payload,method,context);
    }

    public static void onOffBusinessBadges(final Context context,String payload,ResponseHandlerListener responseHandlerListener){
        url=HTTP_ON_OFF_BUSINESS_BADGE;
        method=HttpMethod.HTTP_POST;
        mResponseHandlerListener=responseHandlerListener;
        checkNetworkState(url,payload,method,context);
    }

    public static void recommend_a_badge(final Context context,String payload,ResponseHandlerListener responseHandlerListener){
        url=HTTP_RECOMMEND_A_BADGE;
        method=HttpMethod.HTTP_POST;
        mResponseHandlerListener=responseHandlerListener;
        checkNetworkState(url,payload,method,context);
    }


    public static void create_a_post(final Context context,String payload,ResponseHandlerListener responseHandlerListener){
        url=HTTP_CREATE_POST;
        method=HttpMethod.HTTP_POST;
        mResponseHandlerListener=responseHandlerListener;
        checkNetworkState(url,payload,method,context);
    }


    public static void view_all_posts(final Context context,String payload,ResponseHandlerListener responseHandlerListener){
        url=HTTP_VIEW_ALL_POST;
        method=HttpMethod.HTTP_POST;
        mResponseHandlerListener=responseHandlerListener;
        checkNetworkState(url,payload,method,context);
    }

    public static void like_unlike_post(final Context context,String payload,ResponseHandlerListener responseHandlerListener){
        url=HTTP_LIKE_UNLIKE_POST;
        method=HttpMethod.HTTP_POST;
        mResponseHandlerListener=responseHandlerListener;
        checkNetworkState(url,payload,method,context);
    }

    public static void check_in_a_business(final Context context,String payload,ResponseHandlerListener responseHandlerListener){
        url=HTTP_CHECK_IN_A_BUSINESS;
        method=HttpMethod.HTTP_POST;
        mResponseHandlerListener=responseHandlerListener;
        checkNetworkState(url,payload,method,context);
    }

    public static void make_a_comment(final Context context,String payload,ResponseHandlerListener responseHandlerListener){
        url=HTTP_MAKE_A_COMMENT;
        method=HttpMethod.HTTP_POST;
        mResponseHandlerListener=responseHandlerListener;
        checkNetworkState(url,payload,method,context);
    }

    public static void view_all_comments(final Context context,String payload,ResponseHandlerListener responseHandlerListener){
        url=HTTP_VIEW_ALL_COMMENTS;
        method=HttpMethod.HTTP_POST;
        mResponseHandlerListener=responseHandlerListener;
        checkNetworkState(url,payload,method,context);
    }

    public static void remove_a_post(final Context context,String payload,ResponseHandlerListener responseHandlerListener){
        url=HTTP_REMOVE_A_POST;
        method=HttpMethod.HTTP_POST;
        mResponseHandlerListener=responseHandlerListener;
        checkNetworkState(url,payload,method,context);
    }


    public static void flag_a_post(final Context context,String payload,ResponseHandlerListener responseHandlerListener){
        url=HTTP_FLAG_POST;
        method=HttpMethod.HTTP_POST;
        mResponseHandlerListener=responseHandlerListener;
        checkNetworkState(url,payload,method,context);
    }


    public static void like_unlike_a_comment(final Context context,String payload,ResponseHandlerListener responseHandlerListener){
        url=HTTP_LIKE_UNLIKE_COMMENT;
        method=HttpMethod.HTTP_POST;
        mResponseHandlerListener=responseHandlerListener;
        checkNetworkState(url,payload,method,context);
    }

    public static void remove_a_comment(final Context context,String payload,ResponseHandlerListener responseHandlerListener){
        url=HTTP_REMOVE_A_COMMENT;
        method=HttpMethod.HTTP_POST;
        mResponseHandlerListener=responseHandlerListener;
        checkNetworkState(url,payload,method,context);
    }


    public static void reply_to_comment(final Context context,String payload,ResponseHandlerListener responseHandlerListener){
        url=HTTP_MAKE_A_REPLY;
        method=HttpMethod.HTTP_POST;
        mResponseHandlerListener=responseHandlerListener;
        checkNetworkState(url,payload,method,context);
    }



    public static void view_all_replies(final Context context,String payload,ResponseHandlerListener responseHandlerListener){
        url=HTTP_VIEW_ALL_REPLY;
        method=HttpMethod.HTTP_POST;
        mResponseHandlerListener=responseHandlerListener;
        checkNetworkState(url,payload,method,context);
    }

    public static void like_unlike_a_reply(final Context context,String payload,ResponseHandlerListener responseHandlerListener){
        url=HTTP_LIKE_UNLIKE_REPLY;
        method=HttpMethod.HTTP_POST;
        mResponseHandlerListener=responseHandlerListener;
        checkNetworkState(url,payload,method,context);
    }



    public static void remove_a_reply(final Context context,String payload,ResponseHandlerListener responseHandlerListener){
        url= HTTP_REMOVE_REPLY;
        method=HttpMethod.HTTP_POST;
        mResponseHandlerListener=responseHandlerListener;
        checkNetworkState(url,payload,method,context);
    }

    public static void view_user_profile(final Context context,String payload,String user_Id,ResponseHandlerListener responseHandlerListener){
        url=HTTP_VIEW_USER_PROFILE+user_Id;
        method=HttpMethod.HTTP_POST;
        mResponseHandlerListener=responseHandlerListener;
        checkNetworkState(url,payload,method,context);
    }

    public static void set_favourite(final Context context,String payload,ResponseHandlerListener responseHandlerListener){
        url= HTTP_SET_FAVOURITE;
        method=HttpMethod.HTTP_POST;
        mResponseHandlerListener=responseHandlerListener;
        checkNetworkState(url,payload,method,context);
    }
    public static void get_favourite_list(final Context context,String payload,ResponseHandlerListener responseHandlerListener){
        url= HTTP_FETCH_FAVOURITE;
        method=HttpMethod.HTTP_POST;
        mResponseHandlerListener=responseHandlerListener;
        checkNetworkState(url,payload,method,context);
    }
    public static void block_a_user(final Context context,String payload,ResponseHandlerListener responseHandlerListener){
        url= HTTP_BLOCK_USER;
        method=HttpMethod.HTTP_POST;
        mResponseHandlerListener=responseHandlerListener;
        checkNetworkState(url,payload,method,context);
    }
    public static void get_blocked_userlist(final Context context,String payload,ResponseHandlerListener responseHandlerListener){
        url= HTTP_BLOCKED_USER_LIST;
        method=HttpMethod.HTTP_POST;
        mResponseHandlerListener=responseHandlerListener;
        checkNetworkState(url,payload,method,context);
    }
    public static void buy_a_badge_slot(final Context context,String payload,ResponseHandlerListener responseHandlerListener){
        url= HTTP_BUY_A_SLOT;
        method=HttpMethod.HTTP_POST;
        mResponseHandlerListener=responseHandlerListener;
        checkNetworkState(url,payload,method,context);
    }

    /**
     * Check Available Network connection and make http call only if network is
     * available else show no network available
     *
     * @param url      , the url to be called
     * @param _payload ,the data to be send while making http call
     * @param method   , the requested method
     * @param context  , the context of calling class
     *
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
        private  boolean isDisplayDialog=true;
        private Context mContext;

        public CallWebserviceTask(String url, String _payload,
                                  HttpMethod method, final Context context) {
            mContext = context;
            mUrl = url;
            mPayload = _payload;
            mMethod = method;
            mContext = context;
            Log.d(LOG_TAG,"url is "+url);
            Log.d(LOG_TAG,"payload is "+_payload);

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
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("USER-AGENT", "Mozilla/5.0");
                connection.setRequestProperty("ACCEPT-LANGUAGE", "en-US,en;0.5");
                connection.setReadTimeout(40000);
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


    /**
     * General Async task to call Webservices
     */
    private static class UploadImgaeWTask extends
            AsyncTask<Void, Void, Object[]> {

        private final String mUrl;
        private  boolean isDisplayDialog=true;
        private Context mContext;
        MultipartEntity mReqEntity;
        StringBuilder builder;
        //
        public UploadImgaeWTask(Context mContext,String urlString, MultipartEntity reqEntity) {
            this.mUrl=urlString;
            this.mReqEntity=reqEntity;
            this.mContext=mContext;
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
                                error = WebError.UNKNOWN;
                            }
                        }
                    }
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
