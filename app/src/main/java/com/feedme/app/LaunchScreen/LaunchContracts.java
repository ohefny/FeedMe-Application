package com.feedme.app.LaunchScreen;

import com.feedme.app.MainScreen.Models.Site;

import java.util.ArrayList;

/**
 * Created by BeTheChange on 7/30/2017.
 */

interface LaunchContracts {
    interface View {
        void updateProgressMsg(String str);
        void openMainScreen();
        void showError(String str);
        void showSitesList();

    }
    interface Presenter{
        void loginSuccessful();
        void onSitesFetched(ArrayList<Site> sites, boolean b);
    }
}
