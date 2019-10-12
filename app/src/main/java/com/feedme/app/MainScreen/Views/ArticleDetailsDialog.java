package com.feedme.app.MainScreen.Views;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.DialogFragment;
import androidx.core.app.ShareCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.feedme.app.CustomAspectImage;
import com.feedme.app.MainScreen.Models.FeedMeArticle;
import com.feedme.app.R;
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
    private FloatingActionButton shareFab;
    private FloatingActionButton closeFab;

    public  ArticleDetailsDialog(){

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.DetailsDialogAnimation;
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
        mPhotoView.setAspectRatio(3,4);
        bodyView=(TextView)mRootView.findViewById(R.id.article_body);
        titleTV=(TextView)mRootView.findViewById(R.id.title_dialog_id);
        shareFab=(FloatingActionButton)mRootView.findViewById(R.id.share_fab);
        closeFab=(FloatingActionButton)mRootView.findViewById(R.id.close_fab);
        closeFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        shareFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText(feedMeArticle.getArticle().getDescription()+"\n\n"+feedMeArticle.getArticle().getSource().toString())
                        .getIntent(), getString(R.string.action_share)));
            }
        });
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

