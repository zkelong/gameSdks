package com.u8.sdk;

import com.u8.sdk.log.Log;
import com.vivo.unionsdk.open.VivoUnionSDK;

import android.content.Context;
import android.content.res.Configuration;

public class VIVOApplication implements IApplicationListener {

	@Override
	public void onProxyCreate() {
		// TODO Auto-generated method stub
		Log.d("mmddt", "vivounionSk init sdk");
		SDKParams params = U8SDK.getInstance().getSDKParams();
		VivoUnionSDK.initSdk(U8SDK.getInstance().getApplication(), params.getString("appid"), params.getBoolean("release"));		
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
