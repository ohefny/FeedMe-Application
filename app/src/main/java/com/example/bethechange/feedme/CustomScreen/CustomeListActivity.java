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

public class CustomeListActivity extends AppCompatActivity implements TimelineFragment.ArticlesActivityInteractor{


    public static final String SITE_KEY = "SITE_KEY";
    Site mSite;
    private boolean mSearchActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site);
        Intent intent=getIntent();
        if(Intent.ACTION_SEARCH.equals(intent.getAction())){
            Toast.makeText(this,"Search Search",Toast.LENGTH_SHORT).show();
            mSearchActivity=true;
        }
        else {
            String siteStr = getIntent().getStringExtra(SITE_KEY);
            mSite = new Gson().fromJson(siteStr, Site.class);
        }

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(mSite.getTitle());

        getSupportFragmentManager().beginTransaction().add(R.id.list_fragment_container,TimelineFragment.newInstance(1,this, ArticleType.SITE,mSite),null).commit();
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
