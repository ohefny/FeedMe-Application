package com.example.bethechange.feedme.MainScreen.Presenters;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.bethechange.feedme.ArticleType;
import com.example.bethechange.feedme.Data.ArticleRepositoryActions;
import com.example.bethechange.feedme.Data.ArticlesRepository;
import com.example.bethechange.feedme.Data.ContentFetcher;
import com.example.bethechange.feedme.MainScreen.Models.ArticlesList;
import com.example.bethechange.feedme.MainScreen.Models.FeedMeArticle;
import com.example.bethechange.feedme.MainScreen.Models.Site;
import com.example.bethechange.feedme.MainScreen.ViewContracts.ArticleListContract;
import com.example.bethechange.feedme.Utils.NetworkUtils;
import com.example.mvpframeworkedited.BasePresenter;

/**
 * Created by BeTheChange on 7/10/2017.
 */

public class ArticlesListPresenter extends BasePresenter<ArticlesList,ArticleListContract.View>
    implements ArticleListContract.Presenter, ArticlesRepository.ArticlesRepositoryObserver, NetworkUtils.InternetWatcher {
    private ContentFetcher mFetcher;
    private ArticleRepositoryActions mRepo;
    private FeedMeArticle requestedArticle;
    @ArticleType int mArticleClass;
    private int startPage=0;
    private Site[]mSites=null;
    private int pageSizes=1;
    private boolean openArticle;

    public ArticlesListPresenter(@ArticleType int articleClass,
                                 @NonNull ArticleRepositoryActions repo ){
        this(articleClass);
        mRepo=repo;
        mRepo.setListener(this,mSites);
        setModel(mRepo.getArticles(mSites));

    }
    public ArticlesListPresenter(@NonNull ArticleRepositoryActions repo, ContentFetcher fetcher){
        mRepo=repo;
        mRepo.setListener(this,mSites);
        setModel(mRepo.getArticles(mSites));
        mFetcher=fetcher;

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
        view().showMessage("Article has been deleted", requestedArticle.getArticle().getSource());
    }

    @Override
    public void onPerformSave(FeedMeArticle feedMeArticle) {
        feedMeArticle.setSaved(true);
        mRepo.getFullArticle(feedMeArticle,this);
        view().showMessage("Article has been saved", requestedArticle.getArticle().getSource());
    }

    @Override
    public void onPerformFav(FeedMeArticle feedMeArticle) {
        view().saveArticleAsWebArchive(feedMeArticle);
        feedMeArticle.setFav(!feedMeArticle.isFav());
        mRepo.editArticle(feedMeArticle);
        view().showMessage("Article has been bookmarked", requestedArticle.getArticle().getSource());
    }

    @Override
    public void onWebArchiveSaved(FeedMeArticle feedMeArticle, String path) {
        feedMeArticle.setWebArchivePath(path);
        mRepo.editArticle(feedMeArticle);
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
            mRepo.getFullArticle(requestedArticle,this);

        }
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
        setModel(data);
        Log.d("Presneter","Fuck Data change: "+startPage+ " size= "+data.getArticles().size());
        if(view()!=null){
            view().endProgress();
            view().showMessage("Database Updated..", requestedArticle.getArticle().getSource());
        }
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
}

