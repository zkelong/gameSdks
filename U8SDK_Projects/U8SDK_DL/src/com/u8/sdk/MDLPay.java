package com.u8.sdk;

import android.app.Activity;

public class MDLPay implements IPay {

	private Activity context;

	public MDLPay(Activity context) {
		this.context = context;
	}

	@Override
	public boolean isSupportMethod(String methodName) {
		return false;
	}

	@Override
	public void pay(PayParams data) {
		DLSDK.getInstance().doPay(this.context, data);
	}

}
