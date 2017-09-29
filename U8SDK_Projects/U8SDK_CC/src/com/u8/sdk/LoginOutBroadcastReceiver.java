package com.u8.sdk;


import com.u8.sdk.log.Log;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * 登出广播
 * 
 * @author
 * 
 */
public class LoginOutBroadcastReceiver extends BroadcastReceiver {

	public static final String LOGINOUT_ACTION = "CCPAY_LOGINOUT_ACTION";

	@Override
	public void onReceive(Context context, Intent intent) {
		U8SDK.getInstance().onLogout();
	}
}
