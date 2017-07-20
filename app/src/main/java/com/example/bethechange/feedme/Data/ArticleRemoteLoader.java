package com.example.bethechange.feedme.Data;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.example.bethechange.feedme.MainScreen.Models.ArticlesList;
import com.example.bethechange.feedme.MainScreen.Models.FeedMeArticle;
import com.example.bethechange.feedme.MainScreen.Models.Site;
import com.example.bethechange.feedme.Utils.DBUtils;
import com.pkmmte.pkrss.Article;
import com.pkmmte.pkrss.Callback;
import com.pkmmte.pkrss.PkRSS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * Created by BeTheChange on 7/11/2017.
 */

public class ArticleRemoteLoader extends AsyncTaskLoader<ArticlesList> implements Callback {

    private  int startPage=0;
    private  int pagesSize=1;
    private Site[]sites;

     List<FeedMeArticle> articles= Collections.synchronizedList(new ArrayList<FeedMeArticle>());


    public ArticleRemoteLoader(Context context, Site[]sites, int startPage, int pagesSize) {
        super(context);
        this.sites=sites;
        this.startPage=startPage;
        this.pagesSize=pagesSize;
    }
    public ArticleRemoteLoader(Context context){
        super(context);
    }

    @Override
    public ArticlesList loadInBackground() {
        if(sites==null)
            return null;
        int limit = 3;
        BlockingQueue q = new ArrayBlockingQueue(limit);
        ThreadPoolExecutor ex = new ThreadPoolExecutor(limit, limit, 20, TimeUnit.SECONDS, q);
        articles=new ArrayList<FeedMeArticle>();
        for(Site site:sites){
            ex.execute(new SiteContentFetcher(startPage,pagesSize,site));

        }
        try {
            ex.shutdown();
            while (!ex.awaitTermination(10, TimeUnit.SECONDS)){
                Log.d("ArticleRemoteLoader","Awaiting completion of threads.");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ArticlesList mArticleList=new  ArticlesList();
        mArticleList.setArticles(new ArrayList<FeedMeArticle>(articles));
        DBUtils.articlesToCV(mArticleList);
        return mArticleList;
    }



    @Override
    public void deliverResult(ArticlesList data) {
        if (isReset()) {
            return;
        }

        if (isStarted()) {
            super.deliverResult(data);
        }
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }
    public void setStartPage(int startPage) {
        this.startPage = startPage;
    }

    public void setPagesSize(int pagesSize) {
        this.pagesSize = pagesSize;
    }

    public void setSites(Site[] sites) {
        this.sites = sites;
    }

    @Override
    public void onPreload() {

    }

    @Override
    public void onLoaded(List<Article> newArticles) {
        /*for(Article ar:newArticles) {
        FeedMeArticle feedAr=new FeedMeArticle();
        feedAr.setArticle(ar);
        feedAr.setSite(new Site());
        feedAr.setSiteID(2);
        articles.add(feedAr);}*/

    }

    @Override
    public void onLoadFailed() {
        System.out.println("faild");
    }
    class SiteContentFetcher implements Runnable,Callback{

        private final Site mSite;
        private final int mSize;
        private final int mPage;

        public SiteContentFetcher(int page , int size, Site site){
            mPage=page;
            mSize=size;
            mSite=site;
        }
        @Override
        public void run() {
            for(int i=startPage;i<pagesSize;i++){

                try {
                    List<Article> ls = PkRSS.with(getContext()).load(mSite.getRssUrl()).page(i).callback(this).get();
                    for(Article ar:ls) {
                        FeedMeArticle feedAr=new FeedMeArticle();
                        feedAr.setArticle(ar);
                        feedAr.setSite(mSite);
                        feedAr.setSiteID(mSite.getID());
                        synchronized (this){
                            articles.add(feedAr);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

        @Override
        public void onPreload() {

        }

        @Override
        public void onLoaded(List<Article> newArticles) {

        }

        @Override
        public void onLoadFailed() {

        }
    }
}
