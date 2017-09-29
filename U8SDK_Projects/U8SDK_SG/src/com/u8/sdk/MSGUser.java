package com.u8.sdk;

import android.app.Activity;

import com.u8.sdk.utils.Arrays;

public class MSGUser extends U8UserAdapter {
	
	private Activity context;
	private String[] supportedMethods = {"login","logout" };
		
	public MSGUser(Activity context){
		this.context = context;
		SGSDK.getInstance().initSDK(this.context, U8SDK.getInstance().getSDKParams());
	}
	
	@Override
	public void login() {
		SGSDK.getInstance().doLogin();
	}
	
	public void logout() {
		SGSDK.getInstance().doLogout();
	}

	@Override
	public boolean isSupportMethod(String methodName) {
		return Arrays.contain(supportedMethods, methodName);
	}
	
}
