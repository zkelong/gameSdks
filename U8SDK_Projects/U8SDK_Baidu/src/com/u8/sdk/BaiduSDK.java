package com.u8.sdk;

import android.app.Activity;

import com.u8.sdk.PayParams;
import com.u8.sdk.SDKParams;
import com.u8.sdk.UserExtraData;
import com.u8.sdk.baidu.Utils;
import com.u8.sdk.log.Log;
import com.baidu.gamesdk.BDGameSDKSetting;
import com.baidu.gamesdk.BDGameSDK;
import com.baidu.gamesdk.BDGameSDKSetting.Domain;
import com.baidu.gamesdk.BDGameSDKSetting.Orientation;
import com.baidu.gamesdk.IResponse;
import com.baidu.gamesdk.OnGameExitListener;
import com.baidu.gamesdk.ResultCode;
import com.baidu.platformsdk.PayOrderInfo;

public class BaiduSDK {
	public static BaiduSDK instance;

	enum SDKState {
		StateDefault, StateIniting, StateInited, StateLogin, StateLogined
	}

	private SDKState state = SDKState.StateDefault;
	int appId;
	String appKey;

	private boolean isFixedPay = true;
	private int ratio = 1;

	private BaiduSDK() {
	}

	public static BaiduSDK getInstance() {
		if (instance == null) {
			instance = new BaiduSDK();
		}
		return instance;
	}

	private void parseSDKParams(SDKParams params) {
		this.appId = params.getInt("BD_APPID");
		this.appKey = params.getString("BD_APPKEY");
		this.isFixedPay = params.getBoolean("BD_Fixed_PAY");
		this.ratio = params.getInt("BD_RADIO");
	}

	public void initSDK(Activity context, SDKParams params) {
		this.parseSDKParams(params);
		this.initSDK();
	}

	public void initSDK() {
		this.state = SDKState.StateIniting;
		Log.d("U8SDK", "baiduSdK init");
		try {
			U8SDK.getInstance().setActivityCallback(
					new ActivityCallbackAdapter() {

						@Override
						public void onBackPressed() {
							baiduSdkExit(U8SDK.getInstance().getContext());
						}

						@Override
						public void onCreate() {
							U8SDK.getInstance().runOnMainThread(new Runnable() {
								@Override
								public void run() {
									BDGameSDK.getAnnouncementInfo(U8SDK.getInstance().getContext());
								}
							});							
						}

						@Override
						public void onDestroy() {
							closeFloatView();
						}

						@Override
						public void onStop() {
							closeFloatView();
						}

						@Override
						public void onResume() {
							BDGameSDK.onResume(U8SDK.getInstance().getContext());
						}

						@Override
						public void onPause() {
							BDGameSDK.onPause(U8SDK.getInstance().getContext());
						}
					});

			BDGameSDKSetting mBDGameSDKSetting = new BDGameSDKSetting();
			mBDGameSDKSetting.setAppID(appId); // APPID设置
			mBDGameSDKSetting.setAppKey(appKey); // APPKEY设置
			mBDGameSDKSetting.setDomain(Domain.RELEASE); // 设置为正式模式
			mBDGameSDKSetting.setOrientation(Orientation.LANDSCAPE);// 设置为横屏
			mBDGameSDKSetting.setOrientation(Utils.getOrientation(U8SDK.getInstance().getContext()));

			BDGameSDK.init(U8SDK.getInstance().getContext(), mBDGameSDKSetting,
					new IResponse<Void>() {
						@Override
						public void onResponse(int resultCode, String resultDesc, Void extraData) {
							switch (resultCode) {
							case ResultCode.INIT_SUCCESS:
								if (state != SDKState.StateIniting) {
									U8SDK.getInstance().onResult(U8Code.CODE_INIT_FAIL, "ucsdk init fail. the state is:" + state);
									return;
								}
								state = SDKState.StateInited;

								U8SDK.getInstance().onResult(U8Code.CODE_INIT_SUCCESS, "init success");
								Log.d("U8SDK", "bdSDK init success");
								
								break;
							case ResultCode.INIT_FAIL:
							default:
								state = SDKState.StateDefault;
								U8SDK.getInstance().onResult(U8Code.CODE_INIT_FAIL, "init fail" + resultDesc);
								Log.d("U8SDK", "bdSDK 初始化失败");
							}
						}
					});
		} catch (Exception e) {
			Log.d("U8SDK", "初始化异常...." + e.getMessage());
			e.printStackTrace();
		}

		setSuspendWindowChangeAccountListener(); // 设置切换账号事件监听（个人中心界面切换账号）
		setSessionInvalidListener(); // 设置会话失效监听
	}

