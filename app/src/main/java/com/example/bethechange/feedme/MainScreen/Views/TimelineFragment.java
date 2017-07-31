package com.example.bethechange.feedme.MainScreen.Views;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.CursorLoader;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.bethechange.feedme.ArticleType;
import com.example.bethechange.feedme.CustomScreen.CustomListActivity;
import com.example.bethechange.feedme.CustomScreen.SearchModel;
import com.example.bethechange.feedme.Data.ArticlesRepository;
import com.example.bethechange.feedme.Data.CategoriesRepository;
import com.example.bethechange.feedme.Data.ContentFetcher;
import com.example.bethechange.feedme.Data.SitesRepository;
import com.example.bethechange.feedme.DetailsScreen.DetailsActivity;
import com.example.bethechange.feedme.MainScreen.ArticlesFactory;
import com.example.bethechange.feedme.MainScreen.Models.ArticlesList;
import com.example.bethechange.feedme.MainScreen.Models.Category;
import com.example.bethechange.feedme.MainScreen.Models.FeedMeArticle;
import com.example.bethechange.feedme.MainScreen.Models.Site;
import com.example.bethechange.feedme.MainScreen.Presenters.ArticlesListPresenter;
import com.example.bethechange.feedme.MainScreen.ViewContracts.ArticleListContract;
import com.example.bethechange.feedme.MainScreen.Views.Adapters.MyArticleRecyclerViewAdapter;
import com.example.bethechange.feedme.R;
import com.example.mvpframeworkedited.BasePresenterFragment;
import com.example.mvpframeworkedited.PresenterFactory;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class TimelineFragment extends BasePresenterFragment<ArticlesListPresenter,ArticleListContract.View>
    implements ArticleListContract.View,MyArticleRecyclerViewAdapter.ArticleListItemListener{

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private RecyclerView mRecyclerView;
    private List<FeedMeArticle> mFeedMeArticleList=new ArrayList<>();
    MyArticleRecyclerViewAdapter adapter=
            new MyArticleRecyclerViewAdapter(mFeedMeArticleList,getContext(),this);
    private ArticlesActivityInteractor listener;
    private ArticleListContract.Presenter interactor;
    private ProgressDialog dialog;
    private View mRootView;
    private AppCompatSpinner mSpinner;
    private ArrayAdapter<Category> mSpinAdapter;
    private Category temp=new Category();
    @ArticleType int type=ArticleType.CATEGORY;
    private Site mSite;
    private Category mCategory;
    private SearchModel model;

    public TimelineFragment() {
    }

    public static TimelineFragment newInstance(int columnCount, ArticlesActivityInteractor lis, @ArticleType int type) {
        TimelineFragment fragment = new TimelineFragment();
        fragment.setListener(lis);
        fragment.setType(type);
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }
    public static TimelineFragment newInstance(int columnCount, ArticlesActivityInteractor lis, @ArticleType int type,Site site) {
        TimelineFragment fragment=newInstance(columnCount,lis,type);
        fragment.setSite(site);
        return fragment;
    }
    public static TimelineFragment newInstance(int columnCount, ArticlesActivityInteractor lis, @ArticleType int type, SearchModel model) {
        TimelineFragment fragment=newInstance(columnCount,lis,type);
        fragment.setModel(model);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        temp.setTitle("All");
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
        prepareProgress();


    }

    private void prepareProgress() {
        dialog = new ProgressDialog(getActivity());//,R.style.MyProgressBar);
        dialog.setCancelable(false);
        dialog.setMessage("Fetching The Article");
        dialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
    }

    public void fragmentVisible(){
        if(interactor!=null)
            interactor.onViewVisible();
    }
    @Override
    protected void onPresenterPrepared(@NonNull ArticlesListPresenter presenter) {
        super.onPresenterPrepared(presenter);
        interactor=presenter;


    }

    @Override
    public void onPause() {

        super.onPause();

    }
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(type==ArticleType.CATEGORY) {
            mRootView = inflater.inflate(R.layout.fragment_article_list, container, false);
            mSpinner = (AppCompatSpinner) mRootView.findViewById(R.id.categories_spinner_id);
            mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0)
                        interactor.onCategorySelected(null);
                    else{
                        interactor.onCategorySelected(mSpinAdapter.getItem(position));
                        listener.onCategoryChanged(position);
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            mSpinner.setAdapter(mSpinAdapter);
        }
        else{
            mRootView = inflater.inflate(R.layout.fragment_site, container, false);
        }

        setupRecyclerView(mRootView);
        return mRootView;
    }

    private void setupRecyclerView(View view) {
        // Set the adapter
        adapter=new MyArticleRecyclerViewAdapter(mFeedMeArticleList,getContext(),this);
        Context context = view.getContext();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.articles_recycler);
        if (mColumnCount <= 1) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        mRecyclerView.setAdapter(adapter);//, mListener));

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                if(direction==ItemTouchHelper.RIGHT){
                    int pos=viewHolder.getAdapterPosition();
                    interactor.onPerformDelete(adapter.getListItems().get(pos));
                    //mRecyclerView.removeViewAt(pos);
                   // adapter.notifyItemRemoved(pos);
                   // adapter.notifyItemRangeChanged(pos, adapter.getItemCount());
                   // adapter.notifyDataSetChanged();
                }
            }
        }).attachToRecyclerView(mRecyclerView);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ArticlesActivityInteractor) {
             listener = (ArticlesActivityInteractor) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @NonNull
    @Override
    protected String tag() {

        return null;
    }

    @NonNull
    @Override
    protected PresenterFactory<ArticlesListPresenter> getPresenterFactory() {

        return new ArticlesFactory(type,getActivity(),model,mCategory,mSite);
    }

    @Override
    public void updateList(ArticlesList articlesList) {
        if(adapter==null){
            adapter=new MyArticleRecyclerViewAdapter(articlesList.getArticles(),getContext(),this);
            mRecyclerView.setAdapter(adapter);
        }
        else {
            adapter.setListItems(articlesList.getArticles());
            adapter.notifyDataSetChanged();

        }
        //if(count++==0)
        //getActivity().getSupportLoaderManager().initLoader(5,null,this);


    }

    private void performWebSave(final FeedMeArticle article) {
        final WebView webView= new WebView(getContext());
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                String folderName = getContext().getFilesDir().getAbsolutePath() + "/cachedFiles/";
                File folder = new File(folderName);
                if (!folder.exists()) {
                    if(!folder.mkdir())
                        return;
                }
                view.saveWebArchive(folderName+article.getArticleID(), false, new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        interactor.onWebArchiveSaved(article,value);
                    }
                });


            }
        });
    }

    @Override
    public void showProgress() {
        if(!dialog.isShowing())
            dialog.show();
    }

    @Override
    public void endProgress() {
        if(dialog.isShowing())
            dialog.dismiss();
    }

    @Override
    public void showMessage(String str, final Uri source) {
        Snackbar mySnackbar = Snackbar.make(mRootView, str, Snackbar.LENGTH_LONG);
        if(source!=null)
        mySnackbar.setAction("Open Browser", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.openWebViewFragment(source.toString());
            }
        });
        mySnackbar.show();
    }

    @Override
    public void saveArticleAsWebArchive(FeedMeArticle feedMeArticle) {
        performWebSave(feedMeArticle);
    }


    @Override
    public void onSaveClicked(FeedMeArticle article) {

        interactor.onPerformSave(article);
    }

    @Override
    public void onDeleteClicked(FeedMeArticle article) {
        interactor.onPerformDelete(article);
    }

    @Override
    public void onBookmarkClicked(FeedMeArticle article) {
        interactor.onPerformFav(article);
    }

    @Override
    public void onArticleOpened(FeedMeArticle article,int pos) {
        interactor.onOpenArticle(article);
    }

    @Override
    public void onSiteTitleClicked(Site site) {
        Intent intent=new Intent(getContext(), CustomListActivity.class);
        intent.putExtra(CustomListActivity.SITE_KEY,new Gson().toJson(site,site.getClass()));
        intent.putExtra(CustomListActivity.TYPE_KEY, ArticleType.SITE);
        startActivity(intent);
    }

    @Override
    public void onSnippetClicked(FeedMeArticle article) {

    }
    public void setType(int type) {
        this.type = type;
    }


    @Override
    public void setInteractor(ArticleListContract.Presenter interactor) {
        this.interactor = interactor;
    }

    @Override
    public void showArticle(FeedMeArticle article, boolean onWebView) {
        Log.d("Fuck Show Article",article.getArticle().getSource().toString());
        if(onWebView)
            listener.openWebViewFragment(article.getArticle().getSource().toString());
        else{
            Intent intent=new Intent(getContext(), DetailsActivity.class);
            int id=article.getArticleID();
            intent.putExtra(DetailsActivity.ARTICLE_ID_KEY,id);
            intent.putExtra(DetailsActivity.ARTICLES_IDS,interactor.getArticlesIds());
            startActivity(intent);
        }



        //getActivity().startActivity(intent);
    }

    @Override
    public void imageUpdated(FeedMeArticle article) {
       // adapter.getListItems().get(position).getArticle().setImage(article.getArticle().getImage());
       // adapter.notifyItemChanged(position);
    }

    @Override
    public void updateCategoriesSpinner(ArrayList<Category> cats) {
        if(mSpinAdapter==null){
            cats.add(0, temp);
            mSpinAdapter=new ArrayAdapter<Category>(getContext(),R.layout.simple_category_item,cats);
            mSpinner.setAdapter(mSpinAdapter);
        }
        else {
            mSpinAdapter.clear();
            cats.add(0, temp);
            mSpinAdapter.addAll(cats);
            mSpinAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void deleteWebArchive(FeedMeArticle feedMeArticle) {
        File file=new File(feedMeArticle.getWebArchivePath());
        file.delete();
    }

    public void setListener(ArticlesActivityInteractor listener) {
        this.listener = listener;
    }

    public void fabClicked() {
        mRecyclerView.smoothScrollToPosition(0);
    }

    public void setSite(Site site) {
        this.mSite = site;
    }

    public void setCategory(Category category) {
        this.mCategory = category;
        mSpinner.setSelection(mSpinAdapter.getPosition(category));
    }


    public void setModel(SearchModel model) {
        this.model = model;
    }


    public interface ArticlesActivityInteractor {
        void openWebViewFragment(String link);
        void onCategoryChanged(int id);
    }



}
