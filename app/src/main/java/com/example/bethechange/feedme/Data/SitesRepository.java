package com.example.bethechange.feedme.Data;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Looper;
import android.support.v4.util.Pair;
import android.util.SparseArray;

import com.example.bethechange.feedme.FeedMeApp;
import com.example.bethechange.feedme.MainScreen.Models.Category;
import com.example.bethechange.feedme.MainScreen.Models.FeedMeArticle;
import com.example.bethechange.feedme.MainScreen.Models.Site;
import com.example.bethechange.feedme.Services.ArticlesDownloaderService;
import com.example.bethechange.feedme.Utils.DBUtils;
import com.example.bethechange.feedme.Utils.FirebaseUtils;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by BeTheChange on 7/20/2017.
 */

public class SitesRepository extends AsyncQueryHandler implements SitesRepositoryActions{
    private static Context mContext;
    private static SitesRepository mInstance;
    private SparseArray<Site> allSites=new SparseArray<>();
    private SparseArray<SitesObserver> listeners=new SparseArray<>();
    private SparseArray<Category> categories=new SparseArray<>();
    private int sitesToken=101;
    private int categoryToken=202;
    private int articlesToken=303;

    private SitesRepository(Context context) {
        super(context.getContentResolver());
        super.startQuery(categoryToken,null,Contracts.CategoryEntry.CONTENT_URI,null,null,null,null);
       // Cursor cursor=context.getContentResolver().query(Contracts.CategoryEntry.CONTENT_URI,null,null,null,null);
       // DBUtils.cursorToCategories(cursor);

        mContext=context;
    }

    public static SitesRepository getInstance(Context context){
        if(mInstance!=null)
            return mInstance;
        mInstance=new SitesRepository(context);
        return mInstance;
    }
    public static void destroyInstance(Context context){
        if(context==mContext){
            mContext=null;
            mInstance=null;
        }
    }
    public void setObserver(SitesObserver observer){
        listeners.put(observer.hashCode(),observer);
        if(allSites.size()!=0)
            observer.queryCompleted();
        else
            querySites();
    }

    @Override
    public void addCategory(Category cat) {
        super.startInsert(categoryToken,null, Contracts.CategoryEntry.CONTENT_URI,DBUtils.categoriesToCV(new Category[]{cat})[0]);

    }

    public void unsetObserver(SitesObserver observer){
        listeners.remove(observer.hashCode());
    }
    private void querySites() {
        super.startQuery(sitesToken,null, Contracts.SiteEntry.CONTENT_URI,null,null,null,null);
    }

    @Override
    public Site getSite(int id) {
        return allSites.get(id);
    }

    @Override
    public void removeSite(int id) {
        Site cookie=allSites.get(id);
        super.startDelete(sitesToken,cookie, ContentUris.withAppendedId(Contracts.SiteEntry.CONTENT_URI,id),null,null);
        allSites.remove(id);
    }

    @Override
    public void removeSite(Site site) {
        removeSite(site.getID());
    }

    @Override
    public void addSite(Site site) {
        allSites.put(site.getID(),site);
        super.startInsert(sitesToken,site, Contracts.SiteEntry.CONTENT_URI, DBUtils.sitesToCV(new Site[]{site})[0]);
    }

    @Override
    public void editSite(Site site) {
        allSites.put(site.getID(),site);
        super.startUpdate(sitesToken,null,ContentUris.withAppendedId(Contracts.SiteEntry.CONTENT_URI,site.getID()),
                DBUtils.sitesToCV(new Site[]{site})[0], null,null);
    }

    @Override
    public ArrayList<Site> getSites(Category category) {
        ArrayList<Site>sites=new ArrayList<>();
        if(category==null){
            for(int i=0;i<allSites.size();i++)
                sites.add(allSites.valueAt(i));
        }
        else{
            for(int i=0;i<allSites.size();i++){
                if(allSites.valueAt(i).getCategoryID()==category.getId())
                    sites.add(allSites.valueAt(i));
            }
        }

        return sites;
    }

    @Override
    public ArrayList<Category> getCategorise() {
        ArrayList<Category>cats=new ArrayList<>();
        for(int i=0;i<categories.size();i++)
            cats.add(categories.valueAt(i));
        return cats;
    }

    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        super.onQueryComplete(token, cookie, cursor);
        if(token==categoryToken){
            ArrayList<Category>cats=DBUtils.cursorToCategories(cursor);
            for(Category ct:cats)
                categories.put(ct.getId(),ct);
            querySites();
            return;
        }
        ArrayList<Site> ls = DBUtils.cursorToSites(cursor);
        for(Site st:ls){
            st.setCategory(categories.get(st.getCategoryID()));
            allSites.put(st.getID(),st);
        }
        for(int i=0;i<listeners.size();i++)
            listeners.valueAt(i).queryCompleted();
        FirebaseUtils.insertSuggestionsSites(ls);

    }
    public interface SitesObserver{
        void queryCompleted();
    }

    @Override
    protected void onInsertComplete(int token, Object cookie, Uri uri) {
        super.onInsertComplete(token, cookie, uri);
        if(token==categoryToken)
            super.startQuery(categoryToken,null,Contracts.CategoryEntry.CONTENT_URI,null,null,null,null);
        querySites();
        if(cookie!=null&&cookie instanceof Site)
            fetchNewSiteArticles((Site)cookie);
    }

    private void fetchNewSiteArticles(Site site) {
       ArticlesDownloaderService.startActionUpdateSites(FeedMeApp.getContext(),true,new Gson().toJson(site,Site.class));
    }

    @Override
    protected void onUpdateComplete(int token, Object cookie, int result) {
        super.onUpdateComplete(token, cookie, result);
        querySites();
    }

    @Override
    protected void onDeleteComplete(int token, Object cookie, int result) {
        super.onDeleteComplete(token, cookie, result);
        querySites();
        //super.startQuery(2020,null, Contracts.ArticleEntry.CONTENT_URI,null,null,null,null);
        if(cookie!=null&&cookie instanceof Site) {
            Site st = (Site) cookie;
            super.startDelete(articlesToken, null, Contracts.ArticleEntry.CONTENT_URI, Contracts.ArticleEntry.COLUMN_SITE + " =?", new String[]{st.getID() + ""});
        }
    }
}