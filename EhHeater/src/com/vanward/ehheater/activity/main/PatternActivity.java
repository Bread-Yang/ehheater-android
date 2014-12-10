package com.vanward.ehheater.activity.main;

import java.util.List;

import u.aly.i;
import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.EhHeaterBaseActivity;
import com.vanward.ehheater.activity.global.Global;
import com.vanward.ehheater.dao.BaseDao;
import com.vanward.ehheater.statedata.EhState;
import com.vanward.ehheater.util.TcpPacketCheckUtil;
import com.vanward.ehheater.view.AddPatternButtonDialogUtil;
import com.vanward.ehheater.view.AddPatternNameDialogUtil;
import com.vanward.ehheater.view.AddPatternSettingDialogUtil;
import com.vanward.ehheater.view.ChangeStuteView;
import com.vanward.ehheater.view.CircleListener;
import com.vanward.ehheater.view.CircularView;
import com.vanward.ehheater.view.PowerSettingDialogUtil;
import com.vanward.ehheater.view.SureDelDialogUtil;
import com.vanward.ehheater.view.TimeDialogUtil;
import com.vanward.ehheater.view.TimeDialogUtil.NextButtonCall;
import com.vanward.ehheater.view.WashNumDialogUtil;
import com.vanward.ehheater.view.fragment.SlidingMenu;
import com.vanward.ehheater.view.wheelview.WheelView;
import com.xtremeprog.xpgconnect.generated.StateResp_t;
import com.xtremeprog.xpgconnect.generated.generated;

