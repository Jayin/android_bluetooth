package com.smartdevices.smartqwatchdemo;

import android.app.Activity;
import android.media.AudioRecord;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import cn.ingenic.indroidsync.services.SyncData;
import cn.ingenic.indroidsync.services.SyncException;

public class MainActivity extends Activity {
	protected static final int UPDATE_VIEW_PHONE_RECORD = 0;
	protected static final int UPDATE_VIEW_WATCH_RECORD = 1;
	protected static final String KEY_BOOL = "bool";
	protected static final String KEY_INT = "int";
	protected static final String KEY_FLOAT = "float";
	protected static final String KEY_LONG = "long";
	protected static final String KEY_STRING = "string";
	
	private static String TAG = "MainActivity";
	private DemoModule mDemoModule = null;
	
	/*
	 * 用于区分程序运行在手机上还是在手表上,暂时可以用MODEL="Z1"来区分
	 */
	private boolean mIsWatch = false;
	
	private AudioRecord mRecorder = null;
	private boolean mIsRecording = false;
	
	private Button mRecordBtn = null;
	private Button mDataBtn = null;
	private TextView mInfoText;
	long mTime;
	private StringBuffer sBuffer = null;
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			SyncData data = (SyncData) msg.obj;
			switch (msg.what) {
			case UPDATE_VIEW_PHONE_RECORD:
				for (String key : data.keySet()) {
					if (key.equals(DemoUtil.KEY_RECORD_DATA)) {
						sBuffer.append("receive record date size == "
								+ data.getByteArray(DemoUtil.KEY_RECORD_DATA).length
								+ ";time is "
								+ java.lang.System.currentTimeMillis());
						sBuffer.append("\n");
					} else {
						sBuffer.append("data type is " + key + ";value is "
								+ data.get(key));
						sBuffer.append("\n");
					}
				}
				mInfoText.setText(sBuffer.toString());
				sBuffer.delete(0, sBuffer.length() - 1);
				break;
			case UPDATE_VIEW_WATCH_RECORD:
				for (String key : data.keySet()) {
					if (key.equals(DemoUtil.KEY_RECORD_DATA)) {
						sBuffer.append("record date size :"
								+ data.getByteArray(DemoUtil.KEY_RECORD_DATA).length
								+ ";time:"
								+ java.lang.System.currentTimeMillis());
						sBuffer.append("\n");
					} else {
						sBuffer.append("data type:" + key
								+ ";value :" + data.get(key));
						sBuffer.append("\n");
					}
				}
				mInfoText.setText(sBuffer.toString());
				sBuffer.delete(0, sBuffer.length() - 1);
				break;

			default:
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mIsWatch = Build.MODEL.equals("Z1");
		mDemoModule = new DemoModule(this,mIsWatch);
		mDemoModule.setHandler(mHandler);
		sBuffer = new StringBuffer();
		if(mIsWatch){
			setContentView(R.layout.activity_main);
			mInfoText = (TextView) findViewById(R.id.infos_watch);
			mRecordBtn = (Button) findViewById(R.id.record_bt);
			mDataBtn = (Button) findViewById(R.id.data_bt);
			mDataBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					onStartDataTransport();
				}
			});
			mRecordBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(mRecordBtn.getTag().equals("start")){
						mRecordBtn.setTag("stop");
						mRecordBtn.setText(R.string.stop_record);
						onStartRecord();
					}else if(mRecordBtn.getTag().equals("stop")){
						mRecordBtn.setTag("start");
						mRecordBtn.setText(R.string.start_record);
						onStopRecord();
					}
				}
			});
		}else{
			setContentView(R.layout.activity_main_phone);
			mInfoText = (TextView) findViewById(R.id.infos_phone);
		}
	}
	
	private void onStartDataTransport() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				waitingForConnected();
				constructDataForTransport();
			}
		}).start();
	}
	
	private void onStartRecord(){
		mRecorder = DemoUtil.findAudioRecord();
		if(mRecorder != null){
			mRecorder.startRecording();
			mIsRecording = true;
			new Thread(new Runnable() {
				@Override
				public void run() {
					waitingForConnected();
					readDataFromAudioRecord();
				}
			}).start();
		}
	}
	
	/*
	 * 录音之前先发送随意的数据去连接手机，连接之后才能把连续的数据发送到手机
	 * 连接的状态可以用mDemoModule.isConnected()去查询，也可以监听mDemoModule中的onConnectionStateChanged的参数变化
	 */
	private void waitingForConnected(){
		SyncData data = new SyncData();
		data.putString(DemoUtil.KEY_CONNECT, "connect...");
		try {
			mDemoModule.send(data);
			while(!mDemoModule.isConnected()){
				Thread.sleep(100);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void onStopRecord(){
		if(mRecorder != null){
			mIsRecording = false;
			mRecorder.stop();
			mRecorder.release();
			mRecorder = null;
		}
	}
		
	private void readDataFromAudioRecord() {
		byte[] audioDataInBytes = new byte[DemoUtil.gBufferSize];
		int readsize = 0;
		while (mIsRecording) {
			Message msg = mHandler.obtainMessage(UPDATE_VIEW_WATCH_RECORD);
			readsize = mRecorder.read(audioDataInBytes, 0, DemoUtil.gBufferSize);
			if (AudioRecord.ERROR_INVALID_OPERATION != readsize) {
				SyncData data = new SyncData();
				data.putByteArray(DemoUtil.KEY_RECORD_DATA, audioDataInBytes);
				
				Log.d(TAG,"send data length:" + audioDataInBytes.length);
				msg.obj = data;
				mHandler.sendMessage(msg);
				mTime = java.lang.System.currentTimeMillis();
				try {
					mDemoModule.send(data);
				} catch (SyncException e) {
					Log.d(TAG,e.getMessage());
				}
			}
		}
	}
	
	private void constructDataForTransport() {
		Message msg = mHandler.obtainMessage(UPDATE_VIEW_WATCH_RECORD);
		SyncData data = new SyncData();
		data.putBoolean(KEY_BOOL, true);
		data.putInt(KEY_INT, 123);
		data.putLong(KEY_LONG, java.lang.System.currentTimeMillis());
		data.putString(KEY_STRING, "data transport test");

		msg.obj = data;
		mHandler.sendMessage(msg);
		mTime = java.lang.System.currentTimeMillis();
		try {
			mDemoModule.send(data);
		} catch (SyncException e) {
			Log.d(TAG, e.getMessage());
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
		
