package com.example.bethechange.feedme.MainScreen.Presenters;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.bethechange.feedme.ArticleType;
import com.example.bethechange.feedme.Data.ArticleRepositoryActions;
import com.example.bethechange.feedme.Data.ArticlesRepository;
import com.example.bethechange.feedme.Data.CategoriesRepository;
import com.example.bethechange.feedme.Data.ContentFetcher;
import com.example.bethechange.feedme.Data.SitesRepository;
import com.example.bethechange.feedme.Data.SitesRepositoryActions;
import com.example.bethechange.feedme.MainScreen.Models.ArticlesList;
import com.example.bethechange.feedme.MainScreen.Models.Category;
import com.example.bethechange.feedme.MainScreen.Models.FeedMeArticle;
import com.example.bethechange.feedme.MainScreen.Models.Site;
import com.example.bethechange.feedme.MainScreen.ViewContracts.ArticleListContract;
import com.example.bethechange.feedme.Utils.NetworkUtils;
import com.example.mvpframeworkedited.BasePresenter;

import java.util.ArrayList;

/**
 * Created by BeTheChange on 7/10/2017.
 */

public class ArticlesListPresenter extends BasePresenter<ArticlesList,ArticleListContract.View>
    implements ArticleListContract.Presenter, ArticlesRepository.ArticlesRepositoryObserver,
        NetworkUtils.InternetWatcher, CategoriesRepository.CategoriesListener {
    private CategoriesRepository catRepo;
    private ContentFetcher mFetcher;
    private ArticleRepositoryActions articlesRepo;
    private FeedMeArticle requestedArticle;
    private Category mCategory;
    private int startPage=0;
    private Site[]mSites=null;
    private int pageSizes=1;
    private boolean openArticle;

    public ArticlesListPresenter(@NonNull ArticleRepositoryActions repo, CategoriesRepository catRepo, ContentFetcher fetcher){
        articlesRepo=repo;
        articlesRepo.setListener(this,mSites);
        this.catRepo=catRepo;
        catRepo.getCategories(this);
        setModel(articlesRepo.getArticles(mSites));
        mFetcher=fetcher;

    }
    public ArticlesListPresenter(@NonNull ArticleRepositoryActions repo, CategoriesRepository catRepo, ContentFetcher fetcher,Category category){
        this(repo,catRepo,fetcher);
        mCategory=category;

    }
    @Override
    protected void updateView() {
            view().updateList(model);
    }

    @Override
    public void onPerformDelete(FeedMeArticle feedMeArticle) {

        articlesRepo.removeArticle(feedMeArticle);
        view().showMessage("Article has been deleted", null);
    }

    @Override
    public void onPerformSave(FeedMeArticle feedMeArticle) {
        feedMeArticle.setSaved(true);
        articlesRepo.getFullArticle(feedMeArticle,this);
        view().showMessage("Article has been saved", null);
    }

    @Override
    public void onPerformFav(FeedMeArticle feedMeArticle) {
        view().saveArticleAsWebArchive(feedMeArticle);
        feedMeArticle.setFav(!feedMeArticle.isFav());
        articlesRepo.editArticle(feedMeArticle);
        view().showMessage("Article has been bookmarked", null);
    }

    @Override
    public void onWebArchiveSaved(FeedMeArticle feedMeArticle, String path) {
        feedMeArticle.setWebArchivePath(path);
        articlesRepo.editArticle(feedMeArticle);
    }

    @Override
    public void onOpenArticle(final FeedMeArticle article) {

        if ((article.isSaved() || article.isContentFetched())
                && view() != null) {
            view().showArticle(article, article.getArticle().getContent().isEmpty());
        } else {
            view().showProgress();
            openArticle = true;
            requestedArticle = article;
            NetworkUtils.isInternetAccessible(this);
            articlesRepo.getFullArticle(requestedArticle,this);

        }
    }

    @Override
    public void onCategorySelected(Category item) {
        if(item==null)
            articlesRepo.setListener(this,null);
        else
            articlesRepo.setListener(this,catRepo.getSites(item).toArray(new Site[]{}));
        setModel(articlesRepo.getArticles(catRepo.getSites(item).toArray(new Site[]{})));
    }

    @Override
    public void onViewVisible() {
        catRepo.getCategories(this);
    }

    @Override
    public void internetAvailable(boolean isAvailable) {
        if (!isAvailable)
        {
            if(view()!=null) {
                view().endProgress();
                view().showMessage("No Internet Available Check Internet ",
                        requestedArticle.getArticle().getSource());

            }openArticle=false;
            //onFullArticleFetched(requestedArticle);
        }
    }

    @Override
    public void onDataChanged(ArticlesList data) {
        if(view()!=null){
            view().endProgress();
            if(data.getArticles().size()!=model.getArticles().size())
                view().showMessage("Updates has been made..", null);
        }
        setModel(data);
        Log.d("Presneter","Fuck Data change: "+startPage+ " size= "+data.getArticles().size());

        startPage+=1;
    }

    @Override
    public void bindView(@NonNull ArticleListContract.View view) {
        super.bindView(view);
        view().setInteractor(this);
    }

    @Override
    public void onFullArticleFetched(final FeedMeArticle fetchedArticle) {
        if(openArticle&&view()!=null){
           // view().imageUpdated(fetchedArticle);
            view().endProgress();
            //if getcontent is empty then show the article on web
            view().showArticle(fetchedArticle,!fetchedArticle.isContentFetched()&&
                    (fetchedArticle.getArticle().getContent()==null||fetchedArticle.getArticle().getContent().isEmpty()));


        }
        openArticle=false;
    }


    @Override
    public void categoriesFetched(ArrayList<Category> cats) {
        if(view()!=null)
            view().updateCategoriesSpinner(cats);
    }
}

