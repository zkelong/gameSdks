package com.u8.sdk;

import android.app.Activity;

import com.u8.sdk.IPay;
import com.u8.sdk.PayParams;

public class GuoPanPay implements IPay {
	private Activity context;
	
	public GuoPanPay(Activity context){
		this.context = context;
	}
	
	@Override
	public void pay(PayParams data) {
		GuoPanSDK.getInstance().doPay(this.context, data);
	}

	@Override
	public boolean isSupportMethod(String methodName) {
		
		return true;
	}
}
