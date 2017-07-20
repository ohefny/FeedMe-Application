package com.example.bethechange.feedme.Utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.SparseArray;

import com.example.bethechange.feedme.Data.Contracts;
import com.example.bethechange.feedme.MainScreen.Models.FeedMeArticle;
import com.example.bethechange.feedme.MainScreen.Models.ArticlesList;
import com.example.bethechange.feedme.MainScreen.Models.Category;
import com.example.bethechange.feedme.MainScreen.Models.Site;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by BeTheChange on 7/10/2017.
 */

public class DBUtils {

    public static FeedMeArticle cursorToArticle(Cursor cursor){
        //// TODO: Uncomment contentfetched and webarchive when updating db schema
        cursor.moveToNext();
        FeedMeArticle feedMeArticle = new FeedMeArticle();
        feedMeArticle.getArticle().setTitle(cursor.getString(cursor.getColumnIndex(Contracts.ArticleEntry.COLUMN_TITLE)));
        feedMeArticle.getArticle().setAuthor(cursor.getString(cursor.getColumnIndex(Contracts.ArticleEntry.COLUMN_AUTHOR)));
        feedMeArticle.getArticle().setContent(cursor.getString(cursor.getColumnIndex(Contracts.ArticleEntry.COLUMN_CONTENT)));
        feedMeArticle.getArticle().setComments(cursor.getString(cursor.getColumnIndex(Contracts.ArticleEntry.COLUMN_COMMENTS)));
        feedMeArticle.getArticle().setDate(cursor.getLong(cursor.getColumnIndex(Contracts.ArticleEntry.COLUMN_DATE)));
        feedMeArticle.getArticle().setDescription(cursor.getString(cursor.getColumnIndex(Contracts.ArticleEntry.COLUMN_DESCRIPTION)));
        feedMeArticle.getArticle().setImage(Uri.parse(cursor.getString(cursor.getColumnIndex(Contracts.ArticleEntry.COLUMN_IMAGE))));
        feedMeArticle.getArticle().setSource(Uri.parse(cursor.getString(cursor.getColumnIndex(Contracts.ArticleEntry.COLUMN_SOURCE))));
        feedMeArticle.getArticle().setTags(Arrays.asList(cursor.getString(cursor.getColumnIndex(Contracts.ArticleEntry.COLUMN_TAGS)).split(",")));
        feedMeArticle.setArticleID(cursor.getInt(cursor.getColumnIndex(Contracts.ArticleEntry._ID)));
        feedMeArticle.setSiteID(cursor.getInt(cursor.getColumnIndex(Contracts.ArticleEntry.COLUMN_SITE)));
        feedMeArticle.setFav(cursor.getInt(cursor.getColumnIndex(Contracts.ArticleEntry.COLUMN_FAVORITE)) != 0);
        feedMeArticle.setSaved(cursor.getInt(cursor.getColumnIndex(Contracts.ArticleEntry.COLUMN_SAVED)) != 0);
        feedMeArticle.setContentFetched(cursor.getInt(cursor.getColumnIndex(Contracts.ArticleEntry.COLUMN_CONTENT_FETCHED)) != 0);
        feedMeArticle.setWebArchivePath(cursor.getString(cursor.getColumnIndex(Contracts.ArticleEntry.COLUMN_WEBARCHIVE_PATH)));
        feedMeArticle.setWebArchivePath(cursor.getString(cursor.getColumnIndex(Contracts.ArticleEntry.COLUMN_PUBLISHED_DATE)));
        return feedMeArticle;
    }
    public static  SparseArray<FeedMeArticle> cursorToArticles(Cursor cursor) {
        SparseArray<FeedMeArticle> articles=new SparseArray<>();
        try {
            while (!cursor.isAfterLast()) {
                FeedMeArticle feedMeArticle=cursorToArticle(cursor);
                articles.put(feedMeArticle.getArticleID(),feedMeArticle);
            }

        } catch (Exception ex) {

        } finally {
            cursor.close();
        }
        return articles;
    }

    //public FeedMeArticle cursorToArticle(Cursor cursor){}
    //public Site cursorToSite(Cursor cursor){}
    public static ArrayList<Site> cursorToSites(Cursor cursor) {
        ArrayList<Site> sites = new ArrayList<>();
        try {

            while (cursor.moveToNext()) {
                Site site = new Site();
                site.setTitle(cursor.getString(cursor.getColumnIndex(Contracts.SiteEntry.COLUMN_TITLE)));
                site.setCategoryID(cursor.getInt(cursor.getColumnIndex(Contracts.SiteEntry.COLUMN_CATEGORY)));
                site.setUrl(cursor.getString(cursor.getColumnIndex(Contracts.SiteEntry.COLUMN_URL)));
                site.setRssUrl(cursor.getString(cursor.getColumnIndex(Contracts.SiteEntry.COLUMN_RSS_URL)));
                site.setID(cursor.getInt(cursor.getColumnIndex(Contracts.SiteEntry._ID)));
                site.setBlocked(cursor.getInt(cursor.getColumnIndex(Contracts.SiteEntry.COLUMN_BLOCKED))>0);
                sites.add(site);
            }

        } catch (Exception ex) {

        } finally {
            cursor.close();
        }
        return sites;
    }

