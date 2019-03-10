package com.feedme.app.CustomScreen;

import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.feedme.app.ArticleType;
import com.feedme.app.MainScreen.Views.ArticleListFragment;
import com.feedme.app.R;

public class SearchActivity extends AppCompatActivity implements ArticleListFragment.ArticlesActivityInteractor{
    public static final String FROM_TYPE="FROM_TYPE";
    public static final String FROM_ID="FROM_ID";

    String query ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom);
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            Bundle appData = getIntent().getBundleExtra(SearchManager.APP_DATA);
            query=intent.getStringExtra(SearchManager.QUERY);
            setTitle(intent.getStringExtra(SearchManager.QUERY));
            int id=appData.getInt(FROM_ID,-1);
            int from=appData.getInt(FROM_TYPE,ArticleType.CATEGORY);
            SearchModel model=new SearchModel(query,from,id);
            getSupportFragmentManager().beginTransaction().replace(R.id.list_fragment_container,
                    ArticleListFragment.newInstance(1, this, ArticleType.SEARCH,model), null).commit();
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
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
