package com.vanward.ehheater.activity.more;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

import com.vanward.ehheater.R;
import com.vanward.ehheater.activity.CloudBaseActivity;
import com.vanward.ehheater.service.AccountService;
import com.vanward.ehheater.util.DialogUtil;
import com.xtremeprog.xpgconnect.generated.UserPwdChangeResp_t;
import com.xtremeprog.xpgconnect.generated.XpgDataField;
import com.xtremeprog.xpgconnect.generated.generated;

public class ChangePasswordActivity extends CloudBaseActivity {

	private CheckBox cb_show_pwd;
	private Button btn_confirm;
	private List<EditText> editList;
	int connId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCenterView(R.layout.activity_change_password);
		findViewById();
		setListener();
		init();
	}

	private void findViewById() {
		cb_show_pwd = (CheckBox) findViewById(R.id.cb_show_pwd);
		btn_confirm = (Button) findViewById(R.id.btn_confirm);
	}

	private void setListener() {
		cb_show_pwd.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				 for (EditText editLists : editList ){
					 editLists.setInputType(isChecked ?
					 InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
					 InputType.TYPE_CLASS_TEXT |
					 InputType.TYPE_TEXT_VARIATION_PASSWORD);
					 editLists.setSelection(editLists.getText().length());
				 }
			}
		});

		btn_confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				boolean isNull = false;
				
				for( EditText et : editList ){
					if( et.getText().length() == 0 ){
						isNull = true;
						Toast.makeText(getBaseContext(), R.string.not_null, Toast.LENGTH_SHORT).show();
						break;
					}
				}
				if( !isNull ){
					if( !AccountService.getUserPsw(getBaseContext()).equals(editList.get(0).getEditableText().toString()) ){
						
						Toast.makeText(getBaseContext(), R.string.old_pwd_error, Toast.LENGTH_SHORT).show();
						
					}else{
						if( !editList.get(1).getEditableText().toString().equals(editList.get(2).getEditableText().toString()) ){
							
							Toast.makeText(getBaseContext(), R.string.new_pwd_error, Toast.LENGTH_SHORT).show();
							
						}else{
							DialogUtil.instance().showLoadingDialog(ChangePasswordActivity.this, "");
							XpgDataField uid = generated.String2XpgData(AccountService.getUserId(getBaseContext()));
							XpgDataField upd = generated.String2XpgData(editList.get(1).getEditableText().toString());
							generated.SendUserPwdChangeReq(connId, uid , upd);
						}
					}
				}
				
				
			}
		});
	}

	private void init() {
		setTopText(R.string.change_password);
		setRightButton(View.GONE);
		editList = new ArrayList<EditText>();
		editList.add((EditText) findViewById(R.id.password));
		editList.add((EditText) findViewById(R.id.new_password));
		editList.add((EditText) findViewById(R.id.new_password_confirm));
		setLeftButtonBackground(R.drawable.icon_back);
	}

	
	
	@Override
	public void OnUserPwdChangeResp(UserPwdChangeResp_t pResp, int nConnId) {
		
		DialogUtil.dismissDialog();
		
		super.OnUserPwdChangeResp(pResp, nConnId);
		
		if( pResp.getResult() == 0 ){
			Toast.makeText(getBaseContext(), R.string.success, Toast.LENGTH_SHORT).show();
			onBackPressed();
		}else{
			Toast.makeText(getBaseContext(), R.string.failure, Toast.LENGTH_SHORT).show();
		}
	}

}
