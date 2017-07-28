package com.example.bethechange.feedme.MainScreen.ViewContracts;

import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

import com.example.bethechange.feedme.MainScreen.Models.ArticlesList;
import com.example.bethechange.feedme.MainScreen.Models.Category;
import com.example.bethechange.feedme.MainScreen.Models.FeedMeArticle;

import java.util.ArrayList;

/**
 * Created by BeTheChange on 7/10/2017.
 */

public interface ArticleListContract {
     interface View {
        void updateList(ArticlesList list);

        CursorLoader getLoader();

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
    }
}
