package com.vanward.ehheater.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.vanward.ehheater.R;
import com.vanward.ehheater.util.BaoDialogShowUtil;
import com.vanward.ehheater.util.UIUtil;
import com.xtremeprog.xpgconnect.generated.GeneratedActivity;

public class EhHeaterBaseActivity extends GeneratedActivity implements
		OnClickListener {

	protected Button btn_left, btn_right;
	private TextView tv_center_title;
	private RelativeLayout rlt_center, rlt_top, rlt_center_no_scrollview;
	public Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base);
		initUI();
		initListener();
		initData();

	}

	public void initUI() {
		btn_left = (Button) findViewById(R.id.btn_left);
		btn_right = (Button) findViewById(R.id.btn_right);
		rlt_center = (RelativeLayout) findViewById(R.id.rlt_center);
		rlt_center_no_scrollview = (RelativeLayout) findViewById(R.id.rlt_center_no_scrollview);
		tv_center_title = (TextView) findViewById(R.id.tv_center_title);
		rlt_top = (RelativeLayout) findViewById(R.id.rlt_title);
	}

	public void initListener() {
		UIUtil.setOnClick(this, btn_left, btn_right);
	}

	public void initData() {
		intent = new Intent();
	}

	public void setCenterView(int resId) {
		LayoutInflater inflater = getLayoutInflater();
		View addView = inflater.inflate(resId, null);
		rlt_center.addView(addView, RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
	}

	public void setCenterViewWithNoScrollView(int resId) {
		LayoutInflater inflater = getLayoutInflater();
		View addView = inflater.inflate(resId, null);
		rlt_center_no_scrollview.addView(addView,
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
		rlt_center_no_scrollview.setVisibility(View.VISIBLE);
	}

	protected void setTopText(int resourceId) {
		tv_center_title.setText(resourceId);
	}

	protected void setTopText(CharSequence text) {
		tv_center_title.setText(text);
	}

	protected void setLeftText(int resourceId) {
		btn_left.setText(resourceId);
	}

	protected void setLeftText(CharSequence text) {
		btn_left.setText(text);
	}

	protected void setRightText(int resourceId) {
		btn_right.setText(resourceId);
	}

	protected void setRightText(CharSequence text) {
		btn_right.setText(text);
	}

	protected void setLeftButtonVisible(int visible) {
		btn_left.setVisibility(visible);
	}

	protected void setRightButton(int visibility) {
		btn_right.setVisibility(visibility);
	}

	protected void setLeftButtonBackground(int resId) {
		btn_left.setText("");
		btn_left.setBackgroundResource(resId);
	}

	protected void setRightButtonBackground(int resId) {
		btn_right.setText("");
		btn_right.setBackgroundResource(resId);
	}

	public void setTopDismiss() {
		rlt_top.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View view) {
		if (view == btn_left) {
			onBackPressed();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
