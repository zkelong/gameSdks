package com.lion.ccpaydemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * 登出广播
 * 
 * @author 巍
 * 
 */
public class LoginOutBroadcastReceiver extends BroadcastReceiver {

	public static final String LOGINOUT_ACTION = "CCPAY_LOGINOUT_ACTION";

	@Override
	public void onReceive(Context context, Intent intent) {
		Toast.makeText(context, "注销登录了。。。。。。。。。。", Toast.LENGTH_SHORT).show();
	}
}
