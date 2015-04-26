package net.americanairguns.classifiedads;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class DBAdapter {

    static final String TAG = "DBAdapter";
    static final String KEY_AD_ID = "adId";//0
    static final String KEY_AD_SUBMIT_DATE = "adSubmitDate";//1
    static final String KEY_AD_TIME_STAMP = "adTimeStamp";//2
    static final String KEY_AD_TYPE = "adType";//3
    static final String KEY_AD_ITEM = "adItem";//4
    static final String KEY_AD_SUBJECT = "adSubject";//5
    static final String KEY_AD_TEXT = "adText";//6
    static final String KEY_TRADE_TEXT = "tradeText";//7
    static final String KEY_ASKING_PRICE = "askingPrice";//8
    static final String KEY_PLUS_SHIPPING = "plusShipping";//9
    static final String KEY_NAME = "userName";//10
    static final String KEY_REMOTE_ADDRESS = "remoteAddress";//11
    static final String KEY_EMAIL_ADDRESS = "emailAddress";//12
    static final String KEY_PHONE_NUMBER = "phoneNumber";//13
    static final String KEY_TIME_ZONE = "timeZone";//14
    static final String KEY_IMAGE_1_FILE = "image1File";//15
    static final String KEY_IMAGE_2_FILE = "image2File";//16
    static final String KEY_IMAGE_3_FILE = "image3File";//17
    static final String KEY_IMAGE_4_FILE = "image4File";//18
    static final String KEY_IMAGE_5_FILE = "image5File";//19
    static final String KEY_AD_ICON = "adIcon";//20
    static final String KEY_PASSWORD = "password";//21

    static final String DATABASE_NAME = "american_airguns";
    static final String TABLE_ADS = "classified_ads";
    static final int DATABASE_VERSION = 1;

    static final String TABLE_ADS_CREATE = "CREATE TABLE classified_ads (" +
            "adId INTEGER PRIMARY KEY, " +
            "adSubmitDate TEXT, " +
            "adTimeStamp TEXT, " +
            "adType TEXT, " +
            "adItem TEXT, " +
            "adSubject TEXT, " +
            "adText TEXT, " +
            "tradeText TEXT, " +
            "askingPrice INTEGER, " +
            "plusShipping TEXT, " +
            "userName TEXT, " +
            "remoteAddress TEXT, " +
            "emailAddress TEXT, " +
            "phoneNumber TEXT, " +
            "timeZone TEXT, " +
            "image1File TEXT, " +
            "image2File TEXT, " +
            "image3File TEXT, " +
            "image4File TEXT, " +
            "image5File TEXT, " +
            "adIcon BLOB, " +
            "password TEXT DEFAULT NULL" +
            ");";

    static final String TABLE_VIRTUAL_CREATE = "CREATE VIRTUAL TABLE IF NOT EXISTS virtual USING fts3(" +
            "adId INTEGER PRIMARY KEY, " +
            "adSubmitDate TEXT, " +
            "adTimeStamp TEXT, " +
            "adType TEXT, " +
            "adItem TEXT, " +
            "adSubject TEXT, " +
            "adText TEXT, " +
            "tradeText TEXT, " +
            "askingPrice INTEGER, " +
            "plusShipping TEXT, " +
            "userName TEXT, " +
            "remoteAddress TEXT, " +
            "emailAddress TEXT, " +
            "phoneNumber TEXT, " +
            "timeZone TEXT, " +
            "image1File TEXT, " +
            "image2File TEXT, " +
            "image3File TEXT, " +
            "image4File TEXT, " +
            "image5File TEXT, " +
            "adIcon BLOB, " +
            "password TEXT DEFAULT NULL" +
            ");";

    final Context context;

    DatabaseHelper DBHelper;
    SQLiteDatabase db;

    public DBAdapter(Context ctx) {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(TABLE_ADS_CREATE);
                db.execSQL(TABLE_VIRTUAL_CREATE);
            } catch (SQLException e) { e.printStackTrace(); }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy old data");
            db.execSQL("DROP TABLE IF EXISTS classified_ads");
            onCreate(db);
        }
    }

    public DBAdapter open () throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        DBHelper.close();
    }

    // Input is a SQL statement that will be executed
    public void performExec(String query) {
        db.execSQL(query);
    }

