package com.feedme.app.Data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.feedme.app.CustomScreen.SearchModel;
import com.feedme.app.MainScreen.Models.ArticlesList;
import com.feedme.app.MainScreen.Models.FeedMeArticle;
import com.feedme.app.MainScreen.Models.Site;

import java.util.ArrayList;
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
    ArticlesList getSavedArticles();
    ArticlesList getBookmarkedArticles();
    ArticlesList getArticlesWithIds(ArrayList<Integer>ids,ArticlesRepository.ArticlesRepositoryObserver mListener);

    void getArticlesFromSearchQuery(SearchModel model, ArticlesRepository.ArticlesRepositoryObserver mListener);
}
