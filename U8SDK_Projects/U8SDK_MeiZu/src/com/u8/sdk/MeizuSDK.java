package com.u8.sdk;

import org.json.JSONObject;

import android.os.Bundle;

import com.meizu.gamesdk.model.callback.MzExitListener;
import com.meizu.gamesdk.model.callback.MzLoginListener;
import com.meizu.gamesdk.model.callback.MzPayListener;
import com.meizu.gamesdk.model.model.LoginResultCode;
import com.meizu.gamesdk.model.model.MzAccountInfo;
import com.meizu.gamesdk.model.model.PayResultCode;
import com.meizu.gamesdk.online.core.MzGameBarPlatform;
import com.meizu.gamesdk.online.core.MzGameCenterPlatform;
import com.meizu.gamesdk.online.model.model.MzBuyInfo;
import com.u8.sdk.log.Log;

public class MeizuSDK {
	private static MeizuSDK instance;

	private String appId;
	private String appKey;
	private static MzGameBarPlatform mzGameBarPlatform;

	private MeizuSDK() {
	}

	public static MeizuSDK getInstance() {
		if (instance == null) {
			instance = new MeizuSDK();
		}
		return instance;
	}

	public void initSDK(SDKParams params) {
		this.parseSDKParams(params);
		this.initSDK();
	}

	private void parseSDKParams(SDKParams params) {
		this.appId = params.getString("MZ_APPID");
		this.appKey = params.getString("MZ_APPKEY");
	}

