package com.vanward.ehheater.activity.more;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
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
import com.vanward.ehheater.activity.global.Consts;
import com.vanward.ehheater.activity.global.Global;
import com.vanward.ehheater.activity.info.SelectDeviceActivity;
import com.vanward.ehheater.bean.HeaterInfo;
import com.vanward.ehheater.dao.HeaterInfoDao;
import com.vanward.ehheater.service.AccountService;
import com.vanward.ehheater.service.HeaterInfoService;
import com.vanward.ehheater.service.HeaterInfoService.HeaterType;
import com.vanward.ehheater.util.AlterDeviceHelper;
import com.vanward.ehheater.util.BaoDialogShowUtil;
import com.vanward.ehheater.util.DialogUtil;
import com.vanward.ehheater.util.L;
import com.vanward.ehheater.util.PingWebsiteUtil;
import com.vanward.ehheater.util.SharedPreferUtils;
import com.vanward.ehheater.util.SharedPreferUtils.ShareKey;
import com.vanward.ehheater.util.UIUtil;
import com.xtremeprog.xpgconnect.XPGConnectClient;
import com.xtremeprog.xpgconnect.generated.BindingDelResp_t;
import com.xtremeprog.xpgconnect.generated.generated;

public class HeaterManagementActivity extends EhHeaterBaseActivity {

	private static final String TAG = "HeaterManagementActivity";

	private ListView lv_listview;
	private Button btn_add;
	private HeaterAdapter adapter;
	boolean isEdit;

	/** 唯一触发: 删除了当前选择的设备, 并且还有其他设备 */
	private boolean shouldAlter = false;
	private String macOfHeaterBeingDeleted;
	private String didOfHeaterBeingDeleted;
	private HeaterType typeOfHeaterBeingDeleted;
	private int tempConnId = -2;

	private Dialog deleteHeaterConfirmDialog, deleteFurnaceConfirmDialog;

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
		deleteHeaterConfirmDialog = BaoDialogShowUtil.getInstance(this)
				.createDialogWithTwoButton(R.string.confirm_delete_heater,
						BaoDialogShowUtil.DEFAULT_RESID, -1, null,
						new OnClickListener() {

							@Override
							public void onClick(View v) {
								deleteHeaterConfirmDialog.dismiss();
								deleteHeater();
							}
						});

		deleteFurnaceConfirmDialog = BaoDialogShowUtil.getInstance(this)
				.createDialogWithTwoButton(R.string.confirm_delete_furnace,
						BaoDialogShowUtil.DEFAULT_RESID, -1, null,
						new OnClickListener() {

							@Override
							public void onClick(View v) {
								deleteFurnaceConfirmDialog.dismiss();
								deleteHeater();
							}
						});

