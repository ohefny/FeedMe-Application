package com.example.bethechange.feedme.CustomScreen;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.bethechange.feedme.ArticleType;
import com.example.bethechange.feedme.Data.ArticlesRepository;
import com.example.bethechange.feedme.MainScreen.Models.Site;
import com.example.bethechange.feedme.MainScreen.Views.TimelineFragment;
import com.example.bethechange.feedme.R;
import com.google.gson.Gson;

public class CustomListActivity extends AppCompatActivity implements TimelineFragment.ArticlesActivityInteractor{


    public static final String SITE_KEY = "SITE_KEY";
    private boolean mSearchActivity;
    int type=ArticleType.SITE;
    public static final String TYPE_KEY="TYPE_KEY";
    public static final String CATEGORY_KEY="CATEGORY_KEY";
    private int siteId=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom);
        Intent intent=getIntent();
        type=intent.getIntExtra(TYPE_KEY,ArticleType.SITE);
        switch (type) {
                case ArticleType.SITE:
                    String siteStr = getIntent().getStringExtra(SITE_KEY);
                    Site mSite = new Gson().fromJson(siteStr, Site.class);
                    siteId=mSite.getID();
                    setTitle(mSite.getTitle());
                    getSupportFragmentManager().beginTransaction().add(R.id.list_fragment_container,
                            TimelineFragment.newInstance(1, this, ArticleType.SITE, mSite), null).commit();
                    break;
                case ArticleType.SAVED:
                    getSupportFragmentManager().beginTransaction().add(R.id.list_fragment_container,
                            TimelineFragment.newInstance(1, this, ArticleType.SAVED), null).commit();
                    setTitle(getString(R.string.saved_activity_title));
                    break;
                case ArticleType.BOOKMARKED:
                    getSupportFragmentManager().beginTransaction().add(R.id.list_fragment_container,
                            TimelineFragment.newInstance(1, this, ArticleType.BOOKMARKED), null).commit();
                    setTitle(getString(R.string.bookmark_activity_title));
                    break;
            }
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



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

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

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
            final SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
            // Get the SearchView and set the searchable configuration
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

            // Assumes current activity is the searchable activity
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    Bundle appData = new Bundle();
                    appData.putInt(SearchActivity.FROM_TYPE, type);
                    appData.putInt(SearchActivity.FROM_ID,siteId);
                    searchView.setAppSearchData(appData);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
            //searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.fetch_news:
                ArticlesRepository.getInstance(this).getLatestArticles();
                return true;


        }
        return super.onOptionsItemSelected(item);
    }

}
