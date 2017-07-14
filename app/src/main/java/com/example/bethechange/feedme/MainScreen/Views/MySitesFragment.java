package com.example.bethechange.feedme.MainScreen.Views;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bethechange.feedme.MainScreen.Models.Site;
import com.example.bethechange.feedme.R;

import java.util.List;


public class MySitesFragment extends Fragment {

    private int mColumnCount = 2;
    private RecyclerView mRecyclerView;
    private List<Site> mSites;

    // private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MySitesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mysites_list, container, false);
        setupRecyclerView(view);

        return view;
    }

    private void setupRecyclerView(View view) {
        mRecyclerView=(RecyclerView)view.findViewById(R.id.mysites_list)    ;
        // Set the adapter
        Context context = view.getContext();

        if (mColumnCount <= 1) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        mRecyclerView.setAdapter(new MySiteRecyclerViewAdapter(mSites));//, mListener));

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
               //TODO REMOVE SITE

            }
        }).attachToRecyclerView(mRecyclerView);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
      /*  if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
       // mListener = null;
    }


}
