package com.u8.sdk;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.util.Log;

import cn.m4399.operate.OperateCenter;
import cn.m4399.operate.OperateCenter.OnLoginFinishedListener;
import cn.m4399.operate.OperateCenter.OnRechargeFinishedListener;
import cn.m4399.operate.OperateCenterConfig;
import cn.m4399.operate.OperateCenterConfig.PopLogoStyle;
import cn.m4399.operate.OperateCenterConfig.PopWinPosition;
import cn.m4399.operate.User;

public class SSJJSDK {
	private static SSJJSDK instance;

	private String m_appkey = "";
	private boolean m_debugEnabled = false;
	private boolean m_isSupportExcess = false;

	private SSJJSDK() {

	}

	public static SSJJSDK getInstance() {
		if (instance == null) {
			instance = new SSJJSDK();
		}
		return instance;
	}

	public void initSDK(Activity context, SDKParams params) {
		this.parseSDKParams(params);
		Log.d("ddt2", "4399 appkey : " + this.m_appkey);

		OperateCenter opeCenter = OperateCenter.getInstance();
		// 配置sdk属性,比如可扩展横竖屏配置
		OperateCenterConfig opeConfig = new OperateCenterConfig.Builder(context)
			.setGameKey(m_appkey)
			.setDebugEnabled(m_debugEnabled)
	    	.setOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
	    	.setPopLogoStyle(PopLogoStyle.POPLOGOSTYLE_ONE)
	    	.setPopWinPosition(PopWinPosition.POS_LEFT)
	    	.setSupportExcess(m_isSupportExcess)
	    	.build();
		
		opeCenter.setConfig(opeConfig);

		// 初始化SDK，在这个过程中会读取各种配置和检查当前帐号是否在登录中
		// 只有在init之后， isLogin()返回的状态才可靠

		opeCenter.init(context, new OperateCenter.OnInitGloabListener() {

			@Override
			public void onInitFinished(boolean isLogin, User userInfo) {
				U8SDK.getInstance().onResult(U8Code.CODE_INIT_SUCCESS, "init finished.");
			}

			@Override
			public void onSwitchUserAccountFinished(User userInfo) {
				U8SDK.getInstance().onSwitchAccount();
			}

			@Override
			public void onUserAccountLogout(boolean fromUserCenter, int resultCode) {
				U8SDK.getInstance().onLogout();
			}

		});
	}

	private void parseSDKParams(SDKParams params) {
		m_appkey = params.getString("appkey");
		m_debugEnabled = params.getBoolean("debugEnabled");
		m_isSupportExcess = params.getBoolean("supportExcess");
	}

	public void doLogin() {

		OperateCenter.getInstance().login(U8SDK.getInstance().getContext(), new OnLoginFinishedListener() {
		    @Override
		    public void onLoginFinished(boolean success, int resultCode, User userInfo) {
		    	Log.d("ddt2", "4399: doLogin return without is loging.");
		    	String msg = OperateCenter.getResultMsg(resultCode) + ": " + userInfo;
		    	if (success) {
		    		String dataStr = String.format(" { username:\"%s\", uid:\"%s\", nickname:\"%s\", token:\"%s\" }", userInfo.getName(), userInfo.getUid(), userInfo.getNick(), userInfo.getState());    	
			    	U8SDK.getInstance().onLoginResult(dataStr);
		    	} else {
		    		U8SDK.getInstance().onResult(U8Code.CODE_LOGIN_FAIL, msg);
		    	}	
		    	
		    }
		    
		});
	}
	
	public void doLogout() {
		OperateCenter.getInstance().logout();
	}

	public void doPay(Activity context, PayParams data) {

		OperateCenter.getInstance().recharge(U8SDK.getInstance().getContext(), data.getPrice(), data.getOrderID(),
				data.getProductName(), new OnRechargeFinishedListener() {
					@Override
					public void onRechargeFinished(boolean success, int resultCode, String msg) {
						if (success) {
							U8SDK.getInstance().onResult(U8Code.CODE_PAY_SUCCESS,
									OperateCenter.getResultMsg(resultCode));
						} else {
							U8SDK.getInstance().onResult(U8Code.CODE_PAY_FAIL, OperateCenter.getResultMsg(resultCode));
						}
					}
				});
	}
}
