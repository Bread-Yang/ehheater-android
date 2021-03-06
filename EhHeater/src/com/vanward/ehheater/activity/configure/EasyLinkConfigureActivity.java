package com.vanward.ehheater.activity.configure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easylink.android.EasyLinkWifiManager;
import com.easylink.android.FirstTimeConfig2;
import com.easylink.android.FirstTimeConfigListener;
import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.EhHeaterBaseActivity;
import com.vanward.ehheater.activity.global.Consts;
import com.vanward.ehheater.activity.global.Global;
import com.vanward.ehheater.activity.main.electric.ElectricMainActivity;
import com.vanward.ehheater.activity.main.furnace.FurnaceMainActivity;
import com.vanward.ehheater.activity.main.gas.GasMainActivity;
import com.vanward.ehheater.bean.HeaterInfo;
import com.vanward.ehheater.dao.HeaterInfoDao;
import com.vanward.ehheater.service.AccountService;
import com.vanward.ehheater.service.HeaterInfoService;
import com.vanward.ehheater.service.HeaterInfoService.HeaterType;
import com.vanward.ehheater.util.EasyLinkDialogUtil;
import com.vanward.ehheater.util.L;
import com.vanward.ehheater.util.NetworkStatusUtil;
import com.vanward.ehheater.util.SharedPreferUtils;
import com.vanward.ehheater.util.SharedPreferUtils.ShareKey;
import com.vanward.ehheater.util.TextStyleUtil;
import com.xtremeprog.xpgconnect.XPGConnectClient;
import com.xtremeprog.xpgconnect.generated.EasylinkResp_t;
import com.xtremeprog.xpgconnect.generated.XpgEndpoint;

