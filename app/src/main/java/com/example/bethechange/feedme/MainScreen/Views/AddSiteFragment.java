package com.example.bethechange.feedme.MainScreen.Views;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bethechange.feedme.R;
import com.example.mvpframeworkedited.BasePresenterFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddSiteFragment extends Fragment {


    public AddSiteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_site, container, false);
    }

    public void onAddCategoryClicked(View view) {
    }

    public void onAddSiteClicked(View view) {
    }
}
