package com.u8.sdk;

import android.content.Context;
import android.content.res.Configuration;

public class MDLApplicationListener implements IApplicationListener {

	@Override
	public void onProxyCreate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProxyAttachBaseContext(Context base) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProxyConfigurationChanged(Configuration config) {
		// TODO Auto-generated method stub
		DLSDK.getInstance().onConfigurationChanged(config);
	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub

	}

}
