package com.feedme.app.Services;

import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

/**
 * Created by BeTheChange on 7/31/2017.
 */

public class BackupDispatcher extends JobService {
    @Override
    public boolean onStartJob(JobParameters job) {
        Log.d(BackupDispatcher.class.getSimpleName(),"Started...");
        BackupDataService.startActionBackup(this);
        jobFinished(job,false);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }
}
