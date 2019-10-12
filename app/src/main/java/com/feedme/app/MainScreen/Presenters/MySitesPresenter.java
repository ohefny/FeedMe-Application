package com.feedme.app.MainScreen.Presenters;

import androidx.annotation.NonNull;

import com.feedme.app.Data.CategoriesRepository;
import com.feedme.app.Data.SitesRepository;
import com.feedme.app.Data.SitesRepositoryActions;
import com.feedme.app.MainScreen.Models.Category;
import com.feedme.app.MainScreen.Models.Site;
import com.feedme.app.MainScreen.ViewContracts.MySitesContract;
import com.feedme.mvpframeworkedited.BasePresenter;

import java.util.ArrayList;

/**
 * Created by BeTheChange on 7/10/2017.
 */

public class MySitesPresenter extends BasePresenter<ArrayList<Site>,MySitesContract.View>
        implements SitesRepository.SitesObserver,MySitesContract.Presenter, CategoriesRepository.CategoriesListener {

    private final boolean fetching;
    private final CategoriesRepository catRepo;
    private SitesRepositoryActions repo;
    private Category category;
    private ArrayList<Category> cats=new ArrayList<>();

    public MySitesPresenter(SitesRepositoryActions repo, CategoriesRepository categoriesRepository, Category category){
        super();
        this.repo=repo;
        this.category=category;
        this.catRepo=categoriesRepository;
        cats=catRepo.getCategories(this);
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
        view().openSite(site);

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

    @Override
    public void categoriesFetched(ArrayList<Category> cats) {
        this.cats=cats;
        if(view()!=null)
            view().updateSpinner(cats);
    }
}
