package com.u8.sdk;

import android.app.Activity;

public class TsixiPay implements IPay {
	private Activity context;
	
	public TsixiPay(Activity context){
		this.context = context;
	}
	
	@Override
	public void pay(PayParams data) {
		TsixiSDK.getInstance().doPay(this.context, data);
	}

	@Override
	public boolean isSupportMethod(String methodName) {
		return true;
	}

}
