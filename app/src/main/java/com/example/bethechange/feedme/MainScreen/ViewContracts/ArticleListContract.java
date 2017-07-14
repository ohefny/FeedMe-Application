package com.example.bethechange.feedme.MainScreen.ViewContracts;

import android.database.Cursor;
import android.support.v4.content.CursorLoader;

import com.example.bethechange.feedme.MainScreen.Models.ArticlesList;
import com.example.bethechange.feedme.MainScreen.Models.FeedMeArticle;

import java.util.ArrayList;

/**
 * Created by BeTheChange on 7/10/2017.
 */

public class ArticleListContract {
    public interface View {
        void updateList(ArticlesList list);
        CursorLoader getLoader();
        void showProgress();
        void endProgress();
        void showMessage(String str);
    }
    public interface Presenter{
        void onPerformDelete(FeedMeArticle feedMeArticle);
        void onPerformSave(FeedMeArticle feedMeArticle);
        void onPerformFav(FeedMeArticle feedMeArticle);
        void newDataDelivered(ArticlesList data);
    }
}
