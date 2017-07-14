package com.example.bethechange.feedme;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by BeTheChange on 7/11/2017.
 */

public @IntDef({ArticleType.ALL,ArticleType.CATEGORY,ArticleType.SITE})
 @Retention(RetentionPolicy.SOURCE)
@interface ArticleType{
    int ALL=0;
    int SITE=1;
    int CATEGORY=2;
}