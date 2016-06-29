package com.igniva.indiecore.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.igniva.indiecore.model.BadgesPojo;

import java.util.ArrayList;

/**
 * Created by igniva-andriod-05 on 23/6/16.
 */
public class BadgesDb extends SQLiteOpenHelper {


    //---- DATABASE START-----------

    public static final String DATABASE_NAME = "db_indiecore";
    private static final int DATABASE_VERSION = 1;

    //---- DATABASE END-----------


    //---- TABLE BADGE START-----------

    public static final String TABLE_BADGES = "tbl_badge_masterjun27";
    // COLUMN NAMES
    public static final String S_NO = "s_no";
    public static final String BADGE_ID = "id_badge";
    public static final String BADGE_NAME = "badge_name";
    public static final String BADGE_DESC = "badge_desc";
    public static final String BADGE_ICON = "badge_icon";
    public static final String IS_PREMIUM = "is_premium";
    public static final String BADGE_PRICE = "badge_price";
    public static final String BADGE_SKU = "badge_sku";
    public static final String IS_SELECTED = "is_selected";

    //---- TABLE BADGE END-----------


    public static String createBadgeTable() {

        return "CREATE TABLE " + TABLE_BADGES
                + "(" +BADGE_ID +  " TEXT PRIMARY KEY,"+ S_NO + " INTEGER NOT NULL," +
                BADGE_NAME + " TEXT," + BADGE_DESC + " TEXT," + BADGE_ICON + " TEXT,"
                + IS_PREMIUM + " INTEGER," + BADGE_PRICE + " REAL,"
                + BADGE_SKU + " TEXT," + IS_SELECTED + " INTEGER" + ")";
    }

    public BadgesDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createBadgeTable());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BADGES);
        onCreate(db);
    }


    public void insertSingleBadge(SQLiteDatabase db, BadgesPojo badges) {
        try {
            ContentValues values = new ContentValues();
            values.put(S_NO,1);
            values.put(BADGE_ID, badges.getBadgeId()); // Contact Phone Number
            values.put(BADGE_NAME, badges.getName());
            values.put(BADGE_DESC, badges.getDescription());
            values.put(BADGE_ICON, badges.getIcon());
            values.put(IS_PREMIUM, badges.getIsPremium());
            values.put(BADGE_PRICE, badges.getPrice());
            values.put(BADGE_SKU, badges.getSku());
            values.put(IS_SELECTED, 1);
            // Inserting Single Row
            db.insert(TABLE_BADGES, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public ArrayList<BadgesPojo> getMyBadges() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.query(TABLE_BADGES, null, null, null, null, null, null);
        ArrayList<BadgesPojo> badges = new ArrayList<BadgesPojo>();
        BadgesPojo badgeModel;
        if (cursor.getCount() > 0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                badgeModel = new BadgesPojo();
//                badgeModel.set(cursor.getString(0));
//                badgeModel.setFirstName(cursor.getString(1));
//                badgeModel.setLastName(cursor.getString(2));
//
                badges.add(badgeModel);
            }
        }
        cursor.close();
        database.close();
        return badges;
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
                    badgeModel.setIsSelectedAsMyBadge(8);
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

    public void deleteAllData(SQLiteDatabase db) {
        db.execSQL("delete from " + TABLE_BADGES);
    }

}
