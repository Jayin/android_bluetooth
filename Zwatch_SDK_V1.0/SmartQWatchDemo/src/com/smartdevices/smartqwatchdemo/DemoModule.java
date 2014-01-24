package com.smartdevices.smartqwatchdemo;

import android.content.Context;
import android.media.AudioTrack;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.util.Log;
import cn.ingenic.indroidsync.services.SyncData;
import cn.ingenic.indroidsync.services.SyncModule;

/**
 * 
 * 派生SyncModule类，通过调用SyncModule类函数来发送和接收数据； 数据 SyncData
 * data，类似Bundle类,手机手表传递的数据封装； 发送数据 ：DemoModule.send(SyncData data); 接收数据
 * ：DemoModule.onRetrive(SyncData data) 是个回调函数；
 * 
 */
public class DemoModule extends SyncModule {
	private static final String TAG = "DemoModule";
	public static final String MODULE = "DemoModule";

	private boolean mIsWatch = false;

	private AudioTrack mTrack = null;
	private Handler mHandler;

	public DemoModule(Context context) {
		super(MODULE, context);
	}

	public DemoModule(Context context, boolean isWatch) {
		super(MODULE, context);
		mIsWatch = isWatch;
	}
	
	public void setHandler(Handler handler){
		mHandler = handler;
	}

	@Override
	protected void onCreate() {
		Log.d(TAG, "onCreate......");

		if (!mIsWatch) {
			mTrack = DemoUtil.findAudioTrack();
			if (mTrack != null)
				mTrack.play();
		}
	}

	/*
	 * 通道建立完成回调函数
	 */
	@Override
	protected void onChannelCreateComplete(ParcelUuid uuid, boolean success,
			boolean local) {
		super.onChannelCreateComplete(uuid, success, local);
		Log.d(TAG, "onChannelCreateComplete......");
	}

	/* 
	 * 
	 */
	@Override
	protected void onChannelRetrive(ParcelUuid uuid, SyncData data) {
		super.onChannelRetrive(uuid, data);
		Log.d(TAG, "onChannelRetrive......");
	}

	/* 
	 * mode:SAVING_POWER_MODE = 0;RIGHT_NOW_MODE = 1;代表省电状态与正常状态
	 */
	@Override
	protected void onModeChanged(int mode) {
		super.onModeChanged(mode);
		Log.d(TAG, "onModeChanged......");
	}

	/* 
	 * 数据通信传输完成后回调函数
	 */
	@Override
	protected void onRetrive(SyncData data) {
		super.onRetrive(data);
		Log.d(TAG, "onRetrive......");

		if (mIsWatch) {
			handleRetriveDataOnWatch(data);
		} else {
			handleRetriveDataOnPhone(data);
		}
	}

	private void handleRetriveDataOnWatch(SyncData data) {

	}

	/*
	 * data: 手机端拿到手表端传输过来的数据 用线程去处理数据吧
	 */
	private void handleRetriveDataOnPhone(SyncData data) {
		String connectCmd = data.getString(DemoUtil.KEY_CONNECT);

		if (connectCmd != null) {
			Log.d(TAG, "handleRetriveDataOnPhone connect cmd:" + connectCmd);
		} else {
			Message msg = mHandler.obtainMessage(MainActivity.UPDATE_VIEW_PHONE_RECORD);
			msg.obj = data;
			mHandler.sendMessage(msg);
		}

		// if(mTrack != null)
		// mTrack.write(bytes, 0, bytes.length);
	}

	@Override
	protected void onChannelDestroy(ParcelUuid uuid) {
		super.onChannelDestroy(uuid);
		Log.d(TAG, "onChannelDestroy......");
	}

	@Override
	protected void onClear(String address) {
		super.onClear(address);
		Log.d(TAG, "onClear......");
	}

	/*
	 * 这个手机手表连接状态变化的回调比较重要，如果connect=false，说明连接已经中断，程序要做处理；
	 */
	@Override
	protected void onConnectionStateChanged(boolean connect) {
		super.onConnectionStateChanged(connect);
		Log.d(TAG, "onConnectionStateChanged......" + connect);
	}

	/* 
	 * 文件接收完成回调方法
	 */
	@Override
	protected void onFileRetriveComplete(String fileName, boolean success) {
		super.onFileRetriveComplete(fileName, success);
		Log.d(TAG, "onFileRetriveComplete......");
	}

	/* 
	 * 文件发送完成回调方法
	 */
	@Override
	protected void onFileSendComplete(String fileName, boolean success) {
		super.onFileSendComplete(fileName, success);
		Log.d(TAG, "onFileSendComplete......");
	}

	@Override
	protected void onInit() {
		super.onInit();
		Log.d(TAG, "onInit......");
	}
}
