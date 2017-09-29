package com.u8.sdk;

import android.app.Activity;

public class MSGPay implements IPay {
	
	private Activity context;
	
	public MSGPay(Activity context){
		this.context = context;
	}
	
	@Override
	public boolean isSupportMethod(String methodName) {
		return false;
	}

	@Override
	public void pay(PayParams data) {
		SGSDK.getInstance().doPay(this.context, data);
	}

}
