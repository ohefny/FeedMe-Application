package com.example.bethechange.feedme.MainScreen.Views;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bethechange.feedme.MainScreen.Models.Category;
import com.example.bethechange.feedme.MainScreen.Models.Site;
import com.example.bethechange.feedme.MainScreen.Views.Adapters.MySiteRecyclerViewAdapter;
import com.example.bethechange.feedme.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by BeTheChange on 7/28/2017.
 */

class NavigationCategoryAdapter extends RecyclerView.Adapter<NavigationCategoryAdapter.ViewHolder> {
    private final CategoriesListListener listener;
    private ArrayList<Category> categories=new ArrayList<>();

    NavigationCategoryAdapter(ArrayList<Category> cats, CategoriesListListener listener) {
        categories=cats;
        this.listener=listener;
    }

    @Override
    public long getItemId(int position) {
        return categories.get(position).getId();
    }

    @Override
    public NavigationCategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.simple_nav_category_item, parent, false);
        return new NavigationCategoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.mItem = categories.get(position);
        holder.titleView.setText(categories.get(position).getTitle());
        holder.deleteCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDeleteCategory(position);
            }
        });
        holder.editCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onEditCategory(position);
            }
        });
    }


    @Override
    public int getItemCount() {
        return categories.size();
    }

    public void setCategories(ArrayList<Category> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }
    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView titleView;
        final ImageButton deleteCat;
        final ImageButton editCat;
        Category mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;
            titleView = (TextView) view.findViewById(R.id.cat_item_nav_title);
            deleteCat=(ImageButton)view.findViewById(R.id.cat_item_nav_delete);
            editCat=(ImageButton)view.findViewById(R.id.cat_item_nav_edit);
        }

    }

    interface CategoriesListListener {
        void onCategoryClicked(int position);
        void onDeleteCategory(int position);
        void onEditCategory(int position);
    }
}
