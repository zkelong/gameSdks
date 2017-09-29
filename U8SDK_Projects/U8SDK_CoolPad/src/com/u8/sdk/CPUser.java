package com.u8.sdk;

import android.app.Activity;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import com.u8.sdk.U8SDK;
import com.u8.sdk.utils.Arrays;

public class CPUser extends U8UserAdapter {

	private String[] supportedMethods = { "login", "loginCustom", "logout", "submitExtraData", "exit" };

	private Activity context;

	public CPUser(Activity context) {
		Log.d("mmddt", "VIVOUser init sdk");
		this.context = context;
		CPSDK.getInstance().initSDK(this.context, U8SDK.getInstance().getSDKParams());
	}

	@Override
	public void login() {
		Log.d("mmddt", "YSUser dologin");
		CPSDK.getInstance().doLogin();
	}

	@Override
	public void loginCustom(String aCustomData) {
		Log.d("mmddt", "YSUser dologinCus:" + aCustomData);
		// TODO Auto-generated method stub
		try {
			JSONObject obj = new JSONObject(aCustomData);
			int platfrom = obj.getInt("platfrom");
			CPSDK.getInstance().doLogin(platfrom);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			CPSDK.getInstance().doLogin();
		}
	}

	public void logout() {
		CPSDK.getInstance().doLogout();
	}

	@Override
	public void submitExtraData(UserExtraData extraData) {
		CPSDK.getInstance().doSubmitExtraData(extraData);
	}

	@Override
	public void exit() {
		CPSDK.getInstance().doExit();
		super.exit();
	}

	@Override
	public boolean isSupportMethod(String methodName) {
		boolean result = Arrays.contain(supportedMethods, methodName);
		Log.d("mmddt", "isSupportMethod:" + methodName + "result:" + result);
		return result;
	}

}
