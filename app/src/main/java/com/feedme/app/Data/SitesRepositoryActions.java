package com.feedme.app.Data;

import com.feedme.app.MainScreen.Models.Category;
import com.feedme.app.MainScreen.Models.Site;

import java.util.ArrayList;

/**
 * Created by BeTheChange on 7/20/2017.
 */

public interface SitesRepositoryActions {


        Site getSite(int id);
        void removeSite(int id);
        void removeSite(Site site);
        void addSite(Site site);
        void editSite(Site site);
        ArrayList<Site> getSites(Category category);
        ArrayList<Category> getCategorise();
        void setObserver(SitesRepository.SitesObserver observer);

        void addCategory(Category cat);
}
