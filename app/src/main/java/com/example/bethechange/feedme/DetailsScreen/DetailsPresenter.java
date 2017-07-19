package com.example.bethechange.feedme.DetailsScreen;

import android.support.annotation.NonNull;

import com.example.bethechange.feedme.Data.ArticlesRepository;
import com.example.bethechange.feedme.MainScreen.Models.ArticlesList;
import com.example.bethechange.feedme.MainScreen.Models.FeedMeArticle;
import com.example.bethechange.feedme.MainScreen.Models.Site;
import com.example.mvpframeworkedited.BasePresenter;

import java.util.ArrayList;

/**
 * Created by BeTheChange on 7/18/2017.
 */

public class DetailsPresenter extends BasePresenter<ArticlesList,DetailsContract.DetailsView>
    implements DetailsContract.DetailsPresenter, ArticlesRepository.ArticlesRepositoryObserver {
    private final ArticlesRepository mRepo;

    public DetailsPresenter(ArticlesRepository repository,Site[]sites){
        mRepo=repository;
        //TODO::change null with site/sites(category)
        mRepo.setListener(this,sites);
        ArticlesList ls=mRepo.getArticles(sites);
        setModel(ls);
    }
    @Override
    protected void updateView() {

    }

    @Override
    public void onDataChanged(ArticlesList data) {
        if(data.getArticles().size()!=model.getArticles().size())
            view().sizeChanged(data.getArticles().size());
        setModel(data);
    }

    @Override
    public void onFullArticleFetched(FeedMeArticle article) {

    }


    @Override
    public int getItemsCount() {
        return model.getArticles().size();
    }

    @Override
    public int getArticleID(int pos) {
        return model.getArticles().get(pos).getArticleID();
    }
}
