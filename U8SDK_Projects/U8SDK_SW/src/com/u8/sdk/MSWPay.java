package com.u8.sdk;

import android.app.Activity;

public class MSWPay implements IPay {
	
	private Activity context;
	
	public MSWPay(Activity context){
		this.context = context;
	}
	
	@Override
	public boolean isSupportMethod(String methodName) {
		return false;
	}

	@Override
	public void pay(PayParams data) {
		SWSDK.getInstance().doPay(this.context, data);
	}

}
