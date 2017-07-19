package com.example.bethechange.feedme.DetailsScreen;

import com.example.bethechange.feedme.MainScreen.Models.FeedMeArticle;
import com.example.bethechange.feedme.MainScreen.ViewContracts.ArticleListContract;

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
    }
    interface ItemView{
        void saveArticleAsWebArchive(FeedMeArticle feedMeArticle);
        void setFeedMeArticle(FeedMeArticle model);
        void showProgress();
        void endProgress();
    }
    interface ItemPresenter{
        void onPerformDelete(FeedMeArticle feedMeArticle);
        void onPerformSave(FeedMeArticle feedMeArticle);
        void onPerformFav(FeedMeArticle feedMeArticle);
        void onWebArchiveSaved(FeedMeArticle feedMeArticle,String path);

        void isVisible();
    }
}
