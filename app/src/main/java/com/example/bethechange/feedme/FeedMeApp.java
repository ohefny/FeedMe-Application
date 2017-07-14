package com.example.bethechange.feedme;

import android.app.Application;
import android.content.Context;

/**
 * Created by BeTheChange on 7/12/2017.
 */

public class FeedMeApp extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }



    public  static Context getContext(){
        return mContext;
    }
}
