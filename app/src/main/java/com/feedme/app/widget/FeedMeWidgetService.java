package com.feedme.app.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class FeedMeWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new FeedMeViewsFactory(this,intent);
    }
}
