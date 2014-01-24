package com.smartdevices.smartqwatchdemo;

import java.util.Calendar;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.RemoteViews;

/**
 * Simple widget to show analog clock.
 */
public class WatchFaceDigitalClockProvider extends AppWidgetProvider {

	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		updateTime(context);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		String action = intent.getAction();
		if (Intent.ACTION_TIME_TICK.equals(action)
				|| Intent.ACTION_TIME_CHANGED.equals(action)) {
			updateTime(context);
		}
	}

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_TIME_CHANGED);
		filter.addAction(Intent.ACTION_TIME_TICK);
		context.getApplicationContext().registerReceiver(this, filter);
	}

	private void updateTime(Context context) {
		AppWidgetManager manager = AppWidgetManager.getInstance(context);
		RemoteViews views = new RemoteViews(context.getPackageName(),
				R.layout.digital_appwidget);
		views.setTextViewText(R.id.date, getCurrentWeek(context) + "/"
				+ getCurrentData(context));
		views.setTextViewText(R.id.time, getHourMinute(context));
		manager.updateAppWidget(new ComponentName(context,
				WatchFaceDigitalClockProvider.class), views);
	}

	public static String getCurrentData(Context context) {
		final Calendar calendar = Calendar.getInstance();
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		String dayStr = null;
		if (day < 10)
			dayStr = "0" + day;
		else
			dayStr = String.valueOf(day);

		return dayStr;
	}

	public static String getCurrentWeek(Context context) {
		final Calendar calendar = Calendar.getInstance();
		int week = calendar.get(Calendar.DAY_OF_WEEK);
		String weekStr = null;
		switch (week) {
		case 1:
			weekStr = "sun";
			break;
		case 2:
			weekStr = "mon";
			break;
		case 3:
			weekStr = "tue";
			break;
		case 4:
			weekStr = "wed";
			break;
		case 5:
			weekStr = "thu";
			break;
		case 6:
			weekStr = "fri";
			break;
		case 7:
			weekStr = "sat";
			break;
		default:
			break;
		}

		return weekStr;
	}

	public static String getHourMinute(Context context) {
		boolean is24Hour = is24HourFormat(context);
		String time = "";
		Calendar c = Calendar.getInstance();
		int hour = 0;
		hour = c.get(Calendar.HOUR_OF_DAY);
		if (is24Hour) {
			time += hour < 10 ? "0" + hour : hour;
		} else {
			int tens = hour > 12 ? (hour - 12) / 10 : hour / 10;
			int single = hour > 12 ? (hour - 12) % 10 : hour % 10;
			time = String.valueOf(tens) + String.valueOf(single);
		}
		int minute = c.get(Calendar.MINUTE);
		if (minute < 10) {
			time += ":0" + minute;
		} else
			time += ":" + minute;
		return time;
	}

	public static boolean is24HourFormat(Context context) {
		ContentResolver cv = context.getContentResolver();
		String strTimeFormat = android.provider.Settings.System.getString(cv,
				android.provider.Settings.System.TIME_12_24);

		if (strTimeFormat != null && strTimeFormat.equals("24"))
			return true;
		else
			return false;
	}

}
