package com.u8.sdk;

import android.app.Activity;

import com.u8.sdk.utils.Arrays;

public class CCUser extends U8UserAdapter {

	private String[] supportedMethods = {"login","logout" };
	
	public CCUser(Activity context){
		CCSDK.getInstance().initSDK(context, U8SDK.getInstance().getSDKParams());
	}
	
	@Override
	public void login() {
		CCSDK.getInstance().doLogin();
	}
	
	public void logout() {
		CCSDK.getInstance().doLogout();
	}
	
	@Override
	public void exit() {
		CCSDK.getInstance().doExit();
	}

	@Override
	public boolean isSupportMethod(String methodName) {
		return Arrays.contain(supportedMethods, methodName);
	}

}
