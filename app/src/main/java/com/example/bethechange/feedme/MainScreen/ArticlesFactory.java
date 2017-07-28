package com.example.bethechange.feedme.MainScreen;

import android.content.Context;

import com.example.bethechange.feedme.ArticleType;
import com.example.bethechange.feedme.CustomScreen.SearchModel;
import com.example.bethechange.feedme.Data.ArticlesRepository;
import com.example.bethechange.feedme.Data.CategoriesRepository;
import com.example.bethechange.feedme.Data.ContentFetcher;
import com.example.bethechange.feedme.Data.SitesRepository;
import com.example.bethechange.feedme.MainScreen.Models.Category;
import com.example.bethechange.feedme.MainScreen.Models.Site;
import com.example.bethechange.feedme.MainScreen.Presenters.ArticlesListPresenter;
import com.example.mvpframeworkedited.PresenterFactory;

    /* Presenter Factory */
public class ArticlesFactory implements PresenterFactory<ArticlesListPresenter> {

    private  SearchModel mModel;
    private  Context mContext;
    private  Category mCategory;
    private  Site mSite;
    @ArticleType
    int type;
    public ArticlesFactory(@ArticleType int type , Context context, SearchModel model,Category category,Site site) {
        this.type=type;
        mContext=context;
        mModel=model;
        mCategory=category;
        mSite=site;
    }

    @Override
    public ArticlesListPresenter create() {
        ArticlesListPresenter presenter=null;
        if(type==ArticleType.CATEGORY){
            presenter= new ArticlesListPresenter(
                    ArticlesRepository.getInstance(mContext), SitesRepository.getInstance(mContext),
                    new CategoriesRepository(mContext.getContentResolver())
                    ,new ContentFetcher(mContext),type,mCategory);

        }
        else if(type==ArticleType.SITE){
            presenter= new ArticlesListPresenter(
                    ArticlesRepository.getInstance(mContext),SitesRepository.getInstance(mContext),
                    new CategoriesRepository(mContext.getContentResolver())
                    ,new ContentFetcher(mContext),type,new Site[]{mSite});
        }
        else if(type==ArticleType.SEARCH)
            presenter= new ArticlesListPresenter(
                    ArticlesRepository.getInstance(mContext),SitesRepository.getInstance(mContext),
                    new CategoriesRepository(mContext.getContentResolver())
                    ,new ContentFetcher(mContext),type,mModel);
        else
            presenter= new ArticlesListPresenter(
                    ArticlesRepository.getInstance(mContext),SitesRepository.getInstance(mContext),
                    new CategoriesRepository(mContext.getContentResolver())
                    ,new ContentFetcher(mContext),type);

        return presenter;
    }
}
