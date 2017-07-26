package com.example.bethechange.feedme.MainScreen.ViewContracts;

import android.net.Uri;
import android.support.v4.content.CursorLoader;

import com.example.bethechange.feedme.MainScreen.Models.ArticlesList;
import com.example.bethechange.feedme.MainScreen.Models.Category;
import com.example.bethechange.feedme.MainScreen.Models.FeedMeArticle;
import com.example.bethechange.feedme.MainScreen.Models.Site;

import java.util.ArrayList;

/**
 * Created by BeTheChange on 7/10/2017.
 */

public interface MySitesContract {
     interface View {
        void updateList(ArrayList<Site> list);
        void updateSpinner(ArrayList<Category>categories);
        void onCategoryChanged(int id);
        void showProgress();
        void endProgress();
        void showMessage(String str);
        void showEditDialog(Site site);
        void openSite(Site site);
     }
     interface Presenter{
        void onPerformDelete(Site site);
        void onPerformAdd(Site site);
        void onPerformEdit(Site site);
        void onOpenSiteArticles(Site site);
        void onCategoryChoosed(Category category);
        void onEditPressed(Site mItem);
        void onCategoryAdded(Category cat);
     }
}
