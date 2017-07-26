package com.example.bethechange.feedme.MainScreen.Views;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;

import com.example.bethechange.feedme.MainScreen.Models.Category;
import com.example.bethechange.feedme.MainScreen.ViewContracts.MySitesContract;

import java.util.ArrayList;

/**
 * Created by BeTheChange on 7/22/2017.
 */

class CategoryAdapter implements SpinnerAdapter {
    private ArrayList<Category> mCategories=new ArrayList<>();
    private MySitesContract.Presenter mListener;
    CategoryAdapter(MySitesContract.Presenter listener, ArrayList<Category>cats){
        mCategories=cats;
        mListener=listener;
    }
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return mCategories.size();
    }

    @Override
    public Object getItem(int position) {
        return mCategories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mCategories.get(position).getId();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return mCategories.isEmpty();
    }
}
