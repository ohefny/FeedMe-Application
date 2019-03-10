package com.feedme.app.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.feedme.app.R;
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
            //final String LAST_UPDATE_KEY = context.getString(R.string.pref_last_update_key);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            boolean update;
            boolean initialized= isInitialized(context);
            if(!initialized){
                initializePrefs(prefs,context);
                return true;
            }
            long lastUpdate = prefs.getLong(LAST_UPDATE_KEY, 0);
            int interval=prefs.getInt(INTERVAL_VAL_KEY, 24);
            long lastHours =  (lastUpdate / (1000*60*60));
            long nowHours=(System.currentTimeMillis() / (1000*60*60));
            update=(nowHours-lastHours>=interval);
            return update;
        }

    public static boolean isInitialized(Context context) {
        final String INITIALIZED_KEY=context.getString(R.string.pref_initialized_key);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(INITIALIZED_KEY,false);
    }

    private static void initializePrefs(SharedPreferences prefs,Context context) {
        final String INITIALIZED_KEY=context.getString(R.string.pref_initialized_key);
        final String LAST_UPDATE_KEY = context.getString(R.string.pref_last_update_key);
        final String INTERVAL_VAL_KEY = context.getString(R.string.pref_interval_val_key);
        final String CLEANUP_EVERY_KEY=context.getString(R.string.pref_cleanup_val_key);
        final String BACKUP_EVERY_KEY=context.getString(R.string.pref_backup_val_key);
        boolean initialized= isInitialized(context);
        if(initialized){
           return;
        }
        SharedPreferences.Editor editor = prefs.edit();

        //hours
        editor.putInt(INTERVAL_VAL_KEY,24);
        editor.putLong(LAST_UPDATE_KEY,0);
        //days
        editor.putLong(CLEANUP_EVERY_KEY,7);
        //days
        editor.putInt(BACKUP_EVERY_KEY,1);
        editor.putBoolean(INITIALIZED_KEY, true);
        editor.apply();
        //TODO::remember to replace with real sites
        /*context.getContentResolver().
                bulkInsert(Contracts.SiteEntry.CONTENT_URI,DBUtils.sitesToCV(MainScreenActivity.getSites()));*/
    }

    public static void updateLastUpdate(Context context) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong(context.getString(R.string.pref_last_update_key),System.currentTimeMillis());
            editor.apply();
    }
    public static void setUpdateInterval(Context context, int val) {
        String key = context.getString(R.string.pref_interval_val_key);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, val);
        editor.apply();
    }
    public static void setOutdateVal(Context context, int val) {
        String key = context.getString(R.string.pref_cleanup_val_key);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, val);
        editor.apply();
    }
    public static void setBackupInterval(Context context, int val) {
        String key = context.getString(R.string.pref_backup_val_key);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, val);
        editor.apply();
    }

    public static int getUpdateInterval(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        initializePrefs(prefs,context);
        String key = context.getString(R.string.pref_interval_val_key);
        return prefs.getInt(key,24);

    }
    public static int getBackupInterval(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        initializePrefs(prefs,context);
        String key = context.getString(R.string.pref_backup_val_key);
        return prefs.getInt(key,24);

    }
    public static int getOutdate(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        initializePrefs(prefs,context);
        String key = context.getString(R.string.pref_cleanup_val_key);
        return prefs.getInt(key,24);

    }


    public static void updateLastSynchronized() {

    }


    public static void setSynced(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String key = context.getString(R.string.is_synced_pref_key);
        prefs.edit().putBoolean(key,true).apply();

    }
    public static boolean isSynced(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String key = context.getString(R.string.is_synced_pref_key);
        boolean isSynced=prefs.getBoolean(key,false);
        return isSynced;
    }

    public static long deleteBefore(Context context) {
        //TODO IMPLEMENT THIS TO SEE CURRENT TIME AND SUBTRACT FROM CLEAUNUPAFTEER ATTRIBUTE
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String key = context.getString(R.string.pref_cleanup_val_key);
        long every=prefs.getLong(key,1)*(1000*60*60);
        return System.currentTimeMillis()-every;
    }

    public static int updateEveryMillie(Context context) {
        final String INTERVAL_VAL_KEY = context.getString(R.string.pref_interval_val_key);


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
       // String key = context.getString(R.string.pref_up);

        int interval=prefs.getInt(INTERVAL_VAL_KEY, 24);

        return interval*(1000*60*60);

    }
    public static int backupEveryMillie(Context context) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
         String key = context.getString(R.string.pref_backup_val_key);

        int interval=prefs.getInt(key, 24);
        return interval*(1000*60*60*24);

    }
}


