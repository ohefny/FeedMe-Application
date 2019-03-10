package com.feedme.app.LaunchScreen;

import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;

import com.feedme.app.Data.ArticlesRepository;
import com.feedme.app.Data.Contracts;
import com.feedme.app.FeedMeApp;
import com.feedme.app.MainScreen.Models.ArticlesList;
import com.feedme.app.MainScreen.Models.Category;
import com.feedme.app.MainScreen.Models.FeedMeArticle;
import com.feedme.app.MainScreen.Models.Site;
import com.feedme.app.R;
import com.feedme.app.Utils.DBUtils;
import com.feedme.app.Utils.FirebaseUtils;
import com.feedme.app.Utils.PrefUtils;

import java.util.ArrayList;

/**
 * Created by BeTheChange on 7/29/2017.
 */

class FirebaseSyncManger implements FirebaseUtils.FirebaseUserListener, FirebaseUtils.FirebaseCategoriesListener, FirebaseUtils.FirebaseSitesListener, ArticlesRepository.ArticlesRepositoryObserver {
    private final ArticlesRepository mRepo;
    private Context mContext;
    private CountDownTimer ct;
    private SyncInteractor mInteractor;
    FirebaseSyncManger(SyncInteractor interactor,ArticlesRepository repo) {
        mContext=FeedMeApp.getContext();
        mInteractor=interactor;
        mRepo=repo;

    }

    void startSyncing() {
        if(PrefUtils.isSynced(mContext))
            finishSyncing();
        else{
            FirebaseUtils.checkUserExist(this);
            mInteractor.onNewOperation(FeedMeApp.getContext().getString(R.string.syncing));
        }

    }

    @Override
    public void onUserChecked(boolean exist) {
        if(!exist){
            mInteractor.noSitesFound();
            return;
        }
        if(!PrefUtils.isSynced(mContext)){
            FirebaseUtils.getUserCategories(this);
        }
        else{
            //this means that data is synced and no need to sync again just load from content provider
            prepareArticles();


        }
    }

    @Override
    public void onCategoriesFetched(ArrayList<Category> categories, boolean error) {
        if(error){
            mInteractor.errorOccurred(mContext.getString(R.string.unable_to_sync));
            return;
        }
        if(categories.size()>0){
            FeedMeApp.getContext().getContentResolver().bulkInsert(Contracts.CategoryEntry.CONTENT_URI, DBUtils.categoriesToCV(categories));
            FirebaseUtils.getUserSites(this);
        }
        else{
            mInteractor.noSitesFound();
        }
    }


    @Override
    public void onSitesFetched(ArrayList<Site> sites, boolean error) {
        if(error){
            mInteractor.errorOccurred(mContext.getString(R.string.unable_to_sync));
            return;
        }
        if(sites.size()>0){
            mContext.getContentResolver().bulkInsert(Contracts.SiteEntry.CONTENT_URI, DBUtils.sitesToCV(sites));
            PrefUtils.setSynced(mContext);
            prepareArticles();

        }
        else{
            mInteractor.noSitesFound();
        }
    }

    private void prepareArticles() {
        mInteractor.onNewOperation(FeedMeApp.getContext().getString(R.string.fetching_articles));
        mRepo.setListener(this,null);
        mRepo.getLatestArticles();
        if(ct==null){
            ct=getCountDownTimer();
            ct.start();
        }
        else {
            ct.cancel();
            ct=getCountDownTimer();
            ct.start();
        }
    }

    private CountDownTimer getCountDownTimer() {
        return new CountDownTimer(30000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                System.out.println("ToFinish "+millisUntilFinished);

            }

            @Override
            public void onFinish() {
                new Handler(mContext.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                       mInteractor.errorOccurred(mContext.getString(R.string.slow_fetching));
                       finishSyncing();
                    }
                });
            }
        };
    }

    private void finishSyncing() {
        if(ct!=null)
             ct.cancel();
        mInteractor.syncFinished();
    }

    @Override
    public void onDataChanged(ArticlesList data) {

        finishSyncing();
    }

    @Override
    public void onFullArticleFetched(FeedMeArticle article) {

    }

    interface SyncInteractor{
        void onNewOperation(String str);
        void syncFinished();
        void errorOccurred(String str);
        void noSitesFound();

    }

}
