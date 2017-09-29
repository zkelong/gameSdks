package com.u8.sdk;

import android.app.Activity;

import com.u8.sdk.U8SDK;
import com.u8.sdk.utils.Arrays;

public class AnzhiUser extends U8UserAdapter {

	private String[] supportedMethods = { "login", "logout", "submitExtraData", "exit" };

	private Activity context;

	public AnzhiUser(Activity context) {
		this.context = context;
		AnzhiSDK.getInstance().initSDK(this.context, U8SDK.getInstance().getSDKParams());
	}

	@Override
	public void login() {
		AnzhiSDK.getInstance().doLogin();
	}

	public void logout() {
		AnzhiSDK.getInstance().doLogout();
	}
	
	@Override
	public void submitExtraData(UserExtraData extraData) {
		AnzhiSDK.getInstance().doSubmitExtraData(extraData);
	}

	@Override
	public void exit() {
		AnzhiSDK.getInstance().doExit();
	}
	
	@Override
	public boolean isSupportMethod(String methodName) {
		return Arrays.contain(supportedMethods, methodName);
	}

}
