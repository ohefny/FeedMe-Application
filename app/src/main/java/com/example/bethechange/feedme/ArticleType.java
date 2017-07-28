package com.example.bethechange.feedme;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by BeTheChange on 7/11/2017.
 */

public @IntDef({ArticleType.CATEGORY,ArticleType.SITE,ArticleType.BOOKMARKED,ArticleType.SAVED,ArticleType.SEARCH})
 @Retention(RetentionPolicy.SOURCE)
@interface ArticleType {
    int SITE=1;
    int CATEGORY=2;
    int BOOKMARKED=3;
    int SAVED=4;
    int SEARCH=5;
}