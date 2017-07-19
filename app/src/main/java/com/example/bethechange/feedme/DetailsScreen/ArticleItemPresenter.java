package com.example.bethechange.feedme.DetailsScreen;

import android.support.annotation.NonNull;

import com.example.bethechange.feedme.Data.ArticlesRepository;
import com.example.bethechange.feedme.MainScreen.Models.ArticlesList;
import com.example.bethechange.feedme.MainScreen.Models.FeedMeArticle;
import com.example.bethechange.feedme.MainScreen.Models.Site;
import com.example.mvpframeworkedited.BasePresenter;

/**
 * Created by BeTheChange on 7/19/2017.
 */

public class ArticleItemPresenter extends BasePresenter<FeedMeArticle,DetailsContract.ItemView>
        implements DetailsContract.ItemPresenter,ArticlesRepository.ArticlesRepositoryObserver{
    ArticlesRepository mRepo;
    boolean isFetching;
    public ArticleItemPresenter(ArticlesRepository repo,int arID) {
        super();
        mRepo=repo;
        //mRepo.setListener(this,sites);
        FeedMeArticle article=mRepo.getArticle(arID);
        if(!article.isContentFetched()){
            isFetching=true;
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
    public void isVisible() {
        if(isFetching)
            view().showProgress();
    }

    @Override
    public void onDataChanged(ArticlesList data) {

    }

    @Override
    public void bindView(@NonNull DetailsContract.ItemView view) {
        super.bindView(view);
        //if(isFetching)
          //  view().showProgress();
    }

    @Override
    public void onFullArticleFetched(FeedMeArticle article) {

        isFetching=false;
        if(view()!=null)
            view().endProgress();
        setModel(article);

    }
}
