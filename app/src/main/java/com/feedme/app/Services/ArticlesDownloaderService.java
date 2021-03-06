package com.feedme.app.Services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import com.feedme.app.Data.ArticleFetcher;
import com.feedme.app.Data.Contracts;
import com.feedme.app.Utils.DBUtils;
import com.feedme.app.Utils.PrefUtils;
import com.feedme.app.MainScreen.Models.Site;
import com.google.gson.Gson;

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
    private static final String ACTION_FETCH_ALL= "com.feedme.app.Services.action.LatestArticles";
    private static final String REFRESH_NOW="com.feedme.app.Services.extra.REFRESHNOW";
    private static final String ACTION_FETCH_SITES="com.feedme.app.Services.action.LatestSitesArticles";
    private static final String SITES_STR="com.feedme.app.Services.extra.SITESJSON";
    public static final String ACTION_DATA_UPDATED="com.feedme.app.Services.action.DATA_UPDATED";

    public ArticlesDownloaderService() {
        super("ArticlesDownloaderService");
    }

    public static void startActionUpdateAll(Context context, boolean refreshNow) {
        Intent intent = new Intent(context, ArticlesDownloaderService.class);
        intent.setAction(ACTION_FETCH_ALL);
        intent.putExtra(REFRESH_NOW,refreshNow);
        context.startService(intent);
    }
    public static void startActionUpdateSites(Context context,boolean refreshNow,String gsonSites){
        Intent intent = new Intent(context, ArticlesDownloaderService.class);
        intent.setAction(ACTION_FETCH_SITES);
        intent.putExtra(REFRESH_NOW,refreshNow);
        intent.putExtra(SITES_STR,gsonSites);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("FFFF OnHandle Intent",intent.toString());
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FETCH_ALL.equals(action)) {
                final boolean refreshNow = intent.getBooleanExtra(REFRESH_NOW,false);
                //final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionLatest(refreshNow);
            } else if (ACTION_FETCH_SITES.equals(action)) {
                Gson gson=new Gson();
                final boolean refresh = intent.getBooleanExtra(REFRESH_NOW,true);
                final String json = intent.getStringExtra(SITES_STR);
                Site site=gson.fromJson(json, Site.class);
                if(site!=null&&!site.getRssUrl().isEmpty())
                    handleActionSites(site, refresh);
            }
        }
    }

       /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionLatest(boolean refreshNow) {
        if(!refreshNow&&!PrefUtils.updateNow(this))
            return;
        Site[]sitesArr=DBUtils.cursorToSites(getContentResolver().
                query(Contracts.SiteEntry.CONTENT_URI,null,null,null,null)).toArray(new Site[]{});
        int startPage=0;
        int pageSize=1;
        ArticleFetcher fetcher=new ArticleFetcher(this,sitesArr,startPage,pageSize);
        int inserted=getContentResolver().bulkInsert(Contracts.ArticleEntry.CONTENT_URI,fetcher.getContentValues());
        if(inserted>0){
            PrefUtils.updateLastUpdate(this);
            Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED);
            this.sendBroadcast(dataUpdatedIntent);

        }
        Log.d("FFFF Inserted Items ",inserted+"");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionSites(Site site, boolean refresh) {
        if(!refresh)
            return;
        Site[]sitesArr=new Site[]{site};
        int startPage=0;
        int pageSize=1;
        ArticleFetcher fetcher=new ArticleFetcher(this,sitesArr,startPage,pageSize);
        int newItems=getContentResolver().bulkInsert(Contracts.ArticleEntry.CONTENT_URI,fetcher.getContentValues());
        if(newItems>0) {
            Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED);
            this.sendBroadcast(dataUpdatedIntent);
        }
        Log.d("FFFF Inserted Items",newItems+"");
    }
}
