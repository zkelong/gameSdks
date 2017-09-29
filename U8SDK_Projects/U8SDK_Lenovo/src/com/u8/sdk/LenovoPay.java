package com.u8.sdk;

import android.app.Activity;

import com.u8.sdk.IPay;
import com.u8.sdk.PayParams;

public class LenovoPay implements IPay {
	private Activity context;
	
	public LenovoPay(Activity context){
		this.context = context;
	}
	
	@Override
	public void pay(PayParams data) {
		LenovoSDK.getInstance().doPay(this.context, data);
	}

	@Override
	public boolean isSupportMethod(String methodName) {
		
		return true;
	}
}
