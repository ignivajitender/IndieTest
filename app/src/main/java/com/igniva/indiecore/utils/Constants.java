package com.igniva.indiecore.utils;

import android.os.Environment;

/**
 * Created by igniva-andriod-11 on 2/6/16.
 */
public class Constants {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    //Twitter
    public static final String TWITTER_KEY = "iwSRytw9n49pWN7KsAparQT5Z";
    public static final String TWITTER_SECRET = "upa02Lpa5JMQvVj0U1IrgAOg6i6GI7G5ax7aD9mR9nWHUfTQh3";
    public static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    public static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";

    public static final int STARTACTIVITYFORRESULTFORCHAT = 302;
    public static final int STARTACTIVITYFORRESULTFORVIDEOVIEW = 303;
    public static final int STARTACTIVITYFORRESULTFORCOMMENTACTIVITY = 304;
    public static final int STARTACTIVITYFORRESULTFOR_TWITTER = 305;
    public static final int STARTACTIVITYFORRESULTFOR_TWITTER_BY4J = 306;


    public static final String RESULT_FROM_ACTIVITY="resultFromActivity";

    public static final String FROM_CLASS="fromClass";

    public static final String COUNTRY_CODE_VALIDATION = "Please enter your country code";
    public static final String MOBILE_NUMBER_VALIDATION = "Please enter your mobile number";
    public static final String TEN_DIGIT_MOBILENUMBER_VALIDATION = "Please enter a valid mobile number of 10 digits";
    public static final String NUMBER_LENGTH = "NUMBER_LENGTH";
    public static final String OTP_NOT_VALID = "Please enter OTP code you have just received or request a new OTP";

    public static final String MESSAGE = "message";
    public static final String MESSAGE_ID = "message_id";
    public static final String METHOD_NAME = "method_name";

    //    jsonDataKey
    //    countryCode":"91","mobileNo":"9816428478","code":"3856","locale":"en"

    //
//   token, userId, firstName, lastName, gender, dob(mm/dd/yyyy), desc(240 chars), profilePic(url), coverPic(url)
    public static final String COUNTRY_CODE = "countryCode";

    public static final String MOBILE_NO = "mobileNo";

    public static final String OTP_CODE = "code";

    public static final String LOCALE = "locale";
    public static final String POSITION = "POSITION";
    public static final String INDEX = "";
    public static final String NAME = "NAME";

//activities Name

    public static final String NEWS_FEED_ACTIVITY = "news_feed_activity";
    public static final String BOARD_ACTIVITY = "board_activity";

    public static final String TOKEN = "token";
    public static final String USERID = "userId";
    public static final String FIRSTNAME = "firstName";
    public static final String LASTNAME = "lastName";
    public static final String GENDER = "gender";
    public static final String DOB = "dob";
    public static final String DESCRIPTION = "desc";
    public static final String COVERPIC = "coverPic";
    public static final String PROFILEPIC = "profilePic";
    public static final String NUMBER = "numbers";
    public static final String PAGE = "page";
    public static final String LIMIT = "limit";
    public static final String CATEGORY = "category";
    public static final String DEVICETYPE = "deviceType";
    public static final String DEVICETOKEN = "deviceToken";
    public static final String TYPE = "type";
    public static final String BADGEIDS = "badgeIds";
    public static final String BADGEID = "badgeId";
    public static final String ACTIVE = "active";
    public static final String SELECTED_BADGEIDS = "selectedBadgeids";
    public static final String BADGE_NAME = "badgeName";

    public static final String COMMENTID = "commentId";
    public static final String REPLYID = "replyId";

    public static final String CHATFRAGMENT = "CHATFRAGMENT";
    public static final String MYPROFILEACTIVITY = "MYPROFILEACTIVITY";
    public static final String USERPROFILEACTIVITY = "USERPROFILEACTIVITY";
    public static final String NEWSFEEDACTIVITY = "NEWSFEEDACTIVITY";


    public static final String SELF = "self";
    public static final String LIKE = "like";
    public static final String DISLIKE = "dislike";
    public static final String NEUTRAL = "neutral";
    public static final String COMMENT = "COMMENT";
    public static final String SHARE = "share";
    public static final String DELETE = "DELETE";
    public static final String REPLY = "REPLY";
    public static final String DOWNLOAD = "downlaod";
    public static final String UPLOAD = "upload";
    public static final String UPLOAD_VIDEO = "upload_video";

    public static final String RELATION_SELF = "self";
    public static final String RELATION_UNKNOWN = "unknown";


    public static final String MEDIA_PATH = "media_path";
    public static final String RESULT_RECEIVER = "result_receiver";

//    businessId, badge_status

    //    token, userId, location, latlong, sort, limit, page
    public static final String LOCATION = "location";
    public static final String SORT = "sort";
    public static final String LATLONG = "latlong";
    public static final String BUSINESS_ID = "businessId";
    public static final String BUSINESS_NAME = "BUSINESS_NAME";
    public static final String PERSON_ID = "personId";
    public static final String BADGE_STATUS = "badge_status";
    public static final String CONTEXT_NAME = "context";


//        token, userId, roomId, postType, media, text

    public static final String ROOM_ID = "roomId";
    public static final String POST_TYPE = "postType";
    public static final String MEDIA = "media";
    public static final String THUMBNAIL = "thumb";
    public static final String TEXT = "text";
    public static final String POSTID = "postId";
    public static final String POST_ID = "post_id";

    public static final String ENTER_FIRST_NAME = "Please enter your first name";
    public static final String ENTER_LAST_NAME = "Please enter your last name";
    public static final String ENTER_DOB = "Please enter your Date of Birth";
    public static final String ENTER_DESCRIPTION = "Please enter description";
    public static final String AGE_SHOULD_BE_EIGHTEENPLUS = "You must be aged 18 years or over to register with Indiecore.";


    public static final String MEDIUM = "medium";
    public static final String PROMO_CODE = "promo_code";


    //    ImageDirectory
    public static final String direct = (Environment.getExternalStorageDirectory() + "/IndieCore");
//

    //Meteor constants
    public static final String URLWEBSOCKET = "ws://indiecorelive.ignivastaging.com:3000/websocket";
    public static final String SUBSCRIBECHATS = "subscribeChats";
    public static final String SUBSCRIBEMESSAGES = "subscribeMessages";
    public static final String GETROOMID = "getRoomId";
    public static final String MARK_MESSAGE_DELIVERED = "markMessageDelivered";
    public static final String MARK_ROOM_READ = "markRoomRead";
    public static final String MARK_MESSAGE_READ = "markMessageRead";
    public static final String SENDMESSAGES = "sendMessage";


}
