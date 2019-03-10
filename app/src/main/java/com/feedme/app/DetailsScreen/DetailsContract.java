package com.feedme.app.DetailsScreen;

import android.net.Uri;

import com.feedme.app.MainScreen.Models.FeedMeArticle;

/**
 * Created by BeTheChange on 7/18/2017.
 */

interface DetailsContract {
    interface DetailsView{
        void showMessage(String str);

        void sizeChanged(int size);
    }
    interface DetailsPresenter{
        int getItemsCount();
        int getArticleID(int pos);
        int getArticlePos(int arId);
        int getStartingPos();
    }
    interface ItemView{
        void saveArticleAsWebArchive(FeedMeArticle feedMeArticle);
        void setFeedMeArticle(FeedMeArticle model);
        void showProgress();
        void endProgress();

        void showMessage(String s, Uri source);
    }
    interface ItemPresenter{
        void onPerformDelete(FeedMeArticle feedMeArticle);
        void onPerformSave(FeedMeArticle feedMeArticle);
        void onPerformFav(FeedMeArticle feedMeArticle);
        void onWebArchiveSaved(FeedMeArticle feedMeArticle,String path);

        void isVisible(boolean b);
    }
}