////////    Create      /////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean JSON2ROW(JSONObject jsonObject) throws SQLiteConstraintException{
        ContentValues values = new ContentValues();
        try {
            values.put(KEY_AD_ID, jsonObject.getInt("AdId"));
            values.put(KEY_AD_SUBMIT_DATE, jsonObject.getString("AdSubmitDate"));
            values.put(KEY_AD_TIME_STAMP, jsonObject.getString("AdTimeStamp"));
            values.put(KEY_AD_TYPE, jsonObject.getString("AdType"));
            values.put(KEY_AD_ITEM, jsonObject.getString("AdItem"));
            values.put(KEY_AD_SUBJECT, jsonObject.getString("AdSubject"));
            values.put(KEY_AD_TEXT, jsonObject.getString("AdText"));
            values.put(KEY_TRADE_TEXT, jsonObject.getString("TradeText"));
            values.put(KEY_ASKING_PRICE, jsonObject.getInt("AskingPrice"));
            values.put(KEY_PLUS_SHIPPING, jsonObject.getString("PlusShipping"));
            values.put(KEY_NAME, jsonObject.getString("Name"));
            values.put(KEY_REMOTE_ADDRESS, jsonObject.getString("RemoteAddress"));
            values.put(KEY_EMAIL_ADDRESS, jsonObject.getString("EmailAddress"));
            values.put(KEY_PHONE_NUMBER, jsonObject.getString("PhoneNumber"));
            values.put(KEY_TIME_ZONE, jsonObject.getString("TimeZone"));
            values.put(KEY_IMAGE_1_FILE, jsonObject.getString("Image1File"));
            values.put(KEY_IMAGE_2_FILE, jsonObject.getString("Image2File"));
            values.put(KEY_IMAGE_3_FILE, jsonObject.getString("Image3File"));
            values.put(KEY_IMAGE_4_FILE, jsonObject.getString("Image4File"));
            values.put(KEY_IMAGE_5_FILE, jsonObject.getString("Image5File"));
            values.put(KEY_AD_ICON, Base64.decode(jsonObject.getString("AdIcon"), Base64.DEFAULT));
            values.put(KEY_PASSWORD, jsonObject.getString("Password"));
        } catch (JSONException je) { je.getStackTrace(); }
        return db.insert(TABLE_ADS, null, values) > 0;
    }


