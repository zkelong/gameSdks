package com.u8.sdk;

import android.app.Activity;

public class MYWPay implements IPay {
	
	private Activity context;
	
	public MYWPay(Activity context){
		this.context = context;
	}
	
	@Override
	public boolean isSupportMethod(String methodName) {
		return false;
	}

	@Override
	public void pay(PayParams data) {
		YWSDK.getInstance().doPay(this.context, data);
	}

}
