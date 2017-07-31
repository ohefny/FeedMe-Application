package com.example.bethechange.feedme.Data;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.util.Log;
import android.util.SparseArray;

import com.example.bethechange.feedme.ArticleType;
import com.example.bethechange.feedme.ArticlesObserver;
import com.example.bethechange.feedme.CustomScreen.SearchModel;
import com.example.bethechange.feedme.FeedMeApp;
import com.example.bethechange.feedme.MainScreen.Models.ArticlesList;
import com.example.bethechange.feedme.MainScreen.Models.FeedMeArticle;
import com.example.bethechange.feedme.MainScreen.Models.Site;
import com.example.bethechange.feedme.Services.ArticlesDownloaderService;
import com.example.bethechange.feedme.Utils.CollectionUtils;
import com.example.bethechange.feedme.Utils.DBUtils;
import com.example.bethechange.feedme.Utils.PrefUtils;
import com.pkmmte.pkrss.Article;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by BeTheChange on 7/11/2017.
 */

public class ArticlesRepository extends AsyncQueryHandler implements ArticleRepositoryActions {
    private static Context mContext;
    private int queryToken=1;
    private final int SEARCH_TOKEN=101;
    private Site[]mSites=null;
    private SparseArray<Pair<ArticlesRepositoryObserver,Site[]>> mListeners=new SparseArray<>();
    private SparseArray<ArticlesRepositoryObserver> queryListeners=new SparseArray<>();
    private SparseArray<FeedMeArticle> allArticles;
    private final String SORT_CRITERIA=Contracts.ArticleEntry.COLUMN_DATE+" DESC";
    private final int INITIALIZE_TOKEN =0;
    private static ArticlesRepository repoInstance;
    private static ArticlesObserver observer;
    private boolean freshData;

    private ArticlesRepository(ContentResolver cr) {
        super(cr);
        if(PrefUtils.isInitialized(mContext))
            queryArticles(INITIALIZE_TOKEN);

        if(PrefUtils.updateNow(FeedMeApp.getContext()))
            refreshData();

        observer=new ArticlesObserver(new android.os.Handler(Looper.getMainLooper()),this);

    }
    public static void destroyInstance(Context context) {
        if(context.equals(mContext)){
            mContext=null;
            context.getContentResolver()
                .unregisterContentObserver(observer);
        }
       // repoInstance = null;

    }

    public static ArticlesRepository getInstance(Context context){
        mContext=context;
        if(repoInstance==null)
            repoInstance=new ArticlesRepository(context.getContentResolver());

        context.getContentResolver()
                .registerContentObserver(Contracts.ArticleEntry.CONTENT_URI,false,observer);
        return repoInstance;
    }
    public void setListener(ArticlesRepositoryObserver mListener,Site[]sites) {
       mListeners.put(mListener.hashCode(), new Pair<>(mListener, sites));
    }

    public void unsetListener(ArticlesRepositoryObserver mListener) {
       mListeners.remove(mListener.hashCode());
    }

    @Override
    public void onLocalDataChanged() {
        if(freshData){
            queryArticles(INITIALIZE_TOKEN);
            freshData=false;
        }
    }


