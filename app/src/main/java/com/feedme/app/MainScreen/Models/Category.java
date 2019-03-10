package com.feedme.app.MainScreen.Models;

import com.feedme.app.Identifiable;

/**
 * Created by BeTheChange on 7/10/2017.
 */

public class Category implements Identifiable {

    private String mTitle = "";
    private Boolean mShared;
    private int id;
    public Category(){
        id=hashCode();
    }
    public int getId() {
        if(id==0)
            id=hashCode();
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
        return (obj instanceof Category) && this.getId() == ((Category) obj).getId();
    }

    @Override
    public String getObjectKey() {
        return getId()+"";
    }

    @Override
    public int getIntObjectKey() {
        return getId();
    }
}
