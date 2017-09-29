package com.u8.sdk;

import android.app.Activity;

public class CCPay implements IPay {
	private Activity context;
	
	public CCPay(Activity context){
		this.context = context;
	}
	
	@Override
	public void pay(PayParams data) {
		CCSDK.getInstance().doPay(this.context, data);
	}

	@Override
	public boolean isSupportMethod(String methodName) {
		return true;
	}

}
