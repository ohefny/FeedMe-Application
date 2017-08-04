package com.example.bethechange.feedme.MainScreen.Views;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.bethechange.feedme.ArticleType;
import com.example.bethechange.feedme.Data.CategoriesRepository;
import com.example.bethechange.feedme.Data.SitesRepository;
import com.example.bethechange.feedme.MainScreen.Models.Category;
import com.example.bethechange.feedme.MainScreen.Models.Site;
import com.example.bethechange.feedme.MainScreen.Presenters.MySitesPresenter;
import com.example.bethechange.feedme.MainScreen.ViewContracts.MySitesContract;
import com.example.bethechange.feedme.MainScreen.Views.Adapters.MySiteRecyclerViewAdapter;
import com.example.bethechange.feedme.R;
import com.example.bethechange.feedme.CustomScreen.CustomListActivity;
import com.example.mvpframeworkedited.BasePresenterFragment;
import com.example.mvpframeworkedited.PresenterFactory;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;


public class MySitesFragment extends BasePresenterFragment<MySitesPresenter,MySitesContract.View>
    implements MySitesContract.View{

    private int mColumnCount = 2;
    private RecyclerView mRecyclerView;
    private List<Site> mSites=new ArrayList<>();
    private FragmentActivityInteractor mListener;
    private Category mCategory;
    private AppCompatSpinner mSpinner;
    private ArrayAdapter<Category> mSpinAdapter;
    private MySiteRecyclerViewAdapter mAdapter;
    private MySitesContract.Presenter presenter;
    private ArrayList<Category> cats=new ArrayList<>();
    private Category temp=new Category();
    private View mRootView;
    private ImageView mEmptyView;
    // private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MySitesFragment() {
        temp.setTitle("All");
        cats.add(0,temp);
    }
    public static MySitesFragment newInstance() {
        MySitesFragment fragment = new MySitesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_mysites_list, container, false);
        mSpinner= (AppCompatSpinner) mRootView.findViewById(R.id.categories_spinner_id);
        mSpinAdapter=new ArrayAdapter<Category>(getContext(),R.layout.simple_category_item,cats);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0)
                    presenter.onCategoryChoosed(null);
                else
                presenter.onCategoryChoosed(mSpinAdapter.getItem(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mSpinner.setAdapter(mSpinAdapter);
        setupRecyclerView(mRootView);
        mEmptyView= (ImageView) mRootView.findViewById(R.id.empty_sites_view);
        return mRootView;
    }

    private void setupRecyclerView(View view) {
        mRecyclerView=(RecyclerView)view.findViewById(R.id.mysites_list) ;

        mAdapter=new MySiteRecyclerViewAdapter(mSites,getContext(),presenter);
        DisplayMetrics displayMetrics=new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mColumnCount=calculateNoOfColumns(getContext(),175);
      //  mColumnCount=mColumnCount>getResources().getInteger(R.integer.sites_coulmns_count)?
        //        mColumnCount:getResources().getInteger(R.integer.sites_coulmns_count);
        // Set the adapter
        Context context = view.getContext();
        RecyclerView.LayoutManager layoutManager;
        if (mColumnCount <= 1) {
            layoutManager=new LinearLayoutManager(context);

        } else {
            layoutManager=new GridLayoutManager(getContext(),mColumnCount,LinearLayoutManager.VERTICAL,false);
            //layoutManager =
             //       new StaggeredGridLayoutManager(mColumnCount, StaggeredGridLayoutManager.VERTICAL);
           // SpacesItemDecoration itemDecoration = new SpacesItemDecoration( getResources().getDimensionPixelSize(R.dimen.item_offset));
           // mRecyclerView.addItemDecoration(itemDecoration);
           // mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(),DividerItemDecoration.VERTICAL));

        }

        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setAdapter(mAdapter);//, mListener));

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentActivityInteractor) {
            mListener = (FragmentActivityInteractor) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("MySitesFragment","Resumed");
    }

    @Override
    public void updateList(ArrayList<Site> list) {
        if(list.size()==0){
            mRecyclerView.setVisibility(View.INVISIBLE);
            mEmptyView.setVisibility(View.VISIBLE);
        }
        else{
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.INVISIBLE);
        }

        mAdapter.setSites(list);
        mAdapter.notifyDataSetChanged();

    }

    @Override
    public void updateSpinner(ArrayList<Category> categories) {
        //mSpinAdapter.clear();
        cats.clear();
        cats.add(0,temp);
        cats.addAll(categories);
       // mSpinAdapter.addAll(cats);
        mSpinAdapter.notifyDataSetChanged();
        AddSiteFragment dg = (AddSiteFragment) getFragmentManager().findFragmentByTag("add_site_dialog");
        if (dg != null) {
            dg.setCategories(cats);
        }
    }

    @Override
    public void onCategoryChanged(int id) {

    }

    @Override
    public void showProgress() {

    }

    @Override
    public void endProgress() {

    }

    @Override
    public void showMessage(String str) {
        Snackbar.make(mRootView, str, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showEditDialog(Site site) {
        openDialog(site);
    }

    @Override
    public void openSite(Site site) {
        Intent intent=new Intent(getContext(), CustomListActivity.class);
        intent.putExtra(CustomListActivity.SITE_KEY,new Gson().toJson(site,site.getClass()));
        intent.putExtra(CustomListActivity.TYPE_KEY, ArticleType.SITE);
        startActivity(intent);
    }

    @NonNull
    @Override
    protected String tag() {
        return null;
    }

    @Override
    protected void onPresenterPrepared(@NonNull MySitesPresenter presenter) {
        super.onPresenterPrepared(presenter);
        this.presenter= presenter;
        mAdapter.setListener(presenter);
    }

    @NonNull
    @Override
    protected PresenterFactory<MySitesPresenter> getPresenterFactory() {
        return new PresenterFactory<MySitesPresenter>() {
            @Override
            public MySitesPresenter create() {
                //todo::change null with choosed category
                return new MySitesPresenter(SitesRepository.getInstance(getActivity()),new CategoriesRepository(getContext().getContentResolver()),mCategory);
            }
        };
    }

    public void fabClicked() {
        openDialog(null);
    }

    private void openDialog(Site site) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("add_site_dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        AddSiteFragment newFragment = AddSiteFragment.newInstance(cats,presenter,site);
        newFragment.show(ft, "add_site_dialog");
    }

    interface FragmentActivityInteractor{
    }
    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;

            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildLayoutPosition(view) == 0) {
                outRect.top = space;
            } else {
                outRect.top = 0;
            }
        }
    }
    public static int calculateNoOfColumns(Context context,int colSize) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / colSize);
        return noOfColumns;
    }
}
