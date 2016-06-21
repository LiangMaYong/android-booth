package com.liangmayong.boothmsg;

import com.liangmayong.booth.BoothAcceptMonitor;
import com.liangmayong.booth.BoothManager;
import com.liangmayong.booth.OnBoothAcceptListener;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class AcceptActivity extends BaseActivity {
	private BoothAcceptMonitor acceptMonitor =new BoothAcceptMonitor(new OnBoothAcceptListener() {

				@Override
				public void acceptOk(BluetoothSocket arg0) {
					accept_text.setText(arg0.getRemoteDevice().getName());
					BoothManager.connected(getApplicationContext(), arg0);
					accept_button
							.setBackgroundResource(R.drawable.button_info_cicular);
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							finish();
							startActivity(new Intent(getApplicationContext(),
									ConnectedActivity.class));
						}
					}, 200);
				}

				@Override
				public void accepting(long arg0) {
					accept_text.setText((arg0 / 1000) + "秒后自动停止接收");
					accept_button
							.setBackgroundResource(R.drawable.button_red_cicular);
				}

				@Override
				public void stopAccepting() {
					accept_button
							.setBackgroundResource(R.drawable.button_nor_cicular);
					finish();
				}
			});

	private Button accept_button;
	private TextView accept_text;
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_accept);
		accept_button = (Button) findViewById(R.id.accept_button);
		accept_text = (TextView) findViewById(R.id.accept_text);
		acceptMonitor.startAccepting(getApplicationContext(), 30000);
		accept_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				acceptMonitor.stopAccepting();
			}
		});
	}

}
