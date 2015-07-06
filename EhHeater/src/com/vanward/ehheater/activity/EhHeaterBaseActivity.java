package com.vanward.ehheater.activity;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.vanward.ehheater.R;
import com.vanward.ehheater.util.HttpFriend;
import com.vanward.ehheater.util.L;
import com.vanward.ehheater.util.NetworkStatusUtil;
import com.vanward.ehheater.util.UIUtil;
import com.xtremeprog.xpgconnect.generated.GeneratedActivity;

public class EhHeaterBaseActivity extends GeneratedActivity implements
		OnClickListener {

	protected Button btn_left, btn_right;
	private TextView tv_center_title;
	private RelativeLayout rlt_center, rlt_top, rlt_center_no_scrollview,
			rlt_loading;
	public Intent intent;

	protected HttpFriend mHttpFriend;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		L.e(this, "onCreate()");
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
		rlt_loading = (RelativeLayout) findViewById(R.id.rlt_loading);
		tv_center_title = (TextView) findViewById(R.id.tv_center_title);
		rlt_top = (RelativeLayout) findViewById(R.id.rlt_title);
	}

	public void initListener() {
		UIUtil.setOnClick(this, btn_left, btn_right);

		rlt_loading.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
	}

	public void initData() {
		intent = new Intent();
		mHttpFriend = HttpFriend.create(this);
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

	protected void executeRequest(String requestURL, AjaxParams params,
			final AjaxCallBack callBack) {
		if (!NetworkStatusUtil.isConnected(this)) {
			Toast.makeText(this, "网络访问不了,确保打开GPRS或者WiFi网络",
					Toast.LENGTH_LONG).show();
			return;
		}
		rlt_loading.setVisibility(View.VISIBLE);

		mHttpFriend.toUrl(requestURL).executePost(params,
				new AjaxCallBack<String>() {
					@Override
					public void onSuccess(String jsonString) {
						callBack.onSuccess(jsonString);
						hideLoadingLayout();
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						callBack.onFailure(t, errorNo, strMsg);
						hideLoadingLayout();
					}

					@Override
					public void onStart() {
						callBack.onStart();
					}

					@Override
					public void onLoading(long count, long current) {
						callBack.onLoading(count, current);
					}
					
					@Override
					public void onTimeout() {
						callBack.onTimeout();
						hideLoadingLayout();
						super.onTimeout();
					}

				});
	}
	
	private void hideLoadingLayout() {
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				rlt_loading.setVisibility(View.GONE);
			}
		}, mHttpFriend.delaySeconds * 1000);
	}
}
