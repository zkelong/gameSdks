package com.u8.sdk;

import java.util.HashMap;
import java.util.Map;

import com.sogou.gamecenter.sdk.SogouGamePlatform;
import com.sogou.gamecenter.sdk.bean.SogouGameConfig;
import com.sogou.gamecenter.sdk.bean.UserInfo;
import com.sogou.gamecenter.sdk.listener.InitCallbackListener;
import com.sogou.gamecenter.sdk.listener.LoginCallbackListener;
import com.sogou.gamecenter.sdk.listener.PayCallbackListener;
import com.sogou.gamecenter.sdk.listener.SwitchUserListener;
import com.sogou.gamecenter.sdk.views.FloatMenu;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

public class SGSDK {
	private static SGSDK instance;
	// CP后台可以查询到前三个参数MERCHANT_ID、APP_ID和APP_KEY.
	// 不同服务器序列号可使用不同计费通知地址
	private int APP_ID = 0; //
	private String APP_KEY = null; //
	private String GAME_NAME = null; //
	private Activity context = null;
	private boolean mode = false;

	// 防止内存临界时，垃圾回收了SogouGamePlatform对象
	private SogouGamePlatform mSogouGamePlatform = SogouGamePlatform.getInstance();

	private FloatMenu mFloatMenu;

	private SGSDK() {

	}

	public static SGSDK getInstance() {
		if (instance == null) {
			instance = new SGSDK();
		}
		return instance;
	}

	public void initSDK(Activity context, SDKParams params) {
		this.parseSDKParams(params);
		this.context = context;
		// 配置游戏信息（gid、appKey由搜狗游戏平台统一分配）
		SogouGameConfig config = new SogouGameConfig();
		// 开发模式为true，false是正式环境
		// 请注意，提交版本设置正式环境
		config.devMode = this.mode;
		config.gid = this.APP_ID;
		config.appKey = this.APP_KEY;
		config.gameName = this.GAME_NAME;
		// SDK准备初始化
		mSogouGamePlatform.prepare(context, config);

		if (this.mode) {
			// 发布正式版本，请注释掉打印日志功能
			mSogouGamePlatform.openLogInfo();
		}
		// SDK初始化，整个接入流程只调用一次
		mSogouGamePlatform.init(this.context, new InitCallbackListener() {

			@Override
			public void initSuccess() {

			}

			@Override
			public void initFail(int code, String msg) {
				// SDK初始化失败再此关闭游戏即可

			}
		});

		U8SDK.getInstance().setActivityCallback(new ActivityCallbackAdapter() {
			@Override
			public void onPause() {
				if (mFloatMenu != null)
					mFloatMenu.hide();
			}

			@Override
			public void onResume() {
				if (mFloatMenu != null) {
					// 默认浮在右上角位置，距左边为10，距下边为100位置，单位为像素
					// mFloatMenu.setParamsXY(10, 100);
					mFloatMenu.show();
					// 浮动菜单设置切换帐号监听器或者设置帐号变化监听器（选一种接入即可）
					// 演示设置切换帐号监听器
					mFloatMenu.setSwitchUserListener(new SwitchUserListener() {
						@Override
						public void switchSuccess(int code, UserInfo userInfo) {
							// Log.d(TAG, "FloatMenus witchSuccess code:" + code
							// + " userInfo:" + userInfo);
							U8SDK.getInstance().onLogout();
						}

						@Override
						public void switchFail(int code, String msg) {
							// Log.e(TAG, "FloatMenus switchFail code:" + code +
							// " msg:" + msg);
						}
					});
				}
			}
		});

	}

	private void parseSDKParams(SDKParams params) {
		this.APP_ID = params.getInt("app_id");
		this.APP_KEY = params.getString("APP_KEY");
		this.GAME_NAME = params.getString("game_name");
		this.mode = params.getBoolean("mode");
	}

	public void doLogin() {
		mSogouGamePlatform.login(this.context, new LoginCallbackListener() {

			@Override
			public void loginSuccess(int code, UserInfo userInfo) {
				String dataStr = String.format(" { gid:\"%d\", user_id:\"%s\", session_key:\"%s\" }",
						SGSDK.getInstance().APP_ID, userInfo.getUserId(), userInfo.getSessionKey());
				U8SDK.getInstance().onLoginResult(dataStr);
				Log.d("mmddt", "login success:" + dataStr);
				if (mFloatMenu == null) {
					mFloatMenu = mSogouGamePlatform.createFloatMenu(U8SDK.getInstance().getContext(), true);
					mFloatMenu.setParamsXY(10, 100);
					mFloatMenu.show();
				}
			}

			@Override
			public void loginFail(int code, String msg) {
				U8SDK.getInstance().onResult(U8Code.CODE_LOGIN_FAIL, "login fail code:" + code);
				Log.d("mmddt", "login fail");
			}
		});
	}

	public void doLogout() {
		if (mSogouGamePlatform != null) {
			// 演示切换帐号接口使用，游戏有UI入口只需要调用该接口实现切换帐号
			mSogouGamePlatform.switchUser(U8SDK.getInstance().getContext(), new SwitchUserListener() {

				@Override
				public void switchSuccess(int code, UserInfo userInfo) {
					// 切换帐号成功回调此方法，拿到最新登录态信息
					//Log.d("mmddt", "switchSuccess:" + userInfo);
					U8SDK.getInstance().onLogout();
				}

				@Override
				public void switchFail(int code, String msg) {
					// 切换帐号失败回调
					//Log.e("mmddt", "switchFail code:" + code + " msg:" + msg);
				}
			});
		}
	}

	public void doPay(Activity context, PayParams data) {
		Map<String, Object> map = new HashMap<String, Object>();
		// 游戏货币名字（必传）
		map.put("currency", "游戏币");
		// 人民币兑换比例（必传）,小数 比例得加 f
		map.put("rate", 1);

		// 购买商品名字（必传）
		map.put("product_name", data.getProductName());
		// 充值金额,单位是元，在手游中数据类型为整型（必传）
		map.put("amount", data.getPrice());
		// 透传参数,游戏方自行定义（必传）
		map.put("app_data", data.getOrderID());
		// 是否可以编辑支付金额（可选）如果不传递将表示金额不可以编辑
		map.put("appmodes", false);
		mSogouGamePlatform.pay(context, map, new PayCallbackListener() {

			// 支付成功回调,游戏方可以做后续逻辑处理
			// 收到该回调说明提交订单成功，但成功与否要以服务器回调通知为准
			@Override
			public void paySuccess(String orderId, String appData) {
				// orderId是订单号，appData是游戏方自己传的透传消息
				// Log.d(TAG, "paySuccess orderId:" + orderId + " appData:" +
				// appData);
				U8SDK.getInstance().onResult(U8Code.CODE_PAY_SUCCESS, "支付成功");
			}

			@Override
			public void payFail(int code, String orderId, String appData) {
				// 支付失败情况下,orderId可能为空
				if (orderId != null) {
					// Log.e(TAG, "payFail code:" + code + "orderId:" + orderId
					// + " appData:" + appData);
				} else {
					// Log.e(TAG, "payFail code:" + code + " appData:" +
					// appData);
				}
				U8SDK.getInstance().onResult(U8Code.CODE_PAY_FAIL, "支付失败:" + code);
			}
		});
	}
}