    //public Category cursortToCategory(Cursor cursor){}
    public static ArrayList<Category> cursorToCategories(Cursor cursor) {
        ArrayList<Category> categories = new ArrayList<>();
        try {

            while (cursor.moveToNext()) {
                Category category = new Category();
                category.setTitle(cursor.getString(cursor.getColumnIndex(Contracts.CategoryEntry.COLUMN_TITLE)));
                category.setShared(cursor.getInt(cursor.getColumnIndex(Contracts.CategoryEntry.COLUMN_SHARED)) != 0);
                category.setId(cursor.getInt(cursor.getColumnIndex(Contracts.CategoryEntry._ID)));
                categories.add(category);
            }

        } catch (Exception ex) {

        } finally {
            cursor.close();
        }
        return categories;
    }

    public static ContentValues[] articlesToCV(ArticlesList articlesList) {
        ArrayList<ContentValues> contentValuesList = new ArrayList<ContentValues>();
        for (FeedMeArticle feedMeArticle : articlesList.getArticles()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contracts.ArticleEntry.COLUMN_AUTHOR, feedMeArticle.getArticle().getAuthor());
            contentValues.put(Contracts.ArticleEntry.COLUMN_COMMENTS, feedMeArticle.getArticle().getComments());
            contentValues.put(Contracts.ArticleEntry.COLUMN_CONTENT, feedMeArticle.getArticle().getContent());
            contentValues.put(Contracts.ArticleEntry.COLUMN_DATE, feedMeArticle.getArticle().getDate());
            contentValues.put(Contracts.ArticleEntry.COLUMN_DESCRIPTION, feedMeArticle.getArticle().getDescription());
            contentValues.put(Contracts.ArticleEntry.COLUMN_IMAGE, feedMeArticle.getArticle().getImage().toString());
            contentValues.put(Contracts.ArticleEntry.COLUMN_SITE, feedMeArticle.getSiteID());
            contentValues.put(Contracts.ArticleEntry.COLUMN_SOURCE, feedMeArticle.getArticle().getSource().toString());
            contentValues.put(Contracts.ArticleEntry.COLUMN_TITLE, feedMeArticle.getArticle().getTitle());
            contentValues.put(Contracts.ArticleEntry.COLUMN_TAGS, tagsToString(feedMeArticle.getArticle().getTags()));
            contentValues.put(Contracts.ArticleEntry.COLUMN_SAVED, feedMeArticle.isSaved());
            contentValues.put(Contracts.ArticleEntry.COLUMN_FAVORITE, feedMeArticle.isFav());
            contentValues.put(Contracts.ArticleEntry.COLUMN_CONTENT_FETCHED, feedMeArticle.isContentFetched());
            contentValues.put(Contracts.ArticleEntry.COLUMN_WEBARCHIVE_PATH, feedMeArticle.getWebArchivePath());
            contentValues.put(Contracts.ArticleEntry.COLUMN_PUBLISHED_DATE,feedMeArticle.getPublishedDate());
            contentValues.put(Contracts.ArticleEntry._ID,feedMeArticle.getArticleID());

            contentValuesList.add(contentValues);
        }
        return contentValuesList.toArray(new ContentValues[]{});
    }

    public static  ContentValues[] categoriesToCV(List<Category> cats) {
        ArrayList<ContentValues> contentValuesList = new ArrayList<ContentValues>();
        for(Category cat:cats){
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contracts.CategoryEntry.COLUMN_TITLE,cat.getTitle());
            contentValues.put(Contracts.CategoryEntry.COLUMN_SHARED,cat.getShared());
            contentValuesList.add(contentValues);
        }
        return contentValuesList.toArray(new ContentValues[]{});
    }
    public static  ContentValues[]  categoriesToCV(Category[] cats) {
        return categoriesToCV(Arrays.asList(cats));
    }
    public static ContentValues[]  sitesToCV(Site[] sites){
        return  sitesToCV(Arrays.asList(sites));
    }

    public static ContentValues[]  sitesToCV(List<Site> sites) {
        ArrayList<ContentValues> contentValuesList = new ArrayList<ContentValues>();
        for(Site site:sites){
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contracts.SiteEntry.COLUMN_TITLE,site.getTitle());
            contentValues.put(Contracts.SiteEntry.COLUMN_URL,site.getUrl());
            contentValues.put(Contracts.SiteEntry.COLUMN_RSS_URL,site.getRssUrl());
            contentValues.put(Contracts.SiteEntry.COLUMN_CATEGORY,site.getCategoryID());
            contentValues.put(Contracts.SiteEntry.COLUMN_BLOCKED,site.isBlocked());
            contentValuesList.add(contentValues);
        }
        return contentValuesList.toArray(new ContentValues[]{});
    }

    private static String tagsToString(List<String> tags) {
        StringBuilder stringBuilder = new StringBuilder();
        int len = tags.size();
        int count = 0;
        for (String str : tags) {
            if (count++ == len - 1)
                stringBuilder.append(str);
            else
                stringBuilder.append(str).append(" , ");

        }
        return stringBuilder.toString();
    }
}

