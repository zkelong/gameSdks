package com.u8.sdk;

import android.app.Activity;

import com.u8.sdk.utils.Arrays;

public class M4399User extends U8UserAdapter {
	
	private Activity context;
	private String[] supportedMethods = {"login","logout" };
		
	public M4399User(Activity context){
		this.context = context;
		SSJJSDK.getInstance().initSDK(this.context, U8SDK.getInstance().getSDKParams());
	}
	
	@Override
	public void login() {
		SSJJSDK.getInstance().doLogin();
	}
	
	public void logout() {
		SSJJSDK.getInstance().doLogout();
	}
	
	@Override
	public boolean isSupportMethod(String methodName) {
		return Arrays.contain(supportedMethods, methodName);
	}
	
}
