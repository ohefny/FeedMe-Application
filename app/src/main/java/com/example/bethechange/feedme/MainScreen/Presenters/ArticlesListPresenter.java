package com.example.bethechange.feedme.MainScreen.Presenters;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.bethechange.feedme.ArticleType;
import com.example.bethechange.feedme.Data.ArticleRepositoryActions;
import com.example.bethechange.feedme.Data.ArticlesRepository;
import com.example.bethechange.feedme.Data.Contracts;
import com.example.bethechange.feedme.Data.DBUtils;
import com.example.bethechange.feedme.MainScreen.Models.ArticlesList;
import com.example.bethechange.feedme.MainScreen.Models.FeedMeArticle;
import com.example.bethechange.feedme.MainScreen.Models.Site;
import com.example.bethechange.feedme.MainScreen.ViewContracts.ArticleListContract;
import com.example.mvpframeworkedited.BasePresenter;

import java.util.ArrayList;

/**
 * Created by BeTheChange on 7/10/2017.
 */

public class ArticlesListPresenter extends BasePresenter<ArticlesList,ArticleListContract.View>
    implements ArticleListContract.Presenter, ArticlesRepository.ArticlesRepositoryObserver{
    private ArticleRepositoryActions mRepo;
    @ArticleType int mArticleClass;
    private int startPage=0;
    private Site[]mSites=null;
    private int pageSizes=1;
    public ArticlesListPresenter(@ArticleType int articleClass,
                                 @NonNull ArticleRepositoryActions repo ){
        this(articleClass);
        mRepo=repo;
        mRepo.setListener(this,mSites);
        setModel(mRepo.getArticles(mSites));

    }
    public ArticlesListPresenter(@NonNull ArticleRepositoryActions repo ){
        mRepo=repo;
        mRepo.setListener(this,mSites);
        setModel(mRepo.getArticles(mSites));

    }
    public ArticlesListPresenter(@ArticleType int articleClass){


    }
    @Override
    protected void updateView() {
            view().updateList(model);
    }

    @Override
    public void onPerformDelete(FeedMeArticle feedMeArticle) {

        mRepo.removeArticle(feedMeArticle);
    }

    @Override
    public void onPerformSave(FeedMeArticle feedMeArticle) {
        mRepo.editArticle(feedMeArticle);
    }

    @Override
    public void onPerformFav(FeedMeArticle feedMeArticle) {
        feedMeArticle.setFav(!feedMeArticle.isFav());
        mRepo.editArticle(feedMeArticle);
    }

    @Override
    public void onDataChanged(ArticlesList data) {
        setModel(data);
        Log.d("Presneter","Fuck Data change: "+startPage+ " size= "+data.getArticles().size());
        if(view()!=null){
            view().endProgress();
            view().showMessage("Database Updated..");
        }
        startPage+=1;
    }

    @Override
    public void bindView(@NonNull ArticleListContract.View view) {
        super.bindView(view);
        view().setInteractor(this);
    }

}