    private int insertValues(Uri uri, ContentValues[]cvs){
        for (ContentValues cv:cvs) {
            super.startInsert(0,null,uri,cv);
        }

        return FeedMeApp.getContext().getContentResolver().bulkInsert(uri,cvs);
    }
    private void queryArticles(int token){

        if(mSites==null){
            super.startQuery(token,null, Contracts.ArticleEntry.CONTENT_URI,null,null,null,SORT_CRITERIA);
        }
        else {
            String []selections=new String[mSites.length];
            for(int i=0;i<selections.length;i++){
                selections[i]=mSites[i].getID()+"";
            }
            super.startQuery(token,null,Contracts.ArticleEntry.CONTENT_URI,null,
                    Contracts.ArticleEntry._ID+"=?",selections,SORT_CRITERIA);
        }
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        Log.d(ArticlesRepository.class.getSimpleName(),msg.toString());
    }

    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        if(token==SEARCH_TOKEN){

            ArticlesList ls=new ArticlesList();
            ls.setArticles(CollectionUtils.sparseToArray(DBUtils.cursorToArticles(cursor)));
            Log.d(ArticlesRepository.class.getSimpleName(),"Search Done and Results "+ls.getArticles().size());
            queryListeners.get(cookie.hashCode()).onDataChanged(ls);

        }
        else if(token== INITIALIZE_TOKEN ||mSites==null||mSites.length==0){
            allArticles= DBUtils.cursorToArticles(cursor);

        }
        if(token==queryToken){
            queryToken++;
        }
        //Query Listener with it's registered sites
        deliverToListeners(false);
        if(cursor!=null&&!cursor.isClosed())
            cursor.close();
        super.onQueryComplete(token, cookie, cursor);
    }

    private void deliverToListeners(boolean fresh) {
        if(mListeners.size()!=0){
            for(int i=0;i<mListeners.size();i++){
                Pair<ArticlesRepositoryObserver, Site[]> listener=
                        mListeners.get(mListeners.keyAt(i));
                listener.first.onDataChanged(articlesFromSites(listener.second));
            }
        }
    }

    private void refreshData() {
        ArticlesDownloaderService.startActionUpdateAll(FeedMeApp.getContext(),false);
        freshData=true;
    }
    public void getLatestArticles() {
        ArticlesDownloaderService.startActionUpdateAll(FeedMeApp.getContext(),true);
        freshData=true;
    }

    private void removeValues(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs){
         super.startDelete(0,null,uri,selection ,selectionArgs);
    }
    public  void removeArticle(@NonNull FeedMeArticle feedMeArticle){
         allArticles.remove(feedMeArticle.getArticleID());
        // removeValues(ContentUris.withAppendedId(Contracts.ArticleEntry.CONTENT_URI,feedMeArticle.getArticleID()),null,null);
        removeValues(Contracts.ArticleEntry.CONTENT_URI,Contracts.ArticleEntry._ID+" = ? ",new String[]{feedMeArticle.getArticleID()+""});
        deliverToListeners(false);
    }
    public void insertArticle(final FeedMeArticle article){
        allArticles.put(article.getArticleID(),article);
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
        super.startUpdate(0,null, ContentUris.withAppendedId(Contracts.ArticleEntry.CONTENT_URI,
                article.getArticleID()),
                            DBUtils.articlesToCV(articlesList)[0],null,null);
    }
    @Override
    public ArticlesList getArticles(@Nullable Site[]sites) {
        if(allArticles==null||allArticles.size()==0){
            queryArticles(queryToken);
            return new ArticlesList();
        }
        else if(sites==null)
            return new ArticlesList(allArticles);
        else{
            return articlesFromSites(sites);
        }
    }


    @Override
    public FeedMeArticle getArticle(int id) {
       return allArticles.get(id);
    }

    @Override
    public FeedMeArticle getNextArticle(int currentArticle) {
        int idx= allArticles.indexOfKey(currentArticle);
        return allArticles.valueAt((idx+1)%allArticles.size());
    }

    @Override
    public FeedMeArticle getPreviousArticle(int currentArticle) {
        int idx= allArticles.indexOfKey(currentArticle);
        if(idx==0){
            return allArticles.valueAt(allArticles.size()-1);
        }
        return allArticles.valueAt(idx-1);
    }


    @Override
    public FeedMeArticle getArticleAt(int idx) {
        return allArticles.valueAt(idx);
    }

    @Override
    public int getArticleIndex(int id) {
        return allArticles.indexOfKey(id);
    }

    @Override
    public void getFullArticle(final FeedMeArticle feedMeArticle, final ArticlesRepositoryObserver observer) {
                ContentFetcher fetcher=new ContentFetcher(
                        mContext==null?FeedMeApp.getContext():mContext);

            fetcher.fetchArticle(feedMeArticle.getArticle(), new ContentFetcher.ArticleFetcherCallback() {
                @Override
                public void articleFetched(Article article,boolean successful) {
                   if(successful)
                        feedMeArticle.setContentFetched(true);
                    feedMeArticle.setArticle(article);
                    editArticle(feedMeArticle);
                    observer.onFullArticleFetched(feedMeArticle);

                }
            });

    }

    @Override
    public ArticlesList getSavedArticles() {
        ArrayList<FeedMeArticle>sav=new ArrayList<>();
        if(allArticles.size()==0)
            queryArticles(queryToken);
        else{
            for(int i=0;i<allArticles.size();i++){
                if(allArticles.valueAt(i).isSaved())
                    sav.add(allArticles.valueAt(i));
            }
        }
        ArticlesList ar=new ArticlesList();
        ar.setArticles(sav);
        return ar;
    }

    @Override
    public ArticlesList getBookmarkedArticles() {
        ArrayList<FeedMeArticle>fav=new ArrayList<>();
        if(allArticles.size()==0)
            queryArticles(queryToken);
        else{
            for(int i=0;i<allArticles.size();i++){
                if(allArticles.valueAt(i).isFav())
                    fav.add(allArticles.valueAt(i));
            }
        }
        ArticlesList ar=new ArticlesList();
        ar.setArticles(fav);
        return ar;
    }

    @Override
    public ArticlesList getArticlesWithIds(ArrayList<Integer> ids,ArticlesRepositoryObserver mListener) {
       ArticlesList ls=new ArticlesList();
       ArrayList<FeedMeArticle>articles=new ArrayList<>();
       if(allArticles.size()>0)
           for(int i=0;i<ids.size();i++)
              articles.add(allArticles.get(ids.get(0)));

       else {

           String inClause = ids.toArray().toString();
           inClause = inClause.replace("[","(");
           inClause = inClause.replace("]",")");
           System.out.println("in clause= "+inClause);
           queryListeners.put(mListener.hashCode(),mListener);
           startQuery(SEARCH_TOKEN,mListener.hashCode(), Contracts.ArticleEntry.CONTENT_URI,null,
                   Contracts.ArticleEntry._ID+" in "+inClause,null,SORT_CRITERIA);
       }
       ls.setArticles(articles);
       return ls;
    }

    @Override
    public void getArticlesFromSearchQuery(SearchModel model, ArticlesRepositoryObserver mListener) {
        String query="%"+model.getQuery()+"%";
        queryListeners.put(query.hashCode(),mListener);
        Log.d(ArticlesRepository.class.getSimpleName(),"Search Start "+model.getSearchIn());
        switch (model.getSearchIn()){
            case ArticleType.CATEGORY:
                if(model.getSearchInId()==-1)
                    super.startQuery(SEARCH_TOKEN,query, Contracts.ArticleEntry.CONTENT_URI,null,Contracts.ArticleEntry.COLUMN_DESCRIPTION+" Like ? or "+
                        Contracts.ArticleEntry.COLUMN_TITLE+" Like ?",new String[]{query,query},null);
                else
                    super.startQuery(SEARCH_TOKEN,query, Contracts.ArticleEntry.CONTENT_URI,null,
                            " ( "+Contracts.ArticleEntry.COLUMN_DESCRIPTION+" Like ? or "+ Contracts.ArticleEntry.COLUMN_TITLE+" Like ? ) and "+
                            Contracts.ArticleEntry.COLUMN_SITE+" in ( select "+ Contracts.SiteEntry._ID +" from "+ Contracts.SiteEntry.TABLE_NAME+" where "+ Contracts.SiteEntry.COLUMN_CATEGORY + "= "+model.getSearchInId()+")"
                           ,new String[]{query,query},null);
                break;
            case ArticleType.BOOKMARKED:
                super.startQuery(SEARCH_TOKEN,query, Contracts.ArticleEntry.CONTENT_URI,null,
                        " ( "+Contracts.ArticleEntry.COLUMN_DESCRIPTION+" Like ? or "+
                        Contracts.ArticleEntry.COLUMN_TITLE+" Like ? ) and "+Contracts.ArticleEntry.COLUMN_FAVORITE+" = 1",new String[]{query,query},null);
                break;
            case ArticleType.SAVED:
                super.startQuery(SEARCH_TOKEN,query, Contracts.ArticleEntry.CONTENT_URI,null,
                        " ( "+Contracts.ArticleEntry.COLUMN_DESCRIPTION+" Like ? or "+
                        Contracts.ArticleEntry.COLUMN_TITLE+" Like ? ) and "+Contracts.ArticleEntry.COLUMN_SAVED+" = 1",new String[]{query,query},null);
                break;
            case ArticleType.SITE:
                super.startQuery(SEARCH_TOKEN,query, Contracts.ArticleEntry.CONTENT_URI,null,
                        " ( "+Contracts.ArticleEntry.COLUMN_DESCRIPTION+" Like ? or "+
                        Contracts.ArticleEntry.COLUMN_TITLE+" Like ? ) and "+Contracts.ArticleEntry.COLUMN_SITE+" = "+model.getSearchInId(),new String[]{query,query},null);
                break;
        }
    }


    private ArticlesList articlesFromSites(@Nullable Site[] sites) {
        ArrayList<FeedMeArticle> filteredArticles=new ArrayList<>();
        if(sites==null)
            return new ArticlesList(allArticles);
        for(Site st:sites){
            for (int i=0;i<allArticles.size();i++){
                if(allArticles.valueAt(i).getSiteID()==st.getID()){
                    filteredArticles.add(allArticles.valueAt(i));
                }
            }
        }
        return new ArticlesList(filteredArticles);
    }

    public interface ArticlesRepositoryObserver {
        void onDataChanged(ArticlesList data);
        void onFullArticleFetched(FeedMeArticle article);
    }
}
