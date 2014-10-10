package com.vanward.ehheater.application;

import com.vanward.ehheater.statedata.EhState;
import com.xtremeprog.xpgconnect.generated.GasWaterHeaterStatusResp_t;
import com.xtremeprog.xpgconnect.generated.StateResp_t;

import android.app.Application;
import android.graphics.Typeface;

public class EhHeaterApplication extends Application {

	public static Typeface number_tf;

	public static float device_density;
	
	public static EhState currentEhState;
	
	public static GasWaterHeaterStatusResp_t currentGasHeaterStatus;

	@Override
	public void onCreate() {
		super.onCreate();

		EhHeaterApplication.number_tf = Typeface.createFromAsset(getAssets(),
				"fonts/number.otf");

		EhHeaterApplication.device_density = this.getResources()
				.getDisplayMetrics().density;
	}
}