////////    Read        /////////////////////////////////////////////////////////////////////////////////////////////////

    public Cursor getAllAds(String key, String sortOrder) {
        return db.query(true, TABLE_ADS, new String[] {KEY_AD_ID, KEY_AD_SUBMIT_DATE, KEY_AD_TIME_STAMP, KEY_AD_TYPE, KEY_AD_ITEM, KEY_AD_SUBJECT, KEY_AD_TEXT, KEY_TRADE_TEXT, KEY_ASKING_PRICE, KEY_PLUS_SHIPPING, KEY_NAME, KEY_REMOTE_ADDRESS, KEY_EMAIL_ADDRESS, KEY_PHONE_NUMBER, KEY_TIME_ZONE, KEY_IMAGE_1_FILE, KEY_IMAGE_2_FILE, KEY_IMAGE_3_FILE, KEY_IMAGE_4_FILE, KEY_IMAGE_5_FILE, KEY_AD_ICON}, null, null, null, null, key + " " + sortOrder, null);
    }

    public Cursor getAdsByType(String adType, String key, String sortOrder) {
        return db.query(TABLE_ADS, new String[] {KEY_AD_ID, KEY_AD_SUBMIT_DATE, KEY_AD_TIME_STAMP, KEY_AD_TYPE, KEY_AD_ITEM, KEY_AD_SUBJECT, KEY_AD_TEXT, KEY_TRADE_TEXT, KEY_ASKING_PRICE, KEY_PLUS_SHIPPING, KEY_NAME, KEY_REMOTE_ADDRESS, KEY_EMAIL_ADDRESS, KEY_PHONE_NUMBER, KEY_TIME_ZONE, KEY_IMAGE_1_FILE, KEY_IMAGE_2_FILE, KEY_IMAGE_3_FILE, KEY_IMAGE_4_FILE, KEY_IMAGE_5_FILE, KEY_AD_ICON}, KEY_AD_TYPE + "='" + adType + "'", null, null, null, key + " " + sortOrder, null);
    }

    public Cursor getAdsByItem(String adItem, String key, String sortOrder) {
        return db.query(TABLE_ADS, new String[] {KEY_AD_ID, KEY_AD_SUBMIT_DATE, KEY_AD_TIME_STAMP, KEY_AD_TYPE, KEY_AD_ITEM, KEY_AD_SUBJECT, KEY_AD_TEXT, KEY_TRADE_TEXT, KEY_ASKING_PRICE, KEY_PLUS_SHIPPING, KEY_NAME, KEY_REMOTE_ADDRESS, KEY_EMAIL_ADDRESS, KEY_PHONE_NUMBER, KEY_TIME_ZONE, KEY_IMAGE_1_FILE, KEY_IMAGE_2_FILE, KEY_IMAGE_3_FILE, KEY_IMAGE_4_FILE, KEY_IMAGE_5_FILE, KEY_AD_ICON}, KEY_AD_ITEM + "='" + adItem + "'", null, null, null, key + " " + sortOrder, null);
    }

    public Cursor getAdByAdId(Integer adId) {
        return db.query(TABLE_ADS, new String[] {KEY_AD_ID, KEY_AD_SUBMIT_DATE, KEY_AD_TIME_STAMP, KEY_AD_TYPE, KEY_AD_ITEM, KEY_AD_SUBJECT, KEY_AD_TEXT, KEY_TRADE_TEXT, KEY_ASKING_PRICE, KEY_PLUS_SHIPPING, KEY_NAME, KEY_REMOTE_ADDRESS, KEY_EMAIL_ADDRESS, KEY_PHONE_NUMBER, KEY_TIME_ZONE, KEY_IMAGE_1_FILE, KEY_IMAGE_2_FILE, KEY_IMAGE_3_FILE, KEY_IMAGE_4_FILE, KEY_IMAGE_5_FILE, KEY_AD_ICON}, KEY_AD_ID + "='" + adId + "'", null, null, null, null, null);
    }

    public Cursor getAdsByName(String name, String key, String sortOrder) {
        return db.query(TABLE_ADS, new String[] {KEY_AD_ID, KEY_AD_SUBMIT_DATE, KEY_AD_TIME_STAMP, KEY_AD_TYPE, KEY_AD_ITEM, KEY_AD_SUBJECT, KEY_AD_TEXT, KEY_TRADE_TEXT, KEY_ASKING_PRICE, KEY_PLUS_SHIPPING, KEY_NAME, KEY_REMOTE_ADDRESS, KEY_EMAIL_ADDRESS, KEY_PHONE_NUMBER, KEY_TIME_ZONE, KEY_IMAGE_1_FILE, KEY_IMAGE_2_FILE, KEY_IMAGE_3_FILE, KEY_IMAGE_4_FILE, KEY_IMAGE_5_FILE, KEY_AD_ICON}, KEY_NAME + "='" + name + "'", null, null, null, key + " " + sortOrder, null);
    }

    public Cursor getAdsByKeyword(String keyword, String key, String sortOrder) {
        return db.query(TABLE_ADS, new String[] {KEY_AD_ID, KEY_AD_SUBMIT_DATE, KEY_AD_TIME_STAMP, KEY_AD_TYPE, KEY_AD_ITEM, KEY_AD_SUBJECT, KEY_AD_TEXT, KEY_TRADE_TEXT, KEY_ASKING_PRICE, KEY_PLUS_SHIPPING, KEY_NAME, KEY_REMOTE_ADDRESS, KEY_EMAIL_ADDRESS, KEY_PHONE_NUMBER, KEY_TIME_ZONE, KEY_IMAGE_1_FILE, KEY_IMAGE_2_FILE, KEY_IMAGE_3_FILE, KEY_IMAGE_4_FILE, KEY_IMAGE_5_FILE, KEY_AD_ICON}, KEY_AD_SUBJECT + " LIKE '%" + keyword + "%'", null, null, null, key + " " + sortOrder, null);
    }

    public Cursor getAdsByKeywords(String keyword, String key, String sortOrder) {
        db.execSQL("DELETE FROM virtual");
        db.execSQL("INSERT INTO virtual SELECT * FROM " + TABLE_ADS);
        Cursor cursor = db.query(true, "virtual", new String[] {KEY_AD_ID, KEY_AD_SUBMIT_DATE, KEY_AD_TIME_STAMP, KEY_AD_TYPE, KEY_AD_ITEM, KEY_AD_SUBJECT, KEY_AD_TEXT, KEY_TRADE_TEXT, KEY_ASKING_PRICE, KEY_PLUS_SHIPPING, KEY_NAME, KEY_REMOTE_ADDRESS, KEY_EMAIL_ADDRESS, KEY_PHONE_NUMBER, KEY_TIME_ZONE, KEY_IMAGE_1_FILE, KEY_IMAGE_2_FILE, KEY_IMAGE_3_FILE, KEY_IMAGE_4_FILE, KEY_IMAGE_5_FILE, KEY_AD_ICON}, "adSubject MATCH ?", new String[] {keyword.replaceAll(",", " OR ")}, null, null, key + " " + sortOrder, null);
        Log.i("SEARCH_KEYWORD", keyword.replaceAll(",", " OR "));
        Log.i("SEARCH_CURSOR", String.valueOf(cursor.getCount()));
        db.execSQL("DELETE FROM virtual");
        return cursor;
    }

    public Cursor getAdsByPrice(Integer priceStart, Integer priceEnd, Integer priceMax, String key, String sortOrder) {
        return db.query(TABLE_ADS, new String[] {KEY_AD_ID, KEY_AD_SUBMIT_DATE, KEY_AD_TIME_STAMP, KEY_AD_TYPE, KEY_AD_ITEM, KEY_AD_SUBJECT, KEY_AD_TEXT, KEY_TRADE_TEXT, KEY_ASKING_PRICE, KEY_PLUS_SHIPPING, KEY_NAME, KEY_REMOTE_ADDRESS, KEY_EMAIL_ADDRESS, KEY_PHONE_NUMBER, KEY_TIME_ZONE, KEY_IMAGE_1_FILE, KEY_IMAGE_2_FILE, KEY_IMAGE_3_FILE, KEY_IMAGE_4_FILE, KEY_IMAGE_5_FILE, KEY_AD_ICON}, KEY_AD_TYPE + "='ForSale' AND " + KEY_ASKING_PRICE + ">=" + priceStart + (!priceEnd.equals(priceMax) ? " AND " + KEY_ASKING_PRICE + "<=" + priceEnd : ""), null, null, null, key + " " + sortOrder, null);
    }

    public Cursor getAllAds() {
        return this.getAllAds(KEY_AD_ID, "DESC");
    }

    public Cursor getNames() {
        return db.query(true, TABLE_ADS, new String[] {KEY_NAME}, null, null, null, null, KEY_NAME + " ASC", null);
    }

    public Cursor getItems() {
        return db.query(true, TABLE_ADS, new String[] {KEY_AD_ITEM}, null, null, null, null, KEY_AD_ITEM + " ASC", null);
    }

    public Cursor getImages(Integer adId) {
        return db.query(TABLE_ADS, new String[] {KEY_AD_ICON}, KEY_AD_ID + "=" + adId, null, null, null, null, null);
    }

    public Integer getFirstAd() {
        Cursor cursor = db.query(TABLE_ADS, new String[] {KEY_AD_ID}, null, null, null, null, KEY_AD_ID + " ASC", null);
        return (cursor.getCount() > 0 && cursor.moveToFirst()) ? (cursor.getInt(0)) : (-1);
    }

    public Integer getLastAd() {
        Cursor cursor = db.query(TABLE_ADS, new String[] {KEY_AD_ID}, null, null, null, null, KEY_AD_ID + " ASC", null);
        return (cursor.getCount() > 0 && cursor.moveToLast()) ? (cursor.getInt(0)) : (-1);
    }


