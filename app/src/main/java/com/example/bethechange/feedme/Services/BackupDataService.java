package com.example.bethechange.feedme.Services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.bethechange.feedme.Data.Contracts;
import com.example.bethechange.feedme.FeedMeApp;
import com.example.bethechange.feedme.MainScreen.Models.Category;
import com.example.bethechange.feedme.MainScreen.Models.Site;
import com.example.bethechange.feedme.Utils.DBUtils;
import com.example.bethechange.feedme.Utils.FirebaseUtils;
import com.example.bethechange.feedme.Utils.PrefUtils;

import java.util.ArrayList;

/**
 * Created by BeTheChange on 7/31/2017.
 */

public class BackupDataService extends IntentService {
    private static final String ACTION_BACKUP="com.example.bethechange.feedme.Services.action.Backup";

    public BackupDataService() {
        super("BackupDataService");
    }
    public static void startActionBackup(Context context) {
        Intent intent = new Intent(context, BackupDataService.class);
        intent.setAction(ACTION_BACKUP);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_BACKUP.equals(action)) {
                //final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleBackup();
            }
        }
    }

    private void handleBackup() {
        ArrayList<Category>cats=DBUtils.cursorToCategories(getContentResolver().query(Contracts.CategoryEntry.CONTENT_URI,null,null,null,null));
        ArrayList<Site>sites=DBUtils.cursorToSites(getContentResolver().query(Contracts.SiteEntry.CONTENT_URI,null,null,null,null));
        FirebaseUtils.serialBackup(cats,sites);
        FirebaseUtils.insertSuggestionsSites(DBUtils.cursorToSites(getContentResolver().query(Contracts.SiteEntry.CONTENT_URI,null,null,null,null)));
        FirebaseUtils.updateSuggestionsSites(this);
    }
}
