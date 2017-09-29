package com.u8.sdk;

import android.app.Activity;

import com.u8.sdk.utils.Arrays;

public class TsixiUser extends U8UserAdapter {

	private String[] supportedMethods = {"login","logout" };
	
	public TsixiUser(Activity context){
		TsixiSDK.getInstance().initSDK(context, U8SDK.getInstance().getSDKParams());
	}
	
	@Override
	public void login() {
		TsixiSDK.getInstance().doLogin();
	}
	
	public void logout() {
		TsixiSDK.getInstance().doLogout();
	}

	@Override
	public boolean isSupportMethod(String methodName) {
		return Arrays.contain(supportedMethods, methodName);
	}

}
