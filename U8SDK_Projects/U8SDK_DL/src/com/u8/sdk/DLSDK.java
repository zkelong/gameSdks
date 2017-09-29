package com.u8.sdk;

import com.downjoy.CallbackListener;
import com.downjoy.CallbackStatus;
import com.downjoy.Downjoy;
import com.downjoy.InitListener;
import com.downjoy.LoginInfo;
import com.downjoy.LogoutListener;

import android.app.Activity;
import android.content.res.Configuration;
import android.util.Log;

public class DLSDK {
	private static DLSDK instance;

	private Downjoy downjoy; // 当乐游戏中心实例
	// CP后台可以查询到前三个参数MERCHANT_ID、APP_ID和APP_KEY.
	// 不同服务器序列号可使用不同计费通知地址
	private String MERCHANT_ID = null; // 当乐分配的MERCHANT_ID
	private String APP_ID = null; // 当乐分配的APP_ID.
	private String APP_KEY = null; // 当乐分配的 APP_KEY
	private String SERVER_SEQ_NUM = null; // 此参数自定义，需登录CP后台配置支付通知回调，其中的服务器序号就是SERVER_SEQ_NUM
	private Activity context = null;

	private DLSDK() {

	}

	public static DLSDK getInstance() {
		if (instance == null) {
			instance = new DLSDK();
		}
		return instance;
	}

	public void initSDK(Activity context, SDKParams params) {
		this.parseSDKParams(params);
		// Log.d("ddt2", "4399 appkey : " + this.m_appkey);
		this.context = context;

		downjoy = Downjoy.getInstance(context, MERCHANT_ID, APP_ID, SERVER_SEQ_NUM, APP_KEY, new InitListener() {

			@Override
			public void onInitComplete() {
				// 此处CP请根据自己的逻辑判断是否调用登陆
				// downjoyLogin();
			}
		});
		// 设置登录成功后属否显示当乐游戏中心的悬浮按钮
		// 注意：
		// 此处应在调用登录接口之前设置，默认值是true，即登录成功后自动显示当乐游戏中心的悬浮按钮。
		// 如果此处设置为false，登录成功后，不显示当乐游戏中心的悬浮按钮。
		// 正常使用悬浮按钮还需要实现两个函数,onResume、onPause
		downjoy.showDownjoyIconAfterLogined(true);

		U8SDK.getInstance().setActivityCallback(new ActivityCallbackAdapter() {
			@Override
			public void onPause() {
				if (downjoy != null) {
					downjoy.pause();
				}
			}

			@Override
			public void onResume() {
				if (downjoy != null) {
					downjoy.resume(U8SDK.getInstance().getContext());
				}
			}
		});

		// 设置悬浮窗显示位置
		downjoy.setInitLocation(Downjoy.LOCATION_RIGHT_CENTER_VERTICAL);
		// 设置全局注销监听器，浮标中的注销也能接收到回调
		downjoy.setLogoutListener(mLogoutListener);
	}

	private void parseSDKParams(SDKParams params) {
		this.APP_ID = params.getString("app_id");
		this.APP_KEY = params.getString("APP_KEY");
		this.SERVER_SEQ_NUM = params.getString("SERVER_SEQ_NUM");
		this.MERCHANT_ID = params.getString("MERCHANT_ID");
	}

	private LogoutListener mLogoutListener = new LogoutListener() {
		@Override
		public void onLogoutSuccess() {
			// Util.alert(DemoActivity.this, "注销成功回调->注销成功");
			Log.d("mmddt", "注销成功回调->注销成功");
			doLogout();
		}

		@Override
		public void onLogoutError(String msg) {
			// Util.alert(DemoActivity.this, "注销失败回调->" + msg);
			Log.d("mmddt", "注销失败回调->" + msg);
		}
	};

	public void doLogin() {
		if (null == downjoy)
			return;
		downjoy.openLoginDialog(this.context, new CallbackListener<LoginInfo>() {

			@Override
			public void callback(int status, LoginInfo data) {
				if (status == CallbackStatus.SUCCESS && data != null) {
					// Util.alert(this.context, "登录成功回调->" + data.toString());
					String dataStr = String.format(" { account:\"%s\", accountID:\"%s\", token:\"%s\" }",
							data.getUserName(), data.getUmid(), data.getToken());
					U8SDK.getInstance().onLoginResult(dataStr);
				} else if (status == CallbackStatus.FAIL && data != null) {
					// Util.alert(DemoActivity.this, "登录失败回调->" +
					// data.getMsg());
					U8SDK.getInstance().onResult(U8Code.CODE_LOGIN_FAIL, data.getMsg());
				} else if (status == CallbackStatus.CANCEL && data != null) {
					// Util.alert(DemoActivity.this, "登录取消回调->" +
					// data.getMsg());
				}
			}
		});
	}

	public void doLogout() {
		U8SDK.getInstance().onLogout();
	}

	/**
	 * 支付
	 *
	 * @param money
	 *            商品价格，单位：元
	 */
	public void doPay(Activity context, PayParams data) {
		if (null == downjoy)
			return;
		String productName = data.getProductName(); // 商品名称
		String body = data.getProductDesc(); // 商品描述
		String transNo = data.getOrderID(); // cp订单号，计费结果通知时原样返回，尽量不要使用除字母和数字之外的特殊字符。
		String serverName = ""; // 记录订单产生的服务器，没有可传“”
		String playerName = ""; // 记录订单产生的玩家名称，没有可传“”
		float money = data.getPrice();
		// 打开支付界面,获得订单号
		downjoy.openPaymentDialog(context, money, productName, body, transNo, serverName, playerName,
				new CallbackListener<String>() {
					@Override
					public void callback(int status, String data) {
						if (status == CallbackStatus.SUCCESS) {
							// Util.alert(DemoActivity.this, "成功支付回调->订单号：" +
							// data);
							U8SDK.getInstance().onResult(U8Code.CODE_PAY_SUCCESS, data);
						} else if (status == CallbackStatus.FAIL) {
							// Util.alert(DemoActivity.this, "失败支付回调->error:" +
							// data);
							U8SDK.getInstance().onResult(U8Code.CODE_PAY_FAIL, data);
						} else if (status == CallbackStatus.CANCEL) {
							// Util.alert(DemoActivity.this, "取消支付回调->" + data);
							// U8SDK.getInstance().onResult(U8Code.CODE_PAY_FAIL,
							// data);
						}
					}
				});
	}

	public void onBackPressed() {
		// TODO Auto-generated method stub
		downjoyExit();
	}

	/**
	 * 退出对话框
	 */
	private boolean downjoyExit() {
		if (null == downjoy)
			return false;
		downjoy.openExitDialog(this.context, new CallbackListener<String>() {

			@Override
			public void callback(int status, String data) {
				if (CallbackStatus.SUCCESS == status) {
					DLSDK.getInstance().context.finish();
					System.exit(0);
				} else if (CallbackStatus.CANCEL == status) {
					// Util.alert(getBaseContext(), "退出回调-> " + data);
				}
			}
		});
		return true;
	}

	public void onConfigurationChanged(Configuration newConfig) {
		Log.d("mmddt", "onConfigurationChanged");
		if (downjoy != null) {
			downjoy.onConfigurationChanged(newConfig);
		}
	}
}
