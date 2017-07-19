package com.example.bethechange.feedme.MainScreen.Views.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.example.bethechange.feedme.MainScreen.Views.BlankFragment;
import com.example.bethechange.feedme.MainScreen.Views.MainScreenActivity;
import com.example.bethechange.feedme.MainScreen.Views.TimelineFragment;
import com.example.bethechange.feedme.R;

/**
 * Created by BeTheChange on 7/10/2017.
 */
public class MainPagesAdapter extends FragmentStatePagerAdapter {

    private static final String LOG_TAG = "MainPagesAdapter";
    private FragmentActivity mActivity;


    public MainPagesAdapter(FragmentManager manager, MainScreenActivity activity) {
        super(manager);
        mActivity=activity;
    }

    /**
     * @return the number of pages to display
     */
    @Override
    public int getCount() {
        return 3;
    }


    @Override
    public CharSequence getPageTitle(int position) {

        switch (position){
            case 0:
                return  addImageToTitle("Timeline",R.drawable.ic_launcher);
            case 1:
                return  addImageToTitle("My Sites",R.drawable.ic_launcher);
            case 2:
                return  addImageToTitle("Categories",R.drawable.ic_launcher);

        }
        return "Item " + (position + 1);
    }

    private CharSequence addImageToTitle(String titleStr,@DrawableRes int id) {

        SpannableStringBuilder sb = new SpannableStringBuilder(" "+titleStr+" "); // space added before text for convenience

        Drawable drawable = mActivity.getBaseContext().getResources().getDrawable( id);

        drawable.setBounds(0, 0, 50, 50);
        ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
        sb.setSpan(span, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }
    // END_INCLUDE (pageradapter_getpagetitle)

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                return TimelineFragment.newInstance(1,(MainScreenActivity)mActivity);
        }
        return new BlankFragment();
    }

    /**
     * Instantiate the {@link View} which should be displayed at {@code position}. Here we
     * inflate a layout from the apps resources and then change the text view to signify the position.
     */




}

