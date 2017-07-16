package com.example.bethechange.feedme.Data;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.bethechange.feedme.MainScreen.Models.FeedMeArticle;
import com.example.bethechange.feedme.R;
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
import java.util.List;
import java.util.Random;

/**
 * Created by BeTheChange on 7/16/2017.
 */

public class ContentFetcher implements Callback {
    Context mContext;
    public ContentFetcher(Context context){
        mContext=context;

    }
    public String getImage(String query){
        try {
            return getGoogleImage(mContext.getResources().getString(R.string.googl_api_key),
            mContext.getResources().getString(R.string.googl_api_id),query);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return "";
    }


    private String getGoogleImage(String key,String id,String query) throws IOException, JSONException {
        String link=Uri.parse("https://www.googleapis.com/customsearch/v1").buildUpon()
                .appendQueryParameter("key",key)
                .appendQueryParameter("cx", id)
                .appendQueryParameter("q",query)
                .appendQueryParameter("alt","json")
                .appendQueryParameter("start","1")
                .appendQueryParameter("searchType","image").toString();

        Request request = new Request.Builder()
                .url(link)
                .build();
        OkHttpClient okHttpClient=new OkHttpClient();
        Response response = okHttpClient.newCall(request).execute();
        String str=response.body().string();
        JSONObject jsonObject=new JSONObject(str);
        JSONArray arr=jsonObject.getJSONArray("items");
        String img=arr.getJSONObject(0).getString("link");
        return img;
    }
    private Article fetchArticle(Article article) {
        Random random=new Random();
        int choice=random.nextInt(2);
        int serverNum=random.nextInt(2);
        boolean getImage=false;

        String key="AIzaSyA5nKr9yDKhGiWvOPP55PbOapgdZDsXiPE";
        if (article.getImage() == null||article.getImage().toString().length()==0) {
            getImage=true;
        }
        try {
            switch (choice){
                case 0:
                    article=getExtractedArticle(getImage,
                                mContext.getResources().getString(R.string.extract_api_id),
                                mContext.getResources().getString(R.string.extract_api_key),article);
                    break;
                case 1:
                    article=fullTextRssExtractor(serverNum,article,getImage);
                    break;
            }
            //if one extractor fails try the another
            //TODO::CAN BE DONE BETTER FOR FIVEFILTER MAYBE CLOSE US
            if(article.getContent().isEmpty()||article.getContent().length()<200){
                if(choice==0)
                    article=fullTextRssExtractor(serverNum,article,getImage);
                else
                    article=getExtractedArticle(getImage,
                            mContext.getResources().getString(R.string.extract_api_id),
                            mContext.getResources().getString(R.string.extract_api_key),article);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        if(article.getImage() == null||article.getImage().toString().length()==0){
            article.setImage(Uri.parse(getImage(article.getSource().toString())));
        }

        return article;
    }

    private Article getExtractedArticle(boolean getImage, String extractKey
            , String extractID,Article ar) throws IOException, JSONException {
        String link= Uri.parse("https://api.aylien.com/api/v1/extract").buildUpon().
                appendQueryParameter("best_image",getImage ?"true":"false")
                .appendQueryParameter("url", ar.getSource().toString()).toString();
        Request request = new Request.Builder()
                .url(link)
                .addHeader("X-AYLIEN-TextAPI-Application-Key",extractKey)
                .addHeader("X-AYLIEN-TextAPI-Application-ID", extractID)
                .build();
        OkHttpClient okHttpClient=new OkHttpClient();
        Response response = okHttpClient.newCall(request).execute();
        String str=response.body().string();
        System.out.println("Fuck try "+str);
        JSONObject jsonObject=new JSONObject(str);
        String article=jsonObject.get("article").toString();
        String img="";
        if(getImage){
            img=jsonObject.get("image").toString();
            ar.setImage(Uri.parse(img));
        }
        ar.setContent(article);
        return ar;
    }
    private Article fullTextRssExtractor(int serverNum,Article article,boolean setimg) throws IOException {
        String firstServer="https://www.freefullrss.com/feed.php";
        String secondServer="http://ftr.fivefilters.org/makefulltextfeed.php";
        String baseUrl="";
        baseUrl=(serverNum==0 )?firstServer:secondServer;

        String link=Uri.parse(baseUrl).buildUpon()
                .appendQueryParameter("max","5")
                .appendQueryParameter("url", article.getSource().toString())
                .appendQueryParameter("links","remove")
                .appendQueryParameter("exc","1")
                .appendQueryParameter("submit","Create Full Text RSS").toString();
        List<Article> articles= PkRSS.with(mContext).load(link).page(0).callback(this).get();
        Article newAricle=articles.get(0);
        if(setimg){
            article.setImage(newAricle.getImage());
        }
        article.setContent(newAricle.getContent());
        return article;


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
