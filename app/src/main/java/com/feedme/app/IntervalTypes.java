package com.feedme.app;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


public @StringDef({IntervalTypes.HOURS,IntervalTypes.DAYS})
@Retention(RetentionPolicy.SOURCE)
@interface IntervalTypes{
    String HOURS="HOURS";
    String DAYS="DAYS";

}
