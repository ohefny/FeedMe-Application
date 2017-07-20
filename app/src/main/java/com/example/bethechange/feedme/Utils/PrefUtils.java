package com.example.bethechange.feedme.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.bethechange.feedme.Data.Contracts;
import com.example.bethechange.feedme.IntervalTypes;
import com.example.bethechange.feedme.MainScreen.Views.MainScreenActivity;
import com.example.bethechange.feedme.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by BeTheChange on 7/12/2017.
 */
public final class PrefUtils {


        private PrefUtils() {
        }

        public static boolean updateNow(Context context) {
            final String INITIALIZED_KEY=context.getString(R.string.pref_initialized_key);
            final String LAST_UPDATE_KEY = context.getString(R.string.pref_last_update_key);
            final String INTERVAL_VAL_KEY = context.getString(R.string.pref_interval_val_key);
            final String INTERVAL_TYPE_KEY = context.getString(R.string.pref_interval_type_key);

            //final String LAST_UPDATE_KEY = context.getString(R.string.pref_last_update_key);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            boolean update=false;
            boolean initialized=prefs.getBoolean(INITIALIZED_KEY,false);
            if(!initialized){
                initializePrefs(prefs,context);
                return true;
            }
            long lastUpdate = prefs.getLong(LAST_UPDATE_KEY, 0);
            long interval=prefs.getLong(INTERVAL_VAL_KEY, 24);
            String intervalType=prefs.getString(INTERVAL_TYPE_KEY,IntervalTypes.HOURS);
            
            if (intervalType.equals(IntervalTypes.HOURS)) {
                long now=Long.parseLong(new SimpleDateFormat("yyyMMddhh").format(new Date()));
               if(now-lastUpdate>=interval){
                   update=true;
               }
            }
            else{
                long now=Long.parseLong((new SimpleDateFormat("yyyMMdd")).format(new Date()));
                if(now-lastUpdate>=interval){
                    update=true;
                }
                
            }
            return update;
        }

    private static void initializePrefs(SharedPreferences prefs,Context context) {
        final String INITIALIZED_KEY=context.getString(R.string.pref_initialized_key);
        final String LAST_UPDATE_KEY = context.getString(R.string.pref_last_update_key);
        final String INTERVAL_VAL_KEY = context.getString(R.string.pref_interval_val_key);
        final String INTERVAL_TYPE_KEY = context.getString(R.string.pref_interval_type_key);
        boolean initialized=prefs.getBoolean(INITIALIZED_KEY,false);
        if(initialized){
           return;
        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(INITIALIZED_KEY, true);
        editor.putLong(INTERVAL_VAL_KEY,24);
        editor.putString(INTERVAL_TYPE_KEY, IntervalTypes.HOURS);
        editor.putLong(LAST_UPDATE_KEY,0);
        editor.apply();
        context.getContentResolver().
                bulkInsert(Contracts.SiteEntry.CONTENT_URI,DBUtils.sitesToCV(MainScreenActivity.getSites()));
    }

    public static void updateLastUpdate(Context context) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong(context.getString(R.string.pref_last_update_key),System.currentTimeMillis());
            editor.apply();
        }
    public static void updateIntervalType(Context context, @IntervalTypes String type) {
            String key = context.getString(R.string.pref_interval_type_key);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(key, type);
            editor.apply();
        }
    public static void updateIntervalVal(Context context, int val) {
        String key = context.getString(R.string.pref_interval_val_key);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, val);
        editor.apply();
    }
    public static String getUpdateIntervalType(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        initializePrefs(prefs,context);
        String key = context.getString(R.string.pref_interval_type_key);
        return prefs.getString(key,IntervalTypes.HOURS);


    }
    public static int getUpdateIntervalVal(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        initializePrefs(prefs,context);
        String type=getUpdateIntervalType(context);
        String key = context.getString(R.string.pref_interval_val_key);
        if(type.equals(IntervalTypes.HOURS))
            return prefs.getInt(key,24);
        else
            return prefs.getInt(key,1);
    }



}


