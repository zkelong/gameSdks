package com.u8.sdk;

import com.gionee.gamesdk.floatwindow.GamePlatform;

import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;

public class JinLiApplication implements IApplicationListener {

	@Override
	public void onProxyCreate() {
		GamePlatform.init(U8SDK.getInstance().getApplication(), U8SDK.getInstance().getSDKParams().getString("appkey"));
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