public class PatternActivity extends EhHeaterBaseActivity implements
		OnClickListener, OnCheckedChangeListener {

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
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initViewValue();

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

	}

	@Override
	public void onClick(View v) {
		System.out.println("view:" + v.getId());
		// TODO Auto-generated method stub
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
		case R.id.ivTitleBtnRigh:

			Intent intent = new Intent();
			intent.setClass(this, AddPattenActivity.class);
			startActivity(intent);

			// AddPatternNameDialogUtil.instance(this)
			// .setNextButtonCall(new NextButtonCall() {
			// @Override
			// public void oncall(View v) {
			// final String name = AddPatternNameDialogUtil
			// .instance(PatternActivity.this).getName();
			// if (AddPatternNameDialogUtil.instance(
			// PatternActivity.this).isNameExit()) {
			// Toast.makeText(PatternActivity.this, "此名字已存在",
			// Toast.LENGTH_SHORT).show();
			// return;
			// }
			//
			// if (name != null && name.length() > 0) {
			// AddPatternSettingDialogUtil
			// .instance(PatternActivity.this)
			// .initName(name)
			// .nextButtonCall(new NextButtonCall() {
			//
			// @Override
			// public void oncall(View v) {
			// // TODO Auto-generated method
			// // stub
			// CustomSetVo customSetVo = AddPatternSettingDialogUtil
			// .instance(
			// PatternActivity.this)
			// .getData();
			// customSetVo.setName(name);
			// new BaseDao(
			// PatternActivity.this)
			// .getDb().save(
			// customSetVo);
			//
			// initViewValue();
			// AddPatternSettingDialogUtil
			// .instance(
			// PatternActivity.this)
			// .dissmiss();
			// }
			// }).showDialog();
			// }
			// AddPatternNameDialogUtil.instance(
			// PatternActivity.this).dissmiss();
			// }
			// }).showDialog();
			break;

		case R.id.mornongsetting:
			if (name.equals("晨浴模式")) {
				setMorningWashPeople(true);
			}else {
				setMorningWashPeople(false);
			}
		
			break;
		}
	}

	// 设置到智能模式
	private void setToIntelligenceMode() {
		SendMsgModel.changeToIntelligenceModeWash();
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
		WashNumDialogUtil.instance(PatternActivity.this).setDefaultCheck(0)
				.nextButtonCall(new NextButtonCall() {
					@Override
					public void oncall(View v) {
						TimeDialogUtil.instance(PatternActivity.this)
								.dissmiss();
						WashNumDialogUtil.instance(PatternActivity.this)
								.dissmiss();
						int num = WashNumDialogUtil.instance(
								PatternActivity.this).getNum();
						MoringSeVo moringSeVo = new MoringSeVo();
						moringSeVo.setId("1");
						moringSeVo.setPeople(num);
						MorningSettingModel.getInstance(PatternActivity.this)
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
		customSetVolist = new BaseDao(this).getDb().findAll(CustomSetVo.class);
		addCustomView();
		if (customSetVolist.size() >= 3) {
			ivTitleBtnRigh.setVisibility(View.GONE);
		} else {
			ivTitleBtnRigh.setVisibility(View.VISIBLE);
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
				System.out.println(radioButton.getText());
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
				zidingyiradioGroup.addView(initCustomItemView(
						customSetVolist.get(i), false));
				View view2 = new View(this);
				view2.setBackgroundColor(R.color.line);
				LinearLayout.LayoutParams lParams = new LayoutParams(
						LayoutParams.FILL_PARENT, 1);
				zidingyiradioGroup.addView(view2, lParams);

			}
			nopatterm.setVisibility(View.GONE);
		} else {
			nopatterm.setVisibility(View.VISIBLE);
		}
	}

	public View initCustomItemView(final CustomSetVo customSetVo,
			final Boolean isCheck) {
		View view = LinearLayout.inflate(this,
				R.layout.layout_addcustom_layout, null);
		final RadioButton radioButton = (RadioButton) view
				.findViewById(R.id.radioButton1);
		Button button = (Button) view.findViewById(R.id.textradio1);
		final RadioButton	tempradioButton=	(RadioButton) view.findViewById(R.id.radioButton1);
		view.findViewById(R.id.setting).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						AddPatternButtonDialogUtil
								.instance(PatternActivity.this)
								.nextButtonCall(new NextButtonCall() {
									@Override
									public void oncall(View v) {
										// 重命名
										reNameCustom(customSetVo);
										AddPatternButtonDialogUtil.instance(
												PatternActivity.this)
												.dissmiss();
									}
								}).editButtonCall(new NextButtonCall() {
									@Override
									public void oncall(View v) {
										intent= new  Intent(PatternActivity.this,AddPattenActivity.class);
										intent.putExtra("index", customSetVo.getName());
										intent.putExtra("ischeck", tempradioButton.isChecked());
										startActivity(intent);
										AddPatternButtonDialogUtil.instance(PatternActivity.this).dissmiss();
										if (tempradioButton.isChecked()) {
											finish();
										}
									}
								})

								.lastButtonCall(new NextButtonCall() {

									@Override
									public void oncall(View v) {
										AddPatternButtonDialogUtil.instance(
												PatternActivity.this)
												.dissmiss();
										SureDelDialogUtil
												.instance(PatternActivity.this)
												.setNextButtonCall(
														new NextButtonCall() {
															@Override
															public void oncall(
																	View v) {
																// 删除
																new BaseDao(
																		PatternActivity.this)
																		.getDb()
																		.delete(customSetVo);
																System.out
																		.println("isCheck: "
																				+ isCheck);
																if (radioButton
																		.isChecked()) {
																	customSetVolist = new BaseDao(
																			PatternActivity.this)
																			.getDb()
																			.findAll(
																					CustomSetVo.class);
																	CustomSetVo customSetVo = null;
																	if (customSetVolist
																			.size() != 0) {
																		customSetVo= customSetVolist.get(0);
																		setToDIY(customSetVo);
																		finish();
																	}
																}
																initViewValue();
																SureDelDialogUtil
																		.instance(
																				PatternActivity.this)
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
			
			if (persons >= 1 && persons <= 3) {
				generated.SendPatternSettingReq(Global.connectId, (short) (persons+4));
				return;
			}
			
			System.out.println("自定义");
			SendMsgModel.changeToZidingyiMode();
			Thread.sleep(700);
			System.out.println("自定义 pow: " + customSetVo.getPower());
			SendMsgModel.setPower(customSetVo.getPower());
			Thread.sleep(700);
			System.out.println("自定义 Tem: " + customSetVo.getTempter());
			SendMsgModel.setTempter(customSetVo.getTempter());
			
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
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
								PatternActivity.this).getName();

						if (name != null && name.length() != 0) {
							customSetVo.setName(name);
							new BaseDao(PatternActivity.this).getDb().update(
									customSetVo);
						} else {
							Toast.makeText(PatternActivity.this, "请输入有效名字",
									Toast.LENGTH_SHORT).show();
						}
						initViewValue();
						AddPatternNameDialogUtil.instance(PatternActivity.this)
								.dissmiss();
					}
				}).showDialog();

	}

}
