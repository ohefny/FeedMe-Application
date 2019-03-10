package com.feedme.app.LaunchScreen;

import com.feedme.app.Data.ArticlesRepository;
import com.feedme.app.MainScreen.Models.Site;
import com.feedme.mvpframeworkedited.BasePresenter;

import java.util.ArrayList;

/**
 * Created by BeTheChange on 7/30/2017.
 */

public class LaunchPresenter extends BasePresenter<Void,LaunchContracts.View>
        implements FirebaseSyncManger.SyncInteractor,LaunchContracts.Presenter{
    FirebaseSyncManger syncManger;
    public LaunchPresenter(ArticlesRepository repository){
        syncManger=new FirebaseSyncManger(this,repository);
    }
    @Override
    protected void updateView() {

    }

    @Override
    public void onNewOperation(String str) {
        if(view()!=null)
            view().updateProgressMsg(str);
    }

    @Override
    public void syncFinished() {
        if(view()!=null)
            view().openMainScreen();
    }

    @Override
    public void errorOccurred(String str) {
        if(view()!=null)
            view().showError(str);

    }

    @Override
    public void noSitesFound() {
        if(view()!=null)
            view().showSitesList();
    }

    @Override
    public void loginSuccessful() {

        syncManger.startSyncing();
    }

    @Override
    public void onSitesFetched(ArrayList<Site> sites, boolean b) {
        syncManger.onSitesFetched(sites,b);
    }
}
