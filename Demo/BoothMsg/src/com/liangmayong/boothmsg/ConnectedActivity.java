package com.liangmayong.boothmsg;

import com.liangmayong.booth.BoothManager;
import com.liangmayong.booth.OnBoothDisconnectListener;
import com.liangmayong.booth.OnBoothMessageListener;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class ConnectedActivity extends BaseActivity {

	OnBoothDisconnectListener disconnectListener = new OnBoothDisconnectListener() {

		@Override
		public void disconnect() {
			finish();

		}
	};
	OnBoothMessageListener messageListener = new OnBoothMessageListener() {
		@Override
		public void message(BluetoothDevice arg0, byte[] arg1) {
			msg_list.addView(ViewHandler.getFromView(getApplicationContext(),
					new String(arg1)));
			ViewHandler.scrollToBottom(msg_scrollView, msg_list);
		}
	};
	private EditText msg_editText;
	private TextView name_text;
	private ScrollView msg_scrollView;
	private Button send_button;
	private LinearLayout msg_list;
	Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (!BoothManager.isConnected()) {
					finish();
				} else {
					handler.postDelayed(this, 1000);
				}
			}
		}, 1000);
		setContentView(R.layout.activity_connected);
		BoothManager.setOnBoothDisconnectListener(disconnectListener);
		BoothManager.setOnBoothMessageListener(messageListener);
		msg_editText = (EditText) findViewById(R.id.msg_editText);
		name_text = (TextView) findViewById(R.id.name_text);
		if (BoothManager.getConnectedDevice() != null) {
			name_text.setText(BoothManager.getConnectedDevice().getName());
		}
		msg_scrollView = (ScrollView) findViewById(R.id.msg_scrollView);
		send_button = (Button) findViewById(R.id.send_button);
		msg_list = (LinearLayout) findViewById(R.id.msg_list);
		msg_scrollView.setVerticalScrollBarEnabled(false);
		msg_scrollView.setHorizontalScrollBarEnabled(false);
		send_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String msg = msg_editText.getText().toString();
				if (!msg.equals("")) {
					if (BoothManager.isConnected()) {
						try {
							BoothManager.write(msg);
						} catch (Exception e) {
							e.printStackTrace();
						}
						msg_list.addView(ViewHandler.getToView(
								getApplicationContext(), msg));
						msg_editText.setText("");
						hideSoftInput();
						ViewHandler.scrollToBottom(msg_scrollView, msg_list);
					}else{
						finish();
					}
				}
			}
		});
	}

	public void hideSoftInput() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive()) {
			imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	@Override
	public void finish() {
		super.finish();
		BoothManager.disconnect();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
