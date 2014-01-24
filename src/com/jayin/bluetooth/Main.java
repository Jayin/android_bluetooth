package com.jayin.bluetooth;

import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Main extends BaseActivity {
	private int REQUEST_ENABLE = 1;
	private TextView tv;
	private String info = "";
	private View test1, test2, test3;
	private BluetoothAdapter mAdapter;
	// to receiver the broadcast when it has found the other bluetooth device
	private BroadcastReceiver mReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initData();
		initLayout();
		mReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
					BluetoothDevice device = intent
							.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					//没有被绑定
					if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                       info+="find device-> " + device.getName() +" address-> "+ device.getAddress()+"\n";
						tv.setText(info);
					}
				} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
						.equals(intent.getAction())) {
					Set<BluetoothDevice> pairDevice = mAdapter.getBondedDevices();
					info+="total number of found : "+pairDevice.size()+"\nfind over\n";
					tv.setText(info);
				}
			}
		};
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(mReceiver, filter);
	}

	@Override
	protected void initData() {

	}

	@Override
	protected void initLayout() {
		tv = (TextView) _getView(R.id.textView1);
		test1 = _getView(R.id.button1);
		test2 = _getView(R.id.button2);
		test3 = _getView(R.id.button3);
		test1.setOnClickListener(this);
		test2.setOnClickListener(this);
		test3.setOnClickListener(this);
		init();
		
	}

	// init bluetooth settting
	private void init() {
		// 获取本地蓝牙适配器
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		mAdapter.setName("Bluetooth Test");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button1: // to open
			if (!mAdapter.isEnabled()) {
				// 弹出对话框提示用户是后打开
				Intent enabler = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enabler, REQUEST_ENABLE);
				// called this mAdapter.enable();
			} else {
				toast("It's opening");
			}
			break;
		case R.id.button2:
		      info+="start to find....\n";
              if(mAdapter.startDiscovery()){
            	  info+="start to find....success!\n";
            	  tv.setText(info);
              }else{
            	  info+="start to find....faild!\n";
            	  tv.setText(info);
              }
			break;
		case R.id.button3:
			//请求设备可以被发现，默认是120s内 可以发现; 0-3600,值为0时，一直可以被发现
			//没有打开蓝牙的时候自动打开，并出现这个对话框内			
			int defalutDiscoverableTime = 120;
			Intent discoverableIntent = new
			Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, defalutDiscoverableTime);
			startActivity(discoverableIntent);
			break;

		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_ENABLE) {
			if (resultCode == RESULT_OK) {
				toast("request ok!");
			} else {
				toast("request canle");
			}
		}
	}
	
	private void startServer(){
//		BluetoothServerSocket serverSocket = mAdapter.listenUsingRfcommWithServiceRecord(serverSocketName,UUID);
//
//		serverSocket.accept();
	}

}
