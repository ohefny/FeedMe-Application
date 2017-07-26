package com.example.bethechange.feedme.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.bethechange.feedme.MainScreen.Models.Category;
import com.example.bethechange.feedme.Utils.DBUtils;

/**
 * Created by BeTheChange on 7/10/2017.
 */

public class FeedMeDBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION=1;
    final static String DATABASE_NAME="feedme.db";

    public FeedMeDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_CATEGORY_TABLE="CREATE TABLE " + Contracts.CategoryEntry.TABLE_NAME + " ("+
                Contracts.CategoryEntry._ID + " INTEGER PRIMARY KEY," +
                Contracts.CategoryEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                Contracts.CategoryEntry.COLUMN_SHARED +" BOOLEAN " +");";
        final String SQL_CREATE_SITE_TABLE="CREATE TABLE " + Contracts.SiteEntry.TABLE_NAME + " ("+
                Contracts.SiteEntry._ID + " BIGINT PRIMARY KEY," +
                Contracts.SiteEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                Contracts.SiteEntry.COLUMN_RSS_URL + " TEXT NOT NULL, " +
                Contracts.SiteEntry.COLUMN_URL + " TEXT NOT NULL, " +
                Contracts.SiteEntry.COLUMN_IMG_URL + " TEXT , " +
                Contracts.SiteEntry.COLUMN_CATEGORY +" INTEGER, " +
                Contracts.SiteEntry.COLUMN_BLOCKED +" BOOLEAN, " +
                "FOREIGN KEY("+Contracts.SiteEntry.COLUMN_CATEGORY+") REFERENCES "+
                Contracts.CategoryEntry.TABLE_NAME+"("+Contracts.CategoryEntry._ID+ "));";
        final String SQL_CREATE_ARTICLE_TABLE="CREATE TABLE " + Contracts.ArticleEntry.TABLE_NAME + " ("+
                Contracts.ArticleEntry._ID + " BIGINT PRIMARY KEY," +
                Contracts.ArticleEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                Contracts.ArticleEntry.COLUMN_AUTHOR + " TEXT , " +
                Contracts.ArticleEntry.COLUMN_DATE + " BIGINT , " +
                Contracts.ArticleEntry.COLUMN_DESCRIPTION + " TEXT , " +
                Contracts.ArticleEntry.COLUMN_IMAGE + " TEXT , " +
                Contracts.ArticleEntry.COLUMN_SOURCE + " TEXT NOT NULL, " +
                Contracts.ArticleEntry.COLUMN_CONTENT + " TEXT , " +
                Contracts.ArticleEntry.COLUMN_TAGS + " TEXT , " +
                Contracts.ArticleEntry.COLUMN_COMMENTS + " TEXT, " +
                Contracts.ArticleEntry.COLUMN_SITE +" INTEGER, " +
                Contracts.ArticleEntry.COLUMN_FAVORITE +" BOOLEAN, " +
                Contracts.ArticleEntry.COLUMN_SAVED +" BOOLEAN, " +
                Contracts.ArticleEntry.COLUMN_CONTENT_FETCHED +" BOOLEAN, " +
                Contracts.ArticleEntry.COLUMN_WEBARCHIVE_PATH + " TEXT, " +
                Contracts.ArticleEntry.COLUMN_PUBLISHED_DATE +" TEXT,"+
                "FOREIGN KEY("+Contracts.ArticleEntry.COLUMN_SITE+") REFERENCES "+
                Contracts.SiteEntry.TABLE_NAME+"("+Contracts.SiteEntry._ID+ "));";

        try {


        db.execSQL(SQL_CREATE_CATEGORY_TABLE);
        db.execSQL(SQL_CREATE_SITE_TABLE);
        db.execSQL(SQL_CREATE_ARTICLE_TABLE);
        }
        catch (Exception ex){
            Log.d("Database ","Failed to create");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Contracts.ArticleEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Contracts.SiteEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Contracts.CategoryEntry.TABLE_NAME);
        onCreate(db);
    }
}
