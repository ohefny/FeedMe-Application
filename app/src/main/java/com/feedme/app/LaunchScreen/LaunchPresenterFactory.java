package com.feedme.app.LaunchScreen;

import com.feedme.app.Data.ArticlesRepository;
import com.feedme.mvpframeworkedited.PresenterFactory;

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