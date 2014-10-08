package com.easylink.android;


public class EasyLinkApiManager implements FirstTimeConfigListener
{
	@Override
	public void onFirstTimeConfigEvent(FtcEvent arg0, Exception arg1)
	{
		arg1.printStackTrace();
		switch (arg0)
		{
		case FTC_ERROR:
			break;
		case FTC_SUCCESS:

			break;
		case FTC_TIMEOUT:
			break;

		default:
			break;
		}
	}

}
