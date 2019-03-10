package com.feedme.app.Data;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.SparseArray;

import com.feedme.app.MainScreen.Models.Category;
import com.feedme.app.Utils.DBUtils;

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
        startInsert(CATEGORIES_TOKEN,cat, Contracts.CategoryEntry.CONTENT_URI,DBUtils.categoriesToCV(new Category[]{cat})[0]);
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
            if(listeners.get(token)!=null)
                listeners.get(token).categoriesFetched(cats);
        }
     //   FirebaseUtils.insertCategoryList(cats);
    }
    private void queryCategories(){
        startQuery(CATEGORIES_TOKEN,null,Contracts.CategoryEntry.CONTENT_URI,null,null,null,null);
    }

    public void editCategory(Category cat) {
        super.startUpdate(0,cat, ContentUris.withAppendedId(Contracts.CategoryEntry.CONTENT_URI,
                cat.getId()),
                DBUtils.categoriesToCV(new Category[]{cat})[0],null,null);
    }

    public void deleteCategory(Category category) {
        super.startDelete(0,category,Contracts.CategoryEntry.CONTENT_URI,Contracts.CategoryEntry._ID+" = ? ",new String[]{category.getId()+""});
    }

    @Override
    protected void onDeleteComplete(int token, Object cookie, int result) {
        super.onDeleteComplete(token, cookie, result);
        Category deleted = (Category) cookie;
        //super.startQuery(2020,null, Contracts.SiteEntry.CONTENT_URI,null,null,null,null);
    }

    @Override
    protected void onInsertComplete(int token, Object cookie, Uri uri) {
        super.onInsertComplete(token, cookie, uri);
        Category inserted = (Category) cookie;
    }

    @Override
    protected void onUpdateComplete(int token, Object cookie, int result) {
        super.onUpdateComplete(token, cookie, result);
        Category updated = (Category) cookie;
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
