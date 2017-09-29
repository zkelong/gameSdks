package com.u8.sdk;

import com.lion.ccpay.sdk.CCPaySdk;
import com.lion.ccpay.sdk.CCPaySdkApplicationUtils;
import com.lion.ccpay.sdk.OnAccountPwdChangeListener;
import com.lion.ccpay.sdk.OnLoginCallBack;
import com.lion.ccpay.sdk.OnLoginOutAction;
import com.lion.ccpay.sdk.OnPayListener;
import com.lion.ccpay.sdk.Stats;

import android.app.Activity;
import android.util.Log;

public class CCSDK {
	private static CCSDK instance;
	private SDKParams _sdkParams;
	
	private CCSDK() {
		
	}
	
	public static CCSDK getInstance() {
		if (instance == null) {
			instance = new CCSDK();
		}
		return instance;
	}
	
	public void initSDK(Activity context, SDKParams params) {
		
		CCPaySdk.getInstance().init(context);
		CCPaySdkApplicationUtils.getInstance(U8SDK.getInstance().getApplication());
		
		initCCPaySDKChangePwd();
		initCCpaySDKLoginOut();
		
		_sdkParams = params;
		
		// 虫虫需要通知以下事件
		U8SDK.getInstance().setActivityCallback(new ActivityCallbackAdapter() {
			public void onPause() {
				Stats.onPause(U8SDK.getInstance().getContext());
			}
			public void onResume() {
				Stats.onResume(U8SDK.getInstance().getContext());
			}
		});
	}
	
	public void doLogin() {

		login();
	}
	
	private void login() {
		
		boolean isLogin = CCPaySdk.getInstance().isLogined();
		
		CCPaySdk.getInstance().login(isLogin, new OnLoginCallBack() {

			@Override
			public void onLoginCancel() {
				
			}

			@Override
			public void onLoginFail() {
				U8SDK.getInstance().onResult(U8Code.CODE_LOGIN_FAIL, "CC onLoginFail.");
			}

			@Override
			public void onLoginSuccess(String uid, String token, String userName) {
				String dataStr = "{ token : \"" + token + "\", userid : \"" + uid + "\" }";
				Log.d("ddt2", dataStr);
				U8SDK.getInstance().onResult(U8Code.CODE_LOGIN_SUCCESS, dataStr);
				U8SDK.getInstance().onLoginResult(dataStr);
			}
			
		});
		
	}

	public void doLogout() {
		
		CCPaySdk.getInstance().onOffline(false);
		U8SDK.getInstance().onLogout();
		
	}
	
	public void doExit() {
		CCPaySdk.getInstance().onLogOutApp();
	}
	
	public void doPay(Activity context, PayParams data) {
		String goodsId = getGoodsId(data.getProductId());
		if (goodsId == null) {
			Log.d("ddt2", "��� ��ȡgoodsIdʧ��... productId: " + data.getProductId());
			return;
		}
	
		Log.d("ddt2", String.format("CC pay: %s -> %s ", data.getProductId(), goodsId));
		
		CCPaySdk.getInstance().pay(goodsId, data.getOrderID(), new OnPayListener() {

			@Override
			public void onPayResult(int status, String tn, String money) {
				
				switch (status) {
				
				case OnPayListener.CODE_SUCCESS://支付成功
					break;
				case OnPayListener.CODE_FAIL://支付失败	
					break;
				case OnPayListener.CODE_CANCEL://支付取消
					break;
				case OnPayListener.CODE_UNKNOW://支付结果未知
					break;
				}
			}
			
		});
	}
	
	private String getGoodsId(String productId) {
		String id = String.format("goods_%s", productId);
		if (_sdkParams.contains(id)) {
			return _sdkParams.getString(id);
		} else {
			return null;
		}
	}
	
	/**
	 * 修改密码监听
	 */
	private void initCCPaySDKChangePwd() {
		CCPaySdk.getInstance().setOnAccountPwdChangeListener(new OnAccountPwdChangeListener() {
			
			@Override
			public void onAccountPwdChange() {
				doLogout();
			}
		});
	}
	
	private void initCCpaySDKLoginOut() {
		CCPaySdk.getInstance().setOnLoginOutAction(new OnLoginOutAction() {
			
			@Override
			public void onLoginOut() {
				doLogout();
			}
		});
	}
	
}
