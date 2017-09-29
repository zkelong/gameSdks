package com.u8.sdk;

import android.app.Activity;

public class M4399Pay implements IPay {
	
	private Activity context;
	
	public M4399Pay(Activity context){
		this.context = context;
	}
	
	@Override
	public boolean isSupportMethod(String methodName) {
		return false;
	}

	@Override
	public void pay(PayParams data) {
		SSJJSDK.getInstance().doPay(this.context, data);
	}

}
