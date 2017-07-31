package com.example.bethechange.feedme.MainScreen.Models;

/**
 * Created by BeTheChange on 7/10/2017.
 */

public class Category {

    private String mTitle="";
    private Boolean mShared;
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public Boolean getShared() {
        return mShared;
    }

    public void setShared(Boolean mShared) {
        this.mShared = mShared;
    }

    @Override
    public String toString() {
        return mTitle;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Category) && this.getId()==((Category)obj).getId();
    }
}
