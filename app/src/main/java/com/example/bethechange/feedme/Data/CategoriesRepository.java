package com.example.bethechange.feedme.Data;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.util.SparseArray;

import com.example.bethechange.feedme.MainScreen.Models.Category;
import com.example.bethechange.feedme.MainScreen.Models.Site;
import com.example.bethechange.feedme.MainScreen.Presenters.ArticlesListPresenter;
import com.example.bethechange.feedme.Utils.DBUtils;

import java.util.ArrayList;

/**
 * Created by BeTheChange on 7/25/2017.
 */

public class CategoriesRepository extends AsyncQueryHandler{
    private static final int CATEGORIES_TOKEN =101;
    private ArrayList<Category>cats=new ArrayList<>();
    private SparseArray<CategoriesListener> listeners=new SparseArray<>();

    public CategoriesRepository(ContentResolver cr) {
        super(cr);
        CategoriesObserver observer = new CategoriesObserver(new Handler(Looper.getMainLooper()));
        cr.registerContentObserver(Contracts.CategoryEntry.CONTENT_URI,true, observer);
        queryCategories();
    }

    public ArrayList<Category> getCategories(CategoriesListener listener) {
        listeners.put(listener.hashCode(),listener);
        startQuery(listener.hashCode(),null, Contracts.CategoryEntry.CONTENT_URI,
                        null,null,null,null);
        return cats;

    }
    public void addCategory(Category cat){
        startInsert(CATEGORIES_TOKEN,null, Contracts.CategoryEntry.CONTENT_URI,DBUtils.categoriesToCV(new Category[]{cat})[0]);
        startQuery(CATEGORIES_TOKEN,null, Contracts.CategoryEntry.CONTENT_URI,
                null,null,null,null);
    }
    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        super.onQueryComplete(token, cookie, cursor);
        cats=DBUtils.cursorToCategories(cursor);
        if(token==CATEGORIES_TOKEN){
            for(int i=0;i<listeners.size();i++){
                listeners.valueAt(i).categoriesFetched(cats);
            }
        }
        else {
            listeners.get(token).categoriesFetched(cats);
        }
    }
    private void queryCategories(){
        startQuery(CATEGORIES_TOKEN,null,Contracts.CategoryEntry.CONTENT_URI,null,null,null,null);
    }

    public interface CategoriesListener{
     void categoriesFetched(ArrayList<Category>cats);
 }
    private class CategoriesObserver extends ContentObserver{
        CategoriesObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            queryCategories();
        }
    }

}
