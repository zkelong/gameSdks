package com.u8.sdk;

import android.app.Activity;

import com.u8.sdk.IPay;
import com.u8.sdk.PayParams;
import com.u8.sdk.log.Log;

public class BaiduPay implements IPay {

	public BaiduPay(Activity context) {
	}

	@Override
	public boolean isSupportMethod(String methodName) {
		return true;
	}

	@Override
	public void pay(PayParams data) {
		BaiduSDK.getInstance().pay(data);
	}
}
