package com.vanward.ehheater.activity.main.furnace;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.EhHeaterBaseActivity;
import com.vanward.ehheater.activity.global.Consts;
import com.vanward.ehheater.model.FurnaceAppointmentModel;
import com.vanward.ehheater.service.AccountService;
import com.vanward.ehheater.service.HeaterInfoService;
import com.vanward.ehheater.util.HttpFriend;
import com.vanward.ehheater.util.TextUtil;
import com.vanward.ehheater.view.swipelistview.SwipeListView;

public class FurnaceAppointmentListActivity extends EhHeaterBaseActivity {

	private static final String TAG = "FurnaceAppointmentListActivity";

	private SwipeListView lv_listview;

	private AppointmentListAdapter adapter;

	private HttpFriend mHttpFriend;

	private ArrayList<FurnaceAppointmentModel> adapter_data = new ArrayList<FurnaceAppointmentModel>();

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
		// requestHttpDate();
		// Log.e("格式化前 : ", String.format("%-5s", 11));
		// Log.e("格式化后的 : ", String.format("%-5s", 11).replace(' ', '0'));
	}

	private void findViewById() {
		lv_listview = (SwipeListView) findViewById(R.id.lv_listview);
	}

	private void setListener() {
		btn_right.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(FurnaceAppointmentListActivity.this,
						FurnaceAppointmentTimeActivity.class);
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
		mHttpFriend = HttpFriend.create(this);
		extractDataFromJson(getTestData()); // for test
		// requestHttpData();
		adapter = new AppointmentListAdapter();
		lv_listview.setAdapter(adapter);
	}

	private void requestHttpData() {
		String did = new HeaterInfoService(this).getCurrentSelectedHeater()
				.getDid();
		String uid = AccountService.getUserId(getBaseContext());

		String requestURL = "userinfo/getAppointmentList?did=" + did + "&uid="
				+ uid;

		showRequestDialog();
		mHttpFriend.toUrl(Consts.REQUEST_BASE_URL + requestURL).executeGet(
				new AjaxCallBack<String>() {
					@Override
					public void onSuccess(String jsonString) {
						super.onSuccess(jsonString);
						Log.e(TAG, "请求成功后返回的数据是 : " + jsonString);

						extractDataFromJson(jsonString);

						dismissRequestDialog();
					}
				});
	}

	private void extractDataFromJson(String jsonString) {
		try {
			JSONObject json = new JSONObject(jsonString);
			String responseCode = json.getString("responseCode");
			if ("200".equals(responseCode)) {
				JSONArray result = json.getJSONArray("result");
				if (result.length() >= 3) {
					btn_right.setVisibility(View.INVISIBLE); // 预约数>=3时,隐藏右上角按钮
				}
				for (int i = 0; i < result.length(); i++) {
					JSONObject item = result.getJSONObject(i);
					FurnaceAppointmentModel model = new FurnaceAppointmentModel();
					model.setAppointmentId(item.getInt("appointmentId"));
					model.setAppointmentName(item.getString("appointmentName"));
					model.setDateTime(item.getInt("dateTime"));
					model.setDid(item.getInt("did"));
					model.setLoopflag(item.getInt("loopflag"));
					model.setUserId(item.getInt("userId"));
					model.setWeek(item.getString("week"));
					model.setWorkMode(item.getInt("workMode"));
					model.setIsAppointmentOn(item.getInt("isAppointmentOn"));
					// model.setIsDeviceOn(item.getInt("isDeviceOn"));
					model.setDeviceType(item.getInt("deviceType"));
					model.setTemperature(item.getInt("temperature"));

					adapter_data.add(model);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		public View getView(int position, View convertView, ViewGroup parent) {
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
			final FurnaceAppointmentModel model = adapter_data.get(position);
			// holder.tv_time.setText(appointment.getHour() + ":" +
			// appointment.getMinute());

			holder.tv_nickname.setText(model.getAppointmentName());
			holder.tv_time.setText(dateFormat.format(new Date(model
					.getDateTime() * 1000)));

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

			holder.tv_temperature.setText(model.getTemperature() + "℃");

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
					} else {
						// model.set 打开预约
					}

					String requestURL = "userinfo/updateAppointment";

					showRequestDialog();
					mHttpFriend.clearParams().postBean(model)
							.toUrl(Consts.REQUEST_BASE_URL + requestURL)
							.executePost(new AjaxCallBack<String>() {
								@Override
								public void onSuccess(String t) {
									super.onSuccess(t);
									((ImageButton) view)
											.setImageResource(R.drawable.on);
									((ImageButton) view)
											.setImageResource(R.drawable.off);
									dismissRequestDialog();
								}
							});
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

					String requestURL = "userinfo/deleteAppointment?appointmentId="
							+ model.getAppointmentId();
					mHttpFriend.clearParams()
							.toUrl(Consts.REQUEST_BASE_URL + requestURL)
							.executeGet(new AjaxCallBack<String>() {
								@Override
								public void onSuccess(String jsonString) {
									super.onSuccess(jsonString);
									try {
										JSONObject json;
										json = new JSONObject(jsonString);
										String responseCode = json
												.getString("responseCode");
										if ("200".equals(responseCode)) {
											
										}
									} catch (JSONException e) {
										e.printStackTrace();
									}

								}
							});
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
					intent.setClass(FurnaceAppointmentListActivity.this,
							FurnaceAppointmentTimeActivity.class);
					startActivity(intent);
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
}
