package com.vanward.ehheater.activity.main.common;

public class BaseSendCommandService {

	public interface BeforeSendCommandCallBack {
		void beforeSendCommand();
	}

	protected BeforeSendCommandCallBack beforeSendCommandCallBack;

	public BeforeSendCommandCallBack getBeforeSendCommandCallBack() {
		return beforeSendCommandCallBack;
	}

	public void setBeforeSendCommandCallBack(
			BeforeSendCommandCallBack beforeSendCommandCallBack) {
		this.beforeSendCommandCallBack = beforeSendCommandCallBack;
	}
}
