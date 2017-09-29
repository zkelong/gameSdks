package com.u8.sdk;

import android.app.Activity;

public class HWPay implements IPay {
	
	private Activity context;
	
	public HWPay(Activity context){
		this.context = context;
	}
	
	@Override
	public boolean isSupportMethod(String methodName) {
		return true;
	}

	@Override
	public void pay(PayParams data) {
		HWSDK.getInstance().doPay(this.context, data);
	}

}
