package com.example.bethechange.feedme;

import android.content.Context;

import com.example.bethechange.feedme.MainScreen.Views.MainScreenActivity;
import com.example.bethechange.feedme.Services.BackupDataService;
import com.example.bethechange.feedme.Services.RefreshDataDispatcher;
import com.example.bethechange.feedme.Utils.PrefUtils;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

/**
 * Created by BeTheChange on 8/1/2017.
 */

public class JobManger {
    private static final String REFRESH_JOB_TAG="REFRESH_DATA";
    private static final String BACKUP_JOB_TAG="BACKUP_DATA";

    public static void scheduleRefreshJob(Context context) {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        int refreshEvery= (PrefUtils.updateEveryMillie(context)/1000);
            Job refreshJob = dispatcher.newJobBuilder()
                    // the JobService that will be called
                    .setService(RefreshDataDispatcher.class)
                    // uniquely identifies the job
                    .setTag(REFRESH_JOB_TAG)
                    // one-off job
                    .setRecurring(false)
                    // don't persist past a device reboot
                    .setLifetime(Lifetime.FOREVER)
                    // start between 0 and 60 seconds from now
                    .setTrigger(Trigger.executionWindow(refreshEvery, refreshEvery + 160))
                    // don't overwrite an existing job with the same tag
                    .setReplaceCurrent(false)
                    // retry with exponential backoff
                    .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                    // constraints that need to be satisfied for the job to run
                    .setConstraints(
                            // only run on an unmetered network
                            Constraint.ON_ANY_NETWORK,
                            // only run when the device is charging
                            Constraint.DEVICE_CHARGING
                    )
                    .build();

            dispatcher.mustSchedule(refreshJob);


    }
    public static void scheduleBackupJob(Context context) {
        int backupEvery = PrefUtils.backupEveryMillie(context) / 1000;
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        Job backupJob = dispatcher.newJobBuilder()
                // the JobService that will be called
                .setService(RefreshDataDispatcher.class)
                // uniquely identifies the job
                .setTag(BACKUP_JOB_TAG)
                // one-off job
                .setRecurring(false)
                // don't persist past a device reboot
                .setLifetime(Lifetime.FOREVER)
                // start between 0 and 60 seconds from now
                .setTrigger(Trigger.executionWindow(backupEvery, backupEvery + 160))
                // don't overwrite an existing job with the same tag
                .setReplaceCurrent(false)
                // retry with exponential backoff
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                // constraints that need to be satisfied for the job to run
                .setConstraints(
                        // only run on an unmetered network
                        Constraint.ON_ANY_NETWORK,
                        // only run when the device is charging
                        Constraint.DEVICE_CHARGING
                )
                .build();

        dispatcher.mustSchedule(backupJob);
    }
}
