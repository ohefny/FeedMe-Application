package com.example.bethechange.feedme.Utils;

import android.util.SparseArray;

import com.example.bethechange.feedme.MainScreen.Models.ArticlesList;
import com.example.bethechange.feedme.MainScreen.Models.FeedMeArticle;

import java.util.ArrayList;

/**
 * Created by BeTheChange on 7/27/2017.
 */

public class CollectionUtils {
    public static <T> ArrayList<T> sparseToArray(SparseArray<T>sparseArray){
        ArrayList<T>arrayList=new ArrayList<>();
        for(int i=0;i<sparseArray.size();i++)
            arrayList.add(sparseArray.valueAt(i));

        return arrayList;
    }
}
