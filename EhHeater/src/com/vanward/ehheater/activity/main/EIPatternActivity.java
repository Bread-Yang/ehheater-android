package com.vanward.ehheater.activity.main;

import java.util.List;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.EhHeaterBaseActivity;
import com.vanward.ehheater.activity.global.Global;
import com.vanward.ehheater.dao.BaseDao;
import com.vanward.ehheater.service.AccountService;
import com.vanward.ehheater.util.L;
import com.vanward.ehheater.view.AddPatternButtonDialogUtil;
import com.vanward.ehheater.view.AddPatternNameDialogUtil;
import com.vanward.ehheater.view.SureDelDialogUtil;
import com.vanward.ehheater.view.TimeDialogUtil;
import com.vanward.ehheater.view.TimeDialogUtil.NextButtonCall;
import com.vanward.ehheater.view.WashNumDialogUtil;

public class EIPatternActivity extends EhHeaterBaseActivity implements
		OnClickListener, OnCheckedChangeListener {

	private static final String TAG = "EIPatternActivity";

	@ViewInject(id = R.id.textView1, click = "onClick")
	TextView textView1;
	@ViewInject(id = R.id.nopatterm, click = "onClick")
	TextView nopatterm;

	@ViewInject(id = R.id.couse_layout, click = "onClick")
	LinearLayout couse_layout;
	@ViewInject(id = R.id.textView2, click = "onClick")
	TextView textView2;
	@ViewInject(id = R.id.radioGroup1, click = "onClick")
	LinearLayout radioGroup1;
	@ViewInject(id = R.id.textradio0, click = "onClick")
	Button textradio0;
	@ViewInject(id = R.id.textradio1, click = "onClick")
	Button textradio1;
	@ViewInject(id = R.id.textradio2, click = "onClick")
	Button textradio2;
	@ViewInject(id = R.id.textradio3, click = "onClick")
	Button textradio3;
	@ViewInject(id = R.id.radio3, click = "onClick")
	RadioButton radio3;
	@ViewInject(id = R.id.radio1, click = "onClick")
	RadioButton radio1;
	@ViewInject(id = R.id.radio2, click = "onClick")
	RadioButton radio2;
	@ViewInject(id = R.id.radio0, click = "onClick")
	RadioButton radio0;
	@ViewInject(id = R.id.radioGroup2, click = "onClick")
	RadioGroup group;
	@ViewInject(id = R.id.mornongsetting, click = "onClick")
	ImageView mornongsetting;
	@ViewInject(id = R.id.ivTitleName, click = "onClick")
	TextView ivTitleName;
	@ViewInject(id = R.id.ivTitleBtnLeft, click = "onClick")
	Button ivTitleBtnLeft;
	@ViewInject(id = R.id.ivTitleBtnRigh, click = "onClick")
	Button ivTitleBtnRigh;
	@ViewInject(id = R.id.btn_add_pattern, click = "onClick")
	Button btn_add_pattern;
	@ViewInject(id = R.id.zidingyiradio, click = "onClick")
	LinearLayout zidingyiradioGroup;
	private List<CustomSetVo> customSetVolist;
	private String name;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pattern);
		FinalActivity.initInjectedView(this);
		group.setOnCheckedChangeListener(this);
		init();
	}

	private void init() {
		ivTitleName.setText("模式");
		ivTitleBtnLeft.setBackgroundResource(R.drawable.icon_back);
		ivTitleBtnRigh.setBackgroundResource(R.drawable.icon_add);
		ivTitleBtnRigh.setVisibility(View.INVISIBLE);
	}

	@Override
	protected void onResume() {
		super.onResume();
		initViewValue();

	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		L.e(this, "view:" + v.getId());
		switch (v.getId()) {
		case R.id.textradio0:
			radio0.setChecked(true);
			break;
		case R.id.textradio1:
			radio1.setChecked(true);
			break;
		case R.id.textradio2:
			radio2.setChecked(true);
			break;
		case R.id.textradio3:
			radio3.setChecked(true);
			break;
		case R.id.ivTitleBtnLeft:
			finish();
			break;
		case R.id.btn_add_pattern:
			Intent intent = new Intent();
			intent.setClass(this, EIAddPatternActivity.class);
			startActivity(intent);
			break;

		case R.id.mornongsetting:
			if (name.equals("晨浴模式")) {
				setMorningWashPeople(true);
			} else {
				setMorningWashPeople(false);
			}
			break;
		}
	}

	// 设置到智能模式
	private void setToIntelligenceMode() {
		SendMsgModel.changeToIntelligenceModeWash(this);
	}

	// 设置到自定义模式
	private void setToCustomMode() {
		SendMsgModel.changeToZidingyiMode();
	}

	// 设置到夜电模式
	private void setToNightMode() {
		SendMsgModel.changeNightMode();
	}

	// 设置到晨浴模式
	private void setToMorningWash() {
		MoringSeVo moringSeVo = MorningSettingModel.getInstance(this)
				.getSetting(Global.current_did);
		if (moringSeVo == null) {
			SendMsgModel.changeToMorningWash(1);
		} else {
			SendMsgModel.changeToMorningWash(moringSeVo.getPeople());
		}
		finish();
	}

	// 设置晨浴人数
	public void setMorningWashPeople(final boolean shouldswich) {
		WashNumDialogUtil.instance(EIPatternActivity.this).setDefaultCheck(0)
				.nextButtonCall(new NextButtonCall() {
					@Override
					public void oncall(View v) {
						TimeDialogUtil.instance(EIPatternActivity.this)
								.dissmiss();
						WashNumDialogUtil.instance(EIPatternActivity.this)
								.dissmiss();
						int num = WashNumDialogUtil.instance(
								EIPatternActivity.this).getNum();
						MoringSeVo moringSeVo = new MoringSeVo();
						moringSeVo.setId("1");
						moringSeVo.setPeople(num);
						MorningSettingModel.getInstance(EIPatternActivity.this)
								.saveSetting(moringSeVo);
						if (shouldswich) {
							setToMorningWash();
						}
					}
				}).showDialog();
	}

	@Override
	public void onCheckedChanged(RadioGroup arg0, int arg1) {
		switch (arg1) {
		case R.id.radio0:
			setToCustomMode();
			finish();
			break;
		case R.id.radio1:
			setMorningWashPeople(true);
			break;
		case R.id.radio2:
			setToNightMode();
			finish();
			break;
		case R.id.radio3:
			setToIntelligenceMode();
			finish();
			break;
		}

	}

	public void initViewValue() {
		customSetVolist = new BaseDao(this).getDb().findAllByWhere(
				CustomSetVo.class,
				" uid = '" + AccountService.getUserId(EIPatternActivity.this)
						+ "'");
		addCustomView();
		if (customSetVolist.size() >= 3) {
			btn_add_pattern.setVisibility(View.GONE);
		} else {
			btn_add_pattern.setVisibility(View.VISIBLE);
		}
		name = getIntent().getStringExtra("name");
		name.replace("模式", "");
		setRadiocheck(name, getWindow().getDecorView());
	}

	public void setRadiocheck(String name, View root) {
		try {
			if (root instanceof ViewGroup) {
				ViewGroup viewGroup = (ViewGroup) root;
				for (int i = 0; i < viewGroup.getChildCount(); i++) {
					setRadiocheck(name, viewGroup.getChildAt(i));
				}
			} else if (root instanceof RadioButton) {
				RadioButton radioButton = ((RadioButton) root);
				if (radioButton.getTag().equals(name)) {
					group.setOnCheckedChangeListener(null);
					radioButton.setChecked(true);
					group.setOnCheckedChangeListener(this);
				} else {
					group.setOnCheckedChangeListener(null);
					radioButton.setChecked(false);
					group.setOnCheckedChangeListener(this);
				}
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	public void addCustomView() {
		zidingyiradioGroup.removeAllViews();
		if (customSetVolist != null && customSetVolist.size() > 0) {
			int size = 0;
			if (customSetVolist.size() > 3) {
				size = 3;
			} else {
				size = customSetVolist.size();
			}
			for (int i = 0; i < size; i++) {
				zidingyiradioGroup.addView(
						initCustomItemView(customSetVolist.get(i), false), 0);
				View view2 = new View(this);
				view2.setBackgroundColor(R.color.line);
				LinearLayout.LayoutParams lParams = new LayoutParams(
						LayoutParams.FILL_PARENT, 1);
				zidingyiradioGroup.addView(view2, 1, lParams);
			}
			// nopatterm.setVisibility(View.GONE);
		} else {
			// nopatterm.setVisibility(View.VISIBLE);
		}
	}

	public View initCustomItemView(final CustomSetVo customSetVo,
			final Boolean isCheck) {
		View view = LinearLayout.inflate(this,
				R.layout.layout_addcustom_layout, null);
		final RadioButton radioButton = (RadioButton) view
				.findViewById(R.id.radioButton1);
		Button button = (Button) view.findViewById(R.id.textradio1);
		final RadioButton tempradioButton = (RadioButton) view
				.findViewById(R.id.radioButton1);
		view.findViewById(R.id.setting).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						AddPatternButtonDialogUtil
								.instance(EIPatternActivity.this)
								.nextButtonCall(new NextButtonCall() {
									@Override
									public void oncall(View v) {
										// 重命名
										reNameCustom(customSetVo);
										AddPatternButtonDialogUtil.instance(
												EIPatternActivity.this)
												.dissmiss();
									}
								}).editButtonCall(new NextButtonCall() {
									@Override
									public void oncall(View v) {
										intent = new Intent(
												EIPatternActivity.this,
												EIAddPatternActivity.class);
										intent.putExtra("index",
												customSetVo.getName());
										intent.putExtra("ischeck",
												tempradioButton.isChecked());
										startActivity(intent);
										AddPatternButtonDialogUtil.instance(
												EIPatternActivity.this)
												.dissmiss();
										if (tempradioButton.isChecked()) {
											finish();
										}
									}
								})

								.lastButtonCall(new NextButtonCall() {

									@Override
									public void oncall(View v) {
										AddPatternButtonDialogUtil.instance(
												EIPatternActivity.this)
												.dissmiss();
										SureDelDialogUtil
												.instance(
														EIPatternActivity.this)
												.setNextButtonCall(
														new NextButtonCall() {
															@Override
															public void oncall(
																	View v) {
																// 删除
																new BaseDao(
																		EIPatternActivity.this)
																		.getDb()
																		.delete(customSetVo);
																if (radioButton
																		.isChecked()) {
																	customSetVolist = new BaseDao(
																			EIPatternActivity.this)
																			.getDb()
																			.findAllByWhere(
																					CustomSetVo.class,
																					" uid = '"
																							+ AccountService
																									.getUserId(EIPatternActivity.this)
																							+ "'");
																	CustomSetVo customSetVo = null;
																	if (customSetVolist
																			.size() != 0) {
																		for (CustomSetVo item : customSetVolist) {
																			item.setSet(false);
																			new BaseDao(
																					EIPatternActivity.this)
																					.getDb()
																					.update(item);
																		}
																		customSetVo = customSetVolist
																				.get(customSetVolist
																						.size() - 1);
																		customSetVo
																				.setSet(true);
																		new BaseDao(
																				EIPatternActivity.this)
																				.getDb()
																				.update(customSetVo);
																		setToDIY(customSetVo);
																		finish();
																	} else {
																		setToMorningWash();
																		finish();
																	}
																}
																initViewValue();
																SureDelDialogUtil
																		.instance(
																				EIPatternActivity.this)
																		.dissmiss();
															}
														}).showDialog();

									}
								}).showDialog();

					}
				});

		button.setText(customSetVo.getName());
		radioButton.setTag(customSetVo.getName());
		radioButton.setChecked(isCheck);
		// 点击切换
		OnClickListener onClickListener = new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// 清空其他的
				setRadiocheck("", getWindow().getDecorView());
				radioButton.setChecked(true);
				new Thread(new Runnable() {
					@Override
					public void run() {
						for (CustomSetVo item : customSetVolist) {
							item.setSet(false);
							new BaseDao(EIPatternActivity.this).getDb().update(
									item);
						}
						customSetVo.setSet(true);
						new BaseDao(EIPatternActivity.this).getDb().update(
								customSetVo);
						setToDIY(customSetVo);
						finish();
					}
				}).start();

			}
		};
		button.setOnClickListener(onClickListener);
		radioButton.setOnClickListener(onClickListener);

		return view;
	}

	public void setToDIY(CustomSetVo customSetVo) {
		try {
			int persons = customSetVo.getPeoplenum();

//			if (persons >= 1 && persons <= 3) {
//				generated.SendPatternSettingReq(Global.connectId,
//						(short) (persons + 4));
//				return;
//			}

			L.e(this, "自定义");
			SendMsgModel.changeToZidingyiMode();
			Thread.sleep(700);
			L.e(this, "自定义 pow: " + customSetVo.getPower());
			SendMsgModel.setPower(customSetVo.getPower());
			Thread.sleep(700);
			L.e(this, "自定义 Tem: " + customSetVo.getTempter());
			SendMsgModel.setTempter(customSetVo.getTempter());

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 重命名
	 * 
	 * @param customSetVo
	 */
	public void reNameCustom(final CustomSetVo customSetVo) {
		AddPatternNameDialogUtil.instance(this)
				.nextButtonCall(new NextButtonCall() {

					@Override
					public void oncall(View v) {
						String name = AddPatternNameDialogUtil.instance(
								EIPatternActivity.this).getName();

						if (name != null && name.length() != 0) {
							customSetVo.setName(name);
							new BaseDao(EIPatternActivity.this).getDb().update(
									customSetVo);
						} else {
							Toast.makeText(EIPatternActivity.this, "请输入有效名字",
									Toast.LENGTH_SHORT).show();
						}
						initViewValue();
						AddPatternNameDialogUtil.instance(
								EIPatternActivity.this).dissmiss();
					}
				}).showDialog();

	}

}
