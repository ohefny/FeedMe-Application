package com.example.bethechange.feedme.Data;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.example.bethechange.feedme.MainScreen.Models.ArticlesList;
import com.example.bethechange.feedme.MainScreen.Models.FeedMeArticle;
import com.example.bethechange.feedme.MainScreen.Models.Site;
import com.pkmmte.pkrss.Article;
import com.pkmmte.pkrss.Callback;
import com.pkmmte.pkrss.PkRSS;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * Created by BeTheChange on 7/11/2017.
 */

public class ArticleFetcher {

    private  int startPage=0;
    private  int pagesSize=1;
    private Site[]sites;
    private Context mContext;
    private List<FeedMeArticle> articles= Collections.synchronizedList(new ArrayList<FeedMeArticle>());


    public ArticleFetcher(Context context, Site[]sites, int startPage, int pagesSize) {
        this(context,sites);
        this.startPage=startPage;
        this.pagesSize=pagesSize;
    }

    public ArticleFetcher(Context context, Site[]sites) {
        mContext=context;
        this.sites=sites;
    }


    public ContentValues[] getContentValues() {
        if(sites==null)
            return null;
        int limit = 3;
        BlockingQueue q = new ArrayBlockingQueue(limit);
        ThreadPoolExecutor ex=null;
        try {
            ex = new ThreadPoolExecutor(0, limit, 20, TimeUnit.SECONDS, q);

            articles=new ArrayList<FeedMeArticle>();
            for(Site site:sites){
                Future<List<FeedMeArticle>> ft = ex.submit(new SiteContentFetcher(startPage, pagesSize, site));
                List<FeedMeArticle>ret=ft.get();
                articles.addAll(ret);
            }

        } catch (Exception e){
            e.printStackTrace();
        }

        try {
            if (ex != null) {
                ex.shutdown();
            }
            while (!ex.awaitTermination(10, TimeUnit.SECONDS)){
                Log.d("ArticleRemoteLoader","Awaiting completion of threads.");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ArticlesList mArticleList=new  ArticlesList();
        mArticleList.setArticles(new ArrayList<FeedMeArticle>(articles));
        ContentValues[]cv=DBUtils.articlesToCV(mArticleList);
        return cv;
    }

    private class SiteContentFetcher implements Callable<List<FeedMeArticle>>,Callback {

        private final Site mSite;
        private final int mSize;
        private final int mPage;

        SiteContentFetcher(int page, int size, Site site) {
            mPage = page;
            mSize = size;
            mSite = site;
        }

        @Override
        public List<FeedMeArticle> call() {
            List<FeedMeArticle>list=new ArrayList<>();
            try {
                ContentFetcher fetcher = new ContentFetcher(mContext);

               // for (int i = startPage; i < pagesSize ; i++) {
                    //PkRSS.Builder
                    List<Article> ls = getFeeds(mSite.getRssUrl(), 0);
                    //if (ls.size() == 0)
                    ls.addAll( PkRSS.with(mContext).load(mSite.getRssUrl()).page(0).callback(this).get());
                    for (Article ar : ls) {
                        FeedMeArticle feedAr = new FeedMeArticle();
                        feedAr.setArticle(ar);
                        feedAr.setSite(mSite);
                        feedAr.setSiteID(mSite.getID());
                        list.add(feedAr);
                        synchronized (this) {
                            articles.add(feedAr);
                        }
                    }
                //}//End of for
                //un necessary we have no mean to fetch next page at least not all sites gives us the mean
            } catch (Exception e) {
                e.printStackTrace();
            }
            return list;
        }

        List<Article> getFeeds(String url, int serverNum) throws IOException {
            String firstServer = "https://www.freefullrss.com/feed.php";
            String secondServer = "http://ftr.fivefilters.org/makefulltextfeed.php";
            String baseUrl = "";
            baseUrl = (serverNum == 0) ? firstServer : secondServer;

            String link = Uri.parse(baseUrl).buildUpon()
                    .appendQueryParameter("max", "10")
                    .appendQueryParameter("url", url)
                    .appendQueryParameter("links", "remove")
                    .appendQueryParameter("exc", "1")
                    .appendQueryParameter("submit", "Create Full Text RSS").toString();
            return PkRSS.with(mContext).load(link).page(0).callback(this).get();
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

        private class SiteContentFetcher2 implements Runnable,Callback{

        private final Site mSite;
        private final int mSize;
        private final int mPage;

        SiteContentFetcher2(int page, int size, Site site){
            mPage=page;
            mSize=size;
            mSite=site;
        }
        @Override
        public void run() {
            try {
                ContentFetcher fetcher=new ContentFetcher(mContext);
                for(int i=startPage;i<pagesSize+1;i++){
                        //PkRSS.Builder
                    List<Article> ls=getFeeds(mSite.getRssUrl(),0);
                    if(ls.size()==0)
                        ls = PkRSS.with(mContext).load(mSite.getRssUrl()).page(i+2).callback(this).get();
                    for(Article ar:ls) {
                            FeedMeArticle feedAr=new FeedMeArticle();
                            feedAr.setArticle(ar);
                            feedAr.setSite(mSite);
                            feedAr.setSiteID(mSite.getID());

                            synchronized (this){
                                articles.add(feedAr);
                            }
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        public List<Article> getFeeds(String url,int serverNum) throws IOException {
            String firstServer="https://www.freefullrss.com/feed.php";
            String secondServer="http://ftr.fivefilters.org/makefulltextfeed.php";
            String baseUrl="";
            baseUrl=(serverNum==0 )?firstServer:secondServer;

            String link=Uri.parse(baseUrl).buildUpon()
                    .appendQueryParameter("max","10")
                    .appendQueryParameter("url", url)
                    .appendQueryParameter("links","remove")
                    .appendQueryParameter("exc","1")
                    .appendQueryParameter("submit","Create Full Text RSS").toString();
            return PkRSS.with(mContext).load(link).page(0).callback(this).get();
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
