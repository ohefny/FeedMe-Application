package com.example.bethechange.feedme.DetailsScreen;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.content.ContentUris;
import android.database.Cursor;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.example.bethechange.feedme.Data.ArticlesRepository;
import com.example.bethechange.feedme.Data.Contracts;
import com.example.bethechange.feedme.Data.DBUtils;
import com.example.bethechange.feedme.MainScreen.Models.FeedMeArticle;
import com.example.bethechange.feedme.MainScreen.Models.Site;
import com.example.bethechange.feedme.R;
import com.example.mvpframeworkedited.BasePresenterActivity;
import com.example.mvpframeworkedited.PresenterFactory;
import com.google.gson.Gson;
import com.pkmmte.pkrss.Article;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class DetailsActivity extends BasePresenterActivity<DetailsPresenter,DetailsContract.DetailsView>
        implements ArticleDetailFragment.OnPageActions,DetailsContract.DetailsView{

    public static final String ARTICLE_KEY ="ARTICLE_KEY" ;
    public static final String ARTICLE_ID_KEY = "ARTICLE_ID_KEY";
    private FeedMeArticle feedmeArticle;
    private ArticlesRepository repo;
    private int startingPos=0;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private DetailsContract.DetailsPresenter interactor;
    private Site[]sites=null;
    private ArrayList<WeakReference<ArticleDetailFragment>> fragments=new ArrayList<>();
    public void setInteractor(DetailsContract.DetailsPresenter interactor) {
        this.interactor = interactor;
    }
    //TODO GET SITES FROM INTENT
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);
        repo = ArticlesRepository.getInstance(this);
        int arId=-1;
        if(savedInstanceState!=null)
             arId= getIntent().getExtras().getInt(ARTICLE_ID_KEY,-1);
        else if (getIntent() != null)
             arId = getIntent().getExtras().getInt(ARTICLE_ID_KEY,-1);

        if (arId != -1){
            startingPos=repo.getArticleIndex(arId);

        }


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(ARTICLE_ID_KEY,startingPos);
        super.onSaveInstanceState(outState);
    }

    private void setupViews() {
        mPagerAdapter=new DetailsAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);
        mPager.setPageMargin((int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));

        mPager.setCurrentItem(startingPos);
    }

    @Override
    protected void onStart() {
        ArticlesRepository.getInstance(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        ArticlesRepository.destroyInstance(this);
        super.onStop();
    }

    @Override
    protected void onPresenterPrepared(@NonNull DetailsPresenter presenter) {
        super.onPresenterPrepared(presenter);
        setupViews();
    }

    @NonNull
    @Override
    protected String tag() {
        return null;
    }

    @NonNull
    @Override
    protected PresenterFactory<DetailsPresenter> getPresenterFactory() {
        return new DetailsFactory();
    }



    @Override
    public void onOpenBrowser(FeedMeArticle feedMeArticle) {
        String link=feedMeArticle.getArticle().getSource().toString();
        if(!(link.contains("https://")||link.contains("http://"))){
            link="http://"+link;
        }
        Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        startActivity(intent);
    }

    @Override
    public ArticlesRepository getRepo() {
        return repo;
    }

    @Override
    public void showMessage(String str) {

    }

    @Override
    public void sizeChanged(int size) {
        mPagerAdapter.notifyDataSetChanged();
    }

    @Override
    protected void setPrepared(boolean prepared) {
        super.setPrepared(prepared);
        if(prepared) {
            interactor = getPresenter();
        }
    }



    private class DetailsAdapter extends FragmentStatePagerAdapter {
         DetailsAdapter(FragmentManager fm) {
            super(fm);
         }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, 0, object);

            //mCurrentDetailsFragment=fragment;
            //if (fragment != null) {
            //mSelectedItemUpButtonFloor = fragment.getUpButtonFloor();
            //updateUpButtonPosition();
            //}
        }

        @Override
        public ArticleDetailFragment getItem(int position) {

            return  ArticleDetailFragment.newInstance(interactor.getArticleID(position));
            // return mCurrentDetailsFragment;
        }

        @Override
        public int getCount() {
            return (interactor.getItemsCount()) ;
        }
    }


    private class DetailsFactory implements PresenterFactory<DetailsPresenter> {
        @Override
        public DetailsPresenter create() {
            //TODO: this null sites should be replaced with sites passed by mainactivity
            DetailsPresenter pres= new DetailsPresenter(repo,sites);
            setInteractor(pres);
            return pres;
        }
    }
}
