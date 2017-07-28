package com.example.bethechange.feedme.MainScreen.Views.Adapters;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.bethechange.feedme.MainScreen.Models.Site;
import com.example.bethechange.feedme.MainScreen.ViewContracts.MySitesContract;
import com.example.bethechange.feedme.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class MySiteRecyclerViewAdapter extends RecyclerView.Adapter<MySiteRecyclerViewAdapter.ViewHolder> {




    private  MySitesContract.Presenter mListener;
    private  List<Site> mValues=new ArrayList<>();
    private Context mContext;
    //private final OnListFragmentInteractionListener mListener;

    public MySiteRecyclerViewAdapter(List<Site> items, Context context, MySitesContract.Presenter presenter){//, OnListFragmentInteractionListener listener) {
        mValues = items;
        mContext=context;
        mListener = presenter;
    }

    public void setListener(MySitesContract.Presenter listener) {
        this.mListener = listener;
    }

    public void setSites(List<Site>sites){
        mValues=sites;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.site_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        holder.titleView.setText(mValues.get(position).getTitle());
        holder.siteOptionsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu(v,position,holder);
            }
        });
        Picasso.with(mContext).load(holder.mItem.getmImgSrc()).error(R.drawable.logo_placeholder).
                placeholder(R.drawable.logo_placeholder).resize(120,120).into(holder.logoView);
        //holder.logoView.setText(mValues.get(position);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onOpenSiteArticles(holder.mItem);
                }
            }
        });
    }
    private void showMenu(View view, final int position,final MySiteRecyclerViewAdapter.ViewHolder holder)
    {
        PopupMenu menu = new PopupMenu (mContext, view);
        menu.setOnMenuItemClickListener (new PopupMenu.OnMenuItemClickListener ()
        {

            @Override
            public boolean onMenuItemClick (MenuItem item)
            {
                int id = item.getItemId();
                switch (id)
                {
                    case R.id.item_open:
                        mListener.onOpenSiteArticles(holder.mItem);
                    case R.id.item_edit:
                        mListener.onEditPressed(holder.mItem);
                        break;
                    case R.id.item_delete:
                        mListener.onPerformDelete(holder.mItem);
                        break;
                }
                return true;
            }
        });
        menu.inflate (R.menu.site_item_options);
        menu.show();
    }


    @Override
    public int getItemCount() {
        return mValues.size();
    }

     class ViewHolder extends RecyclerView.ViewHolder {
         final View mView;
         final TextView titleView;
        //public final TextView subtitileView;
         final ImageView logoView;
         final TextView siteOptionsView;
         Site mItem;

         ViewHolder(View view) {
            super(view);
            mView = view;
            titleView = (TextView) view.findViewById(R.id.site_title);
            //subtitileView = (TextView) view.findViewById(R.id.site_subtitle);
            logoView = (ImageView) view.findViewById(R.id.thumbnail);
            siteOptionsView=(TextView)view.findViewById(R.id.site_options);
        }

        @Override
        public String toString() {
            return super.toString() ;//+ " '" + mContentView.getText() + "'";
        }
    }
}
