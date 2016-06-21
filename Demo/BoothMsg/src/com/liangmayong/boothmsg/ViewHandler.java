package com.liangmayong.boothmsg;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ViewHandler {

	public static View getSubView(Context context, String text, boolean pro) {
		View view = LayoutInflater.from(context).inflate(
				R.layout.list_device_title, null);
		TextView textView = (TextView) view.findViewById(R.id.item_text);
		textView.setText(text);
		ProgressBar item_progressBar = (ProgressBar) view
				.findViewById(R.id.item_progressBar);
		if (pro) {
			item_progressBar.setVisibility(0);
		} else {
			item_progressBar.setVisibility(8);
		}
		return view;
	}

	public static View getToView(Context context, String msg) {
		View view = LayoutInflater.from(context).inflate(R.layout.list_to_item,
				null);
		TextView textView = (TextView) view.findViewById(R.id.item_text);
		textView.setText(msg);
		return view;
	}

	public static View getFromView(Context context, String msg) {
		View view = LayoutInflater.from(context).inflate(
				R.layout.list_from_item, null);
		TextView textView = (TextView) view.findViewById(R.id.item_text);
		textView.setText(msg);
		return view;
	}

	public static View getChildView(Context context, String name) {
		View view = LayoutInflater.from(context).inflate(
				R.layout.list_device_item, null);
		TextView textView = (TextView) view.findViewById(R.id.item_text);
		textView.setText(name);
		return view;
	}

	public static void scrollToBottom(final View scroll, final View inner) {
		Handler mHandler = new Handler();
		mHandler.post(new Runnable() {
			public void run() {
				if (scroll == null || inner == null) {
					return;
				}
				int offset = inner.getMeasuredHeight() - scroll.getHeight();
				if (offset < 0) {
					offset = 0;
				}
				scroll.scrollTo(0, offset);
			}
		});
	}
}
