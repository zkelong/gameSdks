package com.u8.sdk;

import android.app.Activity;

import com.u8.sdk.IPay;
import com.u8.sdk.PayParams;

public class XiaoMiPay implements IPay {
	private Activity context;
	
	public XiaoMiPay(Activity context){
		this.context = context;
	}
	
	@Override
	public void pay(PayParams data) {
		XiaoMiSDK.getInstance().doPay(this.context, data);
	}

	@Override
	public boolean isSupportMethod(String methodName) {
		
		return true;
	}
}
