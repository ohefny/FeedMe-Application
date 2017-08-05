package com.example.bethechange.feedme.DetailsScreen;

import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bethechange.feedme.CustomAspectImage;
import com.example.bethechange.feedme.Data.ArticlesRepository;
import com.example.bethechange.feedme.MainScreen.Models.FeedMeArticle;
import com.example.bethechange.feedme.R;
import com.example.mvpframeworkedited.BasePresenterFragment;
import com.example.mvpframeworkedited.PresenterFactory;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.bethechange.feedme.MainScreen.Views.ArticleListFragment.calculateNoOfColumns;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ArticleDetailFragment.OnPageActions} interface
 * to handle interaction events.
 * Use the {@link ArticleDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ArticleDetailFragment extends BasePresenterFragment<ArticleItemPresenter,DetailsContract.ItemView>
implements DetailsContract.ItemView{

    private static final String ARTICLE_ID ="ARTICLE_ID" ;
    private OnPageActions mListener;
    private FeedMeArticle feedMeArticle;
    private View mRootView;
    private CustomAspectImage mPhotoView;
    private Toolbar mToolbar;
    private AppBarLayout mAppBarLayout;
    private CollapsingToolbarLayout mCollapssingToolbar;
    private TextView bodyView;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private TextView byLineTv;
    private ProgressDialog dialog;
    private boolean viewBound=false;
    int article_id;
    private DetailsContract.ItemPresenter mInteractor;
    private ObjectAnimator animation;
    private boolean mVisible;
    private CountDownTimer cd;
    private ImageView erroView;

    public ArticleDetailFragment() {
        // Required empty public constructor
    }
    public static ArticleDetailFragment newInstance(int id) {
        ArticleDetailFragment fragment = new ArticleDetailFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        fragment.setArticleId(id);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRetainInstance(true);
        Log.d("OnCreate",this.hashCode()+"");
        if(savedInstanceState!=null){
            article_id=savedInstanceState.getInt(ARTICLE_ID);
        }
        dialog = new ProgressDialog(getActivity());//,R.style.MyProgressBar);
        dialog.setCancelable(true);
        dialog.setMessage("Fetching Article");
        //dialog.setMessage("Loading Your Screen");
        dialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                showError();
            }
        });


    }

    @NonNull
    private CountDownTimer getCountDownTimer() {
        return new CountDownTimer(15000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onFinish() {
                if(getActivity()!=null)
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            endProgress();
                        }
                    });
            }
        };
    }

    @Override
    public void showProgress() {
        if(!dialog.isShowing()) {
            dialog.show();
            cd=getCountDownTimer();
            cd.start();
        }

    }

    @Override
    public void endProgress() {
        if(dialog.isShowing()){
            dialog.dismiss();
            cd.cancel();
            showError();
        }

    }

    private void showError() {
        if(!feedMeArticle.isContentFetched()){
            bodyView.setText(getResources().getString(R.string.not_available));
            erroView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showMessage(String str, final Uri source) {
        //TODO Replace with snackbar
        Snackbar mySnackbar = Snackbar.make(mRootView, str, Snackbar.LENGTH_LONG);
        mySnackbar.setAction(getString(R.string.open_browser), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onOpenBrowser(source);
            }
        });
        mySnackbar.show();
        //Toast.makeText(getContext(),s,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(ARTICLE_ID,article_id);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        Log.d("DetailsFrag","OnStart and view bound = "+viewBound);
        if (feedMeArticle != null&&!viewBound)
            bindArticleToViews();
        super.onStart();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        mVisible=isVisibleToUser;
        if(mInteractor!=null&&isVisibleToUser){
            mInteractor.isVisible(true);
        }
        else if(mInteractor!=null&&!isVisibleToUser){
            mInteractor.isVisible(false);
        }
        if(feedMeArticle!=null)
            Log.d("VISIBLE",feedMeArticle.getArticle().getTitle());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("OnCreateView",this.hashCode()+"");
        mRootView = inflater.inflate(R.layout.fragment_article_detail, container, false);
        mPhotoView=(CustomAspectImage)mRootView.findViewById(R.id.photo);
        Log.d("OnCreateView",calculateNoOfColumns(getContext(),mPhotoView.getWidth())+" ff width");
        Log.d("OnCreateView",calculateNoOfColumns(getContext(),mPhotoView.getHeight())+" ff height");

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            PortraitViewSetup(); 
        mToolbar = (Toolbar) this.mRootView.findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        mRootView.findViewById(R.id.share_fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText(feedMeArticle.getArticle().getDescription()+"\n\n"+feedMeArticle.getArticle().getSource().toString())
                        .getIntent(), getString(R.string.action_share)));
            }
        });
        mToolbar.inflateMenu(R.menu.article_options);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.item_save:
                        mInteractor.onPerformSave(feedMeArticle);
                        break;
                    case R.id.item_browser:
                        mListener.onOpenBrowser(feedMeArticle.getArticle().getSource());
                        break;
                    case R.id.item_fav:
                        mInteractor.onPerformFav(feedMeArticle);
                        break;
                    case R.id.item_read:
                        mInteractor.onPerformDelete(feedMeArticle);
                        break;
                }



                return true;
            }
        });
        mAppBarLayout= (AppBarLayout) mRootView.findViewById(R.id.app_bar_layout);
        bodyView = (TextView) mRootView.findViewById(R.id.article_body);
        erroView=(ImageView)mRootView.findViewById(R.id.error_img);

       /* if(savedInstanceState!=null&&savedInstanceState.getBoolean(DATA_LOADED)){
            mBody=savedInstanceState.getString(BODY_KEY);
            byLine=savedInstanceState.getString(BY_KEY);
            title=savedInstanceState.getString(TITLE_KEY);
            mImgUrl=savedInstanceState.getString(IMG_KEY);
            data_loaded=savedInstanceState.getBoolean(DATA_LOADED);
            setMetaData();


        }
        else*/

        // updateStatusBar();
        if (feedMeArticle != null&&!viewBound)
            bindArticleToViews();
        return mRootView;
        
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getWindow()
                .getAttributes().windowAnimations = R.style.DetailsFragmentAnimation;
    }

    private void bindArticleToViews() {
        if (mRootView == null) {
            return;
        }


        if (feedMeArticle != null) {
            Picasso.with(getContext()).load(feedMeArticle.getArticle().getImage()).into(
                    mPhotoView);
            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
                byLineTv=((TextView)mRootView.findViewById(R.id.byLine));
                String date= getDateFormatted();
                byLineTv.setText(feedMeArticle.getArticle().getAuthor()+" "+ getDateFormatted());
                //mCollapssingToolbar=((CollapsingToolbarLayout) this.mRootView.findViewById(R.id.collapsing_toolbar_layout));
                mCollapssingToolbar.setTitle(feedMeArticle.getArticle().getTitle());

            }
            else{
                mToolbar.setSubtitle(feedMeArticle.getArticle().getAuthor()+" "+ getDateFormatted());
                mToolbar.setTitle(feedMeArticle.getArticle().getTitle());
            }


            //bodyView.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "Rosario-Regular.ttf"));
            if(feedMeArticle!=null&&feedMeArticle.isContentFetched()&&feedMeArticle.getArticle().getContent()!=null) {
                String body=android.text.Html.fromHtml(feedMeArticle.getArticle().getContent()).toString();
                bodyView.setText(body);
                erroView.setVisibility(View.INVISIBLE);
                viewBound=true;
            }

        }



    }

    private void PortraitViewSetup() {
        mCollapssingToolbar = (CollapsingToolbarLayout) this.mRootView.findViewById(R.id.collapsing_toolbar_layout);
        mPhotoView.setAspectRatio(3,4);
    }

    @Override
    protected void onPresenterPrepared(@NonNull ArticleItemPresenter presenter) {
        super.onPresenterPrepared(presenter);
        mInteractor=presenter;
        if(mVisible)
            mInteractor.isVisible(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPageActions) {
            mListener = (OnPageActions) context;

        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        viewBound=false;
        super.onDetach();
        mListener = null;
    }
    @Override
    public void setFeedMeArticle(FeedMeArticle feedMeArticle) {
        this.feedMeArticle = feedMeArticle;
        if(mRootView!=null&&!viewBound)
            bindArticleToViews();
    }



    public String getDateFormatted() {
        Date date=new Date(feedMeArticle.getArticle().getDate());
        return dateFormat.format(date);

    }

    public void onOpenInBrowser(View view) {
        if(feedMeArticle==null||feedMeArticle.getArticle().getContent()==null
                ||feedMeArticle.getArticle().getContent().isEmpty()){
            String link=feedMeArticle.getArticle().getSource().toString();
            if(!(link.contains("https://")||link.contains("http://"))){
                link="http://"+link;
            }
            Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            startActivity(intent);
        }
    }
    public int getArticleId() {
        return article_id;
    }

    public void setArticleId(int id) {
        this.article_id = id;
    }

    @Override
    public void saveArticleAsWebArchive(final FeedMeArticle article) {
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
                        mInteractor.onWebArchiveSaved(article,value);
                    }
                });


            }
        });
    }

    @NonNull
    @Override
    protected String tag() {
        return null;
    }

    @NonNull
    @Override
    protected PresenterFactory<ArticleItemPresenter> getPresenterFactory() {
        return new PresenterFactory<ArticleItemPresenter>() {
            @Override
            public ArticleItemPresenter create() {
                return new ArticleItemPresenter(mListener.getRepo(),article_id);
            }
        };
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
     interface OnPageActions{
        void onOpenBrowser(Uri link);

        ArticlesRepository getRepo();
    }
}
