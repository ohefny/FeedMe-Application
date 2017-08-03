package com.example.bethechange.feedme.widget;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViewsService;

public class FeedMeWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new FeedMeViewsFactory(this,intent);
    }
}
