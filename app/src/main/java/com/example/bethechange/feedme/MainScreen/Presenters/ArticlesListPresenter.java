package com.example.bethechange.feedme.MainScreen.Presenters;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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
    implements LoaderManager.LoaderCallbacks<Cursor>,ArticleListContract.Presenter,
        ArticlesRepository.ArticlesRepositoryObserver{
    private static final int LOCAL_LOADER_ID=1;
    private ArticleRepositoryActions mRepo;
    private LoaderManager mLoaderManger;
    private CursorLoader mArticleLoader;
    @ArticleType int mArticleClass;
    private int mTypeID;
    private int startPage=0;
    private Site[]mSites=null;
    private int pageSizes=1;
    //Loader needed and maybe repository
    public ArticlesListPresenter(@ArticleType int articleClass,
                                 @NonNull LoaderManager loaderManager, @NonNull CursorLoader loader,@NonNull ArticleRepositoryActions repo  ){
       this(articleClass);
        mLoaderManger=loaderManager;
        setLoader(loader);
        //TODO: SET ANY REQUIRED PARAMETERS FOR PROJECTION AND SELECTION AND SO
     /*   mArticleLoader.setPagesSize(pageSizes);
        mArticleLoader.setStartPage(startPage);
        mArticleLoader.setSites(MainScreenActivity.getSites());*/
      //  loaderManager.initLoader(LOCAL_LOADER_ID,null,this);
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

    //
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(id==LOCAL_LOADER_ID)
            return mArticleLoader;
        return null;
        //else

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(loader==mArticleLoader){
            setModel(new ArticlesList((DBUtils.cursorToArticles(data))));
            view().endProgress();
            startPage+=1;
            Log.d("ArticleListPresenter","Fuck Call Time "+startPage);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public void onLocalDataChanged() {
            if(view()!=null&&mLoaderManger!=null){
                view().showProgress();
                setLoader(view().getLoader());
              //  mLoaderManger.restartLoader(LOCAL_LOADER_ID,null,this);
            }
    }

    void setLoader(CursorLoader loader) {
        mArticleLoader=loader;
        mArticleLoader.setUri(Contracts.ArticleEntry.CONTENT_URI);
        mArticleLoader.setSortOrder(Contracts.ArticleEntry.COLUMN_DATE+" DESC");
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