////////    Update      /////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean updateIcon(String iconBinary, Integer adID) {
        ContentValues values = new ContentValues();
        values.put(KEY_AD_ICON, iconBinary);
        return db.update(TABLE_ADS, values, KEY_AD_ID + "=" + adID, null) > 0;
    }

    public boolean updateIcon(byte[] iconBinary, Integer adID) {
        ContentValues values = new ContentValues();
        values.put(KEY_AD_ICON, iconBinary);
        return db.update(TABLE_ADS, values, KEY_AD_ID + "=" + adID, null) > 0;
    }

    public boolean updateImage(Integer adID, byte[] bytes, String imageKey) {
        ContentValues values = new ContentValues();
        values.put(imageKey, bytes);
        return db.update(TABLE_ADS, values, KEY_AD_ID + "=" + adID, null) > 0;
    }

////////    Delete      /////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean removeOldAds(Integer oldestAd) {
        return db.delete(TABLE_ADS, KEY_AD_ID + "<" + oldestAd, null) > 0;
    }

    public boolean removeAdById(Integer adID) {
        return db.delete(TABLE_ADS, KEY_AD_ID + "=" + adID, null) > 0;
    }

    public DBAdapter deleteAdById(Integer adID) throws SQLException {
        return (db.delete(TABLE_ADS, KEY_AD_ID + "=" + adID, null) > 0 ? this : null);
    }

////////    Other       /////////////////////////////////////////////////////////////////////////////////////////////////

    public Integer[] getAdIds() {
        Cursor cursor = db.query(TABLE_ADS, new String[] {KEY_AD_ID}, null, null, null, null, KEY_AD_ID + " DESC", null);
        Integer[] adIds = new Integer[cursor.getCount()];
        if (cursor.getCount() > 0 && cursor.moveToFirst()) {
            do {
                adIds[cursor.getPosition()] = cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        return adIds;
    }

    private String appendWildcard(String query) {
        if (TextUtils.isEmpty(query)) return query;

        final StringBuilder builder = new StringBuilder();
        final String[] splits = TextUtils.split(query, " ");

        for (String split : splits)
            builder.append(split).append("*").append(" ");

        return builder.toString().trim();
    }
}
