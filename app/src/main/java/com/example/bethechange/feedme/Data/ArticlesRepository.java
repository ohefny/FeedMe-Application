package com.example.bethechange.feedme.Data;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;

import com.example.bethechange.feedme.ArticlesObserver;
import com.example.bethechange.feedme.FeedMeApp;
import com.example.bethechange.feedme.MainScreen.Models.ArticlesList;
import com.example.bethechange.feedme.MainScreen.Models.FeedMeArticle;
import com.example.bethechange.feedme.MainScreen.Models.Site;
import com.example.bethechange.feedme.MainScreen.ViewContracts.ArticleListContract;
import com.example.bethechange.feedme.Services.ArticlesDownloaderService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by BeTheChange on 7/11/2017.
 */

public class ArticlesRepository extends AsyncQueryHandler implements ArticleRepositoryActions {
    private int queryToken=1;
    private Site[]mSites=null;
    private ArticleListContract.Presenter mListener;
    private SparseArray<FeedMeArticle> articlesList;
    private String sortCriteria=Contracts.ArticleEntry.COLUMN_DATE+" DESC";
    private final int INTIALIZE_TOKEN=0;
    private static ArticlesRepository repoInstance;
    private static ArticlesObserver observer;
    private ArticlesRepository(ContentResolver cr) {
        super(cr);
        refreshData();
        queryArticles(INTIALIZE_TOKEN);
        observer=new ArticlesObserver(new android.os.Handler(Looper.getMainLooper()),this);
        cr.registerContentObserver(Contracts.ArticleEntry.CONTENT_URI,false,observer);
    }
    public static void destroyInstance(Context context) {
        context.getContentResolver()
                .unregisterContentObserver(observer);
        repoInstance = null;
    }

    public static ArticlesRepository getInstance(Context context){
        if(repoInstance==null){
            repoInstance=new ArticlesRepository(context.getContentResolver());
            context.getContentResolver()
                    .registerContentObserver(Contracts.ArticleEntry.CONTENT_URI,false,observer);
        }

        return repoInstance;
    }
    public void setListener(ArticleListContract.Presenter mListener) {
        this.mListener = mListener;
    }

    public void unSetListenr() {
        this.mListener = null;
    }

    @Override
    public void onLocalDataChanged() {
            if(mListener!=null)
                queryArticles(queryToken);
    }


    private int insertValues(Uri uri, ContentValues[]cvs){
        for (ContentValues cv:cvs) {
            super.startInsert(0,null,uri,cv);
        }

        return FeedMeApp.getContext().getContentResolver().bulkInsert(uri,cvs);
    }
    private void queryArticles(int token){

        if(mSites==null){
            super.startQuery(token,null, Contracts.ArticleEntry.CONTENT_URI,null,null,null,sortCriteria);
        }
        else {
            String []selections=new String[mSites.length];
            for(int i=0;i<selections.length;i++){
                selections[i]=mSites[i].getID()+"";
            }
            super.startQuery(token,null,Contracts.ArticleEntry.CONTENT_URI,null,
                    Contracts.ArticleEntry._ID+"=?",selections,sortCriteria);
        }
    }

    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        if(token==INTIALIZE_TOKEN){
            articlesList=DBUtils.cursorToArticles(cursor);

        }
        if(token==queryToken){
            queryToken++;
        }
        cursor.close();
        if(mListener!=null)
            mListener.newDataDelivered(new ArticlesList(DBUtils.cursorToArticles(cursor)));
        super.onQueryComplete(token, cookie, cursor);
    }

    private void refreshData() {
        Intent intent=new Intent(FeedMeApp.getContext(),ArticlesDownloaderService.class);
        intent.setAction(ArticlesDownloaderService.ACTION_FETCH_LATEST);
        FeedMeApp.getContext().startService(intent);
    }
    public void getLatestArticles() {
        Intent intent=new Intent(FeedMeApp.getContext(),ArticlesDownloaderService.class);
        intent.setAction(ArticlesDownloaderService.ACTION_FETCH_LATEST);
        intent.putExtra(ArticlesDownloaderService.REFRESH_NOW,true);
        FeedMeApp.getContext().startService(intent);
    }

    private void removeValues(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs){
         super.startDelete(0,null,uri,selection ,selectionArgs);
    }
    public  void removeArticle(@NonNull FeedMeArticle feedMeArticle){
         removeValues(Uri.withAppendedPath(Contracts.ArticleEntry.CONTENT_URI,""+feedMeArticle.getArticleID()),null,null);
    }
    public void insertArticle(final FeedMeArticle article){
        ArticlesList articlesList=new ArticlesList(Collections.singletonList(article));
        insertValues(Contracts.ArticleEntry.CONTENT_URI,DBUtils.articlesToCV(articlesList));
    }
    public void insertArticles( List<FeedMeArticle>articles){
        ArticlesList articlesList=new ArticlesList(articles);
        insertValues(Contracts.ArticleEntry.CONTENT_URI,DBUtils.articlesToCV(articlesList));
    }

    @Override
    public void editArticle(FeedMeArticle article) {
        ArticlesList articlesList=new ArticlesList(Collections.singletonList(article));
        super.startUpdate(0,null,Uri.withAppendedPath(Contracts.ArticleEntry.CONTENT_URI,""+article.getArticleID()),
                            null,null,null);
    }
    @Override
    public ArticlesList getArticles(@Nullable Site[]sites) {
        if(articlesList==null)
            queryArticles(queryToken);
        if(mSites==null)
            return new ArticlesList(articlesList);
        else{

            return new ArticlesList(articlesFromSites(sites));
        }
    }

    private ArrayList<FeedMeArticle> articlesFromSites(@Nullable Site[] sites) {
        ArrayList<FeedMeArticle> articles=new ArrayList<>();
        for(Site st:sites){
            for (int i=0;i<articlesList.size();i++){
                if(articlesList.valueAt(i).getSiteID()==st.getID()){
                    articles.add(articlesList.valueAt(i));
                }
            }
        }
        return articles;
    }

}
