package com.u8.sdk;

import android.app.Activity;

import com.u8.sdk.IPay;
import com.u8.sdk.PayParams;

public class VIVOPay implements IPay {
	private Activity context;
	
	public VIVOPay(Activity context){
		this.context = context;
	}
	
	@Override
	public void pay(PayParams data) {
		VIVOSDK.getInstance().doPay(this.context, data);
	}

	@Override
	public boolean isSupportMethod(String methodName) {
		
		return true;
	}
}
