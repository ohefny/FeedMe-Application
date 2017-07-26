package com.example.bethechange.feedme.MainScreen.Models;

/**
 * Created by BeTheChange on 7/10/2017.
 */

public class Site{
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
        mID=mUrl.hashCode();
    }

    public String getRssUrl() {
        return mRssUrl;
    }

    public void setRssUrl(String mRssUrl) {
        this.mRssUrl = mRssUrl;

    }

    public Category getCategory() {
        return mCategory;
    }

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

}
