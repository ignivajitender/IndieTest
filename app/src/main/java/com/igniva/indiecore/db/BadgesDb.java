package com.igniva.indiecore.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.igniva.indiecore.model.BadgesPojo;
import com.igniva.indiecore.model.ChatListPojo;
import com.igniva.indiecore.model.ChatPojo;
import com.igniva.indiecore.model.CountryCodePojo;
import com.igniva.indiecore.model.ProfilePojo;
import com.igniva.indiecore.model.UsersPojo;
import com.igniva.indiecore.utils.Log;

import java.util.ArrayList;

/**
 * Created by igniva-andriod-05 on 23/6/16.
 */
public class BadgesDb extends SQLiteOpenHelper {

    public static final String LOG_TAG = "BadgesDb";
    //---- DATABASE START-----------

    public static final String DATABASE_NAME = "db_indiecore";
    public static final String TABLE_BADGES = "tbl_badge_master";

    //---- DATABASE END-----------


    //---- TABLE BADGE START-----------
    // COLUMN NAMES
    public static final String S_NO = "s_no";
    public static final String BADGE_ID = "id_badge";
    public static final String BADGE_NAME = "badge_name";
    public static final String BADGE_DESC = "badge_desc";
    public static final String BADGE_ICON = "badge_icon";
    public static final String IS_PREMIUM = "is_premium";
    public static final String BADGE_PRICE = "badge_price";
    public static final String BADGE_SKU = "badge_sku";
    public static final String IS_ACTIVE = "is_active";
    //    TABLE USERS
    public static final String TABLE_USERS = "tbl_users";

    //---- TABLE BADGE END-----------
    //    COLUMN USERS
    public static final String MOBILE_NO = "mobile_no";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String CONTACT_IMG = "contact_img";
    public static final String TYPE = "type";
    public static final String BADGE_COUNT = "badge_count";
    public static final String DESCRIPTION = "description";
    public static final String USER_ID = "userId";
    public static final String COUNTRY_CODE = "countryCode";
    //    TABLE CHAT
    public static final String TABLE_CHAT = "tbl_chat";
    //    CHAT TABLE COULMN
    public static final String IDCHAT = "idChat";

//    TABLE END USERS
public static final String Id = "_id";
    public static final String ROOM_ID = "roomId";
    public static final String USERID = "userId";
    public static final String MESSAGE_TYPE = "type";
    public static final String TEXT = "text";
    public static final String MEDIA = "media";
    public static final String THUMB = "thumb";
    public static final String STATUS = "status";
    public static final String CREATED_AT = "created_at";
    public static final String DATE_UPDATED = "date_updated";
    public static final String MESSAGE_ID = "messageId";
    public static final String NAME = "name";
    public static final String ICON = "icon";
    public static final String RELATION = "relation";
    public static final String BADGES = "badges";
    public static final String IMAGE_PATH = "image_path";
    //    TABLE RECENT CHAT
    public static final String TABLE_RECENT_CHAT = "tbl_recent_chat";
    //   Recent CHAT TABLE COULMN
    public static final String RECENT_CHAT_Id = "_id";
    public static final String RECENT_CHAT_BUISNESSID = "businessId";
    public static final String RECENT_CHAT_TYPE = "type";
    public static final String RECENT_CHAT_CREATED_AT = "created_at";
    public static final String RECENT_CHAT_DATE_UPDATED = "date_updated";
    public static final String RECENT_CHAT_LASTMESSAGE_ID = "last_message_Id";
    public static final String RECENT_CHAT_LASTMESSAGE_BY = "last_message_by";
    public static final String RECENT_CHAT_LASTMESSAGE_TYPE = "last_message_type";
    public static final String RECENT_CHAT_LASTMESSAGE = "last_message";
    public static final String RECENT_CHAT_UNREAD_COUNT = "unreadCount";
    public static final String RECENT_CHAT_ROOMID = "roomId";
    public static final String RECENT_CHAT_USERID = "userId";
    public static final String RECENT_CHAT_NAME = "name";
    public static final String RECENT_CHAT_ICON = "icon";
    public static final String RECENT_CHAT_MEMBERS = "members";
    public static final String RECENT_CHAT_RELATION = "relation";
    public static final String RECENT_CHAT_BADGES = "badges";
    private static final int DATABASE_VERSION = 1;

