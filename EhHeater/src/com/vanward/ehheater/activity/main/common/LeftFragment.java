package com.vanward.ehheater.activity.main.common;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.BaseBusinessActivity;
import com.vanward.ehheater.activity.global.Consts;
import com.vanward.ehheater.activity.global.Global;
import com.vanward.ehheater.activity.main.furnace.FurnaceMainActivity;
import com.vanward.ehheater.activity.main.furnace.FurnaceSeasonActivity;
import com.vanward.ehheater.activity.more.AboutActivity;
import com.vanward.ehheater.activity.more.AccountManagementActivity;
import com.vanward.ehheater.activity.more.HeaterManagementActivity;
import com.vanward.ehheater.activity.more.HelpActivity;
import com.vanward.ehheater.activity.more.RemindSettingActivity;
import com.vanward.ehheater.bean.HeaterInfo;
import com.vanward.ehheater.dao.HeaterInfoDao;
import com.vanward.ehheater.service.AccountService;
import com.vanward.ehheater.service.HeaterInfoService;
import com.vanward.ehheater.service.HeaterInfoService.HeaterType;
import com.vanward.ehheater.util.AlterDeviceHelper;
import com.vanward.ehheater.util.L;
import com.vanward.ehheater.util.SharedPreferUtils;
import com.vanward.ehheater.util.SharedPreferUtils.ShareKey;
import com.vanward.ehheater.util.UIUtil;
import com.xtremeprog.xpgconnect.XPGConnectClient;

