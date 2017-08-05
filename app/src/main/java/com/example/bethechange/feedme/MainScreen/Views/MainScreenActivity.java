package com.example.bethechange.feedme.MainScreen.Views;

import android.animation.ObjectAnimator;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bethechange.feedme.ArticleType;
import com.example.bethechange.feedme.CustomScreen.CustomListActivity;
import com.example.bethechange.feedme.CustomScreen.SearchActivity;
import com.example.bethechange.feedme.Data.ArticlesRepository;
import com.example.bethechange.feedme.Data.CategoriesRepository;
import com.example.bethechange.feedme.Data.SitesRepository;
import com.example.bethechange.feedme.JobManger;
import com.example.bethechange.feedme.MainScreen.Models.Category;
import com.example.bethechange.feedme.MainScreen.Models.Site;
import com.example.bethechange.feedme.MainScreen.Views.Adapters.MainPagesAdapter;
import com.example.bethechange.feedme.R;
import com.example.bethechange.feedme.Services.BackupDataService;
import com.example.bethechange.feedme.SettingsActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MainScreenActivity extends AppCompatActivity implements
        ArticleListFragment.ArticlesActivityInteractor,MySitesFragment.FragmentActivityInteractor, CategoriesRepository.CategoriesListener, NavigationCategoryAdapter.CategoriesListListener {
    private NavigationView mNavigationView;
    private DrawerLayout mDrawer;
    private View mNavHeader;
    private FloatingActionButton mFab;
    private Toolbar mToolbar;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private ProgressBar progressBar;
    private ObjectAnimator animation;
    private WebView webView;
    private MainPagesAdapter mAdapter;
    private int currentPage;
    private ViewPager mViewPager;
    private CategoriesRepository catRepo;
    private RecyclerView navCats;
    private ArrayList<Category> cats;
    private NavigationCategoryAdapter catAdapter;
    private int mId=-1;
    private ImageView profileImg;
    private TextView profileName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        mAdapter=new MainPagesAdapter(getSupportFragmentManager(),this);
        setupViews();
        prepareProgress();
        JobManger.scheduleBackupJob(this);
        JobManger.scheduleRefreshJob(this);

    }

    private void prepareProgress() {
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        animation = ObjectAnimator.ofInt (progressBar, "progress", 0, 500); // see this max value coming back here, we animale towards that value
        animation.setDuration (5000); //in milliseconds
        animation.setInterpolator (new DecelerateInterpolator());
    }


    public static  Site[] getSites() {
        //http://feeds.feedburner.com/TheAtlantic
        //https:no//www.polygon.com/rss/index.xml
        //http://www.coolhunting.com/atom.xml
        //http://www.betterlivingthroughdesign.com/feed
        //http://rss.cnn.com/rss/cnn_topstories.rss
        //http://www.washingtonpost.com/rss/
        //http://feeds.reuters.com/reuters/topNews
        //http://newsrss.bbc.co.uk/rss/newsonline_world_edition/americas/rss.xml
        String url = "http://stackoverflow.com/feeds/tag?tagnames=rome";

        Site site1=new Site();
        site1.setTitle("BBC Top Stories");
        site1.setUrl("bbc.com");
        site1.setRssUrl("http://newsrss.bbc.co.uk/rss/newsonline_world_edition/americas/rss.xml");
        site1.setCategoryID(1);
        site1.setmImgSrc("http://m.files.bbci.co.uk/modules/bbc-morph-news-waf-page-meta/1.2.0/bbc_news_logo.png?cb=1");
        Site site2=new Site();
        site2.setTitle("Washington Post: Today's Highlights");
        site2.setUrl("washingtonpost.com");
        site2.setRssUrl("http://feeds.washingtonpost.com/rss/politics");
        site2.setmImgSrc("http://www.jayheinz.com/wp-content/themes/synthetik/functions/timthumb.php?src=http://jayheinz.com/wp-content/uploads/2010/08/WashPost.jpg&h=290&w=580&zc=1");
        site2.setCategoryID(1);
        Site site3=new Site();
        site3.setTitle("Cool Hunting");
        site3.setUrl("coolhunting.com");
        site3.setRssUrl("http://www.coolhunting.com/atom.xml");
        site3.setmImgSrc("http://www.flat33.com/upload/CoolHuntingLogo_c_400.jpg");
        site3.setCategoryID(1);
        Site site4=new Site();
        site4.setTitle("Better Living Through Design");
        site4.setUrl("betterlivingthroughdesign.com");
        site4.setRssUrl("http://www.betterlivingthroughdesign.com/feed");
        site4.setmImgSrc("https://cdn.shopify.com/s/files/1/0156/3912/files/Better_Living_Through_Design.jpg");
        site4.setCategoryID(1);
        Site[]sites={site1,site3,site2,site4};//,site2};//,site4};
        return sites;
    }

    private void setupViews() {
        navCats=(RecyclerView)findViewById(R.id.categories_nav_list);
        profileImg=(ImageView)findViewById(R.id.img_profile);
        profileName=(TextView)findViewById(R.id.profile_name);
        profileName.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        Picasso.with(this).load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()).into(profileImg);
        TabLayout mSlidingTabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        mViewPager = (ViewPager)findViewById(R.id.viewpager);
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPage=position;
                if(position==1)
                    mFab.setImageResource(R.drawable.ic_fab_add);
                else if(position==0) {
                    mFab.setImageResource(R.drawable.ic_arrow_up);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mSlidingTabLayout.setupWithViewPager(mViewPager);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawer.closeDrawers();
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (currentPage){
                    case 0:
                        ((ArticleListFragment)mAdapter.getCurrentFragment()).fabClicked();
                        break;
                    case 1:
                        ((MySitesFragment)mAdapter.getCurrentFragment()).fabClicked();
                        break;


                }


            }
        });
        setUpNavigationView();
    }
    private void setUpNavigationView() {
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);



                return true;
            }
        });


         mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };


        //Setting the actionbarToggle to drawer layout
        mDrawer.setDrawerListener(mActionBarDrawerToggle);
        mDrawer.closeDrawers();
        mActionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        //calling sync state is necessary or else your hamburger icon wont show up
        mActionBarDrawerToggle.syncState();
      /*  AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);*/
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.fetch_news){
            ArticlesRepository.getInstance(this).getLatestArticles();
            return true;
        }
        if(item.getItemId()==R.id.settings){
            Intent intent=new Intent(this,SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return mActionBarDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        // show menu only when home fragment is selected
            getMenuInflater().inflate(R.menu.main, menu);
            final SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
            // Get the SearchView and set the searchable configuration
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            // Assumes current activity is the searchable activity
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Bundle appData = new Bundle();
                appData.putInt(SearchActivity.FROM_TYPE, ArticleType.CATEGORY);
                appData.putInt(SearchActivity.FROM_ID,mId);
                searchView.setAppSearchData(appData);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
            //searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
            return super.onCreateOptionsMenu(menu);
    }


    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawers();
            return;
        }


        super.onBackPressed();
    }
    public FloatingActionButton getFab() {return mFab;}



    @Override
    protected void onStart() {
        ArticlesRepository.getInstance(this);
        catRepo = new CategoriesRepository(getContentResolver());
        cats=catRepo.getCategories(this);
        catAdapter=new NavigationCategoryAdapter(cats,this);
        navCats.setAdapter(catAdapter);
        navCats.setLayoutManager(new LinearLayoutManager(this));
        super.onStart();
    }

    @Override
    protected void onStop() {
        ArticlesRepository.destroyInstance(this);
        SitesRepository.destroyInstance(this);
        super.onStop();
    }

    @Override
    public void openWebViewFragment(String link) {
        if(!(link.contains("https://")||link.contains("http://"))){
            link="http://"+link;
        }
        Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        startActivity(intent);
    }

    @Override
    public void onCategoryChanged(int id) {
        mId=id;
    }

    public void onSitesClicked(View view) {
        mDrawer.closeDrawers();
        mViewPager.setCurrentItem(1);
        mDrawer.closeDrawers();
        Toast.makeText(this,"Sites Clicked",Toast.LENGTH_SHORT).show();
    }

    public void onFavClicked(View view) {
        mDrawer.closeDrawers();
        Intent intent=new Intent(this, CustomListActivity.class);
        intent.putExtra(CustomListActivity.TYPE_KEY, ArticleType.BOOKMARKED);
        startActivity(intent);
        Toast.makeText(this,"Fav Clicked",Toast.LENGTH_SHORT).show();
    }
    public void onSavedPostsClicked(View view) {
        mDrawer.closeDrawers();
        Intent intent=new Intent(this, CustomListActivity.class);
        intent.putExtra(CustomListActivity.TYPE_KEY, ArticleType.SAVED);
        startActivity(intent);
        Toast.makeText(this,"Saved Clicked",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void categoriesFetched(ArrayList<Category> cats) {
            this.cats=cats;
            if(catAdapter!=null){
                catAdapter.setCategories(cats);
                catAdapter.notifyDataSetChanged();
            }

    }
    @Override
    public boolean onSearchRequested() {
        Bundle appData = new Bundle();
        appData.putInt(SearchActivity.FROM_TYPE, ArticleType.CATEGORY);
        appData.putInt(SearchActivity.FROM_ID,mId);
        startSearch(null, false, appData, false);
        super.onSearchRequested();
        return true;
    }


    public void onAddCategory(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.add_category_title));


        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton(getString(R.string.add_btn_text), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO::NOTIFY TIMELINE AND SITE WITH CHANGES
                Category cat=new Category();
                cat.setTitle(input.getText().toString());
                catRepo.addCategory(cat);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(getString(R.string.cancel_btn_text), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    @Override
    public void onCategoryClicked(int position) {
        mViewPager.setCurrentItem(0);
        ((ArticleListFragment)mAdapter.getCurrentFragment()).setCategory(cats.get(position));
        mDrawer.closeDrawers();
    }

    @Override
    public void onDeleteCategory(final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.delete_category_title));
        builder.setMessage(getString(R.string.delete_category_msg));
        builder.setPositiveButton(getString(R.string.delete_category_pos_btn), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                catRepo.deleteCategory(cats.get(position));
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(getString(R.string.cancel_btn_text), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });
        builder.show();


    }

    @Override
    public void onEditCategory(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.edit_category_title));


        final EditText input = new EditText(this);
        input.setText(cats.get(position).getTitle());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton(getString(R.string.edit_category_pos_btn), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Category cat=new Category();
                cat.setTitle(input.getText().toString());
                catRepo.editCategory(cat);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(getString(R.string.cancel_btn_text), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                dialog.dismiss();
            }
        });

        builder.show();
    }


    public void onBackupClicked(View view) {
        Snackbar.make(view,getString(R.string.performing_backup),Snackbar.LENGTH_SHORT).show();
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawers();
            return;
        }
        BackupDataService.startActionBackup(this);
    }

    public void onSettingsClicked(View view) {
        mDrawer.closeDrawers();
        Intent intent=new Intent(this,SettingsActivity.class);
        startActivity(intent);
    }
}
