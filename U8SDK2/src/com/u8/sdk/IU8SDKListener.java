package com.u8.sdk;

import com.u8.sdk.verify.UToken;

/**
 * 监听器，登录、支付返回值
 * @author Administrator
 */
public interface IU8SDKListener {
	
	public void onResult(int code, String msg);
	
	public void onInitResult(InitResult result);
	
	public void onLoginResult(String data);
	
	public void onSwitchAccount();
	
	public void onSwitchAccount(String data);
	
	public void onLogout();
	
	public void onAuthResult(UToken authResult);
	
	public void onPayResult(PayResult result);
}
