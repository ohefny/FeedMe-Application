package com.example.bethechange.feedme.MainScreen.Models;

import com.example.bethechange.feedme.Identifiable;
import com.google.firebase.database.Exclude;



/**
 * Created by BeTheChange on 7/10/2017.
 */

public class Site implements Identifiable{
    private String mTitle="";
    private String mUrl="";
    private String mRssUrl="";
    private String mImgSrc="";
    private Category mCategory;
    private int categoryID;
    private int mID;
    private boolean mBlocked;

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }
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


    }

    public String getRssUrl() {
        return mRssUrl;
    }

    public void setRssUrl(String mRssUrl) {
        this.mRssUrl = mRssUrl;
        mID=mRssUrl.hashCode();

    }
    @Exclude
    public Category getCategory() {
        return mCategory;
    }
    @Exclude
    public void setCategory(Category mCategory) {
        this.mCategory = mCategory;
    }

    public void setBlocked(boolean blocked) {
        this.mBlocked = blocked;
    }

    public boolean isBlocked() {
        return mBlocked;
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

    @Override
    public String getObjectKey() {
        return getID()+"";
    }

    @Override
    public int getIntObjectKey() {
        return getID();
    }
}
