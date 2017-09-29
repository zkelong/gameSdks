package com.u8.sdk;

import com.yuwan.sdk.api.YuwanCallbackListener;
import com.yuwan.sdk.api.YuwanSDK;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

public class YWSDK {
	private static YWSDK instance;
	// CP后台可以查询到前三个参数MERCHANT_ID、APP_ID和APP_KEY.
	// 不同服务器序列号可使用不同计费通知地址
	private long partnerId = 0; //
	private String APP_KEY = null; //
	private String GAME_ID = null; //

	private YWSDK() {

	}

	public static YWSDK getInstance() {
		if (instance == null) {
			instance = new YWSDK();
		}
		return instance;
	}

	public void initSDK(Activity context, SDKParams params) {
		this.parseSDKParams(params);
		// 此处需要的channelId、gameId、privateKey相对应
		// YuwanSDK.defaultSDK().initSDK(this, partnerId, gameId, privateKey);
		YuwanSDK.defaultSDK().initSDK(context, this.partnerId, this.GAME_ID, this.APP_KEY);

		U8SDK.getInstance().setActivityCallback(new ActivityCallbackAdapter() {
			@Override
			public void onPause() {
				YuwanSDK.defaultSDK().showFloatButton(U8SDK.getInstance().getContext(), false);
			}

			@Override
			public void onResume() {
				YuwanSDK.defaultSDK().showFloatButton(U8SDK.getInstance().getContext(), true);
			}
			@Override
			public void onDestroy() {
				// TODO Auto-generated method stub
				YuwanSDK.defaultSDK().signOut(U8SDK.getInstance().getContext());
				super.onDestroy();
			}
		});
	}

	private void parseSDKParams(SDKParams params) {
		this.partnerId = params.getLong("partnerId");
		this.APP_KEY = params.getString("APP_KEY");
		this.GAME_ID = params.getString("game_ID");
	}

	public void doLogin() {

		YuwanSDK.defaultSDK().login(U8SDK.getInstance().getContext(), new YuwanCallbackListener() {

			@Override
			public void callback(int resultCode, Intent data) {
				switch (resultCode) {
				case YuwanCallbackListener.RESULT_LOGIN_OK:
					//// 获取userid、 username
					// data.getStringExtra(YuwanCallbackListener.RESULT_USERID
					// data.getStringExtra(YuwanCallbackListener.RESULT_USERNAME
					String userId = data.getStringExtra(YuwanCallbackListener.RESULT_USERID);
					String userName = data.getStringExtra(YuwanCallbackListener.RESULT_USERNAME);
					String dataStr = String.format(" { userID:\"%s\", username:\"%s\" }", userId, userName);
					U8SDK.getInstance().onLoginResult(dataStr);

					YuwanSDK.defaultSDK().showFloatButton(U8SDK.getInstance().getContext(), true);
					break;

				case YuwanCallbackListener.RESULT_LOGIN_FAILED:
					// Toast.makeText(MainActivity.this,
					// "RESULT_LOGIN_FAILED===>登录失败..",
					// Toast.LENGTH_SHORT).show();
					break;

				case YuwanCallbackListener.RESULT_LOGIN_CANCEL:
					// Toast.makeText(MainActivity.this,
					// "RESULT_LOGIN_CANCEL===>登录取消...",
					// Toast.LENGTH_SHORT).show();
					break;
				}
			}
		});
	}

	public void doLogout() {
		YuwanSDK.defaultSDK().signOut(U8SDK.getInstance().getContext());
		U8SDK.getInstance().onLogout();
	}

	public void doPay(Activity context, PayParams data) {
		YuwanSDK.defaultSDK().pay(U8SDK.getInstance().getContext(), data.getPrice() * 100,
				data.getProductName(), data.getRoleName(), data.getOrderID(), new YuwanCallbackListener() {

					@Override
					public void callback(int resultCode, Intent data) {
						switch (resultCode) {
						case YuwanCallbackListener.RESULT_PAY_OK:
							U8SDK.getInstance().onResult(U8Code.CODE_PAY_SUCCESS, "充值成功");
							break;

						case YuwanCallbackListener.RESULT_PAY_ERROR:
							U8SDK.getInstance().onResult(U8Code.CODE_PAY_FAIL, "充值失败");
							Log.d("mmddt", "支付失败");
							break;

						case YuwanCallbackListener.RESULT_PAY_ALI_REFUND:
							// U8SDK.getInstance().onResult(U8Code.CODE_PAY_FAIL,
							// "充值失败");
							Log.d("mmddt", "支付回调中");
							break;

						case YuwanCallbackListener.RESULT_PAY_CANCEL:
							// U8SDK.getInstance().onResult(U8Code.CODE_PAY_FAIL,
							// "充值失败");
							Log.d("mmddt", "取消支付");
							break;
						}
					}
				});
	}
}