    public BadgesDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static String createBadgeTable() {

        return "CREATE TABLE IF NOT EXISTS " + TABLE_BADGES
                + "(" + BADGE_ID + " TEXT PRIMARY KEY," + S_NO + " INTEGER," +
                BADGE_NAME + " TEXT," + BADGE_DESC + " TEXT," + BADGE_ICON + " TEXT,"
                + IS_PREMIUM + " INTEGER," + BADGE_PRICE + " REAL,"
                + BADGE_SKU + " TEXT," + IS_ACTIVE + " INTEGER" + ")";
    }

    public static String createUsersTable() {

//        type=0 non-indiecore user
//           type=1 indiecore user
        return "CREATE TABLE IF NOT EXISTS " + TABLE_USERS
                + "(" + MOBILE_NO + " TEXT PRIMARY KEY," +
                FIRST_NAME + " TEXT," + LAST_NAME + " TEXT," + DESCRIPTION + " TEXT," + CONTACT_IMG + " TEXT,"
                + BADGE_COUNT + " INTEGER," + TYPE + " INTEGER," + USER_ID + " TEXT," + COUNTRY_CODE + " TEXT" + ")";
    }

    //    TABLE CHAT END
    public static String createTableUserChat() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_CHAT + "(" + IDCHAT + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + Id + " TEXT," + ROOM_ID + " TEXT," +
                USERID + " TEXT," + MESSAGE_TYPE + " TEXT," + TEXT + " TEXT," + MEDIA + " TEXT," + THUMB + " TEXT," +
                STATUS + " INTEGER," + CREATED_AT + " TEXT," + DATE_UPDATED + " TEXT," + MESSAGE_ID + " TEXT," +
                NAME + " TEXT," + ICON + " TEXT," + RELATION + " TEXT," + BADGES + " TEXT," + IMAGE_PATH + " TEXT" + ")";

    }

    //    TABLE RECENT CHAT END
    public static String createTableRecentChat() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_RECENT_CHAT + "(" + RECENT_CHAT_Id + " TEXT ," + RECENT_CHAT_BUISNESSID + " TEXT," +
                RECENT_CHAT_TYPE + " INTEGER," + RECENT_CHAT_CREATED_AT + " TEXT," + RECENT_CHAT_DATE_UPDATED + " TEXT," + RECENT_CHAT_LASTMESSAGE_ID + " TEXT," + RECENT_CHAT_LASTMESSAGE_BY + " TEXT," +
                RECENT_CHAT_LASTMESSAGE_TYPE + " TEXT," + RECENT_CHAT_LASTMESSAGE + " TEXT," + RECENT_CHAT_UNREAD_COUNT + " TEXT," + RECENT_CHAT_ROOMID + " TEXT PRIMARY KEY NOT NULL UNIQUE," +
                RECENT_CHAT_USERID + " TEXT," + RECENT_CHAT_NAME + " TEXT," + RECENT_CHAT_ICON + " TEXT," + RECENT_CHAT_MEMBERS + " TEXT," + RECENT_CHAT_RELATION + " TEXT," + RECENT_CHAT_BADGES + " TEXT)";

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createBadgeTable());
        db.execSQL(createUsersTable());
        db.execSQL(createTableUserChat());
        db.execSQL(createTableRecentChat());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BADGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECENT_CHAT);
        onCreate(db);
    }

    public void insertSingleMessage(SQLiteDatabase db, ChatPojo message) {
//        return  "CREATE TABLE IF NOT EXISTS " + TABLE_CHAT + "(" + Id + " TEXT," + ROOM_ID + "TEXT PRIMARY KEY," +
//                USERID + " TEXT," + MESSAGE_TYPE + " TEXT," + TEXT + " TEXT," + MEDIA + " TEXT," + THUMB + " TEXT," +
//                STATUS + " INTEGER," + CREATED_AT + " TEXT," + DATE_UPDATED + " TEXT," + MESSAGE_ID + " TEXT," +
//                NAME + " TEXT," + ICON + " TEXT," + RELATION + " TEXT," + BADGES + " TEXT" + ")";
        try {
            ContentValues value = new ContentValues();
            value.put(Id, message.get_id());
            value.put(ROOM_ID, message.getRoomId());
            value.put(USERID, message.getUserId());
            value.put(MESSAGE_TYPE, message.getType());
            value.put(TEXT, message.getText());
            value.put(MEDIA, message.getMedia());
            value.put(THUMB, message.getThumb());
            value.put(STATUS, message.getStatus());
            value.put(CREATED_AT, message.getCreated_at());
            value.put(DATE_UPDATED, message.getDate_updated());
            value.put(MESSAGE_ID, message.getMessageId());
            value.put(NAME, message.getName());
            value.put(ICON, message.getIcon());
            value.put(RELATION, message.getRelation());
            value.put(BADGES, message.getBadges());
            value.put(IMAGE_PATH, message.getImagePath());
            // Inserting Single Row
            db.insertWithOnConflict(TABLE_CHAT, null, value, SQLiteDatabase.CONFLICT_IGNORE);
            android.util.Log.e(LOG_TAG, "In BadgesDb");

        } catch (Exception e) {
            android.util.Log.e(LOG_TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    public void insertRecenChatList(ChatListPojo chatListPojo) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues value = new ContentValues();
            value.put(RECENT_CHAT_Id, chatListPojo.get_id());
            value.put(RECENT_CHAT_BUISNESSID, chatListPojo.getBusinessId());
            value.put(RECENT_CHAT_BADGES, chatListPojo.getBadges());
            value.put(RECENT_CHAT_CREATED_AT, chatListPojo.getCreated_at());
            value.put(RECENT_CHAT_TYPE, chatListPojo.getType());
            value.put(RECENT_CHAT_DATE_UPDATED, chatListPojo.getDate_updated());
            value.put(RECENT_CHAT_ICON, chatListPojo.getIcon());
            value.put(RECENT_CHAT_LASTMESSAGE, chatListPojo.getLast_message());
            value.put(RECENT_CHAT_LASTMESSAGE_BY, chatListPojo.getLast_message_by());
            value.put(RECENT_CHAT_LASTMESSAGE_ID, chatListPojo.getLast_message_Id());
            value.put(RECENT_CHAT_LASTMESSAGE_TYPE, chatListPojo.getLast_message_type());
            value.put(RECENT_CHAT_MEMBERS, chatListPojo.getMembers());
            value.put(RECENT_CHAT_NAME, chatListPojo.getName());
            value.put(RECENT_CHAT_RELATION, chatListPojo.getRelation());
            value.put(RECENT_CHAT_ROOMID, chatListPojo.getRoomId());
            value.put(RECENT_CHAT_USERID, chatListPojo.getUserId());
            value.put(RECENT_CHAT_UNREAD_COUNT, chatListPojo.getUnreadCount());


            // Inserting Single Row
            //db.insertWithOnConflict(TABLE_RECENT_CHAT, null, value, SQLiteDatabase.CONFLICT_IGNORE);
            db.replace(TABLE_RECENT_CHAT, null, value);
            android.util.Log.e(LOG_TAG, "In BadgesDb");

        } catch (Exception e) {
            android.util.Log.e(LOG_TAG, e.getMessage());
            e.printStackTrace();
        }
    }


    public void insertAllMessages(ArrayList<ChatPojo> mMessageList) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            for (int i = 0; i < mMessageList.size(); i++) {
                insertSingleMessage(db, mMessageList.get(i));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

    }


    public void insertMessage(ChatPojo message) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            insertSingleMessage(db, message);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

    }


    public void updateMessageStatus(ChatPojo message) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL("UPDATE " + TABLE_CHAT + " SET " + STATUS + "=" + message.getStatus() + " WHERE " + MESSAGE_ID + " = '" + message.getMessageId() + "'");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public void updateMediaPath(ChatPojo message) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
