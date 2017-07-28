package com.example.bethechange.feedme.CustomScreen;

import com.example.bethechange.feedme.ArticleType;

/**
 * Created by BeTheChange on 7/27/2017.
 */

public class SearchModel {

    private String query;
    private @ArticleType int searchIn;
    private int searchInId;

    public SearchModel(String query, int searchIn, int searchInId) {
        this.query = query;
        this.searchIn = searchIn;
        this.searchInId = searchInId;
    }
    public String getQuery() {
        return query;
    }

    public int getSearchIn() {
        return searchIn;
    }

    public int getSearchInId() {
        return searchInId;
    }
}
