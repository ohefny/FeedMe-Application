package com.example.bethechange.feedme.Utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Handler;

import com.example.bethechange.feedme.Data.ContentFetcher;
import com.example.bethechange.feedme.Data.Contracts;
import com.example.bethechange.feedme.FeedMeApp;
import com.example.bethechange.feedme.MainScreen.Models.Site;
import com.pkmmte.pkrss.Article;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by BeTheChange on 7/20/2017.
 */

public class NetworkUtils {




    public static boolean isNetworkAvailable() {
        Context context= FeedMeApp.getContext();
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
    //make new thread
    public static void isInternetAccessible(final InternetWatcher listener) {
        if(!isNetworkAvailable()){
            listener.internetAvailable(false);
            return;
        }
      /*  new Thread(new Runnable() {
            @Override
            public void run() {
                final boolean available=getAvailability();
                new Handler(FeedMeApp.getContext().getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        listener.internetAvailable(available);
                    }
                });

            }
        }).start();*/


    }
    private static boolean getAvailability(){
        boolean available=false;
        /*try {

            InetAddress address = InetAddress.getByName("google.com");
            available= address.equals("");
            // String command = "ping -c 1 google.com";
            // available= (Runtime.getRuntime().exec(command).waitFor() == 0);
        } catch (Exception e) {
            e.printStackTrace();

        }*/
        try {
            //make a URL to a known source
            URL url = new URL("http://www.google.com");
            //open a connection to that source
            HttpURLConnection urlConnect = (HttpURLConnection)url.openConnection();
            urlConnect.setConnectTimeout(4000);
            urlConnect.setReadTimeout(4000);

            //trying to retrieve data from the source. If there
            //is no connection, this line will fail
            Object objData = urlConnect.getContent();
            available=true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return available;
    }

    public static void TestRssLink(final Site site,final RssCheckedListener listener){
            List<Article>ls=new ArrayList<>();
            new Thread(new Runnable() {
                public boolean exist;
                public boolean valid;

                @Override
                public void run() {
                    String baseUrl="https://api.rss2json.com/v1/api.json";
                    String link="";
                    link = Uri.parse(baseUrl).buildUpon()
                            .appendQueryParameter("count", "3")
                            .appendQueryParameter("rss_url", site.getRssUrl())
                            .appendQueryParameter("api_key", "f4glg6u8tcakm4yxwxinl5n9frtknhaqsi9jcayg")
                            .appendQueryParameter("order_dir", "desc")
                            .appendQueryParameter("order_by", "pubDate")
                            .toString();
                    Request request = new Request.Builder()
                            .url(link)
                            .build();

                    OkHttpClient okHttpClient=new OkHttpClient();
                    //  okHttpClient.setReadTimeout(160,TimeUnit.SECONDS);
                    Response response= null;
                    label:try {
                         response = okHttpClient.newCall(request).execute();
                        JSONObject item=new JSONObject(response.body().string());
                        valid=item.optString("status").equals("ok");
                        if(valid){
                            boolean getImg=false;
                            if(site.getmImgSrc().isEmpty()) {
                                getImg = true;
                                site.setmImgSrc(item.getJSONObject("feed").optString("image"));
                            }

                            site.setUrl(getDomainName(item.getJSONObject("feed").optString("link")));
                            Cursor cursor=FeedMeApp.getContext().getContentResolver().query(
                                    ContentUris.withAppendedId(Contracts.SiteEntry.CONTENT_URI,site.getID()),null,null,null,null);
                            if(cursor.getCount()>0){
                                exist=true;
                                break label;
                            }
                            if(getImg){
                                String img = new ContentFetcher(FeedMeApp.getContext()).getImage(site.getUrl() + " logo", null);
                                site.setmImgSrc(img);
                            }

                        }

                    } catch (IOException | JSONException | URISyntaxException e) {
                        e.printStackTrace();

                    }
                    new Handler(FeedMeApp.getContext().getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onRssLinkChecked(site,valid,exist);
                        }
                    });

                }
            }).start();



    }
    public static String getDomainName(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String domain = uri.getHost();
        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }
    public interface InternetWatcher{
        void internetAvailable(boolean isAvailable);
    }

    public static interface RssCheckedListener {
        void onRssLinkChecked(Site site,boolean valid,boolean edit);
    }
}