public class LeftFragment extends LinearLayout implements
		android.view.View.OnClickListener, OnItemClickListener {

	private static final String TAG = "LeftFragment";

	private Button btn_user_manager, btn_device_manager, btn_tip, btn_help,
			btn_about, btn_season_mode;
	private View deviceSwitchLayout, deviceSwitchBtn;
	private RelativeLayout rlt_season_mode;
	private ImageView iv_season_mode;
	private TextView tv_season_mode;

	public LeftFragment(Context context) {
		super(context);
	}

	public LeftFragment(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		btn_about = (Button) findViewById(R.id.menu_about_btn);
		btn_device_manager = (Button) findViewById(R.id.menu_device_manager_btn);
		btn_help = (Button) findViewById(R.id.menu_help_btn);
		btn_tip = (Button) findViewById(R.id.menu_tip_btn);
		btn_user_manager = (Button) findViewById(R.id.menu_user_manager_btn);
		btn_season_mode = (Button) findViewById(R.id.btn_season_mode);
		tv_season_mode = (TextView) findViewById(R.id.tv_season_mode);
		deviceSwitchLayout = findViewById(R.id.device_switch_layout);
		deviceSwitchBtn = findViewById(R.id.device_switch_btn);
		iv_season_mode = (ImageView) findViewById(R.id.iv_season_mode);
		rlt_season_mode = (RelativeLayout) findViewById(R.id.rlt_season_mode);

		if (getContext() instanceof FurnaceMainActivity) {
			((FurnaceMainActivity) getContext())
					.setTv_sliding_menu_season_mode(tv_season_mode);
		} else {
			rlt_season_mode.setVisibility(View.GONE);
			iv_season_mode.setVisibility(View.GONE);
		}

		DeviceAdapter deviceAdapter = new DeviceAdapter(getContext(),
				R.layout.main_left_device_item,
				new HeaterInfoDao(getContext()).getAllByUid(AccountService
						.getUserId(getContext())));
		((ListView) findViewById(R.id.device_listview))
				.setAdapter(deviceAdapter);
		((ListView) findViewById(R.id.device_listview))
				.setOnItemClickListener(this);
		UIUtil.setOnClick(this, btn_about, btn_device_manager, btn_help,
				btn_tip, btn_user_manager, deviceSwitchLayout, deviceSwitchBtn,
				btn_season_mode);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void onClick(View view) {
		Class clazz = null;
		if (view == btn_about) {
			clazz = AboutActivity.class;
		}
		if (view == btn_device_manager) {
			clazz = HeaterManagementActivity.class;
		}
		if (view == btn_help) {
			clazz = HelpActivity.class;
		}
		if (view == btn_tip) {
			clazz = RemindSettingActivity.class;
		}
		if (view == btn_user_manager) {
			clazz = AccountManagementActivity.class;
		}
		if (view == btn_season_mode) {
			clazz = FurnaceSeasonActivity.class;
			tv_season_mode.performClick();
		}

		if (clazz != null) {
			Intent intent = new Intent();
			intent.setClass(getContext(), clazz);
			if (tv_season_mode.getTag() != null) {
				intent.putExtra("seasonMode", (Integer) tv_season_mode.getTag());
			}
			if (clazz == FurnaceSeasonActivity.class) {
				((Activity) getContext()).startActivityForResult(intent, 0);
			} else {
				getContext().startActivity(intent);
			}
		}

		if (view == deviceSwitchBtn) {
			if (deviceSwitchLayout.getVisibility() == View.GONE) {
				deviceSwitchLayout.setVisibility(View.VISIBLE);
			} else {
				deviceSwitchLayout.setVisibility(View.GONE);
			}
		}
	}

	private List<HeaterInfo> objects;

	class DeviceAdapter extends BaseAdapter {

		int resource;

		public DeviceAdapter(Context context, int resource,
				List<HeaterInfo> objects) {
			this.resource = resource;
			LeftFragment.this.objects = objects;
		}

		@Override
		public int getCount() {
			return objects.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder1 = null;
			if (convertView == null) {
				holder1 = new ViewHolder();
				convertView = LayoutInflater.from(getContext()).inflate(
						resource, parent, false);
				holder1.checkImage = (ImageView) convertView
						.findViewById(R.id.device_check_btn);
				holder1.devicename = (TextView) convertView
						.findViewById(R.id.device_name);
				convertView.setTag(holder1);
			} else {
				holder1 = (ViewHolder) convertView.getTag();
			}
			holder1.devicename.setText(Consts.getHeaterName(objects
					.get(position)));
			HeaterInfo heaterInfo = new HeaterInfoService(getContext())
					.getCurrentSelectedHeater();
			if (heaterInfo != null
					&& heaterInfo.getMac().equals(
							objects.get(position).getMac())) {
				holder1.checkImage.setVisibility(View.VISIBLE);
				holder1.devicename.setTextColor(0xFFF76F21);
			} else {
				holder1.checkImage.setVisibility(View.GONE);
				holder1.devicename.setTextColor(getResources().getColor(
						R.color.black));
			}
			holder1.i = position;
			return convertView;
		}

		@Override
		public Object getItem(int position) {
			return objects.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}
	}

	class ViewHolder {
		TextView devicename;
		ImageView checkImage;
		int i;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		ViewHolder viewHolder = (ViewHolder) arg1.getTag();
		HeaterInfo heaterInfo = objects.get(viewHolder.i);
		HeaterInfo shareheaterInfo = new HeaterInfoService(getContext())
				.getCurrentSelectedHeater();
		if (heaterInfo == null || shareheaterInfo == null) {
			L.e(this, "heaterInfo == null || shareheaterInfo == null");
			return;
		}
		if (heaterInfo.getMac().equals(shareheaterInfo.getMac())) {
			L.e(this, "heaterInfo.getMac().equals(shareheaterInfo.getMac())");
			return;
		} else {

			BaseBusinessActivity hostActivity = (BaseBusinessActivity) getContext();

			HeaterInfoService hser = new HeaterInfoService(getContext());
			HeaterType oriHeaterType = hser.getCurHeaterType();
			HeaterType newHeaterType = hser.getHeaterType(heaterInfo);

			hser.setCurrentSelectedHeater(heaterInfo.getMac());

			SharedPreferUtils spu = new SharedPreferUtils(getContext());
			if (newHeaterType == HeaterType.ELECTRIC_HEATER) {
				spu.put(ShareKey.PollingElectricHeaterDid, heaterInfo.getDid());
				spu.put(ShareKey.PollingElectricHeaterMac, heaterInfo.getMac());

				L.e(this, "切换电热的did : " + heaterInfo.getDid());
				L.e(this, "切换电热的mac : " + heaterInfo.getMac());
				L.e(this, "切换电热的passcode : " + heaterInfo.getPasscode());
			} else if (newHeaterType == HeaterType.GAS_HEATER) {
				spu.put(ShareKey.PollingGasHeaterDid, heaterInfo.getDid());
				spu.put(ShareKey.PollingGasHeaterMac, heaterInfo.getMac());

				L.e(this, "切换燃热的did : " + heaterInfo.getDid());
				L.e(this, "切换燃热的mac : " + heaterInfo.getMac());
				L.e(this, "切换燃热的passcode : " + heaterInfo.getPasscode());
			} else if (newHeaterType == HeaterType.FURNACE) {
				spu.put(ShareKey.PollingFurnaceDid, heaterInfo.getDid());
				spu.put(ShareKey.PollingFurnaceMac, heaterInfo.getMac());

				L.e(this, "切换壁挂炉的did : " + heaterInfo.getDid());
				L.e(this, "切换壁挂炉的mac : " + heaterInfo.getMac());
				L.e(this, "切换壁挂炉的passcode : " + heaterInfo.getPasscode());
			}

			AlterDeviceHelper.newHeaterType = newHeaterType;
			AlterDeviceHelper.typeChanged = !newHeaterType
					.equals(oriHeaterType);

			if (!AlterDeviceHelper.typeChanged) {
				hostActivity.mSlidingMenu.showContent();
			}

			AlterDeviceHelper.hostActivity = hostActivity;

			AlterDeviceHelper.alterDevice();
			
		}
	}

}
