package com.example.bethechange.feedme.Services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import com.example.bethechange.feedme.Data.ArticleFetcher;
import com.example.bethechange.feedme.Data.Contracts;
import com.example.bethechange.feedme.Utils.DBUtils;
import com.example.bethechange.feedme.Utils.PrefUtils;
import com.example.bethechange.feedme.MainScreen.Models.Site;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ArticlesDownloaderService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ACTION_FETCH_LATEST= "com.example.bethechange.feedme.Services.action.LatestArticles";
    private static final String ACTION_BAZ = "com.example.bethechange.feedme.Services.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.example.bethechange.feedme.Services.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.example.bethechange.feedme.Services.extra.PARAM2";
    public static final String REFRESH_NOW="com.example.bethechange.feedme.Services.extra.REFRESHNOW";

    public ArticlesDownloaderService() {
        super("ArticlesDownloaderService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, ArticlesDownloaderService.class);
        intent.setAction(ACTION_FETCH_LATEST);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, ArticlesDownloaderService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FETCH_LATEST.equals(action)) {
                final boolean refreshNow = intent.getBooleanExtra(REFRESH_NOW,false);
                //final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionLatest(refreshNow);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionLatest(boolean refreshNow) {
        if(!PrefUtils.updateNow(this)&&!refreshNow)
            return;
        Site[]sitesArr=DBUtils.cursorToSites(getContentResolver().
                query(Contracts.SiteEntry.CONTENT_URI,null,null,null,null)).toArray(new Site[]{});
        int startPage=0;
        int pageSize=1;
        ArticleFetcher fetcher=new ArticleFetcher(this,sitesArr,startPage,pageSize);
        int inserted=getContentResolver().bulkInsert(Contracts.ArticleEntry.CONTENT_URI,fetcher.getContentValues());
        if(inserted>0){
            PrefUtils.updateLastUpdate(this);
        }
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
