package com.feedme.app.Data;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.util.SparseArray;

import com.feedme.app.MainScreen.Models.Site;
import com.feedme.app.Utils.CollectionUtils;
import com.feedme.app.Utils.DBUtils;

/**
 * Created by BeTheChange on 7/30/2017.
 */

public class SuggestRepository extends AsyncQueryHandler{
    private static SparseArray<Site> suggestions=new SparseArray<>();
    private static SuggestRepository mInstance;
    private SuggestRepository(ContentResolver cr) {
        super(cr);
        startQuery(0,null, Contracts.SiteSuggestEntry.CONTENT_URI,null,null,null,Contracts.SiteSuggestEntry.COLUMN_TITLE);
    }
    public static SuggestRepository getInstance(ContentResolver cr){
        if(mInstance==null){
            mInstance=new SuggestRepository(cr);
        }
        return mInstance;
    }


    public static SparseArray<Site> getSuggestions() {
        return suggestions;
    }

    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        super.onQueryComplete(token, cookie, cursor);
        if(cursor.getCount()!=0)
            suggestions= CollectionUtils.arrayListToSparse(DBUtils.cursorToSuggestSites(cursor));
    }
}
