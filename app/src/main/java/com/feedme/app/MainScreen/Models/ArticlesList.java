package com.feedme.app.MainScreen.Models;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BeTheChange on 7/10/2017.
 */

public class ArticlesList {



    private ArrayList<FeedMeArticle> mFeedMeArticles =new ArrayList<FeedMeArticle>();
    public ArticlesList(){

    }
    public ArticlesList(List<FeedMeArticle> ar){
        this.mFeedMeArticles=new ArrayList<>(ar);

    }

    public ArticlesList(SparseArray<FeedMeArticle> feedMeArticleSparseArray) {
        if (feedMeArticleSparseArray!=null) {
            for (int i = 0; i < feedMeArticleSparseArray.size(); i++)
                mFeedMeArticles.add(feedMeArticleSparseArray.valueAt(i));
        }
    }

    public ArrayList<FeedMeArticle> getArticles() {
        return mFeedMeArticles;
    }

    public void setArticles(ArrayList<FeedMeArticle> feedMeArticles) {
        this.mFeedMeArticles = feedMeArticles;
    }
}
