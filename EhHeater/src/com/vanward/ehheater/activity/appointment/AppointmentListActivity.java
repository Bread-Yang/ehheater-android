package com.vanward.ehheater.activity.appointment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.main.SendMsgModel;
import com.vanward.ehheater.util.db.DBService;
import com.vanward.ehheater.view.swipelistview.SwipeListView;

public class AppointmentListActivity extends Activity implements
		OnClickListener {

	@ViewInject(id = R.id.ivTitleName, click = "onClick")
	TextView ivTitleName;
	@ViewInject(id = R.id.ivTitleBtnLeft, click = "onClick")
	Button ivTitleBtnLeft;
	@ViewInject(id = R.id.ivTitleBtnRigh, click = "")
	Button ivTitleBtnRigh;

	private SwipeListView lv_listview;
	private List<Appointment> adapter_date = new ArrayList<Appointment>();
	private AppointmentListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_appointment_list);
		FinalActivity.initInjectedView(this);
		findViewById();
	}

	@Override
	protected void onResume() {
		reflashList();
		reflashright();
		super.onResume();
	}

	private void findViewById() {
		ivTitleBtnRigh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AppointmentListActivity.this, AppointmentTimeActivity.class);
				startActivityForResult(intent, 1);
			}
		});
		lv_listview = (SwipeListView) findViewById(R.id.lv_listview);
		adapter = new AppointmentListAdapter();

		lv_listview.setAdapter(adapter);
		ivTitleName.setText("预约");
		ivTitleBtnLeft.setBackgroundResource(R.drawable.icon_back);
		ivTitleBtnRigh.setBackgroundResource(R.drawable.icon_add);

	}

	public void reflashright() {

		if (AppointmentModel.getInstance(this).getAdapter_date().size() >= 3) {
			ivTitleBtnRigh.setVisibility(View.GONE);
		} else {
			ivTitleBtnRigh.setVisibility(View.VISIBLE);
		}
	}

	public void reflashList() {
		adapter_date = AppointmentModel.getInstance(this).getAdapter_date();
		adapter.notifyDataSetChanged();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			String hour = data.getStringExtra("hour");
			String minute = data.getStringExtra("minute");
			int[] days = data.getIntArrayExtra("days");
			int temp = data.getIntExtra("temp", 0);
			int number = data.getIntExtra("number", 1);
			String text = "";
			boolean isNever = true; // never repeat
			boolean isEveryDay = true; // every
			for (int i = 0; i < days.length; i++) {
				if (days[i] == 1) {
					switch (i) {
					case 0:
						text += getResources().getString(R.string.Monday) + " ";
						break;
					case 1:
						text += getResources().getString(R.string.Tuesday)
								+ " ";
						break;
					case 2:
						text += getResources().getString(R.string.Wednesday)
								+ " ";
						break;
					case 3:
						text += getResources().getString(R.string.Thursday)
								+ " ";
						break;
					case 4:
						text += getResources().getString(R.string.Friday) + " ";
						break;
					case 5:
						text += getResources().getString(R.string.Saturday)
								+ " ";
						break;
					case 6:
						text += getResources().getString(R.string.Sunday) + " ";
						break;
					}
					isNever = false;
				} else {
					isEveryDay = false;
				}
			}
			if (isEveryDay) {
				text = getResources().getString(R.string.everyday);
			} else if (isNever) {
				text = getResources().getString(R.string.never);
			}
			HashMap<String, String> item = new HashMap<String, String>();
			item.put("hour", hour);
			item.put("minute", minute);
			item.put("days", text);
			item.put("temp", temp + "度");
			item.put("number", number + "人");
			// adapter_date.add(item);
			adapter.notifyDataSetChanged();
		}
	}

	private class AppointmentListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return adapter_date.size();
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
						R.layout.item_appointment_list, null);
				holder.tv_time = (TextView) convertView
						.findViewById(R.id.tv_time);
				holder.name = (TextView) convertView
						.findViewById(R.id.textView1);
				holder.tv_temperature = (TextView) convertView
						.findViewById(R.id.tv_temperature);
				holder.tv_days = (TextView) convertView
						.findViewById(R.id.tv_days);
				holder.tb_switch = (ImageButton) convertView
						.findViewById(R.id.switch1);
				holder.btn_delete = (Button) convertView
						.findViewById(R.id.btn_delete);

				holder.tv_power = (TextView) convertView
						.findViewById(R.id.tv_number);

				holder.rltback = convertView.findViewById(R.id.rlt_back);
				holder.rltfont = convertView.findViewById(R.id.rlt_front);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final Appointment appointment = adapter_date.get(position);
			holder.tv_time.setText(appointment.getHour() + ":"
					+ appointment.getMinute());
			holder.tv_temperature.setText(appointment.getTemper() + "度");
			holder.tv_power.setText(appointment.getPower() + "Kw");
			holder.tv_days.setText(appointment.getDates());
			holder.name.setText("预约" + (position + 1));
			holder.tb_switch.setImageResource(R.drawable.on);
			holder.tb_switch.setTag(0);
			holder.tb_switch.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if ((Integer) arg0.getTag() == 1) {
						((ImageButton) arg0).setImageResource(R.drawable.on);
						arg0.setTag(0);
					} else {
						arg0.setTag(1);
						((ImageButton) arg0).setImageResource(R.drawable.off);
					}

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
				public void onClick(View arg0) {
					if (appointment != null) {
						DBService.getInstance(AppointmentListActivity.this)
								.delete(appointment);
					}
					reflashright();
					reflashList();
				}
			});

			return convertView;
		}

		class ViewHolder {
			TextView tv_time;
			TextView tv_temperature;
			TextView tv_days;
			TextView tv_power;
			TextView name;
			ImageButton tb_switch;
			Button btn_delete;
			View rltfont, rltback;

		}
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.ivTitleBtnLeft:
			finish();
			break;

		default:
			break;
		}
	}
}
