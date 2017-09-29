package com.u8.sdk;

import com.nearme.game.sdk.GameCenterSDK;
import com.u8.sdk.log.Log;

import android.content.Context;
import android.content.res.Configuration;

public class OPPOApplication implements IApplicationListener {

	@Override
	public void onProxyCreate() {
		// TODO Auto-generated method stub
		Log.d("mmddt", "vivounionSk init sdk");
		SDKParams params = U8SDK.getInstance().getSDKParams();
		GameCenterSDK.init(params.getString("appSecret"), U8SDK.getInstance().getApplication());
	}

	@Override
	public void onProxyAttachBaseContext(Context base) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProxyConfigurationChanged(Configuration config) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub

	}

}
