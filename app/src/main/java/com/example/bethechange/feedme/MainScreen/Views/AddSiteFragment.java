package com.example.bethechange.feedme.MainScreen.Views;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.bethechange.feedme.Data.ContentFetcher;
import com.example.bethechange.feedme.MainScreen.Models.Category;
import com.example.bethechange.feedme.MainScreen.Models.Site;
import com.example.bethechange.feedme.MainScreen.Presenters.MySitesPresenter;
import com.example.bethechange.feedme.MainScreen.ViewContracts.MySitesContract;
import com.example.bethechange.feedme.R;
import com.example.bethechange.feedme.Utils.NetworkUtils;
import com.example.mvpframeworkedited.BasePresenterFragment;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.internal.Util;

import java.net.URI;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddSiteFragment extends DialogFragment implements NetworkUtils.RssCheckedListener{


    private ArrayList<Category> categories=new ArrayList<>();
    private Spinner spinner;
    private AutoCompleteTextView titleView;
    private EditText rssLinkView;
    private Button addBtn;
    private MySitesContract.Presenter presenter;
    private ProgressDialog dialog;
    private Site site;
    private TextView errorView;
    private ImageButton catBtn;
    private EditText catTitle;
    private boolean catTiltleEnabled;
    public AddSiteFragment() {

        // Required empty public constructor
    }
    public static AddSiteFragment newInstance(ArrayList<Category> cats, MySitesContract.Presenter presenter, Site site){
        AddSiteFragment fr = new AddSiteFragment();
        fr.setCategories(cats);
        fr.setPresenter(presenter);
        fr.setSite(site);
        return fr;
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mRootView = inflater.inflate(R.layout.fragment_add_site, container, false);
        dialog=new ProgressDialog(getContext());
        dialog.setMessage("Verifying Feeds Url");
        dialog.setCancelable(false);
        dialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        spinner=(Spinner) mRootView.findViewById(R.id.cat_spinner_id);
        spinner.setAdapter(new ArrayAdapter<Category>(getContext(),R.layout.simple_category_item,categories));
        titleView=(AutoCompleteTextView) mRootView.findViewById(R.id.site_title_input_id);
        rssLinkView=(EditText) mRootView.findViewById(R.id.site_rss_input_id);
        errorView=(TextView)mRootView.findViewById(R.id.error_view);
        addBtn=(Button) mRootView.findViewById(R.id.add_btn_id);
        catBtn=(ImageButton)mRootView.findViewById(R.id.add_cat_btn_id);
        catTitle=(EditText)mRootView.findViewById(R.id.cat_editText);
        catBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if(catTiltleEnabled){
                        if(TextUtils.isEmpty(catTitle.getText().toString())){
                            catTitle.setError(getString(R.string.cat_empty_title));
                            return;
                        }
                        Category cat=new Category();
                        cat.setTitle(catTitle.getText().toString());
                        presenter.onCategoryAdded(cat);
                        catTitle.setVisibility(View.INVISIBLE);
                        catBtn.setImageResource(R.drawable.ic_fab_add);
                        //spinner.setSelection(categories.size()-1);
                        catTiltleEnabled=false;
                    }
                    else {
                        catTiltleEnabled=true;
                        catTitle.setVisibility(View.VISIBLE);
                        catBtn.setImageResource(R.drawable.ic_verify);
                    }
            }
        });
        if(site==null){
            addBtn.setText(R.string.add_btn_title);
            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addBtn.setText(R.string.verify_rss_link);
                    addBtn.setEnabled(false);
                    Site site=new Site();
                    boolean add=true;
                    if(!rssLinkView.getText().toString().contains("http")){
                        rssLinkView.setText(String.format("http://%s", rssLinkView.getText().toString()));
                    }
                    if(TextUtils.isEmpty(rssLinkView.getText())|| HttpUrl.parse(rssLinkView.getText().toString())==null){
                        rssLinkView.setError("Please Enter A Valid Http Rss Link ");
                        add=false;
                    }
                    if(TextUtils.isEmpty(titleView.getText().toString().trim())){
                        titleView.setError("Please Enter Title");
                        add=false;
                    }
                    if(!add){
                        addBtn.setText(R.string.add_btn_title);
                        addBtn.setEnabled(true);
                        return;
                    }
                    site.setRssUrl(rssLinkView.getText().toString().trim());
                    site.setUrl(HttpUrl.parse(site.getRssUrl()).host());
                    site.setTitle(titleView.getText().toString().trim());
                    site.setCategoryID(((Category)spinner.getSelectedItem()).getId());
                    site.setCategory((Category)spinner.getSelectedItem());
                    dialog.show();
                    NetworkUtils.TestRssLink(site,AddSiteFragment.this);


                }
            });
        }
        else {
            rssLinkView.setEnabled(false);
            addBtn.setText(R.string.edit_btn_title);
            titleView.setText(site.getTitle());
            rssLinkView.setText(site.getRssUrl());
            spinner.setSelection(categories.indexOf(site.getCategory()));
            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(TextUtils.isEmpty(titleView.getText().toString().trim())){
                        titleView.setError(getContext().getResources().getString(R.string.title_error_msg));
                        return;
                    }
                    site.setTitle(titleView.getText().toString().trim());
                    site.setCategoryID(((Category) spinner.getSelectedItem()).getId());
                    site.setCategory((Category) spinner.getSelectedItem());
                    getDialog().dismiss();
                    presenter.onPerformEdit(site);
                }
            });

        }

        return mRootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_DeviceDefault_Dialog);

    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().setTitle("Add Site");

    }

    public void onAddCategoryClicked(View view) {
    }

    public void onAddSiteClicked(View view) {
    }

    public void setCategories(ArrayList<Category> categories) {
        this.categories.clear();
        this.categories.addAll(categories);
        this.categories.remove(0);
    }

    public void setPresenter(MySitesContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onRssLinkChecked(Site site,boolean valid,boolean exist) {
        errorView.setVisibility(View.INVISIBLE);
        if(exist){
            dialog.dismiss();
            addBtn.setError(getContext().getResources().getString(R.string.site_already_exist));
            errorView.setVisibility(View.VISIBLE);

        }
        else if(valid) {
            dialog.dismiss();
            presenter.onPerformAdd(site);
            this.getDialog().dismiss();
        }
        else{
            dialog.dismiss();
            rssLinkView.setError("Can't Verify unsupported feeds or no connection ");
        }
        addBtn.setText("Add");
        addBtn.setEnabled(true);
    }

    public void setSite(Site site) {
        this.site = site;
    }


}
