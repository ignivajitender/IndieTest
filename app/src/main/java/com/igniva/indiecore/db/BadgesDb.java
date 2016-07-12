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

     public static  final String LOG_TAG="BadgesDb";
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
                + "(" +BADGE_ID +  " TEXT PRIMARY KEY,"+ S_NO + " INTEGER," +
                BADGE_NAME + " TEXT," + BADGE_DESC + " TEXT," + BADGE_ICON + " TEXT,"
                + IS_PREMIUM + " INTEGER," + BADGE_PRICE + " REAL,"
                + BADGE_SKU + " TEXT," + IS_ACTIVE + " INTEGER" + ")";
    }



//    TABLE USERS
public  static  final String TABLE_USERS="tbl_users";
//    COLUMN USERS
    public  static final String MOBILE_NO="";
    public  static final String FIRST_NAME="";
    public  static final String LAST_NAME="";
    public  static final String CONTACT_IMG="";
    public  static final int TYPE=-1;
    public  static final int BADGE_COUNT=-1;

//    TABLE END USERS

    public static String createUsersTable(){

        return "CREATE TABLE IF NOT EXISTS " + TABLE_USERS
                + "(" +MOBILE_NO +  " TEXT PRIMARY KEY,"+ S_NO +
                FIRST_NAME + " TEXT," + LAST_NAME + " TEXT," + CONTACT_IMG + " TEXT,"
                + BADGE_COUNT + " INTEGER," + TYPE + " INTEGER" + ")";
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
            values.put(IS_ACTIVE, badges.getActive());
            // Inserting Single Row
            db.insertWithOnConflict(TABLE_BADGES, null, values,SQLiteDatabase.CONFLICT_REPLACE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void insertSingleContact(SQLiteDatabase db, UsersPojo users){

        try {
            ContentValues values= new ContentValues();
            values.put(MOBILE_NO,users.getMobileNo());
            values.put(FIRST_NAME,users.getProfile().getFirstName());
            values.put(LAST_NAME,users.getProfile().getLastName());
            values.put(CONTACT_IMG,users.getProfile().getProfilePic());
            db.insertWithOnConflict(TABLE_USERS,null,values,SQLiteDatabase.CONFLICT_REPLACE);

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public  void updateSingleRow(BadgesPojo badges) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
//            "UPDATE "+ MyConstants.TABLE_NAME + " SET "+ MyConstants.ISFAV + " = "+fav+ " WHERE " + MyConstants.WORD_NAME + " = \""+word_name+"\"", null);
                db.rawQuery("UPDATE "+ TABLE_BADGES + " SET "+IS_ACTIVE+ "="+badges.getActive()+ " WHERE "+BADGE_ID+ " = '"+badges.getBadgeId()+"'", null);

            String query="SELECT * FROM tbl_badge_master WHERE id_badge='"+badges.getBadgeId()+"'";
            Cursor cursor=db.rawQuery(query,null);
            while (cursor.moveToNext()) {

                for (int i=0;i<9;i++){
                    Log.d(LOG_TAG,"coulumn is "+i+" value "+cursor.getString(i));
                }
//                String result_0=cursor.getString(0);
//                String result_1=cursor.getString(1);
//                Log.d(LOG_TAG,"first column "+result_0);
//                Log.d(LOG_TAG,"second column "+result_0);
//                Log.d(LOG_TAG,"thirrd column "+result_0);
//                Log.d(LOG_TAG,"fourth column "+result_0);
//                Log.d(LOG_TAG,"fifth column "+result_0);

                //and so on
            }
            cursor.close();

               //  db.rawQuery("SELECT * FROM tbl_badge_master WHERE id_badge='"+badges.getBadgeId()+"'",null);

           // Log.d(LOG_TAG,""+db.rawQuery("SELECT * FROM tbl_badge_master WHERE id_badge='"+badges.getBadgeId()+"'",null));
        } catch (Exception e) {
            e.printStackTrace();
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

    public void InsertAllContacts(ArrayList<UsersPojo> mUsers){
        SQLiteDatabase db=this.getWritableDatabase();
        try {
            for(int i=0;i<mUsers.size();i++){

                insertSingleContact(db,mUsers.get(i));
            }

        }catch (Exception e){
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

    public void deleteAllData(SQLiteDatabase db) {
        db.execSQL("delete from " + TABLE_BADGES);
    }

}
