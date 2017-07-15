package com.example.bethechange.feedme.MainScreen.Views;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.bethechange.feedme.ArticleType;
import com.example.bethechange.feedme.ArticlesObserver;
import com.example.bethechange.feedme.Data.ArticleRemoteLoader;
import com.example.bethechange.feedme.Data.ArticlesRepository;
import com.example.bethechange.feedme.Data.Contracts;
import com.example.bethechange.feedme.Data.DBUtils;
import com.example.bethechange.feedme.MainScreen.Models.ArticlesList;
import com.example.bethechange.feedme.MainScreen.Models.FeedMeArticle;
import com.example.bethechange.feedme.MainScreen.Presenters.ArticlesListPresenter;
import com.example.bethechange.feedme.MainScreen.ViewContracts.ArticleListContract;
import com.example.bethechange.feedme.R;
import com.example.mvpframeworkedited.BasePresenterFragment;
import com.example.mvpframeworkedited.PresenterFactory;

import java.util.ArrayList;
import java.util.List;


public class TimelineFragment extends BasePresenterFragment<ArticlesListPresenter,ArticleListContract.View>
    implements ArticleListContract.View,LoaderManager.LoaderCallbacks{

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private RecyclerView mRecyclerView;

    private List<FeedMeArticle> mFeedMeArticleList=new ArrayList<>();
    MyArticleRecyclerViewAdapter adapter=new MyArticleRecyclerViewAdapter(mFeedMeArticleList);
    private ArticleRemoteLoader mLoader;

    @Override
    public void setInteractor(ArticleListContract.Presenter interactor) {
        this.interactor = interactor;
    }

    private ArticleListContract.Presenter interactor;
    private int count;



    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TimelineFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static TimelineFragment newInstance(int columnCount) {
        TimelineFragment fragment = new TimelineFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLoader=new ArticleRemoteLoader(getActivity());
        //mLoader.setSites(MainScreenActivity.getSites());

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

    }

    @Override
    protected void onPresenterPrepared(@NonNull ArticlesListPresenter presenter) {
        super.onPresenterPrepared(presenter);


    }

    @Override
    public void onPause() {

        super.onPause();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_article_list, container, false);
        setupRecyclerView(view);


        return view;
    }

    private void setupRecyclerView(View view) {
        // Set the adapter
        adapter=new MyArticleRecyclerViewAdapter(mFeedMeArticleList);
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
             mRecyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                mRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            mRecyclerView.setAdapter(adapter);//, mListener));
        }
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
                    Log.d(TimelineFragment.class.getSimpleName(),"fuck Deleted :: "+pos);
                   // adapter.notifyItemRemoved(pos);
                   // adapter.notifyItemRangeChanged(pos, adapter.getItemCount());
                   // adapter.notifyDataSetChanged();
                }
                //TODO REMOVE FeedMeArticle
            }
        }).attachToRecyclerView(mRecyclerView);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
       /* if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
      //  mListener = null;
    }

    @NonNull
    @Override
    protected String tag() {

        return null;
    }

    @NonNull
    @Override
    protected PresenterFactory<ArticlesListPresenter> getPresenterFactory() {

        return new ArticlesFactory();
    }

    @Override
    public void updateList(ArticlesList articlesList) {
        if(adapter==null){
            adapter=new MyArticleRecyclerViewAdapter(articlesList.getArticles());
            mRecyclerView.setAdapter(adapter);
        }
        else {
            adapter.setListItems(articlesList.getArticles());
            adapter.notifyDataSetChanged();

        }
        //if(count++==0)
        //getActivity().getSupportLoaderManager().initLoader(5,null,this);


    }

    @Override
    public CursorLoader getLoader() {
        return new CursorLoader(getActivity());
    }

    @Override
    public void showProgress() {
        ((MainScreenActivity)getActivity()).showProgressIndicator();
    }

    @Override
    public void endProgress() {
        ((MainScreenActivity)getActivity()).endProgressIndicator();
    }

    @Override
    public void showMessage(String str) {
        Toast.makeText(getContext(),str,Toast.LENGTH_SHORT).show();
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return mLoader;
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {

        System.out.println("Ad");
        Toast.makeText(getContext(),"Articles Downloaded",Toast.LENGTH_SHORT).show();
       // addToDB((ArticlesList) data);
    }
    private void addToDB(ArticlesList ar) {

        List<FeedMeArticle> mList=new ArrayList<>();
        ArrayList<FeedMeArticle> articles=ar.getArticles();
        for(int i = 0; i<5&&i<articles.size(); i++){
            mList.add(articles.get(i));
        }
        ArticlesList mArticleList=new  ArticlesList();
        mArticleList.setArticles(new ArrayList<FeedMeArticle>(mList));
        getContext().getContentResolver().bulkInsert(
                Contracts.ArticleEntry.CONTENT_URI, DBUtils.articlesToCV(mArticleList));
    }
    @Override
    public void onLoaderReset(Loader loader) {

    }


    /* Presenter Factory */
    private class ArticlesFactory implements PresenterFactory<ArticlesListPresenter> {

        ArticlesFactory() {

        }

        @Override
        public ArticlesListPresenter create() {
            return new ArticlesListPresenter(ArticlesRepository.getInstance(getActivity()));
        }
    }


}
