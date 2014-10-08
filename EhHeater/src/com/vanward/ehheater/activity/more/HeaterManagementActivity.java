package com.vanward.ehheater.activity.more;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.vanward.ehheater.activity.CloudBaseActivity;
import com.vanward.ehheater.activity.configure.ShitActivity;
import com.vanward.ehheater.activity.global.Consts;
import com.vanward.ehheater.activity.global.Global;
import com.vanward.ehheater.bean.HeaterInfo;
import com.vanward.ehheater.dao.HeaterInfoDao;
import com.vanward.ehheater.service.HeaterInfoService;
import com.vanward.ehheater.util.DialogUtil;
import com.vanward.ehheater.util.SharedPreferUtils;
import com.vanward.ehheater.util.SharedPreferUtils.ShareKey;
import com.vanward.ehheater.util.UIUtil;
import com.xtremeprog.xpgconnect.XPGConnectClient;
import com.xtremeprog.xpgconnect.generated.BindingDelResp_t;
import com.xtremeprog.xpgconnect.generated.XpgDataField;
import com.xtremeprog.xpgconnect.generated.generated;

public class HeaterManagementActivity extends CloudBaseActivity {

	private ListView lv_listview;
	private Button btn_add;
	private HeaterAdapter adapter;
	boolean isEdit;
	String mac;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCenterView(R.layout.activity_heater_management);
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
		adapter = new HeaterAdapter(new HeaterInfoDao(getBaseContext()).getAll());
		lv_listview.setAdapter(adapter);
		
		setTopText(R.string.device_manager);
		setRightButtonBackground(R.drawable.icon_edit);
		setLeftButtonBackground(R.drawable.icon_back);
		
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);
		
		if( view == btn_right ){
			if( isEdit ){
				setRightButtonBackground(R.drawable.icon_edit);
			}else{
				setRightButtonBackground(R.drawable.menu_icon_ye);
			}
			isEdit = !isEdit;
			adapter.notifyDataSetChanged();
		}
		
		if( view == btn_add ){
			startActivity(new Intent(getBaseContext(), ShitActivity.class));
		}
	}
	
	class HeaterAdapter extends BaseAdapter {

		List<HeaterInfo> list;
		
		public HeaterAdapter(List<HeaterInfo> list) {
			this.list = list;
		}
		
		public void setList(List<HeaterInfo> list){
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
				convertView = getLayoutInflater().inflate(R.layout.activity_heater_management_list, null);
			}
			HeaterInfo item = list.get(position);
			TextView nameTv = (TextView)convertView.findViewById(R.id.device_name);
			ImageView actionBtn = ((ImageView)convertView.findViewById(R.id.action_btn));
			
			actionBtn.setVisibility(View.VISIBLE);
			actionBtn.setImageResource( isEdit ? R.drawable.icon_del : R.drawable.menu_icon_ye );
			if (!isEdit && !getCurDeviceMac(getBaseContext()).equals(item.getMac())) {
				actionBtn.setVisibility(View.INVISIBLE);
			}
			actionBtn.setTag(item);
			actionBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					if( isEdit ){
						mac = ((HeaterInfo)view.getTag()).getMac();
						XpgDataField data = generated.String2XpgData(((HeaterInfo)view.getTag()).getDid());
						DialogUtil.instance().showLoadingDialog(HeaterManagementActivity.this, "");
						generated.SendBindingDelReq(getConnId(), data );
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
	
	@Override
	public void OnBindingDelResp(BindingDelResp_t pResp, int nConnId) {
		
		DialogUtil.dismissDialog();
		
		if ( pResp.getResult() == 0 ){
			Toast.makeText(getBaseContext(), R.string.success, Toast.LENGTH_SHORT).show();
			if (getCurDeviceMac(getBaseContext()).equals(mac)) {
				XPGConnectClient.xpgcDisconnectAsync(Global.connectId);
				new SharedPreferUtils(getBaseContext()).put(ShareKey.CurDeviceMac, "");
			}
			new HeaterInfoService(getBaseContext()).deleteHeater(mac);
			adapter.setList(new HeaterInfoDao(getBaseContext()).getAll());
			adapter.notifyDataSetChanged();
		}else{
			Toast.makeText(getBaseContext(), R.string.failure, Toast.LENGTH_SHORT).show();
		}
	}
	
	private String getCurDeviceMac(Context context) {
		return new SharedPreferUtils(context).get(ShareKey.CurDeviceMac, "");
	}
	
	private void showRenameDialog(final HeaterInfo heater) {
		final Dialog renameDialog = new Dialog(this, R.style.custom_dialog);
		renameDialog.setContentView(R.layout.dialog_rename_heater);
		final EditText etName = (EditText) renameDialog.findViewById(R.id.drename_et);
		renameDialog.findViewById(R.id.drename_btn_cancel).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				renameDialog.dismiss();
			}
		});
		renameDialog.findViewById(R.id.drename_btn_confirm).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String nameSet = etName.getText().toString();
				if (!TextUtils.isEmpty(nameSet)) {
					// update name
					heater.setName(nameSet);
					new HeaterInfoDao(getBaseContext()).getDb().update(heater);
				}
				adapter.notifyDataSetChanged();
				renameDialog.dismiss();
			}
		});
		renameDialog.show();
	}
	
}
