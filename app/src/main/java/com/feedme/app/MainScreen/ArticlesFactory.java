package com.feedme.app.MainScreen;

import android.content.Context;

import com.feedme.app.ArticleType;
import com.feedme.app.CustomScreen.SearchModel;
import com.feedme.app.Data.ArticlesRepository;
import com.feedme.app.Data.CategoriesRepository;
import com.feedme.app.Data.ContentFetcher;
import com.feedme.app.Data.SitesRepository;
import com.feedme.app.MainScreen.Models.Category;
import com.feedme.app.MainScreen.Models.Site;
import com.feedme.app.MainScreen.Presenters.ArticlesListPresenter;
import com.feedme.mvpframeworkedited.PresenterFactory;

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
