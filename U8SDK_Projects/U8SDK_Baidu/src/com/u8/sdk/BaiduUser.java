package com.u8.sdk;

import android.app.Activity;

import com.u8.sdk.U8SDK;
import com.u8.sdk.U8UserAdapter;
import com.u8.sdk.UserExtraData;
import com.u8.sdk.log.Log;
import com.u8.sdk.utils.Arrays;

public class BaiduUser extends U8UserAdapter {
	
	private String[] supportedMethods = {"login","logout","submitExtraData","exit","switchLogin"};
	
	public BaiduUser(Activity context) {
		BaiduSDK.getInstance().initSDK(U8SDK.getInstance().getContext(), U8SDK.getInstance().getSDKParams());
	}
	
	@Override
	public void login() {
		BaiduSDK.getInstance().login();
	}
	
	public void logout() {
		BaiduSDK.getInstance().logout();
	}

	@Override
	public void submitExtraData(UserExtraData extraData) {
		BaiduSDK.getInstance().submitExtendData(extraData);
	}

	@Override
	public void exit() {
		BaiduSDK.getInstance().baiduSdkExit(U8SDK.getInstance().getContext());
		super.exit();
	}

	@Override
	public void switchLogin() {
		BaiduSDK.getInstance().logout();
		BaiduSDK.getInstance().login();
	}

	@Override
	public boolean isSupportMethod(String methodName) {
		return Arrays.contain(supportedMethods, methodName);
	}

}
