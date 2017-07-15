package com.example.bethechange.feedme.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by BeTheChange on 7/11/2017.
 */

public class FeedMeProvider extends ContentProvider {
    private FeedMeDBHelper mFeedMeDBHelper;
    private static UriMatcher sUriMatcher;
    public static final String GROUP_BY_SITE="group_site";
    public static final String GROUP_BY_CATEGORY="group_category";
    public static final int ARTICLES=100;
    public static final int ARTICLE_WITH_ID=101;
    public static final int ARTICLE_GROUPED_BY_SITE =102;
    public static final int ARTICLE_GROUPED_BY_CATEGORY =103;
    public static final int CATEGORIES=200;
    public static final int CATEGORY_WITH_ID=201;
    public static final int SITES=300;
    public static final int SITE_WITH_ID=301;
    private UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher=new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(Contracts.AUTHORITY,Contracts.ARTICLES_PATH,ARTICLES);
        uriMatcher.addURI(Contracts.AUTHORITY,Contracts.ARTICLES_PATH+"/*",ARTICLE_WITH_ID);
        uriMatcher.addURI(Contracts.AUTHORITY,Contracts.ARTICLES_PATH+"/"+GROUP_BY_SITE, ARTICLE_GROUPED_BY_SITE);
        uriMatcher.addURI(Contracts.AUTHORITY,Contracts.ARTICLES_PATH+"/"+GROUP_BY_CATEGORY,ARTICLE_GROUPED_BY_CATEGORY);
        uriMatcher.addURI(Contracts.AUTHORITY,Contracts.CATEGORY_PATH,CATEGORIES);
        uriMatcher.addURI(Contracts.AUTHORITY,Contracts.CATEGORY_PATH+"/*",CATEGORY_WITH_ID);
        uriMatcher.addURI(Contracts.AUTHORITY,Contracts.SITES_PATH,SITES);
        uriMatcher.addURI(Contracts.AUTHORITY,Contracts.SITES_PATH+"/*",SITE_WITH_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mFeedMeDBHelper=new FeedMeDBHelper(getContext());
        sUriMatcher=buildUriMatcher();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db=mFeedMeDBHelper.getReadableDatabase();
        Cursor retCursor=null;
        switch (sUriMatcher.match(uri)){
            case ARTICLES:
                retCursor=db.query(Contracts.ArticleEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case CATEGORIES:
                retCursor=db.query(Contracts.CategoryEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case SITES:
                retCursor=db.query(Contracts.SiteEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case ARTICLE_WITH_ID:
                retCursor=db.query(Contracts.ArticleEntry.TABLE_NAME,projection,Contracts.ArticleEntry._ID
                        + " = ?",new String[]{String.valueOf(uri.getPathSegments().get(1))},null,null,sortOrder);
                break;
            case SITE_WITH_ID:
                retCursor=db.query(Contracts.SiteEntry.TABLE_NAME,projection,Contracts.SiteEntry._ID
                        +"= ?",new String[]{String.valueOf(uri.getPathSegments().get(1))},null,null,sortOrder);
                break;
            case CATEGORY_WITH_ID:
                retCursor=db.query(Contracts.SiteEntry.TABLE_NAME,projection,Contracts.SiteEntry._ID
                        +"= ?",new String[]{String.valueOf(uri.getPathSegments().get(1))},null,null,sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknwon URI "+ uri);
        }
        Context context = getContext();
        if (context != null){
            retCursor.setNotificationUri(context.getContentResolver(), uri);
        }
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)){
            case ARTICLES:
                return "vnd.android.cursor.dir" + "/" +Contracts.ArticleEntry.CONTENT_URI;
            case ARTICLE_WITH_ID:
                return "vnd.android.cursor.item" + "/" +Contracts.AUTHORITY+"/"+Contracts.ARTICLES_PATH;
            case CATEGORIES:
                return "vnd.android.cursor.dir" + "/" +Contracts.AUTHORITY+"/"+Contracts.CATEGORY_PATH;
            case CATEGORY_WITH_ID:
                return "vnd.android.cursor.item" + "/" +Contracts.AUTHORITY+"/"+Contracts.CATEGORY_PATH;
            case SITES:
                return "vnd.android.cursor.dir" + "/" +Contracts.AUTHORITY+"/"+Contracts.SITES_PATH;
            case SITE_WITH_ID:
                return "vnd.android.cursor.item" + "/" +Contracts.AUTHORITY+"/"+Contracts.SITES_PATH;
            default:
                throw new UnsupportedOperationException("Unknown uri : "+uri);

        }

    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mFeedMeDBHelper.getWritableDatabase();
        Uri retUri = null;
        switch (sUriMatcher.match(uri)) {
            case ARTICLES:
                long mId = db.insertWithOnConflict(Contracts.ArticleEntry.TABLE_NAME, null, values,
                        SQLiteDatabase.CONFLICT_IGNORE);
                if (mId > 0)
                    retUri = ContentUris.withAppendedId(uri, mId);
                else
                    throw new SQLException("Failed To Insert row into " + uri);
                break;
            case CATEGORIES:
                long RId = db.insertOrThrow(Contracts.CategoryEntry.TABLE_NAME, null, values);
                if (RId > 0)
                    retUri = ContentUris.withAppendedId(uri, RId);
                else
                    throw new SQLException("Failed To Insert row into " + uri);
                break;
            case SITES:
                long TId = db.insertOrThrow(Contracts.SiteEntry.TABLE_NAME, null, values);
                if (TId > 0)
                    retUri = ContentUris.withAppendedId(uri, TId);
                else
                    throw new SQLException("Failed To Insert row into " + uri);
                break;
        }
        Context context = getContext();
        if (context != null){
            context.getContentResolver().notifyChange(uri, null);
        }
        return  retUri;
    }
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db= mFeedMeDBHelper.getWritableDatabase();
        int deletedRows=0;
        switch (sUriMatcher.match(uri)){
            case ARTICLE_WITH_ID:
                deletedRows=db.delete(Contracts.ArticleEntry.TABLE_NAME, Contracts.ArticleEntry._ID+" = ? ",
                        new String[]{uri.getPathSegments().get(1)});
                break;
            case CATEGORY_WITH_ID:
                deletedRows=db.delete(Contracts.CategoryEntry.TABLE_NAME, Contracts.CategoryEntry._ID+" = ? ",
                        new String[]{uri.getPathSegments().get(1)});
                break;
            case SITE_WITH_ID:
                deletedRows=db.delete(Contracts.SiteEntry.TABLE_NAME, Contracts.SiteEntry._ID+" = ? ",
                        new String[]{String.valueOf(uri.getPathSegments().get(1))});
                break;
            case CATEGORIES:
                deletedRows=db.delete(Contracts.CategoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case ARTICLES:
                deletedRows=db.delete(Contracts.ArticleEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case SITES:
                deletedRows=db.delete(Contracts.SiteEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("the operation of this uri is unsupported :: " +uri);

        }
        if (deletedRows != 0) {
            Context context = getContext();
            if (context != null){
                context.getContentResolver().notifyChange(uri, null);
            }
        }
        return deletedRows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db= mFeedMeDBHelper.getWritableDatabase();
        int updatedRows=0;
        switch (sUriMatcher.match(uri)){
            case ARTICLE_WITH_ID:
                updatedRows=db.update(Contracts.ArticleEntry.TABLE_NAME,values,Contracts.ArticleEntry._ID+" = ? ",
                        new String[]{uri.getPathSegments().get(1)});
                break;
            case CATEGORY_WITH_ID:
                updatedRows=db.update(Contracts.CategoryEntry.TABLE_NAME,values, Contracts.CategoryEntry._ID+" = ? ",
                        new String[]{uri.getPathSegments().get(1)});
                break;
            case SITE_WITH_ID:
                updatedRows=db.update(Contracts.SiteEntry.TABLE_NAME,values, Contracts.SiteEntry._ID+" = ? ",
                        new String[]{String.valueOf(uri.getPathSegments().get(1))});
                break;
            case CATEGORIES:
                updatedRows=db.update(Contracts.CategoryEntry.TABLE_NAME,values, selection, selectionArgs);
                break;
            case ARTICLES:
                updatedRows=db.update(Contracts.ArticleEntry.TABLE_NAME,values, selection, selectionArgs);
                break;
            case SITES:
                updatedRows=db.update(Contracts.SiteEntry.TABLE_NAME,values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("the operation of this uri is unsupported :: " +uri);

        }


        if (updatedRows != 0) {
            Context context = getContext();
            if (context != null){
                context.getContentResolver().notifyChange(uri, null);
            }
        }

        return updatedRows;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mFeedMeDBHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {
            case ARTICLES:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long id=db.insertWithOnConflict(
                                Contracts.ArticleEntry.TABLE_NAME,
                                null,
                                value,SQLiteDatabase.CONFLICT_IGNORE
                        );
                        if(id!=-1)
                            returnCount++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                Context context = getContext();
                if (context != null) {
                    context.getContentResolver().notifyChange(uri, null);
                }

                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
