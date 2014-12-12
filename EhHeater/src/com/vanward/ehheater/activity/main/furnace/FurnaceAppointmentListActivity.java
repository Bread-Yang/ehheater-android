package com.vanward.ehheater.activity.main.furnace;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

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
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.google.gson.Gson;
import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.EhHeaterBaseActivity;
import com.vanward.ehheater.activity.appointment.AppointmentTimeActivity;
import com.vanward.ehheater.activity.global.Consts;
import com.vanward.ehheater.model.AppointmentVo;
import com.vanward.ehheater.service.AccountService;
import com.vanward.ehheater.service.HeaterInfoService;
import com.vanward.ehheater.service.HeaterInfoService.HeaterType;
import com.vanward.ehheater.util.HttpFriend;
import com.vanward.ehheater.util.TextUtil;
import com.vanward.ehheater.view.swipelistview.SwipeListView;

public class FurnaceAppointmentListActivity extends EhHeaterBaseActivity {

	private static final String TAG = "FurnaceAppointmentListActivity";

	private SwipeListView lv_listview;

	private AppointmentListAdapter adapter;

	private HttpFriend mHttpFriend;

	private ArrayList<AppointmentVo> adapter_data = new ArrayList<AppointmentVo>();

	private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");

	private HeaterType deviceType;

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

		// Log.e("格式化前 : ", String.format("%-5s", 11));
		// Log.e("格式化后的 : ", String.format("%-5s", 11).replace(' ', '0'));
	}

	@Override
	protected void onResume() {
		super.onResume();
		requestHttpData();
	}

	private void findViewById() {
		lv_listview = (SwipeListView) findViewById(R.id.lv_listview);
	}

	private void setListener() {
		btn_right.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				if (deviceType == HeaterType.Eh) {    // 电热水器
					intent.setClass(FurnaceAppointmentListActivity.this,
							AppointmentTimeActivity.class);
				} else {
					intent.setClass(FurnaceAppointmentListActivity.this,
							FurnaceAppointmentTimeActivity.class);
				}
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
		HeaterInfoService service = new HeaterInfoService(getBaseContext());
		deviceType = service.getHeaterType(service.getCurrentSelectedHeater());

		mHttpFriend = HttpFriend.create(this);

		// extractDataFromJson(getTestData()); // for test

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

		// String requestURL =
		// "userinfo/getAppointmentList?did=LWFWwtEcFWJ5hSBPXrVXFS&uid=q1231";

		showRequestDialog();
		mHttpFriend.toUrl(Consts.REQUEST_BASE_URL + requestURL).executeGet(
				null, new AjaxCallBack<String>() {
					@Override
					public void onSuccess(String jsonString) {
						super.onSuccess(jsonString);

						Log.e("请求返回来的数据是 : ", jsonString);

						extractDataFromJson(jsonString);

						lv_listview.setAdapter(new AppointmentListAdapter());

						dismissRequestDialog();
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						super.onFailure(t, errorNo, strMsg);
						Toast.makeText(FurnaceAppointmentListActivity.this,
								"服务器错误", Toast.LENGTH_LONG).show();
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
				} else {
					btn_right.setVisibility(View.VISIBLE);
				}

				adapter_data.clear();
				for (int i = 0; i < result.length(); i++) {
					JSONObject item = result.getJSONObject(i);
					AppointmentVo model = new AppointmentVo();
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
					model.setPower(item.getString("power"));
					model.setDeviceType(item.getInt("deviceType"));
					model.setTemper(item.getString("temper"));
					model.setPeopleNum(item.getString("peopleNum"));

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
			final AppointmentVo model = adapter_data.get(position);
			// holder.tv_time.setText(appointment.getHour() + ":" +
			// appointment.getMinute());

			holder.tv_nickname.setText(model.getName());
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

			if (deviceType == HeaterType.EH_FURNACE) { // 壁挂炉
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
			} else { // 电热水器
				holder.tv_mode.setText(model.getPower() + "kw");
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
					} else {
						// model.set 打开预约
						model.setIsAppointmentOn(1);
					}

					// 热水器多余的数据
					model.setPeopleNum("0");

					model.setPower("0");

					String requestURL = "userinfo/updateAppointment";

					showRequestDialog();

					Gson gson = new Gson();
					String json = gson.toJson(model);

					AjaxParams params = new AjaxParams();
					params.put("data", json);
					params.put("ignoreConflict", "true");

					mHttpFriend.toUrl(Consts.REQUEST_BASE_URL + requestURL)
							.executePost(params, new AjaxCallBack<String>() {
								@Override
								public void onSuccess(String t) {
									super.onSuccess(t);

									Log.e("添加成功返回的json : ", t);

									if ((Integer) view.getTag() == 1) {
										((ImageButton) view)
												.setImageResource(R.drawable.off);
										model.setIsAppointmentOn(0);
									} else {
										((ImageButton) view)
												.setImageResource(R.drawable.on);
										model.setIsAppointmentOn(1);
									}

									dismissRequestDialog();
								}

								@Override
								public void onFailure(Throwable t, int errorNo,
										String strMsg) {
									super.onFailure(t, errorNo, strMsg);
									Toast.makeText(
											FurnaceAppointmentListActivity.this,
											"服务器错误", Toast.LENGTH_LONG).show();
									dismissRequestDialog();
								}
							});

					// mHttpFriend.clearParams().postBean(model)
					// .toUrl(Consts.REQUEST_BASE_URL + requestURL)
					// .executePost(new AjaxCallBack<String>() {
					// @Override
					// public void onSuccess(String jsonString) {
					// super.onSuccess(jsonString);
					//
					// Log.e("添加成功返回的json : ", jsonString);
					//
					// ((ImageButton) view)
					// .setImageResource(R.drawable.on);
					// ((ImageButton) view)
					// .setImageResource(R.drawable.off);
					// dismissRequestDialog();
					// }
					// });
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

					showRequestDialog();
					mHttpFriend.clearParams()
							.toUrl(Consts.REQUEST_BASE_URL + requestURL)
							.executeGet(null, new AjaxCallBack<String>() {
								@Override
								public void onSuccess(String jsonString) {
									super.onSuccess(jsonString);
									try {
										JSONObject json;
										json = new JSONObject(jsonString);
										String responseCode = json
												.getString("responseCode");
										if ("200".equals(responseCode)) {
											// adapter_data.remove(position);
											// adapter.notifyDataSetChanged();
											requestHttpData();
										}
									} catch (JSONException e) {
										e.printStackTrace();
									}

								}

								@Override
								public void onFailure(Throwable t, int errorNo,
										String strMsg) {
									super.onFailure(t, errorNo, strMsg);
									Toast.makeText(
											FurnaceAppointmentListActivity.this,
											"服务器错误", Toast.LENGTH_LONG).show();
									dismissRequestDialog();
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
					if (deviceType == HeaterType.EH_FURNACE) {
						intent.setClass(FurnaceAppointmentListActivity.this,
								FurnaceAppointmentTimeActivity.class);
					} else {
						intent.setClass(FurnaceAppointmentListActivity.this,
								AppointmentTimeActivity.class);
					}

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
