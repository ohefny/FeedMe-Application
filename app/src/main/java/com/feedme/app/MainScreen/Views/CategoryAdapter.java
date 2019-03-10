package com.feedme.app.MainScreen.Views;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.feedme.app.MainScreen.Models.Category;

import java.util.ArrayList;

/**
 * Created by BeTheChange on 7/22/2017.
 */

class CategoryAdapter extends RecyclerView.Adapter {
    private ArrayList<Category> mCategories=new ArrayList<>();
    //private MySitesContract.Presenter mListener;
    CategoryAdapter(ArrayList<Category>cats){
        mCategories=cats;
       // mListener=listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
