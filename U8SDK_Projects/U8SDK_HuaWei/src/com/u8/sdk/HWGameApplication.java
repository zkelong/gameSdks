package com.u8.sdk;

import com.huawei.gameservice.sdk.GameServiceSDK;
import com.huawei.gameservice.sdk.control.GameCrashHandler;

import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;

public class HWGameApplication implements IApplicationListener {
	@Override
	public void onProxyCreate() {
		Log.d("mmddt", "init HWGameApplication");
		// TODO Auto-generated method stub
		// 设置捕获异常崩溃的回调
		// set crash handler
		GameServiceSDK.setCrashHandler(U8SDK.getInstance().getApplication(), new GameCrashHandler() {

			@Override
			public void onCrash(String stackInfo) {
				Log.e("GameApplication", "onCrash:" + stackInfo);
			}

		});
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