	public void login() {
		
		if (state.ordinal() < SDKState.StateInited.ordinal()) {
			initSDK();
			return;
		}

		if (!SDKTools.isNetworkAvailable(U8SDK.getInstance().getContext())) {
			U8SDK.getInstance().onResult(U8Code.CODE_NO_NETWORK, "The network now is unavailable");
			return;
		}
		try {
			state = SDKState.StateLogin;
			BDGameSDK.login(new IResponse<Void>() {
				@Override
				public void onResponse(int resultCode, String resultDesc, Void extraData) {
					switch (resultCode) {
					case ResultCode.LOGIN_SUCCESS:
						state = SDKState.StateLogined;
						String uid = BDGameSDK.getLoginUid();
						String token = BDGameSDK.getLoginAccessToken();

						U8SDK.getInstance().onResult(U8Code.CODE_LOGIN_SUCCESS,"login success");
						U8SDK.getInstance().onLoginResult(token);
						//Log.d("U8SDK", "登录成功x....." + token);
						showFloatView();
						return;
					case ResultCode.LOGIN_CANCEL:
						U8SDK.getInstance().onResult(U8Code.CODE_LOGIN_FAIL,"login cancel");
						//Log.d("U8SDK", "登录取消.....");
						state = SDKState.StateInited;
						break;
					case ResultCode.LOGIN_FAIL:
					default:
						U8SDK.getInstance().onResult(U8Code.CODE_LOGIN_FAIL,"login fail" + resultDesc);
						state = SDKState.StateInited;
						Log.d("U8SDK", "登录失败.....");
					}
					
				}
			});
		} catch (Exception e) {
			Log.d("U8SDK", "登录异常.....");
			e.printStackTrace();
		}
	}

	public void pay(PayParams data) {
		PayOrderInfo payOrderInfo = buildOrderInfo(data);
		if (payOrderInfo == null) {
			return;
		}

		BDGameSDK.pay(payOrderInfo, null, new IResponse<PayOrderInfo>() {

			@Override
			public void onResponse(int resultCode, String resultDesc,
					PayOrderInfo extraData) {
				switch (resultCode) {
				case ResultCode.PAY_SUCCESS: // 支付成功
					U8SDK.getInstance().onResult(U8Code.CODE_PAY_SUCCESS, "pay success");
					break;
				case ResultCode.PAY_CANCEL: // 订单支付取消
					U8SDK.getInstance().onResult(U8Code.CODE_PAY_FAIL, "pay canceled");
					break;
				case ResultCode.PAY_FAIL: // 订单支付失败
					U8SDK.getInstance().onResult(U8Code.CODE_PAY_FAIL,
							"pay fail");
					break;
				case ResultCode.PAY_SUBMIT_ORDER: // 订单已经提交，支付结果未知（比如：已经请求了，但是查询超时）
					U8SDK.getInstance().onResult(U8Code.CODE_PAY_FAIL, "pay submit order");
					break;
				default:
					U8SDK.getInstance().onResult(U8Code.CODE_PAY_FAIL, "pay fail");
					break;
				}
			}
		});
	}

