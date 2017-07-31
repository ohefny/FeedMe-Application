package com.example.bethechange.feedme.LaunchScreen;

import com.example.bethechange.feedme.Data.ArticlesRepository;
import com.example.mvpframeworkedited.PresenterFactory;

/**
 * Created by BeTheChange on 7/30/2017.
 */
public class LaunchPresenterFactory implements PresenterFactory<LaunchPresenter> {
    private final ArticlesRepository repository;

    LaunchPresenterFactory(ArticlesRepository repository){
        this.repository=repository;
    }
    @Override
    public LaunchPresenter create() {
        return new LaunchPresenter(repository);
    }
}