package com.liangmayong.boothmsg;

import java.util.List;

import com.liangmayong.booth.BoothManager;
import com.liangmayong.booth.BoothRequestMonitor;
import com.liangmayong.booth.BoothStateMonitor;
import com.liangmayong.booth.OnBoothRequestListener;
import com.liangmayong.booth.OnBoothStateListener;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RequestActivity extends BaseActivity {

	private LinearLayout request_list_linearlayout, request_ing_linearlayout,
			request_bluetooth_list;
	private Button request_bluetooth_enabled, request_button;
	private TextView request_text;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_request);
		stateMonitor.startMonitor(this);
		if (BoothManager.isConnected()) {
			startActivity(new Intent(getApplicationContext(),
					ConnectedActivity.class));
		}
		request_list_linearlayout = (LinearLayout) findViewById(R.id.request_list_linearlayout);
		request_ing_linearlayout = (LinearLayout) findViewById(R.id.request_ing_linearlayout);
		request_bluetooth_list = (LinearLayout) findViewById(R.id.request_bluetooth_list);
		request_bluetooth_enabled = (Button) findViewById(R.id.request_bluetooth_enabled);
		request_text = (TextView) findViewById(R.id.request_text);
		request_button = (Button) findViewById(R.id.request_button);
		if (BoothManager.isEnabled()) {
			request_bluetooth_enabled.setText(R.string.close);
		} else {
			request_bluetooth_enabled.setText(R.string.open);
		}
		request_bluetooth_enabled.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (BoothManager.isEnabled()) {
					BoothManager.closeBluetooth();
				} else {
					BoothManager.openBluetooth();
				}
			}
		});
		request_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				requestMonitor.stopRequesting();
			}
		});
		initList();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		requestMonitor.stopRequesting();
		stateMonitor.stopMonitor();
		BoothManager.disconnect();
	}

	private void initList() {
		request_bluetooth_list.removeAllViews();
		if (BoothManager.isEnabled()) {
			List<BluetoothDevice> list = BoothManager.getBluetoothDevices();
			if (list.size() > 0) {
				request_bluetooth_list.addView(ViewHandler.getSubView(
						getApplicationContext(), "Have paired devices", false));
				for (int i = 0; i < list.size(); i++) {
					final BluetoothDevice device = list.get(i);
					if (device != null) {
						View view = ViewHandler.getChildView(
								getApplicationContext(), device.getName());
						view.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								requestMonitor.startRequesting(
										getApplicationContext(), device, 30000);
							}
						});
						request_bluetooth_list.addView(view);
					}
				}
			}
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		initList();
		if (BoothManager.isConnected()) {
			BoothManager.disconnect();
		}
		request_list_linearlayout.setVisibility(0);
		request_ing_linearlayout.setVisibility(8);
	}

	private BoothRequestMonitor requestMonitor = new BoothRequestMonitor(
			new OnBoothRequestListener() {
				@Override
				public void stopConnecting(BluetoothDevice arg0) {
					request_list_linearlayout.setVisibility(0);
					request_ing_linearlayout.setVisibility(8);
				}

				@Override
				public void connectOk(BluetoothSocket arg0) {
					BoothManager.connected(getApplicationContext(), arg0);
					request_button
							.setBackgroundResource(R.drawable.button_info_cicular);
					request_text.setText(arg0.getRemoteDevice().getName()
							+ "\n连接成功");
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							if (BoothManager.isConnected()) {
								startActivity(new Intent(
										getApplicationContext(),
										ConnectedActivity.class));
							}
						}
					}, 200);
				}

				@Override
				public void connecting(BluetoothDevice arg0, long arg1) {
					request_list_linearlayout.setVisibility(8);
					request_ing_linearlayout.setVisibility(0);
					request_button
							.setBackgroundResource(R.drawable.button_red_cicular);
					request_text.setText(arg0.getName() + "\n" + arg1 / 1000
							+ "秒后停止请求");
				}
			});

	public void Accept(View v) {
		if (!BoothManager.isEnabled()) {
			toast("请先开启蓝牙！");
			return;
		}
		startActivity(new Intent(getApplicationContext(), AcceptActivity.class));
	}

	private BoothStateMonitor stateMonitor = new BoothStateMonitor(
			new OnBoothStateListener() {
				@Override
				public void onEnableChanged(boolean enable, boolean ing) {
					request_list_linearlayout.setVisibility(0);
					request_ing_linearlayout.setVisibility(8);
					if (ing) {
						request_bluetooth_enabled.setEnabled(false);
						if (enable) {
							request_bluetooth_list.removeAllViews();
							request_bluetooth_list.addView(ViewHandler
									.getSubView(
											getApplicationContext(),
											getString(R.string.StartingBluetooth),
											true));
							request_bluetooth_enabled.setText(R.string.opening);
						} else {
							request_bluetooth_list.removeAllViews();
							request_bluetooth_list.addView(ViewHandler
									.getSubView(
											getApplicationContext(),
											getString(R.string.ClosingBluetooth),
											true));
							request_bluetooth_enabled
									.setText(R.string.closeing);
						}
					} else {
						request_bluetooth_enabled.setEnabled(true);
						if (enable) {
							initList();
							request_bluetooth_enabled.setText(R.string.close);
						} else {
							initList();
							request_bluetooth_list.addView(ViewHandler
									.getSubView(
											getApplicationContext(),
											getString(R.string.NotStartBluetooth),
											false));
							request_bluetooth_enabled.setText(R.string.open);
						}
					}
				}

				@Override
				public void onBondChanged(BluetoothDevice arg0, int arg1) {

				}
			});

}