public class EasyLinkConfigureActivity extends EhHeaterBaseActivity implements
		OnClickListener, FirstTimeConfigListener {

	private static final String TAG = "EasyLinkConfigureActivity";

	public static final String DIRECT_CONNECT_AFTER_EASYLINK = "directLinkAfterEasyLink";

	private RelativeLayout mRlStepContainer;
	private Button mBtnNextStep;
	private TextView mTvWifiSsid;
	private EditText mEtWifiPsw;
	private Dialog dialog_easylink;

	private Map<Integer, View> mMapStepViews = new HashMap<Integer, View>();
	static int curindex = 0;

	HeaterType mType = HeaterType.ELECTRIC_HEATER;

	private void initHeaterType() {
		String typeStr = getIntent().getStringExtra("type");
		if (typeStr == null) {
			return;
		}
		if (typeStr.equals("gas")) {
			mType = HeaterType.GAS_HEATER;
			setTopText(R.string.setting_new_device);
		} else if (typeStr.equals("furnace")) {
			mType = HeaterType.FURNACE;
			setTopText(R.string.setting_new_furnace);
		} else if (typeStr.equals("elect")) {
			mType = HeaterType.ELECTRIC_HEATER;
			setTopText(R.string.setting_new_device);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void initUI() {
		super.initUI();
		initHeaterType();
		curindex = 0;
		setCenterView(R.layout.activity_configure);
		setRightButton(View.INVISIBLE);
		setLeftButtonBackground(R.drawable.icon_back);

		mBtnNextStep = (Button) findViewById(R.id.ac_btn_next_step);
		mBtnNextStep.setOnClickListener(this);
		mRlStepContainer = (RelativeLayout) findViewById(R.id.ac_rl_step_container);
		mRlStepContainer.addView(getStepView(1));

		// initEasyLinkDialog();
		dialog_easylink = EasyLinkDialogUtil.initEasyLinkDialog(this,
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mEasyLinkTimeoutTimer.cancel();
						dialog_easylink.dismiss();
						stopEasyLink();
					}
				});

		IntentFilter filter = new IntentFilter(
				Consts.INTENT_FILTER_KILL_CONFIGURE_ACTIVITY);
		BroadcastReceiver receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				finish();
			}
		};
		LocalBroadcastManager.getInstance(getBaseContext()).registerReceiver(
				receiver, filter);

		// new Handler().postDelayed(new Runnable() {
		//
		// @Override
		// public void run() {
		// startActivity(new Intent(EasyLinkConfigureActivity.this,
		// ManualConfigFailActivity.class));
		// }
		// }, 5000);
	}

	@Override
	protected void onPause() {
		super.onPause();
		XPGConnectClient.RemoveActivity(this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
	}

	@Override
	protected void onResume() {
		super.onResume();
		applyCurWifiSsid();

		// if (curindex == 3 && !dialog_easylink.isShowing()) {
		// mRlStepContainer.removeAllViews();
		// mRlStepContainer.addView(getStepView(1));
		// }
		if (getIntent().getBooleanExtra("isStep2", false)) {
			onBackPressed();
		}
		XPGConnectClient.AddActivity(this);
	}

	@Override
	public void onBackPressed() {
		int childCount = mRlStepContainer.getChildCount();

		if (childCount > 1) {
			mRlStepContainer.removeViewAt(childCount - 1);
			mRlStepContainer.getChildAt(childCount - 2).setVisibility(
					View.VISIBLE);
		} else {

			boolean shouldKillProcess = getIntent()
					.getBooleanExtra(
							Consts.INTENT_EXTRA_CONFIGURE_ACTIVITY_SHOULD_KILL_PROCESS_WHEN_FINISH,
							false);
			if (shouldKillProcess) {
				android.os.Process.killProcess(android.os.Process.myPid());
			} else {
				super.onBackPressed();
			}

		}
	}

	private void nextStep() {
		int curStep = mRlStepContainer.getChildCount();
		View step = getStepView(curStep + 1);
		if (step != null) {
			if (mRlStepContainer.getChildCount() != 3) {
				mRlStepContainer.getChildAt(
						mRlStepContainer.getChildCount() - 1).setVisibility(
						View.INVISIBLE);
			}
			mRlStepContainer.addView(step);
		} else {
			// 到了最后一步
			easyLink();
		}

	}

	private View getStepView(int whichStep) {

		View v = mMapStepViews.get(whichStep);
		if (v == null) {
			v = initStepView(whichStep);
			mMapStepViews.put(whichStep, v);
		}

		if (v != null && v.getParent() != null) {
			((ViewGroup) v.getParent()).removeView(v);
		}

		return v;
	}

	private View initStepView(int whichStep) {

		View v = null;

		switch (whichStep) {

		case 1:
			v = getLayoutInflater().inflate(R.layout.activity_configure_step1,
					mRlStepContainer, false);
			TextView s1tip = (TextView) v.findViewById(R.id.acs1_tv_tip);
			ImageView img = (ImageView) v.findViewById(R.id.img);

			switch (mType) {
			case ELECTRIC_HEATER:
				img.setImageResource(R.drawable.setting_img1);
				break;
			case GAS_HEATER:
				img.setImageResource(R.drawable.device_img1_1);
				break;
			case FURNACE:
				img.setImageResource(R.drawable.device_img3_1);
				s1tip.setText(R.string.set_device_tip2_eh_furnace);
				break;
			default:
				break;
			}

			TextStyleUtil.setColorStringInTextView(s1tip,
					Color.parseColor("#ff5f00"), new String[] { "电源" });

			break;

		case 2:

			v = getLayoutInflater().inflate(R.layout.activity_configure_step2,
					mRlStepContainer, false);
			TextView tv_tips = (TextView) v.findViewById(R.id.tv_tips);
			mTvWifiSsid = (TextView) v.findViewById(R.id.acs2_tv_ssid);
			mEtWifiPsw = (EditText) v.findViewById(R.id.acs2_et_psw);

			if (mType == HeaterType.FURNACE) {
				tv_tips.setText(R.string.set_device_tip3_eh_furnace);
			}
			
			v.findViewById(R.id.llt_root).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

				}
			});
			
			v.findViewById(R.id.llt_root).setOnFocusChangeListener(
					new OnFocusChangeListener() {

						@Override
						public void onFocusChange(View view, boolean hasFocus) {
							if (hasFocus) {
								((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
										.hideSoftInputFromWindow(
												EasyLinkConfigureActivity.this
														.getCurrentFocus()
														.getWindowToken(),
												InputMethodManager.HIDE_NOT_ALWAYS);
							} else {
							}
						}
					});

			applyCurWifiSsid();
			break;
		case 3:
			v = getLayoutInflater().inflate(R.layout.activity_configure_step3,
					mRlStepContainer, false);
			TextView s3tip = (TextView) v.findViewById(R.id.acs3_tv_tip);
			ImageView img3 = (ImageView) v.findViewById(R.id.img);

			switch (mType) {
			case ELECTRIC_HEATER:
				img3.setImageResource(R.drawable.setting_img4);
				s3tip.setText(R.string.setup_step3_eh);
				TextStyleUtil.setColorStringInTextView(s3tip,
						Color.parseColor("#ff5f00"),
						new String[] { "3秒", "响一声" });
				break;
			case GAS_HEATER:
				img3.setImageResource(R.drawable.setting_img5);
				s3tip.setText(R.string.setup_step3_st);
				TextStyleUtil.setColorStringInTextView(s3tip,
						Color.parseColor("#ff5f00"), new String[] { "一下",
								"听到蜂鸣" });
				break;
			case FURNACE:
				img3.setImageResource(R.drawable.device_img3);
				s3tip.setText(R.string.setup_step3_eh_furnace);
				TextStyleUtil.setColorStringInTextView(s3tip,
						Color.parseColor("#ff5f00"),
						new String[] { "3秒", "响一声" });
			default:
				break;
			}

			break;

		}

		return v;

	}

	private void applyCurWifiSsid() {
		String curWifiSsid = new EasyLinkWifiManager(getBaseContext())
				.getCurrentSSID();
		if (mTvWifiSsid != null) {
			mTvWifiSsid.setText(curWifiSsid);
		}

		new SharedPreferUtils(getBaseContext()).put(ShareKey.PendingSsid,
				curWifiSsid);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ac_btn_next_step:
			if (!NetworkStatusUtil.isConnected(this)) {
				Toast.makeText(getBaseContext(), "请连接至wifi网络", 500).show();
				return;
			}
			if ((mRlStepContainer.getChildCount() == 1 || mRlStepContainer
					.getChildCount() == 2) && mTvWifiSsid != null) {
				applyCurWifiSsid();
			}

			if (mRlStepContainer.getChildCount() == 2
					&& mTvWifiSsid != null
					&& "<unknown ssid>"
							.equals(mTvWifiSsid.getText().toString())) {
				Toast.makeText(getBaseContext(), "请连接至wifi网络", 500).show();
				return;
			}

			// if (mRlStepContainer.getChildCount() == 2
			// &&TextUtils.isEmpty(mEtWifiPsw.getText().toString())&& mEtWifiPsw
			// != null) {
			// Toast.makeText(getBaseContext(), "请输入wifi密码", 500).show();
			// return;
			// }

			nextStep();
			break;
		case R.id.btn_left:
			onBackPressed();
			break;
		}
	}

	private void easyLink() {

		EasyLinkWifiManager manager = new EasyLinkWifiManager(this);
		String ssid = manager.getCurrentSSID();
		String gateway = manager.getGatewayIpAddress();
		String pss = /* "go4xpggo4xpg" */mEtWifiPsw.getText().toString();

		dialog_easylink.show();
		mEasyLinkTimeoutTimer.start();

		try {
			configer = new FirstTimeConfig2(this, pss, null, gateway, ssid);
			configer.transmitSettings();
			isWaitingCallback = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	FirstTimeConfig2 configer;
	EasyLinkTimeoutCounter mEasyLinkTimeoutTimer = new EasyLinkTimeoutCounter(
			60000, 1000);

	private void stopEasyLink() {
		try {
			configer.stopTransmitting();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onFirstTimeConfigEvent(FtcEvent paramFtcEvent,
			Exception paramException) {

	}

	@Override
	public void onEasyLinkResp(XpgEndpoint endpoint) {
		super.onEasyLinkResp(endpoint);
		L.e(this, "onEasyLinkResp(XpgEndpoint endpoint)回调了");

		L.e(this,
				"null == endpoint.getSzProductKey() : "
						+ (null == endpoint.getSzProductKey()));
		L.e(this, ("endpoint.getSzProductKey()为空字符串 : " + "".equals(endpoint
				.getSzProductKey())));

		L.e(this,
				"null == endpoint.getSzMac() : "
						+ (null == endpoint.getSzMac()));
		L.e(this,
				("endpoint.getSzMac()为空字符串 : " + "".equals(endpoint.getSzMac())));

		L.e(this,
				"null == endpoint.getSzDid() : "
						+ (null == endpoint.getSzDid()));
		L.e(this,
				("endpoint.getSzDid()为空字符串 : " + "".equals(endpoint.getSzDid())));

		L.e(this,
				"null == endpoint.getSzPasscode() : "
						+ (null == endpoint.getSzPasscode()));
		L.e(this, ("endpoint.getSzPasscode()为空字符串 : " + "".equals(endpoint
				.getSzPasscode())));
	}

	@Override
	public void OnEasylinkResp(EasylinkResp_t pResp) {
		super.OnEasylinkResp(pResp);
		L.e(this, "OnEasylinkResp(EasylinkResp_t pResp)回调了");
	}

	@Override
	public void onAirLinkResp(final XpgEndpoint endpoint) {
		super.onAirLinkResp(endpoint);
		L.e(this, "OnAirLinkResp()回调了");
		if (isWaitingCallback) {
			
			// 配置成功, 保存设备(此时密码为空), 跳转回welcome

			// L.e(this, "null == endpoint.getSzProductKey() : " + (null ==
			// endpoint.getSzProductKey()));
			// L.e(this, ("endpoint.getSzProductKey()为空字符串 : " +
			// "".equals(endpoint.getSzProductKey())));
			//
			// L.e(this, "null == endpoint.getSzMac() : " + (null ==
			// endpoint.getSzMac()));
			// L.e(this, ("endpoint.getSzMac()为空字符串 : " +
			// "".equals(endpoint.getSzMac())));
			//
			// L.e(this, "null == endpoint.getSzDid() : " + (null ==
			// endpoint.getSzDid()));
			// L.e(this, ("endpoint.getSzDid()为空字符串 : " +
			// "".equals(endpoint.getSzDid())));

			// L.e(this, "null == endpoint.getSzPasscode() : " + (null ==
			// endpoint.getSzPasscode()));
			// L.e(this, ("endpoint.getSzPasscode()为空字符串 : " +
			// "".equals(endpoint.getSzPasscode())));

			if (endpoint.getSzProductKey() == null
					|| "".equals(endpoint.getSzProductKey())) {
				return;
			}
			if (endpoint.getSzMac() == null || "".equals(endpoint.getSzMac())) {
				return;
			}
			if (endpoint.getSzDid() == null || "".equals(endpoint.getSzDid())) {
				return;
			}
			// if (endpoint.getSzPasscode() == null
			// || "".equals(endpoint.getSzPasscode())) {
			// return;
			// }
			L.e(this, "onEasyLinkResp()返回的endpoint.getSzProductKey() : "
					+ endpoint.getSzProductKey());
			L.e(this,
					"onEasyLinkResp()返回的endpoint.getSzMac() : "
							+ endpoint.getSzMac());
			L.e(this,
					"onEasyLinkResp()返回的endpoint.getSzDid() : "
							+ endpoint.getSzDid());
			L.e(this,
					"onEasyLinkResp()返回的endpoint.getAddr() : "
							+ endpoint.getAddr());

			// L.e(this, "onEasyLinkResp()返回的endpoint.getSzPasscode() : " +
			// endpoint.getSzPasscode());

			// if (endpoint.getSzDid() == null ||
			// "".equals(endpoint.getSzDid())) {
			// return;
			// }
			// if (endpoint.getSzPasscode() == null ||
			// "".equals(endpoint.getSzPasscode())) {
			// return;
			// }
			isWaitingCallback = false;
			stopEasyLink();
			mEasyLinkTimeoutTimer.cancel();
			dialog_easylink.dismiss();
			XPGConnectClient.RemoveActivity(this);

			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					finishingConfig(endpoint);
					// endpoint.delete();
				}
			}, 300);
		}
	}

	boolean isWaitingCallback = false;

	private void finishingConfig(XpgEndpoint endpoint) {

		HeaterInfo hinfo = new HeaterInfo(AccountService.getUserId(getApplicationContext()), endpoint);
		HeaterInfoService hser = new HeaterInfoService(getBaseContext());

		if (!hser.isValidDevice(hinfo)) {
			Toast.makeText(getBaseContext(), "无法识别该设备", Toast.LENGTH_LONG)
					.show();
			return;
		}

		if (!mType.pkey.equals(hinfo.getProductKey())) {
			Toast.makeText(getBaseContext(), "设备类型错误", Toast.LENGTH_LONG)
					.show();
			return;
		}

		Toast.makeText(getBaseContext(), "配置成功!", 1000).show();

		HeaterInfoDao hdao = new HeaterInfoDao(this);
		List<HeaterInfo> list = hdao.getAllByUid(AccountService.getUserId(getApplicationContext()));

		boolean flag = false;

		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getMac().equals(hinfo.getMac())) {
				flag = true;
			}
		}

		boolean directLinkAfterEasyLink = true;
		if (flag) {
			Toast.makeText(this, "此设备已存在", 1000).show();
			new SharedPreferUtils(this).put(ShareKey.CurDeviceMac,
					hinfo.getMac());
		} else {
			hser.addNewHeater(hinfo);
		}

		SharedPreferUtils spu = new SharedPreferUtils(this);
		if (hser.getHeaterType(hinfo) == HeaterType.ELECTRIC_HEATER) {
			spu.put(ShareKey.PollingElectricHeaterDid, hinfo.getDid());
			spu.put(ShareKey.PollingElectricHeaterMac, hinfo.getMac());
		} else if (hser.getHeaterType(hinfo) == HeaterType.GAS_HEATER) {
			spu.put(ShareKey.PollingGasHeaterDid, hinfo.getDid());
			spu.put(ShareKey.PollingGasHeaterMac, hinfo.getMac());
		} else if (hser.getHeaterType(hinfo) == HeaterType.FURNACE) {
			spu.put(ShareKey.PollingFurnaceDid, hinfo.getDid());
			spu.put(ShareKey.PollingFurnaceMac, hinfo.getMac());
		}

		new SharedPreferUtils(this).put(ShareKey.CurDeviceDid,
				endpoint.getSzDid());
		new SharedPreferUtils(this).put(ShareKey.CurDeviceAddress,
				endpoint.getAddr());

		L.e(this, "成功通过easylink将电器连上wifi");

		String username = AccountService.getPendingUserId(getBaseContext());
		String userpsw = AccountService.getPendingUserPsw(getBaseContext());
		if (!AccountService.isLogged(getBaseContext())
				&& !TextUtils.isEmpty(username) && !TextUtils.isEmpty(userpsw)) {
			AccountService.setUser(getBaseContext(), username, userpsw);
		}

		// send a broad cast to finish login activity
		Intent killerIntent = new Intent(
				Consts.INTENT_FILTER_KILL_LOGIN_ACTIVITY);
		LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(
				killerIntent);

		Intent intent = null;
		
		L.e(this, "XPGConnectClient.xpgcDisconnectAsync()");
		XPGConnectClient.xpgcDisconnectAsync(Global.connectId);

		switch (hser.getHeaterType(hinfo)) {

		case ELECTRIC_HEATER:

			intent = new Intent(getBaseContext(), ElectricMainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra(DIRECT_CONNECT_AFTER_EASYLINK,
					directLinkAfterEasyLink);

			XPGConnectClient.RemoveActivity(this);
			finish();

			startActivity(intent);
			break;

		case GAS_HEATER:

			intent = new Intent(getBaseContext(), GasMainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra(DIRECT_CONNECT_AFTER_EASYLINK,
					directLinkAfterEasyLink);

			XPGConnectClient.RemoveActivity(this);
			finish();

			startActivity(intent);
			break;

		case FURNACE:

			intent = new Intent(getBaseContext(), FurnaceMainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra(DIRECT_CONNECT_AFTER_EASYLINK,
					directLinkAfterEasyLink);

			XPGConnectClient.RemoveActivity(this);
			finish();

			startActivity(intent);
			break;

		default:
			Toast.makeText(getBaseContext(), "无法识别该设备", Toast.LENGTH_LONG)
					.show();
			break;

		}

	}

	private class EasyLinkTimeoutCounter extends CountDownTimer {

		public EasyLinkTimeoutCounter(long millisInFuture,
				long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			dialog_easylink.dismiss();
			stopEasyLink();
			curindex = 3;
			Intent intent = new Intent(getBaseContext(),
					AutoConfigureFailActivity.class);
			if (mType == HeaterType.FURNACE) {
				intent.putExtra("isFurnace", true);
			}
			startActivityForResult(intent, 0);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			((TextView) dialog_easylink.findViewById(R.id.easylink_time_tv))
					.setText(millisUntilFinished / 1000 + "");
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mEasyLinkTimeoutTimer != null) {
			mEasyLinkTimeoutTimer.cancel();
			mEasyLinkTimeoutTimer = null;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			onBackPressed();
		}
	}
}
