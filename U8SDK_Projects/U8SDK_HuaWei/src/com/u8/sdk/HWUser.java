package com.u8.sdk;

import com.u8.sdk.log.Log;
import com.u8.sdk.utils.Arrays;

import android.app.Activity;

public class HWUser extends U8UserAdapter {
	
	private Activity context;
	private String[] supportedMethods = {"login", "loginCustom" };
		
	public HWUser(Activity context){
		Log.d("mmddt", "HWUser init");
		this.context = context;
		HWSDK.getInstance().initSDK(this.context, U8SDK.getInstance().getSDKParams());
	}
	
	@Override
	public void login() {
		HWSDK.getInstance().doLogin();
	}
	
	public void logout() {
		HWSDK.getInstance().doLogout();
	}

	@Override
	public boolean isSupportMethod(String methodName) {
		return Arrays.contain(supportedMethods, methodName);
	}
	
}
