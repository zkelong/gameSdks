package com.u8.sdk;

import com.u8.sdk.utils.Arrays;

import android.app.Activity;

public class MYWUser extends U8UserAdapter {
	
	private Activity context;
	private String[] supportedMethods = {"login" };
		
	public MYWUser(Activity context){
		this.context = context;
		YWSDK.getInstance().initSDK(this.context, U8SDK.getInstance().getSDKParams());
	}
	
	@Override
	public void login() {
		YWSDK.getInstance().doLogin();
	}
	
	public void logout() {
		YWSDK.getInstance().doLogout();
	}

	@Override
	public boolean isSupportMethod(String methodName) {
		return Arrays.contain(supportedMethods, methodName);
	}
	
}
