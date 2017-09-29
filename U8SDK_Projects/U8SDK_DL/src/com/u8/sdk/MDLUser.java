package com.u8.sdk;

import android.app.Activity;

import com.u8.sdk.utils.Arrays;

public class MDLUser extends U8UserAdapter {

	private Activity context;
	private String[] supportedMethods = { "login", "logout", "exit" };

	public MDLUser(Activity context) {
		this.context = context;
		DLSDK.getInstance().initSDK(this.context, U8SDK.getInstance().getSDKParams());
	}

	@Override
	public void login() {
		DLSDK.getInstance().doLogin();
	}

	public void logout() {
		DLSDK.getInstance().doLogout();
	}

	@Override
	public void exit() {
		// TODO Auto-generated method stub
		DLSDK.getInstance().onBackPressed();
		super.exit();
	}

	@Override
	public boolean isSupportMethod(String methodName) {
		return Arrays.contain(supportedMethods, methodName);
	}

}
