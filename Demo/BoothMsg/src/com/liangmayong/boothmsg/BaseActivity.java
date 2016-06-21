package com.liangmayong.boothmsg;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class BaseActivity extends Activity {
	public void back(View v) {
		finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	@SuppressLint("ShowToast")
	public void toast(String msg) {
		Toast.makeText(this, msg, 1500).show();
	}
}
