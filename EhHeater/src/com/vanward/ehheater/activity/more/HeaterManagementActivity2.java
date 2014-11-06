package com.vanward.ehheater.activity.more;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.EhHeaterBaseActivity;
import com.vanward.ehheater.activity.configure.ShitActivity;
import com.vanward.ehheater.activity.global.Consts;
import com.vanward.ehheater.activity.global.Global;
import com.vanward.ehheater.activity.info.SelectDeviceActivity;
import com.vanward.ehheater.bean.HeaterInfo;
import com.vanward.ehheater.dao.HeaterInfoDao;
import com.vanward.ehheater.service.AccountService;
import com.vanward.ehheater.service.HeaterInfoService;
import com.vanward.ehheater.util.DialogUtil;
import com.vanward.ehheater.util.SharedPreferUtils;
import com.vanward.ehheater.util.SharedPreferUtils.ShareKey;
import com.vanward.ehheater.util.UIUtil;
import com.vanward.ehheater.util.XPGConnShortCuts;
import com.xtremeprog.xpgconnect.XPGConnectClient;
import com.xtremeprog.xpgconnect.generated.BindingDelResp_t;
import com.xtremeprog.xpgconnect.generated.XPG_RESULT;
import com.xtremeprog.xpgconnect.generated.generated;

public class HeaterManagementActivity2 extends EhHeaterBaseActivity {

	private ListView lv_listview;
	private Button btn_add;
	private HeaterAdapter adapter;
	boolean isEdit;

	private String macOfHeaterBeingDeleted;
	private String didOfHeaterBeingDeleted;
	private int tempConnId = -2;

	@Override
	public void initUI() {
		super.initUI();
		setCenterView(R.layout.activity_heater_management);
		setTopText(R.string.device_manager);
		setRightButtonBackground(R.drawable.icon_edit);
		setLeftButtonBackground(R.drawable.icon_back);

		findViewById();
		setListener();
		init();
	}

	private void findViewById() {
		lv_listview = (ListView) findViewById(R.id.lv_listview);
		btn_add = (Button) findViewById(R.id.btn_add);
	}

	private void setListener() {
		UIUtil.setOnClick(this, btn_add);
	}

	private void init() {
		adapter = new HeaterAdapter(
				new HeaterInfoDao(getBaseContext()).getAll());
		lv_listview.setAdapter(adapter);
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);

		if (view == btn_right) {
			if (isEdit) {
				setRightButtonBackground(R.drawable.icon_edit);
			} else {
				setRightButtonBackground(R.drawable.menu_icon_ye);
			}
			isEdit = !isEdit;
			adapter.notifyDataSetChanged();
		}

