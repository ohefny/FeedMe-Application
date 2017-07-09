package com.example.bethechange.feedme.MainScreen.Views;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.bethechange.feedme.R;

public class MainScreenActivity extends AppCompatActivity {
    private NavigationView mNavigationView;
    private DrawerLayout mDrawer;
    private View mNavHeader;
    private FloatingActionButton mFab;
    private Toolbar mToolbar;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private boolean mSearchActivity=false;
   //TODO if seprate search activity remove this boolean and it's usage
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        setupViews();
        Intent intent = getIntent();
        if(Intent.ACTION_SEARCH.equals(intent.getAction())){
            Toast.makeText(this,"Search Search",Toast.LENGTH_SHORT).show();
            mSearchActivity=true;
        }
        else
            getSupportFragmentManager().beginTransaction().add(R.id.frameContainer,new MainScreenFragment(),null).commit();


    }

    private void setupViews() {
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
        if (mActionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
    public FloatingActionButton getmFab() {
        return mFab;
    }

}
