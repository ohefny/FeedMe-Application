package com.feedme.app.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.feedme.app.FeedMeApp;
import com.feedme.app.MainScreen.Models.FeedMeArticle;
import com.feedme.app.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class WidgetRecyclerViewAdapter extends RecyclerView.Adapter<WidgetRecyclerViewAdapter.ViewHolder>  {

    private final Context mContext;
    private List<FeedMeArticle> mValues;


    //TODO::ADD LISTENER TO ARGS
    public WidgetRecyclerViewAdapter(List<FeedMeArticle> items, Context context) {
        mValues = items;
        mContext=context;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.article_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        holder.showSnippet=true;
       // holder.mArticleImg.setText(mValues.get(position).id);
        holder.mTitleView.setText(mValues.get(position).getArticle().getTitle());
        Picasso.with(FeedMeApp.getContext())
                    .load(holder.mItem.getArticle().getImage())
                    .placeholder(R.drawable.thumbnail)
                    .error(R.drawable.thumbnail)
                    .into(holder.mArticleImg);

    }

    @Override
    public int getItemCount() {

        return mValues.size();
    }

    public void setListItems(List<FeedMeArticle> listItems) {
        this.mValues = listItems;

        notifyDataSetChanged();

    }

    public List<FeedMeArticle> getListItems() {
        return mValues;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
         final View mView;
         TextView mTitleView;
        ImageView mArticleImg;
         FeedMeArticle mItem;

         boolean showSnippet=true;

         ViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView= (TextView) mView.findViewById(R.id.article_title_id);
            mArticleImg=(ImageView)mView.findViewById(R.id.article_img);


        }

        @Override
        public String toString() {
            return super.toString() ;//+ " '" + mContentView.getText() + "'";
        }
    }

}
