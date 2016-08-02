package com.igniva.indiecore.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.igniva.indiecore.model.BadgesPojo;
import com.igniva.indiecore.model.ContactPojo;
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
    private static final int DATABASE_VERSION = 1;

    //---- DATABASE END-----------


    //---- TABLE BADGE START-----------

    public static final String TABLE_BADGES = "tbl_badge_master";
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

    //---- TABLE BADGE END-----------

    public static String createBadgeTable() {

        return "CREATE TABLE IF NOT EXISTS " + TABLE_BADGES
                + "(" + BADGE_ID + " TEXT PRIMARY KEY," + S_NO + " INTEGER," +
                BADGE_NAME + " TEXT," + BADGE_DESC + " TEXT," + BADGE_ICON + " TEXT,"
                + IS_PREMIUM + " INTEGER," + BADGE_PRICE + " REAL,"
                + BADGE_SKU + " TEXT," + IS_ACTIVE + " INTEGER" + ")";
    }


    //    TABLE USERS
    public static final String TABLE_USERS = "tbl_users";
    //    COLUMN USERS
    public static final String MOBILE_NO = "mobile_no";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String CONTACT_IMG = "contact_img";
    public static final String TYPE = "type";
    public static final String BADGE_COUNT = "badge_count";
    public static final String DESCERIPTION = "description";

//    TABLE END USERS

    public static String createUsersTable() {


//        type=0 non-indiecore user
//           type=1 indiecore user
        return "CREATE TABLE IF NOT EXISTS " + TABLE_USERS
                + "(" + MOBILE_NO + " TEXT PRIMARY KEY," +
                FIRST_NAME + " TEXT," + LAST_NAME + " TEXT," + DESCERIPTION + " TEXT," + CONTACT_IMG + " TEXT,"
                + BADGE_COUNT + " INTEGER," + TYPE + " INTEGER" + ")";
    }


    public BadgesDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createBadgeTable());
        db.execSQL(createUsersTable());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BADGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
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
            db.insertWithOnConflict(TABLE_BADGES, null, values, SQLiteDatabase.CONFLICT_REPLACE);
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
            values.put(DESCERIPTION, users.getProfile().getDesc());
            values.put(BADGE_COUNT, 12);
            values.put(TYPE, 1);
            db.insertWithOnConflict(TABLE_USERS, null, values, SQLiteDatabase.CONFLICT_REPLACE);

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

    public ArrayList<ProfilePojo> retrieveSavedUsersList() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, null, null, null, null, null);
        ArrayList<ProfilePojo> savedUsersList = new ArrayList<ProfilePojo>();
        ProfilePojo usersPojoObj;

        try {
            if (cursor.getCount() > 0) {
                for (int i = 0; i < cursor.getCount(); i++) {

                    cursor.moveToNext();
                    usersPojoObj = new ProfilePojo();
                    usersPojoObj.setFirstName(cursor.getString(1));
                    usersPojoObj.setDesc(cursor.getString(3));
                    usersPojoObj.setProfilePic(cursor.getString(4));
                    savedUsersList.add(usersPojoObj);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return savedUsersList;

    }

    public void deleteAllData(SQLiteDatabase db) {
        db.execSQL("delete from " + TABLE_BADGES);
    }

}
