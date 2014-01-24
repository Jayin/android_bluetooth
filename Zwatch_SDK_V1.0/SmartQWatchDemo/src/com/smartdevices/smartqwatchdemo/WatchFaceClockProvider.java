package com.smartdevices.smartqwatchdemo;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

/**
 * Simple widget to show analog clock.you need reboot your watch after install this app at first time.
 */
public class WatchFaceClockProvider extends AppWidgetProvider {

	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		RemoteViews views = new RemoteViews(context.getPackageName(),
				R.layout.analog_appwidget);
		appWidgetManager.updateAppWidget(appWidgetIds, views);
	}
}
