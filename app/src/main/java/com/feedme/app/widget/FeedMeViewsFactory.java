package com.feedme.app.widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.feedme.app.Data.Contracts;
import com.feedme.app.DetailsScreen.DetailsActivity;
import com.feedme.app.MainScreen.Models.FeedMeArticle;
import com.feedme.app.R;
import com.feedme.app.Utils.CollectionUtils;
import com.feedme.app.Utils.DBUtils;

import java.util.ArrayList;

/**
 * Created by BeTheChange on 8/2/2017.
 */

public class FeedMeViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private final Context mContext;
    private final Intent mIntent;
    private Cursor cursor;
    ArrayList<FeedMeArticle>articles=new ArrayList<>();

    public FeedMeViewsFactory(Context context, Intent intent) {
        mContext=context;
        mIntent=intent;

    }
    private void initCursor(){
        if (cursor != null) {
            cursor.close();
        }
        final long identityToken = Binder.clearCallingIdentity();
        /**This is done because the widget runs as a separate thread
         when compared to the current app and hence the app's data won't be accessible to it */

        cursor = mContext.getContentResolver().query(Contracts.ArticleEntry.CONTENT_URI,
                null,null, null, Contracts.ArticleEntry.COLUMN_DATE+" DESC");
        if (cursor != null &&!cursor.isClosed()) {
            articles= CollectionUtils.sparseToArray(DBUtils.cursorToArticles(cursor));
        }
        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onCreate() {
        initCursor();

    }

    @Override
    public void onDataSetChanged() {
        /** Listen for data changes and initialize the cursor again **/
        initCursor();
    }

    @Override
    public RemoteViews getViewAt(final int i) {
        /** Populate your widget's single list item **/
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
        final int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(mContext,getClass()));
        final RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);
        remoteViews.setTextViewText(R.id.widget_item_title_id,articles.get(i).getArticle().getTitle());

        //Picasso.with(mContext).load(articles.get(i).getArticle().getImage()).into(remoteViews, R.id.widget_item_img_id, appWidgetIds);
          // set Onclick Item Intent
        Intent onClickItemIntent = new Intent();
        ArrayList<Integer> ids = new ArrayList<Integer>();
        ids.add(articles.get(i).getArticleID());
        onClickItemIntent.putExtra(DetailsActivity.ARTICLES_IDS,ids);
        onClickItemIntent.putExtra(DetailsActivity.ARTICLE_ID_KEY,articles.get(i).getArticleID());
        remoteViews.setOnClickFillInIntent(R.id.widget_item_row,onClickItemIntent);
        return remoteViews;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public int getCount() {
        return articles.size();
    }

    @Override
    public long getItemId(int i) {
        return articles.get(i).getArticleID();
    }

    @Override
    public void onDestroy() {
        if (cursor!=null&&!cursor.isClosed())
            cursor.close();
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }


}
