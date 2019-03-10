package com.feedme.app.MainScreen.ViewContracts;

import android.net.Uri;

import com.feedme.app.MainScreen.Models.ArticlesList;
import com.feedme.app.MainScreen.Models.Category;
import com.feedme.app.MainScreen.Models.FeedMeArticle;

import java.util.ArrayList;

/**
 * Created by BeTheChange on 7/10/2017.
 */

public interface ArticleListContract {
     interface View {
        void updateList(ArticlesList list);
        void showProgress();
        void endProgress();
        void showMessage(String str, Uri source);
        void saveArticleAsWebArchive(FeedMeArticle feedMeArticle);
        void setInteractor(ArticleListContract.Presenter interactor);
        void showArticle(FeedMeArticle article, boolean onWebView);
        void imageUpdated(FeedMeArticle article);
        void updateCategoriesSpinner(ArrayList<Category> cats);
        void deleteWebArchive(FeedMeArticle feedMeArticle);
     }
     interface Presenter{
        void onPerformDelete(FeedMeArticle feedMeArticle);
        void onPerformSave(FeedMeArticle feedMeArticle);
        void onPerformFav(FeedMeArticle feedMeArticle);
        void onWebArchiveSaved(FeedMeArticle feedMeArticle,String path);
        void onOpenArticle(FeedMeArticle article);
        void onCategorySelected(Category item);
        void onViewVisible();
        ArrayList<Integer> getArticlesIds();
     }
}
