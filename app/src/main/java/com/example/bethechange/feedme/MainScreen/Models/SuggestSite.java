package com.example.bethechange.feedme.MainScreen.Models;

import com.example.bethechange.feedme.Identifiable;
import com.google.firebase.database.Exclude;

/**
 * Created by BeTheChange on 7/31/2017.
 */

public class SuggestSite implements Identifiable{
    private String mTitle="";
    private String mUrl="";
    private String mRssUrl="";
    private String mImgSrc="";

    private int mID;
    public SuggestSite(Site site){
        setTitle(site.getTitle());
        setUrl(site.getUrl());
        setRssUrl(site.getRssUrl());
        setID(site.getID());
        setmImgSrc(site.getmImgSrc());
    }
    @Exclude
    public int getID() {
        return mID;
    }

    public void setID(int mID) {
        this.mID = mID;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String mUrl) {
        this.mUrl = mUrl;
        mID=mUrl.hashCode();
    }

    public String getRssUrl() {
        return mRssUrl;
    }

    public void setRssUrl(String mRssUrl) {
        this.mRssUrl = mRssUrl;

    }
    public String getmImgSrc() {
        return mImgSrc;
    }

    public void setmImgSrc(String mImgSrc) {
        this.mImgSrc = mImgSrc;
    }

    @Override
    public String toString() {
        return getTitle();
    }
    @Exclude
    @Override
    public String getObjectKey() {
        return getID()+"";
    }

    @Override
    public int getIntObjectKey() {
        return getID();
    }
}
