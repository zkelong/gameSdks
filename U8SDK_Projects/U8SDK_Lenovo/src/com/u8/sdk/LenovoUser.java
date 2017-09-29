package com.u8.sdk;

import android.app.Activity;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import com.u8.sdk.U8SDK;
import com.u8.sdk.utils.Arrays;

public class LenovoUser extends U8UserAdapter {

	private String[] supportedMethods = { "login", "loginCustom", "logout", "submitExtraData", "exit" };

	private Activity context;

	public LenovoUser(Activity context) {
		Log.d("mmddt", "VIVOUser init sdk");
		this.context = context;
		LenovoSDK.getInstance().initSDK(this.context, U8SDK.getInstance().getSDKParams());
	}

	@Override
	public void login() {
		Log.d("mmddt", "YSUser dologin");
		LenovoSDK.getInstance().doLogin();
	}

	@Override
	public void loginCustom(String aCustomData) {
		Log.d("mmddt", "YSUser dologinCus:" + aCustomData);
		// TODO Auto-generated method stub
		try {
			JSONObject obj = new JSONObject(aCustomData);
			int platfrom = obj.getInt("platfrom");
			LenovoSDK.getInstance().doLogin(platfrom);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LenovoSDK.getInstance().doLogin();
		}
	}

	public void logout() {
		LenovoSDK.getInstance().doLogout();
	}

	@Override
	public void submitExtraData(UserExtraData extraData) {
		LenovoSDK.getInstance().doSubmitExtraData(extraData);
	}

	@Override
	public void exit() {
		LenovoSDK.getInstance().doExit();
		super.exit();
	}

	@Override
	public boolean isSupportMethod(String methodName) {
		boolean result = Arrays.contain(supportedMethods, methodName);
		Log.d("mmddt", "isSupportMethod:" + methodName + "result:" + result);
		return result;
	}

}
