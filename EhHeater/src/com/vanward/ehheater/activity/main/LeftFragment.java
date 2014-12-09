package com.vanward.ehheater.activity.main;

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
import android.widget.Toast;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.configure.ConnectActivity;
import com.vanward.ehheater.activity.global.Consts;
import com.vanward.ehheater.activity.global.Global;
import com.vanward.ehheater.activity.main.furnace.FurnaceMainActivity;
import com.vanward.ehheater.activity.main.furnace.FurnaceSeasonActivity;
import com.vanward.ehheater.activity.main.gas.GasMainActivity;
import com.vanward.ehheater.activity.more.AboutActivity;
import com.vanward.ehheater.activity.more.AccountManagementActivity;
import com.vanward.ehheater.activity.more.HeaterManagementActivity2;
import com.vanward.ehheater.activity.more.HelpActivity;
import com.vanward.ehheater.activity.more.RemindSettingActivity;
import com.vanward.ehheater.bean.HeaterInfo;
import com.vanward.ehheater.dao.HeaterInfoDao;
import com.vanward.ehheater.service.AccountService;
import com.vanward.ehheater.service.HeaterInfoService;
import com.vanward.ehheater.service.HeaterInfoService.HeaterType;
import com.vanward.ehheater.util.AlterDeviceHelper;
import com.vanward.ehheater.util.UIUtil;
import com.xtremeprog.xpgconnect.XPGConnectClient;
import com.xtremeprog.xpgconnect.generated.GeneratedActivity;

public class LeftFragment extends LinearLayout implements
		android.view.View.OnClickListener, OnItemClickListener {

	Button btn_user_manager, btn_device_manager, btn_tip, btn_help, btn_about,
			btn_season_mode;
	View deviceSwitchLayout, deviceSwitchBtn;
	private RelativeLayout rlt_season_mode;
	private ImageView iv_season_mode;
	public TextView tv_season_mode;

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
				new HeaterInfoDao(getContext()).getAll());
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
			clazz = HeaterManagementActivity2.class;
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

	List<HeaterInfo> objects;

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
			if (heaterInfo!=null&&heaterInfo.getMac().equals(objects.get(position).getMac())) {
				holder1.checkImage.setVisibility(View.VISIBLE);
				holder1.devicename.setTextColor(R.color.yeallow);
			} else {
				holder1.checkImage.setVisibility(View.GONE);
				holder1.devicename.setTextColor(R.color.black);
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
		if (heaterInfo.getMac().equals(shareheaterInfo.getMac())) {
			System.out.println("heaterInfo.getMac(): " + heaterInfo.getMac());
			return;
		} else {

			Activity hostActivity = (Activity) getContext();

			HeaterInfoService hser = new HeaterInfoService(getContext());
			HeaterType oriHeaterType = hser.getCurHeaterType();
			HeaterType newHeaterType = hser.getHeaterType(heaterInfo);
			
			hser.setCurrentSelectedHeater(heaterInfo.getMac()); 
			
			AlterDeviceHelper.newHeaterType = newHeaterType;
			AlterDeviceHelper.typeChanged = !newHeaterType.equals(oriHeaterType);
			AlterDeviceHelper.hostActivity = hostActivity;
			
			if (Global.connectId > 0) {
				//触发BaseBusinessActivity里的断开连接回调, 具体的切换逻辑在该回调中处理
				XPGConnectClient.xpgcDisconnectAsync(Global.connectId); 
			} else {
				//如果当前未建立连接, 直接调用此方法
				AlterDeviceHelper.alterDevice();
			}
			
		}

	}
	
}
