package com.u8.sdk;

import android.app.Activity;

import com.u8.sdk.utils.Arrays;

public class MeizuUser extends U8UserAdapter {

	private String[] supportedMethods = { "login", "logout", "exit", "switchLogin" };

	public MeizuUser(Activity context) {
		MeizuSDK.getInstance().initSDK(U8SDK.getInstance().getSDKParams());
	}

	@Override
	public void login() {
		MeizuSDK.getInstance().doLogin();
	}

	@Override
	public void logout() {
		MeizuSDK.getInstance().doLogOut();
	}

	@Override
	public void exit() {
		MeizuSDK.getInstance().exitGame();
		super.exit();
	}

	@Override
	public void switchLogin() {
		MeizuSDK.getInstance().doLogOut();
		MeizuSDK.getInstance().doLogin();
	}

	@Override
	public boolean isSupportMethod(String methodName) {
		return Arrays.contain(supportedMethods, methodName);
	}

}
