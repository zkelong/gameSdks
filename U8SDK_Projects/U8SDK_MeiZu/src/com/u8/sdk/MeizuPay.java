package com.u8.sdk;

import android.app.Activity;

public class MeizuPay implements IPay {

	@Override
	public boolean isSupportMethod(String methodName) {
		return true;
	}
	
	public MeizuPay(Activity context) {
	}
	
	@Override
	public void pay(PayParams data) {
		MeizuSDK.getInstance().doPay(data);
	}

}
