package com.example.bethechange.feedme.MainScreen.Views;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bethechange.feedme.CustomAspectImage;
import com.example.bethechange.feedme.DetailsScreen.ArticleDetailFragment;
import com.example.bethechange.feedme.FeedMeApp;
import com.example.bethechange.feedme.MainScreen.Models.FeedMeArticle;
import com.example.bethechange.feedme.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by BeTheChange on 8/4/2017.
 */

public class ArticleDetailsDialog extends DialogFragment {
    private View mRootView;
    private CustomAspectImage mPhotoView;
    private FeedMeArticle feedMeArticle; 
    private TextView byLineTv;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private TextView bodyView;
    private TextView titleTV;

    public  ArticleDetailsDialog(){

    }
    public static ArticleDetailsDialog newInstance(FeedMeArticle article) {
        ArticleDetailsDialog fragment = new ArticleDetailsDialog();
        fragment.setArticle(article);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        setRetainInstance(true);
        mRootView = inflater.inflate(R.layout.details_dialog_fragment, container, false);
        mPhotoView=(CustomAspectImage)mRootView.findViewById(R.id.photo);
        bodyView=(TextView)mRootView.findViewById(R.id.article_body);
        titleTV=(TextView)mRootView.findViewById(R.id.title_dialog_id);
        bindArticleToViews();
        return mRootView;
    }
    private void bindArticleToViews() {
        if (mRootView == null) {
            return;
        }


        if (feedMeArticle != null) {
            Picasso.with(getContext()).load(feedMeArticle.getArticle().getImage()).into(
                    mPhotoView);
                byLineTv=((TextView)mRootView.findViewById(R.id.byLine));
                String date= getDateFormatted();
                byLineTv.setText(feedMeArticle.getArticle().getAuthor()+" "+ getDateFormatted());
                String body=android.text.Html.fromHtml(feedMeArticle.getArticle().getContent()).toString();
                bodyView.setText(body);
                titleTV.setText(feedMeArticle.getArticle().getTitle());
        }



    }


    public String getDateFormatted() {
        Date date=new Date(feedMeArticle.getArticle().getDate());
        return dateFormat.format(date);

    }
    public void setArticle(FeedMeArticle article) {
        this.feedMeArticle = article;
    }
}

