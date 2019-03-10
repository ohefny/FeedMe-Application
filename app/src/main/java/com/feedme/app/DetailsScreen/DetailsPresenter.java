package com.feedme.app.DetailsScreen;

import com.feedme.mvpframeworkedited.BasePresenter;

import java.util.ArrayList;

/**
 * Created by BeTheChange on 7/18/2017.
 */

class DetailsPresenter extends BasePresenter<ArrayList<Integer>,DetailsContract.DetailsView>
    implements DetailsContract.DetailsPresenter{
    int startingPos=0;
    DetailsPresenter( ArrayList<Integer>ids,int startingId){
        setModel(ids);
        startingPos=getArticlePos(startingId);
    }
    @Override
    protected void updateView() {

    }

    @Override
    public int getItemsCount() {
        if(model!=null)
            return model.size();
        return 0;
    }

    @Override
    public int getArticleID(int pos) {
        if(model!=null)
            return model.get(pos);
        return 0;
    }

    @Override
    public int getArticlePos(int arId) {
        for(int i=0;i<model.size();i++)
            if(model.get(i)==arId)
                return i;

        return 0;
    }

    @Override
    public int getStartingPos() {
        return startingPos;
    }
}
