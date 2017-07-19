package com.example.bethechange.feedme.Data;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;

import com.example.bethechange.feedme.FeedMeApp;
import com.example.bethechange.feedme.MainScreen.Models.ArticlesList;
import com.example.bethechange.feedme.MainScreen.Models.FeedMeArticle;
import com.example.bethechange.feedme.MainScreen.Models.Site;
import com.example.bethechange.feedme.MainScreen.ViewContracts.ArticleListContract;

import java.util.Collections;
import java.util.List;

/**
 * Created by BeTheChange on 7/14/2017.
 */

public interface ArticleRepositoryActions {
    void removeArticle(@NonNull FeedMeArticle feedMeArticle);
    void insertArticle(final FeedMeArticle article);
    void insertArticles( List<FeedMeArticle> articles);
    void editArticle(final FeedMeArticle article);
    void onLocalDataChanged();
    void setListener(ArticlesRepository.ArticlesRepositoryObserver mListener,Site[]sites) ;
    void unsetListener(ArticlesRepository.ArticlesRepositoryObserver mListener);
    ArticlesList getArticles(@Nullable Site[]sites);
    FeedMeArticle getArticle(int id);
    FeedMeArticle getNextArticle(int currentArticle);
    FeedMeArticle getPreviousArticle(int currentArticle);
    FeedMeArticle getArticleAt(int idx);
    int getArticleIndex(int id);
    void getFullArticle(FeedMeArticle article,final ArticlesRepository.ArticlesRepositoryObserver observer);
}
