package com.example.bethechange.feedme.MainScreen.Models;


import android.support.annotation.IntDef;

import com.pkmmte.pkrss.Article;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by BeTheChange on 7/10/2017.
 */

public class FeedMeArticle {

    private Article mArticle=new Article();
    private Site mSite;
    private int mID;
    private int siteID;
    private boolean fav;
    private boolean saved;
    public Article getArticle() {
        return mArticle;
    }

    public void setArticle(Article mArticle) {
        this.mArticle = mArticle;
        setArticleID(mArticle.getSource().toString().hashCode());
    }

    public boolean isFav() {
        return fav;
    }

    public void setFav(boolean fav) {
        this.fav = fav;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public int getSiteID() {
        return siteID;
    }

    public void setSiteID(int siteID) {
        this.siteID = siteID;
    }
    public Site getSite() {
        return mSite;
    }

    public void setSite(Site mSite) {
        this.mSite = mSite;
    }
    public int getArticleID() {
        if(mID==0&&mArticle!=null&&mArticle.getSource()!=null)
            setArticleID(mArticle.getSource().toString().hashCode());

        return mID;
    }

    public void setArticleID(int mID) {
        this.mID = mID;
    }
}
