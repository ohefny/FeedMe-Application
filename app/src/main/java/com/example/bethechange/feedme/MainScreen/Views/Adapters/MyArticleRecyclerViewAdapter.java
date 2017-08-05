package com.example.bethechange.feedme.MainScreen.Views.Adapters;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bethechange.feedme.FeedMeApp;
import com.example.bethechange.feedme.MainScreen.Models.FeedMeArticle;
import com.example.bethechange.feedme.MainScreen.Models.Site;
import com.example.bethechange.feedme.R;
import com.squareup.picasso.Picasso;


import java.util.List;

public class MyArticleRecyclerViewAdapter extends RecyclerView.Adapter<MyArticleRecyclerViewAdapter.ViewHolder> {

    private final Context mContext;
    private List<FeedMeArticle> mValues;
    private SparseBooleanArray mapSnippet = new SparseBooleanArray();
    ArticleListItemListener mListener;
    private final int VIEW_EMPTY = 100;
    private int VIEW_ARTICLES = 200;

    //TODO::ADD LISTENER TO ARGS
    public MyArticleRecyclerViewAdapter(List<FeedMeArticle> items, Context context,
                                        ArticleListItemListener listener) {
        mValues = items;
        mapSnippet = new SparseBooleanArray();
        for (FeedMeArticle ar : items) {
            mapSnippet.put(ar.getArticleID(), false);
        }
        Log.d("Snippet", "" + mapSnippet.get(0));
        mContext = context;
        mListener = listener;
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
        holder.showSnippet = true;
        // holder.mArticleImg.setText(mValues.get(position).id);
        holder.mTitleView.setText(mValues.get(position).getArticle().getTitle());
        holder.mDescriptionView.setText(mValues.get(position).getArticle().getDescription());
        Picasso.with(FeedMeApp.getContext())
                .load(holder.mItem.getArticle().getImage())
                .placeholder(R.drawable.thumbnail)
                .error(R.drawable.thumbnail)
                .into(holder.mArticleImg);
        makeViewType(holder, false);
        Log.d(getClass().getSimpleName(), "Fuck Link :: " + holder.mItem.getArticle().getImage().toString());
        // holder.mSiteView.setText(mValues.get(position).getArticle().getDescription());
        holder.mSnippet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                makeViewType(holder, true);
            }
        });
        holder.mArticleOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu(v, position, holder);

            }
        });
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO:NOTIFY THE LISTENER WITH THE CLICK
                mListener.onArticleOpened(getListItems().get(position), position);

            }
        });
        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //TODO:CREATE CONTEXT MENU
                showMenu(v, position, holder);
                return true;
            }
        });
        if (holder.mItem.getSite() != null && !TextUtils.isEmpty(holder.mItem.getSite().getTitle())) {
            holder.mSiteView.setText(holder.mItem.getSite().getTitle());
            holder.mSiteView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onSiteTitleClicked(holder.mItem.getSite());
                }
            });
        }
    }

    private void showMenu(View view, final int position, final ViewHolder holder) {
        PopupMenu menu = new PopupMenu(mContext, view);
        menu.getMenu().add(0, R.id.item_snippet, 0, FeedMeApp.getContext().getString(R.string.item_snippet));
        menu.getMenu().add(0, R.id.item_fav, 1, holder.mItem.isFav() ?FeedMeApp.getContext().getString(R.string.item_unbookmark) : FeedMeApp.getContext().getString(R.string.item_bookmark));
        menu.getMenu().add(0, R.id.item_save, 2, holder.mItem.isSaved() ? FeedMeApp.getContext().getString(R.string.item_unsave) : FeedMeApp.getContext().getString(R.string.item_save));
        menu.getMenu().add(0, R.id.item_delete, 3, FeedMeApp.getContext().getString(R.string.item_delete));
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.item_fav:
                        mListener.onBookmarkClicked(getListItems().get(position),position);
                        break;
                    case R.id.item_save:
                        mListener.onSaveClicked(getListItems().get(position),position);
                        break;
                    case R.id.item_snippet:
                        makeViewType(holder, true);
                        break;
                    case R.id.item_delete:
                        mListener.onDeleteClicked(getListItems().get(position),position);
                        break;
                }
                return true;
            }
        });
        //menu.inflate (R.menu.list_item_options);
        menu.show();
    }

    private void makeViewType(ViewHolder holder, boolean change) {
        Log.d("Hashmap ", holder.mItem.getArticleID() + " " + mapSnippet.get(holder.mItem.getArticleID()));
        if (change && !mapSnippet.get(holder.mItem.getArticleID())) {
            holder.mTitleView.setVisibility(View.GONE);
            //holder.mSiteView.setVisibility(View.GONE);
            holder.mDescriptionView.setVisibility(View.VISIBLE);
            mapSnippet.put(holder.mItem.getArticleID(), true);
            holder.mSnippet.setText(R.string.title_val_on_btn);
            Log.d("Hashmap Clicked", holder.mItem.getArticleID() + " " + mapSnippet.get(holder.mItem.getArticleID()));
            return;
        }
        if (change && mapSnippet.get(holder.mItem.getArticleID())) {
            holder.mDescriptionView.setVisibility(View.GONE);
            holder.mSiteView.setVisibility(View.VISIBLE);
            holder.mTitleView.setVisibility(View.VISIBLE);
            mapSnippet.put(holder.mItem.getArticleID(), false);
            holder.mSnippet.setText(R.string.snippet_val_btn);
            return;
        }
        if (mapSnippet.get(holder.mItem.getArticleID())) {
            holder.mTitleView.setVisibility(View.GONE);
           // holder.mSiteView.setVisibility(View.GONE);
            holder.mDescriptionView.setVisibility(View.VISIBLE);
            holder.mSnippet.setText(R.string.title_val_on_btn);
            return;

        }
        if (!mapSnippet.get(holder.mItem.getArticleID())) {
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
        for (FeedMeArticle ar : listItems) {

            mapSnippet.get(ar.getArticleID(), mapSnippet.get(ar.getArticleID()));
        }
        notifyDataSetChanged();

    }

    @Override
    public int getItemViewType(int position) {
        if (mValues.size() > 0)
            return VIEW_EMPTY;
        return VIEW_ARTICLES;
    }

    public List<FeedMeArticle> getListItems() {
        return mValues;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        ImageButton mArticleOptions;
        private Button mSnippet;
        TextView mTitleView;
        TextView mSiteView;
        ImageView mArticleImg;

        FeedMeArticle mItem;
        TextView mDescriptionView;
        boolean showSnippet = true;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView = (TextView) mView.findViewById(R.id.article_title_id);
            mSiteView = (TextView) mView.findViewById(R.id.site_title_id);
            mSnippet = (Button) mView.findViewById(R.id.snippet_btn);
            mDescriptionView = (TextView) mView.findViewById(R.id.snippet_text_tv);
            mArticleImg = (ImageView) mView.findViewById(R.id.article_img);
            mArticleOptions = (ImageButton) mView.findViewById(R.id.article_options_btn);

        }

        @Override
        public String toString() {
            return super.toString();//+ " '" + mContentView.getText() + "'";
        }
    }
    class EmptyViewHolder extends RecyclerView.ViewHolder {
        ImageView imgview;
        public EmptyViewHolder(View itemView) {
            super(itemView);
            imgview= (ImageView) itemView.findViewById(R.id.empty_articles_view);
        }
    }
    public interface ArticleListItemListener {
        void onSaveClicked(FeedMeArticle article, int position);

        void onDeleteClicked(FeedMeArticle article, int position);

        void onBookmarkClicked(FeedMeArticle article, int position);

        void onArticleOpened(FeedMeArticle article, int pos);

        void onSiteTitleClicked(Site site);

        void onSnippetClicked(FeedMeArticle article);
    }
}