	private void initSDK() {
		try {
			U8SDK.getInstance().setActivityCallback(
					new ActivityCallbackAdapter() {
						@Override
						public void onBackPressed() {
							exitGame();
						}

						@Override
						public void onCreate() {
							U8SDK.getInstance().runOnMainThread(new Runnable() {
								@Override
								public void run() {
//									Log.d("U8SDK", "WRONG ONCREATE>...");
//									mzGameBarPlatform = new MzGameBarPlatform(U8SDK.getInstance().getContext(), MzGameBarPlatform.GRAVITY_RIGHT_BOTTOM);
//									Log.d("U8SDK", "WRONG ONCREATE>...XXXX");
//									mzGameBarPlatform.onActivityCreate();
								}
							});
						}

						@Override
						public void onDestroy() {
							if(mzGameBarPlatform != null)
								mzGameBarPlatform.onActivityDestroy();
						}

						@Override
						public void onStop() {
							hideFloatView();
						}

						@Override
						public void onResume() {
							if(mzGameBarPlatform != null)
								mzGameBarPlatform.onActivityResume();
						}

						@Override
						public void onPause() {
							if(mzGameBarPlatform != null)
								mzGameBarPlatform.onActivityPause();
						}
					});
			MzGameCenterPlatform.init(U8SDK.getInstance().getContext(), this.appId, this.appKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void doLogin() {
		try {
			MzGameCenterPlatform.login(U8SDK.getInstance().getContext(),
					new MzLoginListener() {
						@Override
						public void onLoginResult(int code, MzAccountInfo accountInfo, String errorMsg) {
							// TODO 登录结果回调。 注意，该回调跑在应用主线程，不能在这里做耗时操作
							switch (code) {
							case LoginResultCode.LOGIN_SUCCESS:
								String dataStr = "{ uid: \""
										+ accountInfo.getUid()
										+ "\", name : \""
										+ accountInfo.getName()
										+ "\", session : \""
										+ accountInfo.getSession() + "\" }";
								U8SDK.getInstance().onResult(U8Code.CODE_LOGIN_SUCCESS, "login success");
								U8SDK.getInstance().onLoginResult(dataStr);
								showFloatView();
								break;
							case LoginResultCode.LOGIN_ERROR_CANCEL:
								U8SDK.getInstance().onResult(U8Code.CODE_LOGIN_FAIL, "login error");
								break;
							default:
								U8SDK.getInstance().onResult(U8Code.CODE_LOGIN_FAIL, "login fail");
								break;
							}
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void doPay(final PayParams data) {
		try {
			U8SDK.getInstance().runOnMainThread(new Runnable() {
				@Override
				public void run() {
					Bundle buyBundle = buildOrder(data);
					if(buyBundle == null) {
						U8SDK.getInstance().onResult(U8Code.CODE_PAY_FAIL, "pay cancel");
					} else {
						MzGameCenterPlatform.payOnline(U8SDK.getInstance()
								.getContext(), buyBundle, new MzPayListener() {
							@Override
							public void onPayResult(int code, Bundle info,
									String errorMsg) {
								// TODO 支付结果回调，该回调跑在应用主线程。
								// 注意，该回调跑在应用主线程，不能在这里做耗时操作
								switch (code) {
								case PayResultCode.PAY_SUCCESS:
									// TODO 如果成功，接下去需要到自己的服务器查询订单结果
									// MzBuyInfo payInfo =
									// MzBuyInfo.fromBundle(info);
									U8SDK.getInstance().onResult(U8Code.CODE_PAY_SUCCESS, "pay success");
									break;
								case PayResultCode.PAY_ERROR_CANCEL:
									// TODO 用户主动取消支付操作，不需要提示用户失败
									U8SDK.getInstance().onResult(U8Code.CODE_PAY_FAIL, "pay cancel");
									break;
								default:
									// 支付失败，注意，错误消息(errorMsg)需要由游戏展示给用户，提示失败原因
									U8SDK.getInstance().onResult(U8Code.CODE_PAY_FAIL, "pay fail");
									//Log.d("U8SDK", "doPay.., message:" + errorMsg + ",code:" + code);
									break;
								}
							}
						});
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Bundle buildOrder(PayParams data) {
		try {
			final PayParams pobj = data;
			//Log.d("U8SDK", "buildOrder......" + pobj.getExtension());
			if (pobj.getExtension().length() > 0) {
				JSONObject object = new JSONObject(pobj.getExtension());
				// cp_order_id (不能为空)
				String orderId = object.getString("cp_order_id");
				// sign (不能为空)
				String sign = object.getString("sign");
				// sign_type (不能为空)
				String signType = object.getString("sign_type"); 
				int buyCount = object.getInt("buy_amount"); 
				String cpUserInfo = object.has("user_info") ? object.getString("user_info") : ""; 
				String amount = object.has("total_price") ? object.getString("total_price") : "0"; 
				String productId = object.getString("product_id"); 
				String productSubject = object.getString("product_subject"); 
				String productBody = object.getString("product_body"); 
				String productUnit = object.getString("product_unit");
				// app_id (不能为空)
				String appid = object.getString("app_id");  //= this.appId;
				// uid (不能为空)flyme账号用户ID
				String uid = object.getString("uid");
				String perPrice = object.getString("product_per_price"); // product_per_price
				long createTime = object.getLong("create_time"); // create_time
				int payType = object.getInt("pay_type"); // pay_type：0-定额；1-不定额
				return new MzBuyInfo().setBuyCount(buyCount)
						.setCpUserInfo(cpUserInfo).setOrderAmount(amount)
						.setOrderId(orderId).setPerPrice(perPrice)
						.setProductBody(productBody).setProductId(productId)
						.setProductSubject(productSubject)
						.setProductUnit(productUnit).setSign(sign)
						.setSignType(signType).setCreateTime(createTime)
						.setAppid(appid).setUserUid(uid).setPayType(payType)
						.toBundle();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// 登出
	public void doLogOut() {
		MzGameCenterPlatform.logout(U8SDK.getInstance().getContext(),
				new MzLoginListener() {
					@Override
					public void onLoginResult(int code, MzAccountInfo mzAccountInfo, String msg) {
						// TODO 在这里处理登出逻辑
						//Toast.makeText(U8SDK.getInstance().getContext(), "调用doLogOut", Toast.LENGTH_LONG).show();
						U8SDK.getInstance().onLogout();
					}
				});
	}

	public void exitGame() {
		MzGameCenterPlatform.exitSDK(U8SDK.getInstance().getContext(), new MzExitListener() {
			public void callback(int code, String msg) {  //MzExitListener.CODE_SDK_LOGOUT
				if (code == MzExitListener.CODE_SDK_EXIT) {
					U8SDK.getInstance().getContext().finish();
					hideFloatView();
				} else if (code == MzExitListener.CODE_SDK_CONTINUE) {
					// TODO 继续游戏
				}
			}
		});
	}
	
	public void showFloatView() {
		U8SDK.getInstance().runOnMainThread(new Runnable() {
			@Override
			public void run() {
				if(mzGameBarPlatform == null) {
					mzGameBarPlatform = new MzGameBarPlatform(U8SDK.getInstance().getContext(), MzGameBarPlatform.GRAVITY_RIGHT_BOTTOM);
					mzGameBarPlatform.onActivityCreate();
				}				
				mzGameBarPlatform.showGameBar();
			}
		});
	}
	public void hideFloatView() {
		U8SDK.getInstance().runOnMainThread(new Runnable() {			
			@Override
			public void run() {
				if(mzGameBarPlatform != null) {
					mzGameBarPlatform.hideGameBar();
				}
			}
		});
	}
	
}
