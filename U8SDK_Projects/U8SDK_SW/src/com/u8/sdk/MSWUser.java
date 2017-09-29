package com.u8.sdk;

import android.app.Activity;

import com.u8.sdk.utils.Arrays;

public class MSWUser extends U8UserAdapter {
	
	private Activity context;
	private String[] supportedMethods = {"login","logout","exit" };
		
	public MSWUser(Activity context){
		this.context = context;
		SWSDK.getInstance().initSDK(this.context, U8SDK.getInstance().getSDKParams());
	}
	
	@Override
	public void login() {
		SWSDK.getInstance().doLogin();
	}
	
	public void logout() {
		SWSDK.getInstance().doLogout();
	}
	
	

	@Override
	public void exit() {
		// TODO Auto-generated method stub
		SWSDK.getInstance().onBackPressed();
		super.exit();
	}

	@Override
	public boolean isSupportMethod(String methodName) {
		return Arrays.contain(supportedMethods, methodName);
	}
	
}
