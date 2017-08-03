package com.example.bethechange.feedme.Data;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.bethechange.feedme.MainScreen.Models.ArticlesList;
import com.example.bethechange.feedme.MainScreen.Models.FeedMeArticle;
import com.example.bethechange.feedme.MainScreen.Models.Site;
import com.example.bethechange.feedme.Utils.DBUtils;
import com.pkmmte.pkrss.Article;
import com.pkmmte.pkrss.Callback;
import com.pkmmte.pkrss.PkRSS;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
    private static final int RSS2JSON_SERVER=1;
    private static final int FULLRSSTEXT_SERVER=2;
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
        BlockingQueue q = new ArrayBlockingQueue(100);
        ThreadPoolExecutor ex=null;
        try {
            ex = new ThreadPoolExecutor(0, limit, 60, TimeUnit.SECONDS, q);

            articles=new ArrayList<FeedMeArticle>();
            for(int i=0;i<sites.length;i++){
               ex.execute(new SiteContentFetcher(startPage, pagesSize, sites[i]));

            }

        } catch (Exception e){
            e.printStackTrace();
        }

        try {
            if (ex != null) {
                ex.shutdown();
            }
            while (!ex.awaitTermination(20, TimeUnit.SECONDS)){
                Log.d("ArticleRemoteLoader","Awaiting completion of threads.");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ArticlesList mArticleList=new  ArticlesList();
        mArticleList.setArticles(new ArrayList<FeedMeArticle>(articles));
        ContentValues[]cv= DBUtils.articlesToCV(mArticleList);
        return cv;
    }

    private class SiteContentFetcher implements Runnable, Callback {

        private final Site mSite;
        private final int mSize;
        private final int mPage;

        SiteContentFetcher(int page, int size, Site site) {
            mPage = page;
            mSize = size;
            mSite = site;
        }

        @Override
        public void run() {
            List<FeedMeArticle>list=new ArrayList<>();
            try {
                    //multiple sources to guarantee that feeds will be fetched and minimize error
                    boolean contentFetched=true;
                    List<Article> ls=new ArrayList<>();
                    //ls=getFeeds(mSite.getRssUrl());
                    //ls= getJsonFeeds(mSite.getRssUrl(), FULLRSSTEXT_SERVER);

                    if (ls.size() == 0) {
                        contentFetched=false;
                        ls = getJsonFeeds(mSite.getRssUrl(), RSS2JSON_SERVER);
                    }
                    if(ls.size()==0)
                        ls.addAll( PkRSS.with(mContext).load(mSite.getRssUrl()).page(0).callback(this).get());
                    for (Article ar : ls) {
                        FeedMeArticle feedAr = new FeedMeArticle();
                        if(ar.getImage()==null||ar.getImage().toString().isEmpty())
                            ar.setImage(Uri.parse(mSite.getmImgSrc()));
                        feedAr.setArticle(ar);
                        feedAr.setSite(mSite);
                        feedAr.setSiteID(mSite.getID());
                        feedAr.setContentFetched(contentFetched);
                        feedAr.setFetchedDate(System.currentTimeMillis());

                        list.add(feedAr);

                    }
                //}//End of for
                //un necessary we have no mean to fetch next page at least not all sites gives us the mean
            } catch (Exception e) {
                e.printStackTrace();
            }
            synchronized (this) {
                articles.addAll(list);
            }
        }

        private List<Article> getJsonFeeds(String url, int serverNum) {
            List<Article>ls=new ArrayList<>();
            //String firstServer = "https://www.freefullrss.com/feed.php";
            String secondServer="http://10.0.2.2/full-text-rss/makefulltextfeed.php";
            String firstServer="https://api.rss2json.com/v1/api.json";
            //String secondServer = "http://ftr.fivefilters.org/makefulltextfeed.php";
            String baseUrl = "";
            String link="";
            baseUrl = (serverNum == RSS2JSON_SERVER) ? firstServer : secondServer;
            if(serverNum==FULLRSSTEXT_SERVER){
                baseUrl=secondServer;
                 link = Uri.parse(baseUrl).buildUpon()
                        .appendQueryParameter("max", "20")
                         .appendQueryParameter("format","json")
                        .appendQueryParameter("url", url)
                        .appendQueryParameter("links", "remove")
                        .appendQueryParameter("exc", "1")
                        .appendQueryParameter("submit", "Create Full Text RSS").toString();
            }
            else{
                baseUrl=firstServer;
                link = Uri.parse(baseUrl).buildUpon()
                        .appendQueryParameter("count", "20")
                        .appendQueryParameter("rss_url", url)
                        .appendQueryParameter("api_key", "f4glg6u8tcakm4yxwxinl5n9frtknhaqsi9jcayg")
                        .appendQueryParameter("order_dir", "desc")
                        .appendQueryParameter("order_by", "pubDate")
                        .toString();
            }
            Request request = new Request.Builder()
                    .url(link)
                    .build();

            OkHttpClient okHttpClient=new OkHttpClient();
          //  okHttpClient.setReadTimeout(160,TimeUnit.SECONDS);
            Response response= null;
            try {
                response = okHttpClient.newCall(request).execute();
                ls= formatJsonToArticle(response,20,serverNum);
            } catch (IOException e) {
                e.printStackTrace();

            }
            return ls;
        }

        private List<Article> formatJsonToArticle(Response response,int count,int server) {
            List<Article>list=new ArrayList<>();
            try {
                String body=response.body().string();
                Log.d("Json Body ",body);
                if(server==RSS2JSON_SERVER) {

                    JSONObject responseJson = new JSONObject(body);
                    JSONArray items = responseJson.getJSONArray("items");


                    for (int i = 0; i < count; i++) {
                        Article article = new Article();
                        try {
                            JSONObject item = items.getJSONObject(i);
                            article.setTitle(item.optString("title"));
                            article.setSource(Uri.parse(item.optString("link")));
                            article.setImage(Uri.parse(item.optString("thumbnail")));
                            article.setDescription(android.text.Html.fromHtml(item.optString("description")).toString());
                            article.setAuthor(item.optString("author"));
                            list.add(article);
                            if(item.optString("pubDate").isEmpty())
                                article.setDate(System.currentTimeMillis());
                            else
                                article.setDate(Date.parse(item.optString("pubDate")));
                            //article.setDate();
                        } catch (IllegalArgumentException ex) {
                            article.setDate(System.currentTimeMillis());
                        }
                    }
                }
                //FULL-TEXT-RSS JSON
                else{
                    JSONObject responseJson = new JSONObject(body).getJSONObject("rss");
                    Log.d("Json rss",responseJson.toString());
                    JSONArray items = responseJson.getJSONObject("channel").getJSONArray("item");


                    for (int i = 0; i < count; i++) {
                        try {
                            JSONObject item = items.getJSONObject(i);
                            Article article = new Article();
                            article.setTitle(item.optString("title"));
                            article.setSource(Uri.parse(item.optString("link")));

                            article.setDescription(android.text.Html.fromHtml(item.optString("description")).toString());
                            if(item.has("content_encoded"))
                                article.setContent(android.text.Html.fromHtml(item.optString("content_encoded")).toString());
                            else
                                article.setContent(item.optString("description"));
                            //article.setAuthor(item.getString("author"));

                            if(item.has("media_thumbnail")&&item.getJSONObject("media_thumbnail").has("@attributes")
                                    &&item.getJSONObject("media_thumbnail").getJSONObject("@attributes").has("url")){
                                JSONObject temp = item.getJSONObject("media_thumbnail");
                                JSONObject tempAttr=temp.getJSONObject("@@attributes");
                                article.setImage(Uri.parse(tempAttr.optString("url")));
                            }
                            article.setAuthor(item.optString("dc_creator"));

                            list.add(article);
                            article.setDate(Date.parse(item.optString("pubDate")));
                        } catch (IllegalArgumentException ignored) {
                        }
                    }

                }
                response.body().close();
                }catch (Exception ex){
                    ex.printStackTrace();
                }

            return list;
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

        List<Article> getFeeds(String url) {
            ArrayList<Article>ls =new ArrayList<>();
            String firstServer = "http://10.0.2.2/full-text-rss/makefulltextfeed.php";
            String secondServer = "http://ftr.fivefilters.org/makefulltextfeed.php";
            String baseUrl = "";
            //baseUrl = (serverNum == 0) ? firstServer : secondServer;
            baseUrl=firstServer;
            String link = Uri.parse(baseUrl).buildUpon()
                    .appendQueryParameter("max", "20")
                    .appendQueryParameter("url", url)
                    .appendQueryParameter("links", "remove")
                    .appendQueryParameter("exc", "1")
                    .appendQueryParameter("submit", "Create Full Text RSS").toString();
            try {
                 ls.addAll(PkRSS.with(mContext).load(link).page(0).callback(this).get());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return ls;
        }
    }


}
