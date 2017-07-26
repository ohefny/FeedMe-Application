package com.example.bethechange.feedme.MainScreen.Presenters;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.bethechange.feedme.Data.SitesRepository;
import com.example.bethechange.feedme.Data.SitesRepositoryActions;
import com.example.bethechange.feedme.MainScreen.Models.Category;
import com.example.bethechange.feedme.MainScreen.Models.Site;
import com.example.bethechange.feedme.MainScreen.ViewContracts.MySitesContract;
import com.example.mvpframeworkedited.BasePresenter;

import java.util.ArrayList;

/**
 * Created by BeTheChange on 7/10/2017.
 */

public class MySitesPresenter extends BasePresenter<ArrayList<Site>,MySitesContract.View>
        implements SitesRepository.SitesObserver,MySitesContract.Presenter {

    private final boolean fetching;
    private SitesRepositoryActions repo;
    private Category category;
    private ArrayList<Category> cats=new ArrayList<>();

    public MySitesPresenter(SitesRepositoryActions repo, Category category){
        super();
        this.repo=repo;
        this.category=category;
        setModel(new ArrayList<Site>());
        repo.setObserver(this);
        fetching=true;
    }

    @Override
    public void bindView(@NonNull MySitesContract.View view) {
        super.bindView(view);
        //Log.d("MySitesPresenter","View Bound");
    }

    @Override
    protected void updateView() {
        view().updateList(model);
        /*ArrayList<String>strs=new ArrayList<>();
        for(Category cat:cats)
            strs.add(cat.getTitle());*/
        view().updateSpinner(cats);
    }

    @Override
    public void queryCompleted() {
        if(repo.getSites(category).size()-1==model.size()&&view()!=null){
            view().showMessage("New site added");
        }
        else if(repo.getSites(category).size()+1==model.size()&&view()!=null){
            view().showMessage("Site deleted");
        }
        setModel(repo.getSites(category));
        cats.clear();
        cats.addAll(repo.getCategorise());

    }

    @Override
    public void onPerformDelete(Site site) {
        repo.removeSite(site);
    }

    @Override
    public void onPerformAdd(Site site) {
        repo.addSite(site);
    }

    @Override
    public void onPerformEdit(Site site) {
        repo.editSite(site);
    }

    @Override
    public void onOpenSiteArticles(Site site) {

    }

    @Override
    public void onCategoryChoosed(Category category) {
        this.category=category;
        setModel(repo.getSites(category));
    }

    @Override
    public void onEditPressed(Site mItem) {
        view().showEditDialog(mItem);
    }

    @Override
    public void onCategoryAdded(Category cat) {
        repo.addCategory(cat);
    }
}