//            db.execSQL("UPDATE " + TABLE_CHAT + " SET " + IMAGE_PATH +"=" + message.getImagePath() + " WHERE " + MESSAGE_ID + " = '" + message.getMessageId() + "'");
            db.execSQL("UPDATE " + TABLE_CHAT + " SET " + IMAGE_PATH + "=" + "'" + message.getImagePath() + "'" + " WHERE " + MESSAGE_ID + " = '" + message.getMessageId() + "'");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public void insertSingleBadge(SQLiteDatabase db, BadgesPojo badges) {
        try {
            ContentValues values = new ContentValues();
            values.put(S_NO, 1);
            values.put(BADGE_ID, badges.getBadgeId()); // Contact Phone Number
            values.put(BADGE_NAME, badges.getName());
            values.put(BADGE_DESC, badges.getDescription());
            values.put(BADGE_ICON, badges.getIcon());
            values.put(IS_PREMIUM, badges.getIsPremium());
            values.put(BADGE_PRICE, badges.getPrice());
            values.put(BADGE_SKU, badges.getSku());
            values.put(IS_ACTIVE, badges.getActive());
            // Inserting Single Row
            db.insertWithOnConflict(TABLE_BADGES, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void insertSingleContact(SQLiteDatabase db, UsersPojo users) {

        try {
            ContentValues values = new ContentValues();
            values.put(MOBILE_NO, users.getMobileNo());
            values.put(FIRST_NAME, users.getProfile().getFirstName());
            values.put(LAST_NAME, users.getProfile().getLastName());
            values.put(CONTACT_IMG, users.getProfile().getProfilePic());
            values.put(DESCRIPTION, users.getProfile().getDesc());
            values.put(BADGE_COUNT, 12);
            values.put(TYPE, 1);
            values.put(USER_ID, users.getUserId());
            values.put(COUNTRY_CODE, users.getLocation().getCountryCode());
            db.insertWithOnConflict(TABLE_USERS, null, values, SQLiteDatabase.CONFLICT_IGNORE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void updateSingleRow(BadgesPojo badges) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL("UPDATE " + TABLE_BADGES + " SET " + IS_ACTIVE + "=" + badges.getActive() + " WHERE " + BADGE_ID + " = '" + badges.getBadgeId() + "'");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }


    public void insertAllBadges(ArrayList<BadgesPojo> mTotalBadges) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            for (int i = 0; i < mTotalBadges.size(); i++) {
                insertSingleBadge(db, mTotalBadges.get(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Closing database connection
            db.close();
        }
    }


    public void inserPremiumBadges(BadgesPojo mPremiumBadge) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            insertSingleBadge(db, mPremiumBadge);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Closing database connection
            db.close();
        }
    }


    public void insertAllContacts(ArrayList<UsersPojo> mUsers) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            for (int i = 0; i < mUsers.size(); i++) {

                insertSingleContact(db, mUsers.get(i));

                Log.e(LOG_TAG, "inseretd" + mUsers.get(i));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }


    public ArrayList<BadgesPojo> retrieveSelectedBadges() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_BADGES, null, null, null, null, null, null);
        ArrayList<BadgesPojo> myBadgesList = new ArrayList<BadgesPojo>();
        BadgesPojo badgeModel;
        try {
            if (cursor.getCount() > 0) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToNext();
                    badgeModel = new BadgesPojo();
                    badgeModel.setBadgeId(cursor.getString(0));
                    badgeModel.setSr_no(cursor.getInt(1));
                    badgeModel.setName(cursor.getString(2));
                    badgeModel.setDescription(cursor.getString(3));
                    badgeModel.setIcon(cursor.getString(4));
                    badgeModel.setIsPremium(cursor.getInt(5));
                    badgeModel.setPrice(cursor.getString(6));
                    badgeModel.setSku(cursor.getString(7));
                    badgeModel.setActive(cursor.getInt(8));
                    myBadgesList.add(badgeModel);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
            cursor.close();
        }
        return myBadgesList;
    }

    public ArrayList<UsersPojo> retrieveSavedUsersList() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, null, null, null, null, null);
        ArrayList<UsersPojo> savedUsersList = new ArrayList<UsersPojo>();
        UsersPojo mUsersPojoObj;
        ProfilePojo mUserProfileObj;
        CountryCodePojo mCountryCodeObj;
        try {
            if (cursor.getCount() > 0) {
                for (int i = 0; i < cursor.getCount(); i++) {

                    cursor.moveToNext();
                    mUserProfileObj = new ProfilePojo();
                    mUsersPojoObj = new UsersPojo();
                    mCountryCodeObj = new CountryCodePojo();

                    mUsersPojoObj.setMobileNo(cursor.getString(0));


                    mUserProfileObj.setFirstName(cursor.getString(1));
                    mUsersPojoObj.setProfile(mUserProfileObj);

                    mUserProfileObj.setDesc(cursor.getString(3));
                    mUsersPojoObj.setProfile(mUserProfileObj);


                    mUserProfileObj.setProfilePic(cursor.getString(4));
                    mUsersPojoObj.setProfile(mUserProfileObj);

                    mUsersPojoObj.setUserId(cursor.getString(7));

                    mCountryCodeObj.setCountryCode(cursor.getString(8));
                    mUsersPojoObj.setLocation(mCountryCodeObj);

                    savedUsersList.add(mUsersPojoObj);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return savedUsersList;

    }


    /*public ArrayList<ChatPojo> retrieveUserChat(String RoomId,Context context) {
        SQLiteDatabase db = getReadableDatabase();
//        Cursor cursor = db.query(TABLE_CHAT, null, null, null, null, null, null);
        Cursor cursor = db.rawQuery("select * from tbl_chat where "+ ROOM_ID + " = '"+RoomId+"'",null);
        ArrayList<ChatPojo> savedUserMessages = new ArrayList<ChatPojo>();
        ChatPojo mUsersChatPojoObj;
        try {
            if (cursor.getCount() > 0) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToNext();
                   mUsersChatPojoObj= new ChatPojo();
                    mUsersChatPojoObj.set_id(cursor.getString(0));
                    mUsersChatPojoObj.setRoomId(cursor.getString(1));
                    mUsersChatPojoObj.setUserId(cursor.getString(2));
                    mUsersChatPojoObj.setType(cursor.getString(3));
                    mUsersChatPojoObj.setText(cursor.getString(4));
                    mUsersChatPojoObj.setMedia(cursor.getString(5));
                    mUsersChatPojoObj.setThumb(cursor.getString(6));
                    mUsersChatPojoObj.setStatus(cursor.getInt(7));
                    mUsersChatPojoObj.setCreated_at(cursor.getString(8));
                    mUsersChatPojoObj.setDate_updated(cursor.getString(9));
                    mUsersChatPojoObj.setMessageId(cursor.getString(10));
                    mUsersChatPojoObj.setName(cursor.getString(11));
                    mUsersChatPojoObj.setIcon(cursor.getString(12));
                    mUsersChatPojoObj.setRelation(cursor.getString(13));
                    mUsersChatPojoObj.setBadges(cursor.getString(14));
                    mUsersChatPojoObj.setImagePath(cursor.getString(15));
                    savedUserMessages.add(mUsersChatPojoObj);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return savedUserMessages;

    }*/

    public ArrayList<ChatPojo> retrieveUserChat(String roomId, int limit, int offset, int lastIndex) {
        SQLiteDatabase db = getReadableDatabase();
//        Cursor cursor = db.query(TABLE_CHAT, null, null, null, null, null, null);
        //String query = "SELECT * FROM ( SELECT * FROM " + TABLE_CHAT + " where " + ROOM_ID + " = '" + roomId + "' ORDER BY " + Id + " DESC LIMIT " + limit + " OFFSET " + offset + ") As AliasName ORDER BY " + Id + " ASC";
        String query = null;
        if (offset == 0) {
            query = "SELECT * FROM " + TABLE_CHAT + " where " + ROOM_ID + " = '" + roomId + "' ORDER BY " + IDCHAT + " DESC LIMIT " + limit + " OFFSET " + offset + "";
            Log.e(LOG_TAG, offset + " if" + query);
        } else {
            query = "SELECT * FROM " + TABLE_CHAT + " where " + ROOM_ID + " = '" + roomId + "' and " + IDCHAT + " <= " + lastIndex + " ORDER BY " + IDCHAT + " DESC LIMIT " + limit + " OFFSET " + offset + "";
            Log.e(LOG_TAG, offset + " else" + query);
        }

        Cursor cursor = db.rawQuery(query, null);
        ArrayList<ChatPojo> savedUserMessages = new ArrayList<ChatPojo>();
        ChatPojo mUsersChatPojoObj;
        try {
            if (cursor.getCount() > 0) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToNext();
                    mUsersChatPojoObj = new ChatPojo();
                    mUsersChatPojoObj.setId_chat(cursor.getInt(0));
                    mUsersChatPojoObj.set_id(cursor.getString(1));
                    mUsersChatPojoObj.setRoomId(cursor.getString(2));
                    mUsersChatPojoObj.setUserId(cursor.getString(3));
                    mUsersChatPojoObj.setType(cursor.getString(4));
                    mUsersChatPojoObj.setText(cursor.getString(5));
                    mUsersChatPojoObj.setMedia(cursor.getString(6));
                    mUsersChatPojoObj.setThumb(cursor.getString(7));
                    mUsersChatPojoObj.setStatus(cursor.getInt(8));
                    mUsersChatPojoObj.setCreated_at(cursor.getString(9));
                    mUsersChatPojoObj.setDate_updated(cursor.getString(10));
                    mUsersChatPojoObj.setMessageId(cursor.getString(11));
                    mUsersChatPojoObj.setName(cursor.getString(12));
                    mUsersChatPojoObj.setIcon(cursor.getString(13));
                    mUsersChatPojoObj.setRelation(cursor.getString(14));
                    mUsersChatPojoObj.setBadges(cursor.getString(15));
                    mUsersChatPojoObj.setImagePath(cursor.getString(16));
                    savedUserMessages.add(mUsersChatPojoObj);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return savedUserMessages;

    }

    public void deleteAllData(SQLiteDatabase db) {
        db.execSQL("delete from " + TABLE_BADGES);
    }

    // Getting contacts Count
    public int getParticularChatCount(String roomId) {
        String countQuery = "SELECT  * FROM " + TABLE_CHAT + " WHERE " + ROOM_ID + " = '" + roomId + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }

    public void deleteAllDataOfRecentChat() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from " + TABLE_RECENT_CHAT);
    }

    //rohit get all recent messages

    public ArrayList<ChatListPojo> getAllRecenChatMsg() {
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_RECENT_CHAT;
        Log.e(LOG_TAG, query);


        Cursor cursor = db.rawQuery(query, null);
        ArrayList<ChatListPojo> recentChatList = new ArrayList<ChatListPojo>();
        ChatListPojo mChatListPojo;
        try {
            if (cursor.getCount() > 0) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToNext();
                    mChatListPojo = new ChatListPojo();
                    mChatListPojo.set_id(cursor.getString(0));
                    mChatListPojo.setBusinessId(cursor.getString(1));
                    mChatListPojo.setType(cursor.getInt(2));
                    mChatListPojo.setCreated_at(cursor.getString(3));
                    mChatListPojo.setDate_updated(cursor.getString(4));
                    mChatListPojo.setLast_message_Id(cursor.getString(5));
                    mChatListPojo.setLast_message_by(cursor.getString(6));
                    mChatListPojo.setLast_message_type(cursor.getString(7));
                    mChatListPojo.setLast_message(cursor.getString(8));
                    mChatListPojo.setUnreadCount(cursor.getString(9));
                    mChatListPojo.setRoomId(cursor.getString(10));
                    mChatListPojo.setUserId(cursor.getString(11));
                    mChatListPojo.setName(cursor.getString(12));
                    mChatListPojo.setIcon(cursor.getString(13));
                    mChatListPojo.setMembers(cursor.getString(14));
                    mChatListPojo.setRelation(cursor.getString(15));
                    mChatListPojo.setBadges(cursor.getString(16));
                    recentChatList.add(mChatListPojo);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return recentChatList;
    }


}