		if (view == btn_add) {
			startActivity(new Intent(getBaseContext(), SelectDeviceActivity.class));
		}
		if (view == btn_left) {
			if (new HeaterInfoDao(getBaseContext()).getAll().size() == 0) {
				startActivity(new Intent(getBaseContext(), SelectDeviceActivity.class));
			} else {
				onBackPressed();
			}
		}
	}

	class HeaterAdapter extends BaseAdapter {

		List<HeaterInfo> list;

		public HeaterAdapter(List<HeaterInfo> list) {
			this.list = list;
		}

		public void setList(List<HeaterInfo> list) {
			this.list = list;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(
						R.layout.activity_heater_management_list, null);
			}
			HeaterInfo item = list.get(position);
			TextView nameTv = (TextView) convertView
					.findViewById(R.id.device_name);
			ImageView actionBtn = ((ImageView) convertView
					.findViewById(R.id.action_btn));

			actionBtn.setVisibility(View.VISIBLE);
			actionBtn.setImageResource(isEdit ? R.drawable.icon_del
					: R.drawable.menu_icon_ye);
			if (!isEdit
					&& !getCurDeviceMac(getBaseContext()).equals(item.getMac())) {
				actionBtn.setVisibility(View.INVISIBLE);
			}
			actionBtn.setTag(item);
			actionBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					if (isEdit) {
						macOfHeaterBeingDeleted = ((HeaterInfo) view.getTag())
								.getMac();
						didOfHeaterBeingDeleted = ((HeaterInfo) view.getTag())
								.getDid();
						deleteHeater();
						// XpgDataField data =
						// generated.String2XpgData(((HeaterInfo)view.getTag()).getDid());
						// DialogUtil.instance().showLoadingDialog(getBaseContext(),
						// "");
						// generated.SendBindingDelReq(getConnId(), data );
					}
				}
			});

			nameTv.setTag(item);
			nameTv.setText(Consts.getHeaterName(item));
			nameTv.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (isEdit) {
						showRenameDialog((HeaterInfo) v.getTag());
					}
				}
			});
			return convertView;
		}

	}

	private void deleteHeater() {
		if (TextUtils.isEmpty(didOfHeaterBeingDeleted)) {
			// local delete
			new HeaterInfoService(getBaseContext())
					.deleteHeater(macOfHeaterBeingDeleted);
			deleted();
		} else {
			// server delete
			DialogUtil.instance().showLoadingDialog(this, "");
			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
					if (/* 失败 */tempConnId == -2) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								DialogUtil.dismissDialog();
								Toast.makeText(getBaseContext(), "删除失败", 3000)
										.show();
							}
						});
					}
				}
			}, 5000);
			XPGConnShortCuts.connect2big();
		}
	}

	@Override
	public void onConnectEvent(int connId, int event) {
		super.onConnectEvent(connId, event);
		Log.d("emmm", "onConnectEvent@HeaterManagement: " + connId + "-"
				+ event);

		if (event == XPG_RESULT.ERROR_NONE.swigValue()) {
			// 连接成功
			tempConnId = connId;
			XPGConnectClient.xpgcLogin(tempConnId,
					AccountService.getUserId(getBaseContext()),
					AccountService.getUserPsw(getBaseContext())); // login to
																	// server
		}
	}

	@Override
	public void onLoginCloudResp(int result, String mac) {
		super.onLoginCloudResp(result, mac);
		Log.d("emmm", "onLoginCloudResp@HeaterManagement: " + result);

		generated.SendBindingDelReq(tempConnId,
				generated.String2XpgData(didOfHeaterBeingDeleted));
	}

	@Override
	public void OnBindingDelResp(BindingDelResp_t pResp, int nConnId) {
		super.OnBindingDelResp(pResp, nConnId);
		Log.d("emmm", "OnBindingDelResp@HeaterManagement: " + pResp.getResult());

		DialogUtil.dismissDialog();

		if (pResp.getResult() == 0) {
			new HeaterInfoService(getBaseContext())
					.deleteHeater(macOfHeaterBeingDeleted);
			deleted();
		} else {
			Toast.makeText(getBaseContext(), R.string.failure,
					Toast.LENGTH_SHORT).show();
		}
	}

	private void deleted() {
		Toast.makeText(getBaseContext(), R.string.success, Toast.LENGTH_SHORT)
				.show();
		if (getCurDeviceMac(getBaseContext()).equals(macOfHeaterBeingDeleted)) {
			XPGConnectClient.xpgcDisconnectAsync(Global.connectId);
			new SharedPreferUtils(getBaseContext()).put(ShareKey.CurDeviceMac,
					"");
		}
		adapter.setList(new HeaterInfoDao(getBaseContext()).getAll());
		adapter.notifyDataSetChanged();

		Intent heaterNameIntent = new Intent(
				Consts.INTENT_FILTER_HEATER_NAME_CHANGED);
		LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(
				heaterNameIntent);
	}

	private String getCurDeviceMac(Context context) {
		return new SharedPreferUtils(context).get(ShareKey.CurDeviceMac, "");
	}

	private void showRenameDialog(final HeaterInfo heater) {
		final Dialog renameDialog = new Dialog(this, R.style.custom_dialog);
		renameDialog.setContentView(R.layout.dialog_rename_heater);
		final EditText etName = (EditText) renameDialog
				.findViewById(R.id.drename_et);
		renameDialog.findViewById(R.id.drename_btn_cancel).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						renameDialog.dismiss();
					}
				});
		renameDialog.findViewById(R.id.drename_btn_confirm).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						String nameSet = etName.getText().toString();
						if (!TextUtils.isEmpty(nameSet)) {
							// update name
							heater.setName(nameSet);
							new HeaterInfoService(getBaseContext())
									.changeDuplicatedName(heater);
							new HeaterInfoDao(getBaseContext()).getDb().update(
									heater);
							adapter.notifyDataSetChanged();

							Intent heaterNameIntent = new Intent(
									Consts.INTENT_FILTER_HEATER_NAME_CHANGED);
							LocalBroadcastManager.getInstance(getBaseContext())
									.sendBroadcast(heaterNameIntent);
						}
						renameDialog.dismiss();
					}
				});
		renameDialog.show();
	}

}