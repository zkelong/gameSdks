package com.u8.sdk;

import android.app.Activity;

public class QikePay implements IPay {
	private Activity context;
	
	public QikePay(Activity context){
		this.context = context;
	}
	
	@Override
	public void pay(PayParams data) {
		QikeSDK.getInstance().doPay(this.context, data);
	}

	@Override
	public boolean isSupportMethod(String methodName) {
		return true;
	}

}
