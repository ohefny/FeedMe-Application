package com.example.bethechange.feedme.MainScreen.Views;

import android.database.DataSetObserver;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;

import com.example.bethechange.feedme.MainScreen.Models.Category;
import com.example.bethechange.feedme.MainScreen.ViewContracts.MySitesContract;

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