		adapter = new HeaterAdapter(
				new HeaterInfoDao(getBaseContext()).getAll());
		lv_listview.setAdapter(adapter);
		shouldAlter = false;
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
			startActivity(new Intent(getBaseContext(),
					SelectDeviceActivity.class));
		}
		if (view == btn_left) {
			// if (new HeaterInfoDao(getBaseContext()).getAll().size() == 0) {
			// startActivity(new Intent(getBaseContext(),
			// SelectDeviceActivity.class));
			// } else {
			onBackPressed();
			// }
		}
	}

	@Override
	public void onBackPressed() {
		HeaterInfoService hser = new HeaterInfoService(getBaseContext());
		HeaterInfo hinfo = hser.getCurrentSelectedHeater();

		if (hinfo == null) {
			// 删光了

			Intent intent = new Intent(getBaseContext(),
					SelectDeviceActivity.class);
			intent.putExtra(
					Consts.INTENT_EXTRA_CONFIGURE_ACTIVITY_SHOULD_KILL_PROCESS_WHEN_FINISH,
					true);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra("isDeleteAll", true);
			startActivity(intent);
			finish();

		} else {

			super.onBackPressed();

			if (shouldAlter) {
				shouldAlter = false;
				LocalBroadcastManager
						.getInstance(getBaseContext())
						.sendBroadcast(
								new Intent(
										Consts.INTENT_ACTION_ALTER_DEVICE_DUE_TO_DELETE));
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
			ImageView deviceImage = (ImageView) convertView
					.findViewById(R.id.device_img);
			switch (new HeaterInfoService(getBaseContext()).getHeaterType(item)) {
			case ELECTRIC_HEATER:
				deviceImage.setImageResource(R.drawable.setting_img3);
				break;
			case GAS_HEATER:
				deviceImage.setImageResource(R.drawable.device_line_img2);
				break;
			case FURNACE:
				deviceImage.setImageResource(R.drawable.device_line_img3);
				break;
			default:
				deviceImage.setImageResource(R.drawable.setting_img3);
				break;
			}

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
						HeaterInfo hinfo = (HeaterInfo) view.getTag();
						macOfHeaterBeingDeleted = hinfo.getMac();
						didOfHeaterBeingDeleted = hinfo.getDid();
						typeOfHeaterBeingDeleted = new HeaterInfoService(view
								.getContext()).getHeaterType(hinfo);
						HeaterType type = new HeaterInfoService(view
								.getContext()).getHeaterType(hinfo);
						if (HeaterType.FURNACE == type) {
							deleteFurnaceConfirmDialog.show();
						} else {
							deleteHeaterConfirmDialog.show();
						}
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

			PingWebsiteUtil.testGizwitsAvail(

			new Runnable() {/* onSuccess */
				@Override
				public void run() {
					L.e(this, "ping gizwits SUCCESS");
					serverAcessHandler.sendEmptyMessage(0);
				}
			},

			new Runnable() {/* onFail */
				@Override
				public void run() {
					L.e(this, "ping gizwits FAIL");
					serverAcessHandler.sendEmptyMessage(1);
				}
			});

		}
	}

	private Handler serverAcessHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {

			switch (msg.what) {

			case 0: // 与服务器连接通畅
				// XPGConnectClient.xpgcLogin2Wan(
				// AccountService.getUserId(getBaseContext()),
				// AccountService.getUserPsw(getBaseContext()), "", "");
				// XPGConnShortCuts.connect2big();

				if ("".equals(Global.token) || "".equals(Global.uid)) {
					XPGConnectClient.xpgc4Login(Consts.VANWARD_APP_ID,
							AccountService.getUserId(getBaseContext()),
							AccountService.getUserPsw(getBaseContext()));
				} else {
					XPGConnectClient.xpgc4UnbindDevice(Consts.VANWARD_APP_ID,
							Global.token, didOfHeaterBeingDeleted);
				}

				break;
			case 1: // 未与服务器连接
				DialogUtil.dismissDialog();
				Toast.makeText(getBaseContext(), "删除失败", 3000).show();
				break;
			default:
				break;
			}

			return false;
		}
	});

	@Override
	public void onConnectEvent(int connId, int event) {
		super.onConnectEvent(connId, event);
		L.e(this, "onConnectEvent@HeaterManagementActivity@: " + connId + "-"
				+ event);

		// if (event == XPG_RESULT.ERROR_NONE.swigValue()) {
		// // 连接成功
		// tempConnId = connId;
		// XPGConnectClient.xpgcLogin(tempConnId,
		// AccountService.getUserId(getBaseContext()),
		// AccountService.getUserPsw(getBaseContext())); // login to
		// // server
		// }
	}

	@Override
	public void onV4Login(int errorCode, String uid, String token,
			String expire_at) {
		L.e(this, "onV4Login()");
		if (errorCode == 0) {
			Global.uid = uid;
			Global.token = token;

			L.e(this, "token : " + token);

			XPGConnectClient.xpgc4UnbindDevice(Consts.VANWARD_APP_ID,
					Global.token, didOfHeaterBeingDeleted);
		}
	}

	@Override
	public void onWanLoginResp(int result, int connId) {
		super.onWanLoginResp(result, connId);
		L.e(this, "onWanLoginResp()调用了");
		tempConnId = connId;
		generated.SendBindingDelReq(connId,
				generated.String2XpgData(didOfHeaterBeingDeleted));
	}

	@Override
	public void onLoginCloudResp(int result, String mac) {
		super.onLoginCloudResp(result, mac);
		L.e(this, "onLoginCloudResp@HeaterManagement: " + result);

		// generated.SendBindingDelReq(tempConnId,
		// generated.String2XpgData(didOfHeaterBeingDeleted));
	}

	@Override
	public void onV4UnbindDevice(int errorCode, String successString,
			String failString) {
		L.e(this, "onV4UnbindDevice()");
		super.onV4UnbindDevice(errorCode, successString, failString);

		if (errorCode == 0) {
			DialogUtil.dismissDialog();
			
			new HeaterInfoService(getBaseContext())
					.deleteHeater(macOfHeaterBeingDeleted);
			deleted();
		}
		XPGConnectClient.xpgcDisconnectAsync(tempConnId);
	}

	@Override
	public void OnBindingDelResp(BindingDelResp_t pResp, int nConnId) {
		super.OnBindingDelResp(pResp, nConnId);
		L.e(this, "OnBindingDelResp@HeaterManagement: " + pResp.getResult());

		DialogUtil.dismissDialog();

		// if (pResp.getResult() == 0) {
		new HeaterInfoService(getBaseContext())
				.deleteHeater(macOfHeaterBeingDeleted);
		deleted();
		// } else {
		// Toast.makeText(getBaseContext(), R.string.failure,
		// Toast.LENGTH_SHORT).show();
		// }

		XPGConnectClient.xpgcDisconnectAsync(tempConnId);
	}

	private void deleted() {

		Toast.makeText(this, R.string.success, Toast.LENGTH_SHORT).show();
		HeaterInfoService hser = new HeaterInfoService(getBaseContext());

		if (getCurDeviceMac(getBaseContext()).equals(macOfHeaterBeingDeleted)) {
			// 删除的设备是当前选定的设备, 此时有2种可能, 1 删光了, 2 未删光 需切换至别的设备

			List<HeaterInfo> all = new HeaterInfoDao(getBaseContext()).getAll();
			if (all == null || all.size() == 0) {
				// 删光了
				hser.setCurrentSelectedHeater("");
				XPGConnectClient.xpgcDisconnectAsync(Global.connectId);
			} else {
				// TODO 需切换至其他设备
				hser.setCurrentSelectedHeater(all.get(0).getMac());

				HeaterType oriHeaterType = typeOfHeaterBeingDeleted;
				HeaterType newHeaterType = hser.getCurHeaterType();

				AlterDeviceHelper.newHeaterType = newHeaterType;
				AlterDeviceHelper.typeChanged = !newHeaterType
						.equals(oriHeaterType);

				shouldAlter = true;

				// LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(
				// new Intent(Consts.INTENT_ACTION_ALTER_DEVICE_DUE_TO_DELETE));
			}

		}

		adapter.setList(new HeaterInfoDao(getBaseContext()).getAll());
		adapter.notifyDataSetChanged();

		Intent heaterNameIntent = new Intent(
				Consts.INTENT_FILTER_HEATER_NAME_CHANGED);
		LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(
				heaterNameIntent);

		// HeaterInfoService hser = new HeaterInfoService(getBaseContext());
		HeaterInfo hinfo = hser.getCurrentSelectedHeater();

		if (hinfo == null) {
			// 删光了

			Intent intent = new Intent(getBaseContext(),
					SelectDeviceActivity.class);
			intent.putExtra(
					Consts.INTENT_EXTRA_CONFIGURE_ACTIVITY_SHOULD_KILL_PROCESS_WHEN_FINISH,
					true);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra("isDeleteAll", true);
			startActivity(intent);
			finish();

		}

	}

	private String getCurDeviceMac(Context context) {
		return new SharedPreferUtils(context).get(ShareKey.CurDeviceMac, "");
	}

	/**
	 * 编辑设备名称
	 * 
	 * @param heater
	 */
	private void showRenameDialog(final HeaterInfo heater) {
		final Dialog renameDialog = new Dialog(this, R.style.custom_dialog);
		renameDialog.setContentView(R.layout.dialog_rename_heater);
		final EditText etName = (EditText) renameDialog
				.findViewById(R.id.drename_et);
		etName.setText(heater.getName());
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