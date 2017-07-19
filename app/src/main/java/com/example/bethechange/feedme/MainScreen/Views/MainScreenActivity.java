package com.example.bethechange.feedme.MainScreen.Views;

import android.animation.ObjectAnimator;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.bethechange.feedme.Data.ArticlesRepository;
import com.example.bethechange.feedme.Data.FeedMeDBHelper;
import com.example.bethechange.feedme.MainScreen.Models.Site;
import com.example.bethechange.feedme.MainScreen.Views.Adapters.MainPagesAdapter;
import com.example.bethechange.feedme.R;

import java.net.URL;

public class MainScreenActivity extends AppCompatActivity implements  TimelineFragment.FragmentActivityInteractor{
    private NavigationView mNavigationView;
    private DrawerLayout mDrawer;
    private View mNavHeader;
    private FloatingActionButton mFab;
    private Toolbar mToolbar;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private boolean mSearchActivity=false;
    private ProgressBar progressBar;
    private ObjectAnimator animation;
    private WebView webView;

    //TODO if seprate search activity remove this boolean and it's usage
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        setupViews();
        FeedMeDBHelper dbHelper=new FeedMeDBHelper(this);
        SQLiteDatabase wdb = dbHelper.getWritableDatabase();
      //  wdb.execSQL("ALTER TABLE Article_Table ADD content_fetched BOOLEAN  ;");
       // wdb.execSQL("ALTER TABLE Article_Table ADD PUBLISHED_DATE TEXT;");
       // wdb.execSQL("ALTER TABLE Article_Table ADD webarchive_path TEXT;");
        Intent intent = getIntent();
        if(Intent.ACTION_SEARCH.equals(intent.getAction())){
            Toast.makeText(this,"Search Search",Toast.LENGTH_SHORT).show();
            mSearchActivity=true;
        }

        prepareAnimation();
       // getContentResolver().delete(Contracts.SiteEntry.CONTENT_URI,null,null);
      //  getContentResolver().bulkInsert(Contracts.SiteEntry.CONTENT_URI,DBUtils.sitesToCV(getSites()));
        //insertSites();
    }

    private void prepareAnimation() {
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        animation = ObjectAnimator.ofInt (progressBar, "progress", 0, 500); // see this max value coming back here, we animale towards that value
        animation.setDuration (5000); //in milliseconds
        animation.setInterpolator (new DecelerateInterpolator());
    }


    public void showProgressIndicator(){
        progressBar.setVisibility(View.VISIBLE);
        animation.start();
    }

    public void endProgressIndicator(){
        progressBar.setVisibility(View.GONE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT&&animation.isRunning())
            animation.pause();
    }
    public static  Site[] getSites() {
        //http://feeds.feedburner.com/TheAtlantic
        //https://www.polygon.com/rss/index.xml
        //http://www.coolhunting.com/atom.xml
        //http://www.betterlivingthroughdesign.com/feed
        //http://rss.cnn.com/rss/cnn_topstories.rss
        //http://www.washingtonpost.com/rss/
        //http://feeds.reuters.com/reuters/topNews
        //http://newsrss.bbc.co.uk/rss/newsonline_world_edition/americas/rss.xml
        String url = "http://stackoverflow.com/feeds/tag?tagnames=rome";

        Site site1=new Site();
        site1.setTitle("Cnn Top Stories");
        site1.setUrl("cnn.com");
        site1.setRssUrl("http://feeds.feedburner.com/TheAtlantic");
        site1.setCategoryID(2);
        Site site2=new Site();
        site2.setTitle("Washington Post: Today's Highlights");
        site2.setUrl("washingtonpost.com");
        site2.setRssUrl("https://www.polygon.com/rss/index.xml");
        site2.setCategoryID(2);
        Site site3=new Site();
        site3.setTitle("Reuters: Top News");
        site3.setUrl("reuters.com");
        site3.setRssUrl("http://www.coolhunting.com/atom.xml");
        site3.setCategoryID(2);
        Site site4=new Site();
        site4.setTitle("BBC News: Americas");
        site4.setUrl("bbc.co.uk");
        site4.setRssUrl("http://www.betterlivingthroughdesign.com/feed");
        site4.setCategoryID(2);
        Site[]sites={site3};//{site1,site2,site3,site4};//,site2};//,site4};
        //getContentResolver().bulkInsert(Contracts.SiteEntry.CONTENT_URI,DBUtils.sitesToCV(sites));
        return sites;
    }

    private void setupViews() {
        TabLayout mSlidingTabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        ViewPager mViewPager = (ViewPager)findViewById(R.id.viewpager);
        mViewPager.setAdapter(new MainPagesAdapter(getSupportFragmentManager(),this));
        mSlidingTabLayout.setupWithViewPager(mViewPager);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawer.closeDrawers();
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
//        mNavHeader = mNavigationView.getHeaderView(0);
  //      mNavHeader.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        //ToDo:: implement my own header and inflate it using mNavigationView.inflateHeaderView()
        setUpNavigationView();
    }
    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        //TODO :: MAYBE ADD ADAPTER TO NAVIGATION VIEW
        //Todo :: replace action in this listener for navigationview
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;

                }

                //Checking if the item is in checked state or not, if not make it in checked state
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
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.fetch_news){
            ArticlesRepository.getInstance(this).getLatestArticles();
            return true;
        }
        return mActionBarDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        // show menu only when home fragment is selected
        if(mSearchActivity){
            getMenuInflater().inflate(R.menu.nosearch,menu);
        }
        else {
            getMenuInflater().inflate(R.menu.main, menu);
            SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
            // Get the SearchView and set the searchable configuration
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

            // Assumes current activity is the searchable activity
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            //searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        }

        return true;
    }


    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawers();
            return;
        }


        super.onBackPressed();
    }
    public FloatingActionButton getFab() {
        return mFab;
    }

    public void onSavedPostsClicked(View view) {
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
    public void openWebViewFragment(String link) {
        if(!(link.contains("https://")||link.contains("http://"))){
            link="http://"+link;
        }
        Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        startActivity(intent);
    }
}
