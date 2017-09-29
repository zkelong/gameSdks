package com.u8.sdk;

import android.app.Activity;

import com.u8.sdk.U8SDK;
import com.u8.sdk.utils.Arrays;

public class MuZhiUser extends U8UserAdapter {

	private String[] supportedMethods = { "login", "logout", "submitExtraData", "exit" };

	private Activity context;

	public MuZhiUser(Activity context) {
		this.context = context;
		MuZhiSDK.getInstance().initSDK(this.context, U8SDK.getInstance().getSDKParams());
	}

	@Override
	public void login() {
		MuZhiSDK.getInstance().doLogin();
	}

	public void logout() {
		MuZhiSDK.getInstance().doLogout();
	}

	@Override
	public void submitExtraData(UserExtraData extraData) {
		MuZhiSDK.getInstance().doSubmitExtraData(extraData);
	}
	
	@Override
	public void exit() {
		MuZhiSDK.getInstance().doExit();
		super.exit();
	}
	
	@Override
	public boolean isSupportMethod(String methodName) {
		return Arrays.contain(supportedMethods, methodName);
	}

}
