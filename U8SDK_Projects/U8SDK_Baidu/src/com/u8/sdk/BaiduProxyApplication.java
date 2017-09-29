package com.u8.sdk;

import com.u8.sdk.log.Log;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

public class BaiduProxyApplication extends Application implements IApplicationListener{

	@Override
	public void onProxyCreate() {
		super.onCreate();
		com.baidu.gamesdk.BDGameSDK.initApplication(this);
	}

	@Override
	public void onProxyAttachBaseContext(Context base) {
		super.attachBaseContext(base);
	}

	@Override
	public void onProxyConfigurationChanged(Configuration config) {
		super.onConfigurationChanged(config);
	}	
}
