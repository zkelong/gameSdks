package com.u8.sdk;

import android.app.Activity;

import com.u8.sdk.U8SDK;
import com.u8.sdk.utils.Arrays;

public class XiaoMiUser extends U8UserAdapter {

	private String[] supportedMethods = { "login", "logout" };

	private Activity context;

	public XiaoMiUser(Activity context) {
		this.context = context;
		XiaoMiSDK.getInstance().initSDK(this.context, U8SDK.getInstance().getSDKParams());
	}

	@Override
	public void login() {
		XiaoMiSDK.getInstance().doLogin();
	}

	public void logout() {
		XiaoMiSDK.getInstance().doLogout();
	}

	@Override
	public boolean isSupportMethod(String methodName) {
		return Arrays.contain(supportedMethods, methodName);
	}

}
