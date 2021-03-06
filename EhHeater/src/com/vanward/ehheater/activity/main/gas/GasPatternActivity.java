package com.vanward.ehheater.activity.main.gas;

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
import com.vanward.ehheater.dao.BaseDao;
import com.vanward.ehheater.service.AccountService;
import com.vanward.ehheater.util.L;
import com.vanward.ehheater.view.AddPatternButtonDialogUtil;
import com.vanward.ehheater.view.AddPatternNameDialogUtil;
import com.vanward.ehheater.view.BathSettingDialogUtil;
import com.vanward.ehheater.view.SureDelDialogUtil;
import com.vanward.ehheater.view.TimeDialogUtil.NextButtonCall;

public class GasPatternActivity extends EhHeaterBaseActivity implements
		OnClickListener, OnCheckedChangeListener {

	private static final String TAG = "GasPatternActivity";

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
	@ViewInject(id = R.id.imageView1, click = "onClick")
	ImageView yugaosetting;

	private List<GasCustomSetVo> customSetVolist;
	private String name;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pattern_gas);
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
	public void onCheckedChanged(RadioGroup arg0, int arg1) {
		L.e(this, "onCheckedChanged()");
		switch (arg1) {
		case R.id.radio0:
			GasHeaterSendCommandService.getInstance().setToDIYMode();
			finish();
			break;
		case R.id.radio1:
			GasHeaterSendCommandService.getInstance().setToKictionMode();
			finish();
			break;
		case R.id.radio2:
			GasHeaterSendCommandService.getInstance().setToSolfMode();
			finish();
			break;
		case R.id.radio3:
			GasHeaterSendCommandService.getInstance().setToBathtubMode(this);
			finish();
			break;
		case R.id.radio4:
			GasHeaterSendCommandService.getInstance().setToEnergyMode();
			finish();
			break;
		case R.id.radio5:
			GasHeaterSendCommandService.getInstance().setToIntelligenceMode();
			finish();
			break;
		}

	}

	@Override
	public void onClick(View v) {
		System.out.println("view:" + v.getId());
		switch (v.getId()) {
		case R.id.ivTitleBtnLeft:
			finish();
			break;
		case R.id.btn_add_pattern:
			Intent intent = new Intent();
			intent.setClass(this, GasAddPatternActivity.class);
			startActivity(intent);
			break;
		case R.id.radio3:
			GasHeaterSendCommandService.getInstance().setToBathtubMode(this);
			finish();
			break;
		case R.id.imageView1:
			BathSettingDialogUtil.instance(this)
					.nextButtonCall(new NextButtonCall() {
						@Override
						public void oncall(View v) {
							if (radio3.isChecked()) {
								GasHeaterSendCommandService.getInstance()
										.setToBathtubMode(
												GasPatternActivity.this);
								finish();
							}
						}
					}).showDialog();
			break;
		}
	}

	public void initViewValue() {
		customSetVolist = new BaseDao(this).getDb().findAllByWhere(
				GasCustomSetVo.class,
				" uid = '" + AccountService.getUserId(GasPatternActivity.this)
						+ "'");
		name = getIntent().getStringExtra("name");
		addCustomView();
		if (customSetVolist.size() >= 3) {
			// ivTitleBtnRigh.setVisibility(View.GONE);
			btn_add_pattern.setVisibility(View.GONE);
		} else {
			// ivTitleBtnRigh.setVisibility(View.VISIBLE);
			btn_add_pattern.setVisibility(View.VISIBLE);
		}
		// name = name.replace("模式", "");
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
					System.out.println(radioButton.getTag() + "true");
					group.setOnCheckedChangeListener(this);
				} else {
					group.setOnCheckedChangeListener(null);
					radioButton.setChecked(false);
					System.out.println(name + "：" + radioButton.getTag()
							+ "false");
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
				if (name.equals(customSetVolist.get(i).getName())) {
					zidingyiradioGroup
							.addView(
									initCustomItemView(customSetVolist.get(i),
											true), 0);
				} else {
					zidingyiradioGroup.addView(
							initCustomItemView(customSetVolist.get(i), false),
							0);
				}

				View view2 = new View(this);
				view2.setBackgroundColor(getResources().getColor(R.color.line));
				LinearLayout.LayoutParams lParams = new LayoutParams(
						LayoutParams.FILL_PARENT, 1);
				zidingyiradioGroup.addView(view2, 1, lParams);
			}
			// nopatterm.setVisibility(View.GONE);
		} else {
			// nopatterm.setVisibility(View.VISIBLE);
		}
	}

	public View initCustomItemView(final GasCustomSetVo customSetVo,
			final Boolean isCheck) {
		View view = LinearLayout.inflate(this,
				R.layout.layout_addcustom_layout, null);
		final RadioButton radioButton = (RadioButton) view
				.findViewById(R.id.radioButton1);
		Button button = (Button) view.findViewById(R.id.textradio1);
		view.findViewById(R.id.setting).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						AddPatternButtonDialogUtil
								.instance(GasPatternActivity.this)
								.nextButtonCall(new NextButtonCall() {
									@Override
									public void oncall(View v) {
										// 重命名
										reNameCustom(customSetVo);
										AddPatternButtonDialogUtil.instance(
												GasPatternActivity.this)
												.dissmiss();
									}
								}).editButtonCall(new NextButtonCall() {
									@Override
									public void oncall(View v) {
										/*
										 * AddPatternGasSettingDialogUtil
										 * .instance(PatternActivity.this)
										 * .initName( customSetVo.getName(),
										 * customSetVo.getId()) .nextButtonCall(
										 * new NextButtonCall() {
										 * 
										 * @Override public void oncall( View v)
										 * { GasCustomSetVo tempcustomSetVo =
										 * AddPatternGasSettingDialogUtil
										 * .instance( PatternActivity.this)
										 * .getData(); tempcustomSetVo
										 * .setId(customSetVo .getId()); new
										 * BaseDao( PatternActivity.this)
										 * .getDb() .update(tempcustomSetVo); if
										 * (isCheck) { SendMsgModel
										 * .setDIYModel( tempcustomSetVo
										 * .getId(), tempcustomSetVo); finish();
										 * }
										 * 
										 * initViewValue();
										 * AddPatternGasSettingDialogUtil
										 * .instance( PatternActivity.this)
										 * .dissmiss(); } }).showDialog();
										 */

										Intent intent = new Intent();
										intent.setClass(
												GasPatternActivity.this,
												GasAddPatternActivity.class);
										intent.putExtra("gasCusVoId",
												customSetVo.getId());
										intent.putExtra("ischeck", isCheck);
										startActivity(intent);

										AddPatternButtonDialogUtil.instance(
												GasPatternActivity.this)
												.dissmiss();
										if (isCheck) {
											finish();
										}

									}
								})

								.lastButtonCall(new NextButtonCall() {

									@Override
									public void oncall(View v) {
										AddPatternButtonDialogUtil.instance(
												GasPatternActivity.this)
												.dissmiss();
										SureDelDialogUtil
												.instance(
														GasPatternActivity.this)
												.setNextButtonCall(
														new NextButtonCall() {
															@Override
															public void oncall(
																	View v) {
																// 删除
																new BaseDao(
																		GasPatternActivity.this)
																		.getDb()
																		.delete(customSetVo);
																if (isCheck) {
																	customSetVolist = new BaseDao(
																			GasPatternActivity.this)
																			.getDb()
																			.findAllByWhere(
																					GasCustomSetVo.class,
																					" uid = '"
																							+ AccountService
																									.getUserId(GasPatternActivity.this)
																							+ "'");
																	if (customSetVolist
																			.size() != 0) {
																		for (GasCustomSetVo item : customSetVolist) {
																			item.setSet(false);
																			new BaseDao(
																					GasPatternActivity.this)
																					.getDb()
																					.update(item);
																		}
																		GasCustomSetVo tempcustomSetVo = customSetVolist
																				.get(customSetVolist
																						.size() - 1);
																		tempcustomSetVo
																				.setSet(true);
																		new BaseDao(
																				GasPatternActivity.this)
																				.getDb()
																				.update(tempcustomSetVo);
																		// GasCustomSetVo
																		// tempcustomSetVo
																		// = new
																		// BaseDao(
																		// PatternActivity.this)
																		// .getDb()
																		// .findById(
																		// customSetVo
																		// .getId()
																		// + 1,
																		// GasCustomSetVo.class);
																		GasHeaterSendCommandService
																				.getInstance()
																				.setDIYModel(
																						customSetVo
																								.getSendId(),
																						tempcustomSetVo);
																		finish();
																	} else {
																		GasHeaterSendCommandService
																				.getInstance()
																				.setToSolfMode();
																		finish();
																	}
																}

																initViewValue();

																SureDelDialogUtil
																		.instance(
																				GasPatternActivity.this)
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
				L.e(this, "customSetVolist的大小是 : " + customSetVolist.size());
				setRadiocheck("", getWindow().getDecorView());
				radioButton.setChecked(true);
				for (GasCustomSetVo item : customSetVolist) {
					item.setSet(false);
					new BaseDao(GasPatternActivity.this).getDb().update(item);
				}
				customSetVo.setSet(true);
				new BaseDao(GasPatternActivity.this).getDb()
						.update(customSetVo);

				GasHeaterSendCommandService.getInstance().setDIYModel(
						customSetVo.getSendId(), customSetVo);
				finish();
			}
		};
		button.setOnClickListener(onClickListener);
		radioButton.setOnClickListener(onClickListener);
		return view;
	}

	/**
	 * 重命名
	 * 
	 * @param customSetVo
	 */
	public void reNameCustom(final GasCustomSetVo customSetVo) {
		AddPatternNameDialogUtil.instance(this)
				.nextButtonCall(new NextButtonCall() {

					@Override
					public void oncall(View v) {
						String name = AddPatternNameDialogUtil.instance(
								GasPatternActivity.this).getName();

						if (name != null && name.length() != 0) {
							List<GasCustomSetVo> list = new BaseDao(
									GasPatternActivity.this)
									.getDb()
									.findAllByWhere(
											GasCustomSetVo.class,
											" uid = '"
													+ AccountService
															.getUserId(GasPatternActivity.this)
													+ "'");
							for (int i = 0; i < list.size(); i++) {
								GasCustomSetVo gasCustomSetVo = list.get(i);
								if (gasCustomSetVo.getName().equals(name)) {
									Toast.makeText(GasPatternActivity.this,
											"请输入有效名字", Toast.LENGTH_SHORT)
											.show();
									return;
								}
							}
							customSetVo.setName(name);
							new BaseDao(GasPatternActivity.this).getDb()
									.update(customSetVo);
						} else {
							Toast.makeText(GasPatternActivity.this, "请输入有效名字",
									Toast.LENGTH_SHORT).show();
						}
						initViewValue();
						AddPatternNameDialogUtil.instance(
								GasPatternActivity.this).dissmiss();
					}
				}).showDialog();
	}

}
