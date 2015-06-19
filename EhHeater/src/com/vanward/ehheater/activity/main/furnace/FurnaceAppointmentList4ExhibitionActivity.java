package com.vanward.ehheater.activity.main.furnace;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.EhHeaterBaseActivity;
import com.vanward.ehheater.model.AppointmentVo4Exhibition;
import com.vanward.ehheater.util.SharedPreferUtils;
import com.vanward.ehheater.util.SharedPreferUtils.ShareKey;
import com.vanward.ehheater.util.TextUtil;
import com.vanward.ehheater.view.swipelistview.SwipeListView;

public class FurnaceAppointmentList4ExhibitionActivity extends
		EhHeaterBaseActivity {

	private static final String TAG = "FurnaceAppointmentList4ExhibitionActivity";

	private SwipeListView lv_listview;

	private AppointmentListAdapter adapter;

	private ArrayList<AppointmentVo4Exhibition> adapter_data = new ArrayList<AppointmentVo4Exhibition>();

	private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCenterView(R.layout.activity_furnace_appointment_list);
		setTopText(R.string.appointment);
		setLeftButtonBackground(R.drawable.icon_back);
		setRightButtonBackground(R.drawable.icon_add);
		findViewById();
		setListener();
		init();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private void findViewById() {
		lv_listview = (SwipeListView) findViewById(R.id.lv_listview);
	}

	private void setListener() {
		btn_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				saveTestData();
				finish();
			}
		});

		btn_right.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(
						FurnaceAppointmentList4ExhibitionActivity.this,
						FurnaceAppointmentTime4ExhibitionActivity.class);
				startActivityForResult(intent, 0);
			}
		});
	}

	private String getTestData() {

		InputStream inputStream;
		try {
			inputStream = getAssets().open(
					"furnace_test_data/appointment_list.txt");
			String json = new TextUtil(getApplication())
					.readTextFile(inputStream);
			return json;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void init() {
		// extractDataFromJson(getTestData()); // for test

		ArrayList<AppointmentVo4Exhibition> data = readTestData();

		if (data != null) {
			adapter_data = data;
		}

		if (adapter_data.size() >= 3) {
			btn_right.setVisibility(View.INVISIBLE); // 预约数>=3时,隐藏右上角按钮
		} else {
			btn_right.setVisibility(View.VISIBLE);
		}

		adapter = new AppointmentListAdapter();
		lv_listview.setAdapter(adapter);
	}

	private void extractDataFromJson(String jsonString) {
		try {
			JSONObject json = new JSONObject(jsonString);
			String responseCode = json.getString("responseCode");
			if ("200".equals(responseCode)) {
				JSONArray result = json.getJSONArray("result");
				if (result.length() >= 3) {
					btn_right.setVisibility(View.INVISIBLE); // 预约数>=3时,隐藏右上角按钮
				} else {
					btn_right.setVisibility(View.VISIBLE);
				}

				adapter_data.clear();
				for (int i = 0; i < result.length(); i++) {
					JSONObject item = result.getJSONObject(i);
					AppointmentVo4Exhibition model = new AppointmentVo4Exhibition();
					model.setAppointmentId(item.getInt("appointmentId"));
					model.setName(item.getString("name"));
					model.setDateTime(item.getLong("dateTime"));
					model.setDid(item.getString("did"));
					model.setLoopflag(item.getInt("loopflag"));
					model.setUid(item.getString("uid"));
					model.setWeek(item.getString("week"));
					model.setPasscode(item.getString("passcode"));
					model.setWorkMode(item.getInt("workMode"));
					model.setIsAppointmentOn(item.getInt("isAppointmentOn"));
					// model.setIsDeviceOn(item.getInt("isDeviceOn"));
					model.setDeviceType(item.getInt("deviceType"));
					model.setTemper(item.getString("temper"));
					model.setPeopleNum("0");
					model.setPower("0");

					adapter_data.add(model);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void saveTestData() {
		SharedPreferences preferences = getSharedPreferences("appointmentList",
				MODE_PRIVATE);
		// 创建字节输出流
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			// 创建对象输出流，并封装字节流
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			// 将对象写入字节流
			oos.writeObject(adapter_data);
			// 将字节流编码成base64的字符窜
			String data_Base64 = new String(Base64.encodeToString(
					baos.toByteArray(), Base64.DEFAULT));
			Editor editor = preferences.edit();
			editor.putString("data", data_Base64);

			editor.commit();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<AppointmentVo4Exhibition> readTestData() {
		ArrayList<AppointmentVo4Exhibition> data = null;
		SharedPreferences preferences = getSharedPreferences("appointmentList",
				MODE_PRIVATE);
		String productBase64 = preferences.getString("data", "");

		// 读取字节
		byte[] base64 = Base64.decode(productBase64, Base64.DEFAULT);

		// 封装到字节流
		ByteArrayInputStream bais = new ByteArrayInputStream(base64);
		try {
			// 再次封装
			ObjectInputStream bis = new ObjectInputStream(bais);
			try {
				// 读取对象
				data = (ArrayList<AppointmentVo4Exhibition>) bis.readObject();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (StreamCorruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;
	}

	private class AppointmentListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return adapter_data.size();
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
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = getLayoutInflater().inflate(
						R.layout.item_furnace_appointment_list, null);
				holder.tv_nickname = (TextView) convertView
						.findViewById(R.id.tv_nickname);
				holder.tv_time = (TextView) convertView
						.findViewById(R.id.tv_time);
				holder.tv_days = (TextView) convertView
						.findViewById(R.id.tv_days);
				holder.tv_temperature = (TextView) convertView
						.findViewById(R.id.tv_temperature);
				holder.tv_mode = (TextView) convertView
						.findViewById(R.id.tv_mode);
				holder.ib_switch = (ImageButton) convertView
						.findViewById(R.id.ib_switch);
				holder.btn_delete = (Button) convertView
						.findViewById(R.id.btn_delete);
				holder.parent = convertView.findViewById(R.id.parent);

				holder.rltback = convertView.findViewById(R.id.rlt_back);
				holder.rltfont = convertView.findViewById(R.id.rlt_front);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final AppointmentVo4Exhibition model = adapter_data.get(position);
			// holder.tv_time.setText(appointment.getHour() + ":" +
			// appointment.getMinute());

			String name = new SharedPreferUtils(
					FurnaceAppointmentList4ExhibitionActivity.this).get(
					ShareKey.UserNickname, "");
			if (!"".equals(name)) {
				holder.tv_nickname.setText(name);
			} else {
				holder.tv_nickname.setText(model.getName());
			}
			holder.tv_time.setText(dateFormat.format(new Date(model
					.getDateTime())));

			String loopDays = "";

			if (model.getLoopflag() == 1) {
				loopDays = getResources().getString(R.string.everyday);
			} else if (model.getLoopflag() == 2) {
				for (int i = 0; i < model.getWeek().length(); i++) {
					int flag = model.getWeek().charAt(i);
					if (flag == '1') {
						switch (i) {

						case 0:
							loopDays += getResources().getString(
									R.string.Monday)
									+ " ";
							break;
						case 1:
							loopDays += getResources().getString(
									R.string.Tuesday)
									+ " ";
							break;
						case 2:
							loopDays += getResources().getString(
									R.string.Wednesday)
									+ " ";
							break;
						case 3:
							loopDays += getResources().getString(
									R.string.Thursday)
									+ " ";
							break;
						case 4:
							loopDays += getResources().getString(
									R.string.Friday)
									+ " ";
							break;
						case 5:
							loopDays += getResources().getString(
									R.string.Saturday)
									+ " ";
							break;
						case 6:
							loopDays += getResources().getString(
									R.string.Sunday);
							break;
						}
					}
				}
			}
			holder.tv_days.setText(loopDays);

			holder.tv_temperature.setText(model.getTemper() + "℃");

			switch (model.getWorkMode()) {
			case 0:
				holder.tv_mode.setText(R.string.mode_default);
				break;
			case 1:
				holder.tv_mode.setText(R.string.mode_outdoor);
				holder.tv_temperature.setText("30℃");

				break;
			case 2:
				holder.tv_mode.setText(R.string.mode_night);
				break;

			}

			if (model.getIsAppointmentOn() == 0) {
				holder.ib_switch.setImageResource(R.drawable.off);
				holder.ib_switch.setTag(0);
			} else {
				holder.ib_switch.setImageResource(R.drawable.on);
				holder.ib_switch.setTag(1);
			}

			holder.ib_switch.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(final View view) {
					if ((Integer) view.getTag() == 1) {
						// model.set 关闭预约
						model.setIsAppointmentOn(0);
						((ImageButton) view).setImageResource(R.drawable.off);
						((ImageButton) view).setTag(0);
					} else {
						// model.set 打开预约
						model.setIsAppointmentOn(1);
						((ImageButton) view).setImageResource(R.drawable.on);
						((ImageButton) view).setTag(1);
					}
					saveTestData();
				}
			});

			LinearLayout.LayoutParams lp1 = new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			holder.rltfont.setLayoutParams(lp1);
			LinearLayout.LayoutParams lp2 = new LayoutParams(150,
					LayoutParams.MATCH_PARENT);
			holder.rltback.setLayoutParams(lp2);

			holder.btn_delete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View view) {

					adapter_data.remove(position);

					lv_listview.setAdapter(new AppointmentListAdapter());

					if (adapter_data.size() >= 3) {
						btn_right.setVisibility(View.INVISIBLE); // 预约数>=3时,隐藏右上角按钮
					} else {
						btn_right.setVisibility(View.VISIBLE);
					}

					saveTestData();
				}
			});

			holder.parent.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent();
					// intent.putExtra("id",
					// adapter_date.get(position).getId());
					// intent.putExtra("week", appointment.getDates());
					intent.putExtra("editAppointment", model);
					intent.putExtra("position", position);
					intent.setClass(
							FurnaceAppointmentList4ExhibitionActivity.this,
							FurnaceAppointmentTime4ExhibitionActivity.class);
					startActivityForResult(intent, 0);
				}
			});

			return convertView;
		}

		class ViewHolder {
			TextView tv_nickname;
			TextView tv_time;
			TextView tv_days;
			TextView tv_temperature;
			TextView tv_mode;
			ImageButton ib_switch;
			Button btn_delete;
			View rltfont, rltback;

			View parent;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (data != null) {
				AppointmentVo4Exhibition editModel = (AppointmentVo4Exhibition) data
						.getSerializableExtra("editAppointment");
				int position = data.getIntExtra("position", -1);
				if (position != -1) {
					adapter_data.set(position, editModel);
				} else {
					adapter_data.add(editModel);
				}
				lv_listview.setAdapter(new AppointmentListAdapter());
				if (adapter_data.size() >= 3) {
					btn_right.setVisibility(View.INVISIBLE); // 预约数>=3时,隐藏右上角按钮
				} else {
					btn_right.setVisibility(View.VISIBLE);
				}
				saveTestData();
			}
		}
	}
}
