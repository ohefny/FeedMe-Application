package com.example.bethechange.feedme.DetailsScreen;

import android.support.annotation.NonNull;

import com.example.bethechange.feedme.Data.ArticlesRepository;
import com.example.bethechange.feedme.Data.Contracts;
import com.example.bethechange.feedme.FeedMeApp;
import com.example.bethechange.feedme.MainScreen.Models.ArticlesList;
import com.example.bethechange.feedme.MainScreen.Models.FeedMeArticle;
import com.example.bethechange.feedme.Utils.DBUtils;
import com.example.bethechange.feedme.Utils.NetworkUtils;
import com.example.mvpframeworkedited.BasePresenter;

/**
 * Created by BeTheChange on 7/19/2017.
 */

public class ArticleItemPresenter extends BasePresenter<FeedMeArticle,DetailsContract.ItemView>
        implements DetailsContract.ItemPresenter,ArticlesRepository.ArticlesRepositoryObserver,NetworkUtils.InternetWatcher{
    private FeedMeArticle article;
    ArticlesRepository mRepo;
    private boolean isFetching;
    private boolean isVisible;
    ArticleItemPresenter(ArticlesRepository repo, int arID) {
        super();
        mRepo=repo;
        //mRepo.setListener(this,sites);
        article=mRepo.getArticle(arID);
        if(article==null){
            if(view()!=null)
                view().showProgress();
            article=DBUtils.cursorToArticle(FeedMeApp.getContext().getContentResolver().query(Contracts.ArticleEntry.CONTENT_URI,null,Contracts.ArticleEntry._ID+" = ?",new String[]{arID+""},null));
        }
        if(!article.isContentFetched()){
            if(view()!=null)
                view().showProgress();
            isFetching=true;
            NetworkUtils.isInternetAccessible(this);
            mRepo.getFullArticle(article,this);

        }
        setModel(article);
    }

    @Override
    protected void updateView() {

        view().setFeedMeArticle(model);
    }

    @Override
    public void onPerformDelete(FeedMeArticle feedMeArticle) {
        mRepo.removeArticle(feedMeArticle);
    }

    @Override
    public void onPerformSave(FeedMeArticle feedMeArticle) {
        feedMeArticle.setSaved(true);
        mRepo.editArticle(feedMeArticle);

    }

    @Override
    public void onPerformFav(FeedMeArticle feedMeArticle) {
        feedMeArticle.setFav(true);
        mRepo.editArticle(feedMeArticle);

    }

    @Override
    public void onWebArchiveSaved(FeedMeArticle feedMeArticle, String path) {
        feedMeArticle.setWebArchivePath(path);
    }

    @Override
    public void isVisible(boolean b) {
        isVisible=b;
        if(isVisible&&isFetching&&view()!=null)
            view().showProgress();
    }

    @Override
    public void onDataChanged(ArticlesList data) {

    }

    @Override
    public void bindView(@NonNull DetailsContract.ItemView view) {
        super.bindView(view);
        if(isFetching&&isVisible)
            view().showProgress();
    }

    @Override
    public void onFullArticleFetched(FeedMeArticle article) {


        if(isFetching&&view()!=null){
            view().endProgress();

        }
        isFetching=false;
        setModel(article);

    }

    @Override
    public void internetAvailable(boolean isAvailable) {
        if(!isAvailable&&view()!=null){
            view().endProgress();
            view().showMessage("No Internet Available Check Internet ",
                    article.getArticle().getSource());

        }
    }
}
