package com.example.bethechange.feedme.Services;

import android.content.Intent;
import android.util.Log;

import com.example.bethechange.feedme.FeedMeApp;
import com.example.bethechange.feedme.Utils.PrefUtils;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

/**
 * Created by BeTheChange on 7/31/2017.
 */

public class RefreshDataService extends JobService {
    @Override
    public boolean onStartJob(JobParameters job) {
        Log.d(RefreshDataService.class.getSimpleName(),"Started...");
        if(PrefUtils.updateNow(this)){
            CleanupService.startActionCleanOld(this);
            ArticlesDownloaderService.startActionUpdateAll(this,true);
            jobFinished(job,false);
        }
        else{
            jobFinished(job,true);
        }
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }
}