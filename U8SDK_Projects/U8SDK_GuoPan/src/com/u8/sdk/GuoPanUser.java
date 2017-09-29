package com.u8.sdk;

import android.app.Activity;

import com.u8.sdk.U8SDK;
import com.u8.sdk.utils.Arrays;

public class GuoPanUser extends U8UserAdapter {

	private String[] supportedMethods = { "login", "logout", "submitExtraData", "exit" };

	private Activity context;

	public GuoPanUser(Activity context) {
		this.context = context;
		GuoPanSDK.getInstance().initSDK(this.context, U8SDK.getInstance().getSDKParams());
	}

	@Override
	public void login() {
		GuoPanSDK.getInstance().doLogin();
	}

	public void logout() {
		GuoPanSDK.getInstance().doLogout();
	}
	
	@Override
	public void exit() {
		GuoPanSDK.getInstance().doExit();
	}
	
	@Override
	public void submitExtraData(UserExtraData extraData) {
		GuoPanSDK.getInstance().doSubmitExtraData(extraData);
	}

	@Override
	public boolean isSupportMethod(String methodName) {
		return Arrays.contain(supportedMethods, methodName);
	}

}
