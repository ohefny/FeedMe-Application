package com.example.bethechange.feedme.MainScreen.Views;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bethechange.feedme.FeedMeApp;
import com.example.bethechange.feedme.MainScreen.Models.ArticlesList;
import com.example.bethechange.feedme.MainScreen.Models.FeedMeArticle;
import com.example.bethechange.feedme.R;
import com.squareup.picasso.Picasso;


import java.util.HashMap;
import java.util.List;

public class MyArticleRecyclerViewAdapter extends RecyclerView.Adapter<MyArticleRecyclerViewAdapter.ViewHolder>  {

    private List<FeedMeArticle> mValues;
    private SparseBooleanArray mapSnippet=new SparseBooleanArray();

    //TODO::ADD LISTENER TO ARGS
    MyArticleRecyclerViewAdapter(List<FeedMeArticle> items) {
        mValues = items;
        mapSnippet=new SparseBooleanArray();
        for(FeedMeArticle ar:items){
            mapSnippet.put(ar.getArticleID(),false);
        }
        Log.d("Snippet",""+mapSnippet.get(0));

       // mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.article_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder,  int position) {
        holder.mItem = mValues.get(position);
        holder.showSnippet=true;
       // holder.mArticleImg.setText(mValues.get(position).id);
        holder.mTitleView.setText(mValues.get(position).getArticle().getTitle());
        holder.mDescriptionView.setText(mValues.get(position).getArticle().getDescription());
        Picasso.with(FeedMeApp.getContext())
                .load(holder.mItem.getArticle().getImage())
                .placeholder(R.drawable.thumbnail)
                .error(R.drawable.thumbnail)
                .into(holder.mArticleImg);
        makeViewType(holder,false);
        Log.d(getClass().getSimpleName(),"Fuck Link :: "+holder.mItem.getArticle().getImage().toString());
        // holder.mSiteView.setText(mValues.get(position).getArticle().getDescription());
        holder.mSnippet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                makeViewType(holder,true);
            }
        });
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO:NOTIFY THE LISTENER WITH THE CLICK
                Log.d("Fuck Article ",holder.mItem.getArticle().getSource().toString());
             /*   if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                } */
            }
        });
        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //TODO:CREATE CONTEXT MENU
                return false;
            }
        });
    }

    private void makeViewType(ViewHolder holder,boolean change) {
        Log.d("Hashmap ",holder.mItem.getArticleID()+" "+mapSnippet.get(holder.mItem.getArticleID()));
        if(change&&!mapSnippet.get(holder.mItem.getArticleID()))
        {
            holder.mTitleView.setVisibility(View.GONE);
            holder.mSiteView.setVisibility(View.GONE);
            holder.mDescriptionView.setVisibility(View.VISIBLE);
            mapSnippet.put(holder.mItem.getArticleID(),true);
            holder.mSnippet.setText(R.string.title_val_on_btn);
            Log.d("Hashmap Clicked",holder.mItem.getArticleID()+" "+mapSnippet.get(holder.mItem.getArticleID()));
            return;
        }
        if (change&&mapSnippet.get(holder.mItem.getArticleID())){
            holder.mDescriptionView.setVisibility(View.GONE);
            holder.mSiteView.setVisibility(View.VISIBLE);
            holder.mTitleView.setVisibility(View.VISIBLE);
            mapSnippet.put(holder.mItem.getArticleID(),false);
            holder.mSnippet.setText(R.string.snippet_val_btn);
            return;
        }
        if(mapSnippet.get(holder.mItem.getArticleID())){
            holder.mTitleView.setVisibility(View.GONE);
            holder.mSiteView.setVisibility(View.GONE);
            holder.mDescriptionView.setVisibility(View.VISIBLE);
            holder.mSnippet.setText(R.string.title_val_on_btn);
            return;

        }
        if(!mapSnippet.get(holder.mItem.getArticleID())){
            holder.mDescriptionView.setVisibility(View.GONE);
            holder.mSiteView.setVisibility(View.VISIBLE);
            holder.mTitleView.setVisibility(View.VISIBLE);
            holder.mSnippet.setText(R.string.snippet_val_btn);
        }
        //TODO://REMEMBER TO CHANGE THIS TO USE ITEMVIEW
    }

    @Override
    public int getItemCount() {

        return mValues.size();
    }

    public void setListItems(List<FeedMeArticle> listItems) {
        this.mValues = listItems;
        for (FeedMeArticle ar:listItems){

            mapSnippet.get(ar.getArticleID(),mapSnippet.get(ar.getArticleID()));
        }
        notifyDataSetChanged();

    }

    public List<FeedMeArticle> getListItems() {
        return mValues;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
         final View mView;
        private Button mSnippet;
          TextView mTitleView;
          TextView mSiteView;
          ImageView mArticleImg;

         FeedMeArticle mItem;
         TextView mDescriptionView;
         boolean showSnippet=true;

         ViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView= (TextView) mView.findViewById(R.id.article_title_id);
            mSiteView= (TextView) mView.findViewById(R.id.site_title_id);
            mSnippet=(Button)mView.findViewById(R.id.snippet_btn);
            mDescriptionView=(TextView)mView.findViewById(R.id.snippet_text_tv);
            mArticleImg=(ImageView)mView.findViewById(R.id.article_img);
           // mArticleOptions= (ImageButton) mView.findViewById(R.id.article_options_btn);

        }

        @Override
        public String toString() {
            return super.toString() ;//+ " '" + mContentView.getText() + "'";
        }
    }
}
