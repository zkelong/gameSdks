package com.u8.sdk;

import android.app.Activity;

import com.u8.sdk.utils.Arrays;

public class QikeUser extends U8UserAdapter {
	
	private String[] supportedMethods = {"login","logout" };
	
	private Activity context;
	
	public QikeUser(Activity context){
		this.context = context;
		QikeSDK.getInstance().initSDK(this.context, U8SDK.getInstance().getSDKParams());
	}
	
	@Override
	public void login() {
		QikeSDK.getInstance().doLogin();
	}
	
	public void logout() {
		QikeSDK.getInstance().doLogout();
	}

	@Override
	public boolean isSupportMethod(String methodName) {
		return Arrays.contain(supportedMethods, methodName);
	}
}