	/**
	 * 构建订单信息
	 */
	private PayOrderInfo buildOrderInfo(PayParams data) {
		PayOrderInfo payOrderInfo = new PayOrderInfo();
		payOrderInfo.setCooperatorOrderSerial(data.getOrderID());
		payOrderInfo.setProductName(data.getProductName());

		if (this.isFixedPay) {
			payOrderInfo.setTotalPriceCent(data.getPrice() * 100); // 以分为单位
			//payOrderInfo.setTotalPriceCent(1); // 以分为单位 //测试充值1分
			payOrderInfo.setRatio(1);
		} else {
			payOrderInfo.setTotalPriceCent(0); // 以分为单位
			payOrderInfo.setRatio(this.ratio);
		}
		payOrderInfo.setExtInfo(""); // 该字段将会在支付成功后原样返回给CP(不超过500个字符)
		payOrderInfo.setCpUid(BDGameSDK.getLoginUid()); // 必传字段，需要验证uid是否合法,此字段必须是登陆后或者切换账号后保存的uid

		return payOrderInfo;
	}

	public void showFloatView() {
		try{
			U8SDK.getInstance().runOnMainThread(new Runnable() {
				@Override
				public void run() {
					BDGameSDK.showFloatView(U8SDK.getInstance().getContext()); // 显示悬浮窗
				}
	
			});
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void closeFloatView() {
		try{
			U8SDK.getInstance().runOnMainThread(new Runnable() {
				@Override
				public void run() {
					BDGameSDK.closeFloatView(U8SDK.getInstance().getContext());
				}
			});
		} catch(Exception e) {
			Log.d("U8SDK", "closeFloatView异常.....");
			e.printStackTrace();
		}
	}

	private void setSuspendWindowChangeAccountListener() { // 设置切换账号事件监听（个人中心界面切换账号）
		BDGameSDK.setSuspendWindowChangeAccountListener(new IResponse<Void>() {

			@Override
			public void onResponse(int resultCode, String resultDesc, Void extraData) {
				switch (resultCode) {
				case ResultCode.LOGIN_SUCCESS:
					// TODO 登录成功，不管之前是什么登录状态，游戏内部都要切换成新的用户
					String uid = BDGameSDK.getLoginUid();
					String token = BDGameSDK.getLoginAccessToken();
					U8SDK.getInstance().onSwitchAccount(token);
					break;
				case ResultCode.LOGIN_FAIL:
					// 登录失败，游戏内部之前如果是已经登录的，要清除自己记录的登录状态，设置成未登录。如果之前未登录，不用处理。
					U8SDK.getInstance().onLogout();
					break;
				case ResultCode.LOGIN_CANCEL:
					// TODO 取消，操作前后的登录状态没变化
					break;
				default:
					// 此时当登录失败处理，参照ResultCode.LOGIN_FAIL（正常情况下不会到这个步骤，除非SDK内部异常）
					break;
				}
			}

		});
	}

	/**
	 * @Description: 监听session失效时重新登录
	 */
	private void setSessionInvalidListener() {
		BDGameSDK.setSessionInvalidListener(new IResponse<Void>() {

			@Override
			public void onResponse(int resultCode, String resultDesc, Void extraData) {
				if (resultCode == ResultCode.SESSION_INVALID) {
					// 会话失效，开发者需要重新登录或者重启游戏
					U8SDK.getInstance().onLogout();
				}
			}
		});
	}

	public void logout() {
		try {
			BDGameSDK.logout();
			U8SDK.getInstance().onLogout();
		} catch (Exception e) {
			Log.d("U8SDK", "logout异常.....");
			e.printStackTrace();
		}
	}

	public void switchAccount() {
		logout();
	}

	public void submitExtendData(UserExtraData extraData) {
		// TODO Auto-generated method stub

	}

	public void baiduSdkExit(Activity context) {
		BDGameSDK.gameExit(U8SDK.getInstance().getContext(), new OnGameExitListener() {
					@Override
					public void onGameExit() {
						// 在此加入退出游戏的代码
						U8SDK.getInstance().getContext().finish();
						closeFloatView();
					}
				});
	}
	
	public boolean isInited() {
		return this.state.ordinal() >= SDKState.StateInited.ordinal();
	}
	
	public boolean isLogined() {
		return this.state.ordinal() >= SDKState.StateLogined.ordinal();
	}
}
