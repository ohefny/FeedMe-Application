package com.example.bethechange.feedme.Data;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.content.AsyncTaskLoader;

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

public class ContentFetcher extends AsyncTaskLoader implements Callback {
    Context mContext;
    public ContentFetcher(Context context){
        super(context);
        mContext=context;

    }

    @Override
    public Object loadInBackground() {
        return null;
    }
    public String getImage(String query,ImageFetcherCallback listener){
        try {
            return getGoogleImage(mContext.getResources().getString(R.string.googl_api_key),
                    mContext.getResources().getString(R.string.googl_api_id),query,listener);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return "";
    }





    private String getGoogleImage(String key, String id, String query, final ImageFetcherCallback listener) throws IOException, JSONException {
        final Request request=buildImgRequest(key,id,query);
        OkHttpClient okHttpClient=new OkHttpClient();
        if(listener!=null){
            okHttpClient.newCall(request).enqueue(new com.squareup.okhttp.Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    listener.imageFetched("");
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    try {
                        listener.imageFetched(parseImgStr(response.body().string()));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        listener.imageFetched("");
                    }
                }
            });

        }
        else{
            Response response = okHttpClient.newCall(request).execute();
            String str=response.body().string();
            return parseImgStr(str);
        }
        return "";
    }

    private String parseImgStr(String str) throws JSONException {
        JSONObject jsonObject=new JSONObject(str);
        JSONArray arr=jsonObject.getJSONArray("items");
        return arr.getJSONObject(0).getString("link");
    }
    private Request buildImgRequest(String key,String id,String query) {
        String link = Uri.parse("https://www.googleapis.com/customsearch/v1").buildUpon()
                .appendQueryParameter("key", key)
                .appendQueryParameter("cx", id)
                .appendQueryParameter("q", query)
                .appendQueryParameter("alt", "json")
                .appendQueryParameter("start", "1")
                .appendQueryParameter("searchType", "image").toString();

        return (new Request.Builder()).url(link).build();
    }
    Article fetchArticle(Article article,ArticleFetcherCallback listener) {
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
                                mContext.getResources().getString(R.string.extract_api_key),
                                mContext.getResources().getString(R.string.extract_api_id),
                                article,listener);
                    break;
                case 1:
                    article=fullTextRssExtractor(serverNum,article,getImage,listener);
                    break;
            }
            //if one extractor fails try the another
            //TODO::CAN BE DONE BETTER FOR FIVEFILTER MAYBE CLOSE US
         /*   if(article.getContent().isEmpty()||article.getContent().length()<200){
                if(choice==0)
                    article=fullTextRssExtractor(serverNum,article,getImage, listener);
                else
                    article=getExtractedArticle(getImage,
                            mContext.getResources().getString(R.string.extract_api_id),
                            mContext.getResources().getString(R.string.extract_api_key),article, listener);
            }*/
        } catch (IOException | JSONException e ) {
            e.printStackTrace();
        }
        catch (Exception e){
            System.out.println("Fuck Used to be uncaught");
            e.printStackTrace();
        }


        return article;
    }

    private Article getExtractedArticle(final boolean getImage, String extractKey
            , String extractID, final Article ar, final ArticleFetcherCallback listener) throws IOException, JSONException {
        String link= Uri.parse("https://api.aylien.com/api/v1/extract").buildUpon().
                appendQueryParameter("best_image",getImage ?"true":"false")
                .appendQueryParameter("url", ar.getSource().toString()).toString();
        Request request = new Request.Builder()
                .url(link)
                .addHeader("X-AYLIEN-TextAPI-Application-Key",extractKey)
                .addHeader("X-AYLIEN-TextAPI-Application-ID", extractID)
                .build();
        OkHttpClient okHttpClient=new OkHttpClient();
        if(listener!=null){
            okHttpClient.newCall(request).enqueue(new com.squareup.okhttp.Callback() {
                 Article tempAr = ar;
                //to make callback run on ui
                 Handler mainHandler= new Handler(mContext.getMainLooper());

                @Override
                public void onFailure(Request request, IOException e) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.articleFetched(ar,false);
                        }
                    });
                }

                @Override
                public void onResponse(final Response response) throws IOException {
                    boolean successful=false;
                    try {
                        tempAr = formatArticle(getImage, ar, response);
                        successful=true;
                    } catch (JSONException e) {

                        e.printStackTrace();
                    }

                    final boolean finalSuccessful = successful;
                    mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.articleFetched(tempAr, finalSuccessful);
                            }
                        });

                }
            });
        }
        else{
            Response response=okHttpClient.newCall(request).execute();
            return formatArticle(getImage, ar, response);
        }
        return ar;
    }

    private Article formatArticle(boolean getImage, Article ar, Response response) throws IOException, JSONException {
        String str=response.body().string();
        JSONObject jsonObject=new JSONObject(str);
        String articleStr=jsonObject.get("article").toString();
        articleStr=android.text.Html.fromHtml(articleStr).toString();
        String img="";
        if(getImage){
            img=jsonObject.get("image").toString();
            ar.setImage(Uri.parse(img));
        }
        ar.setContent(articleStr);
        //get Image from google search api if not fetched by others
        if(ar.getImage() == null||ar.getImage().toString().length()==0){
            ar.setImage(Uri.parse(getImage(ar.getSource().toString(),null)));
        }
        if(ar.getAuthor()==null||ar.getAuthor().isEmpty())
            ar.setAuthor(jsonObject.get("author").toString());
        return ar;
    }

    private Article fullTextRssExtractor(int serverNum, final Article article, final boolean setimg, final ArticleFetcherCallback listener) throws IOException {
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
        if(listener!=null){
            PkRSS.with(mContext).load(link).page(0).callback(new Callback() {
                @Override
                public void onPreload() {

                }

                @Override
                public void onLoaded(List<Article> ls) {
                    if(ls.size()>0){
                        if(setimg){
                            article.setImage(ls.get(0).getImage());
                        }
                        article.setContent(android.text.Html.fromHtml(ls.get(0).getDescription()).toString());

                    }
                    listener.articleFetched(article,true);

                }

                @Override
                public void onLoadFailed() {
                    listener.articleFetched(article,false);
                }
            }).async();
        }
        else{
            List<Article>ls=PkRSS.with(mContext).load(link).page(0).callback(this).get();
            if(ls.size()>0){
                if(setimg){
                    article.setImage(ls.get(0).getImage());
                }
                article.setContent(ls.get(0).getContent());
            }
        }
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
    interface ArticleFetcherCallback{
        void articleFetched(Article article,boolean successful);
    }
    interface ImageFetcherCallback{
        void imageFetched(String str);
    }
}
