package com.example.bethechange.feedme.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Handler;

import com.example.bethechange.feedme.FeedMeApp;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

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
        new Thread(new Runnable() {
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
        }).start();


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
    public interface InternetWatcher{
        void internetAvailable(boolean isAvailable);
    }

}
